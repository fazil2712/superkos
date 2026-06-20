package com.superkos.app.controller;

import com.superkos.app.dto.MatchResult;
import com.superkos.app.model.*;
import com.superkos.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
// #naufal(ChatRoom & Message)
@Controller
public class ChatController {

    @Autowired private PencariHunianRepository pencariHunianRepository;
    @Autowired private RoommateRequestRepository requestRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private ReservasiRepository reservasiRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private HunianRepository hunianRepository;

    

    private PencariHunian getMe(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || !(u instanceof PencariHunian)) return null;
        return pencariHunianRepository.findById(u.getId()).orElse(null);
    }

    private User getMeGeneric(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return null;
        return userRepository.findById(u.getId()).orElse(null);
    }

    private String initial(String nama) {
        return (nama != null && !nama.isEmpty())
                ? String.valueOf(nama.charAt(0)).toUpperCase() : "?";
    }

    

    @GetMapping("/roommate/profile/{id}")
    public String candidateProfile(@PathVariable int id, HttpSession session, Model model) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        PencariHunian candidate = pencariHunianRepository.findById(id).orElse(null);
        if (candidate == null || candidate.getId() == me.getId()) return "redirect:/roommate/match";

        
        MatchResult matchResult = null;
        RoommateSurvey mySurvey   = me.getRoommateSurvey();
        RoommateSurvey theirSurvey = candidate.getRoommateSurvey();
        if (mySurvey != null && mySurvey.isQuizComplete()
                && theirSurvey != null && theirSurvey.isQuizComplete()) {
            double overall       = mySurvey.hitungKecocokan(theirSurvey);
            double[] breakdown   = mySurvey.getBreakdown(theirSurvey);
            matchResult = new MatchResult(
                    candidate.getId(), candidate.getNama(), candidate.getEmail(),
                    candidate.getKontak(), candidate.getLokasi(), candidate.getGender(),
                    candidate.getPekerjaan(), candidate.getBiodata(), candidate.getUmur(), candidate.getFotoProfil(),
                    overall, breakdown[0], breakdown[1], breakdown[2],
                    RoommateSurvey.fuzzyLabel(overall));
        }

        
        Optional<RoommateRequest> pendingOpt  = requestRepository.findPendingBetween(me, candidate);
        Optional<RoommateRequest> acceptedOpt = requestRepository.findAcceptedBetween(me, candidate);

        String requestState; 
        int    chatRoomId = -1;
        int    requestId  = -1;

        if (acceptedOpt.isPresent()) {
            requestState = "ACCEPTED";
            RoommateRequest acc = acceptedOpt.get();
            requestId = acc.getIdRequest();
            if (acc.getChatRoom() != null) chatRoomId = acc.getChatRoom().getIdChat();

            
            if (acc.getPencariHunian().getId() == me.getId() && !acc.isSenderRead()) {
                acc.setSenderRead(true);
                requestRepository.save(acc);
            }
        } else if (pendingOpt.isPresent()) {
            RoommateRequest req = pendingOpt.get();
            requestId = req.getIdRequest();
            requestState = req.getPencariHunian().getId() == me.getId()
                    ? "PENDING_SENT" : "PENDING_RECEIVED";
        } else {
            requestState = "NONE";
        }

        
        long pendingCount = requestRepository.countByTargetPencariAndStatus(me, "PENDING");

        model.addAttribute("loggedInUser",   me);
        model.addAttribute("candidate",      candidate);
        model.addAttribute("matchResult",    matchResult);
        model.addAttribute("requestState",   requestState);
        model.addAttribute("chatRoomId",     chatRoomId);
        model.addAttribute("requestId",      requestId);
        model.addAttribute("pendingCount",   pendingCount);
        model.addAttribute("initial",        initial(candidate.getNama()));
        return "candidate_profile";
    }

    

    @PostMapping("/roommate/request/{targetId}")
    public String sendRequest(@PathVariable int targetId, HttpSession session) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        PencariHunian target = pencariHunianRepository.findById(targetId).orElse(null);
        if (target == null || target.getId() == me.getId()) return "redirect:/roommate/match";

        
        if (requestRepository.findPendingBetween(me, target).isPresent()
                || requestRepository.findAcceptedBetween(me, target).isPresent()) {
            return "redirect:/roommate/profile/" + targetId;
        }

        me.kirimRoommateRequest(target, requestRepository);

        return "redirect:/roommate/profile/" + targetId + "?sent=true";
    }

    

    @GetMapping("/roommate/inbox")
    public String showInbox(HttpSession session, Model model) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        List<RoommateRequest> received = requestRepository.findByTargetPencariOrderByIdRequestDesc(me);
        List<RoommateRequest> sent     = requestRepository.findByPencariHunianOrderByIdRequestDesc(me);
        List<ChatRoom>        chats    = chatRoomRepository.findByParticipant(me);

        
        long unreadChatsCount    = chats.stream().filter(c -> c.getUnreadCount(me) > 0).count();
        long unreadAcceptedCount = requestRepository.countByPencariHunianAndStatusAndSenderRead(me, "ACCEPTED", false);
        long pendingCount        = requestRepository.countByTargetPencariAndStatus(me, "PENDING");

        model.addAttribute("loggedInUser",       me);
        model.addAttribute("received",           received);
        model.addAttribute("sent",               sent);
        model.addAttribute("chats",              chats);
        model.addAttribute("pendingCount",       pendingCount);
        model.addAttribute("unreadChatsCount",    unreadChatsCount);
        model.addAttribute("unreadAcceptedCount", unreadAcceptedCount);
        return "chat_inbox";
    }

    

    @PostMapping("/roommate/request/{requestId}/accept")
    public String acceptRequest(@PathVariable int requestId, HttpSession session) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        RoommateRequest req = requestRepository.findById(requestId).orElse(null);
        if (req == null || req.getTargetPencari().getId() != me.getId()) return "redirect:/roommate/inbox";
        if (!"PENDING".equals(req.getStatus())) return "redirect:/roommate/inbox";

        
        ChatRoom room = new ChatRoom();
        room.setChatType("ROOMMATE");
        room.addParticipant(req.getPencariHunian());
        room.addParticipant(req.getTargetPencari());
        room.setCreatedAt(new Date());
        room = chatRoomRepository.save(room);

        
        req.terima();
        req.setChatRoom(room);
        requestRepository.save(req);

        return "redirect:/chat/" + room.getIdChat();
    }

    

    @PostMapping("/roommate/request/{requestId}/reject")
    public String rejectRequest(@PathVariable int requestId, HttpSession session) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        RoommateRequest req = requestRepository.findById(requestId).orElse(null);
        if (req == null || req.getTargetPencari().getId() != me.getId()) return "redirect:/roommate/inbox";

        req.tolak();
        requestRepository.save(req);
        return "redirect:/roommate/inbox";
    }

    

    @GetMapping("/chat/{chatId}")
    public String showChat(@PathVariable int chatId, HttpSession session, Model model) {
        User me = getMeGeneric(session);
        if (me == null) return "redirect:/login";

        ChatRoom room = chatRoomRepository.findById(chatId).orElse(null);
        if (room == null) return "redirect:/";

        
        if (!room.isParticipant(me)) return "redirect:/";

        List<Message> messages = messageRepository.findByChatRoomOrderByTimestampAsc(room);

        
        boolean msgUpdated = false;
        for (Message msg : messages) {
            if (msg.getSender().getId() != me.getId() && !msg.isRead()) {
                msg.setRead(true);
                msgUpdated = true;
            }
        }
        if (msgUpdated) {
            messageRepository.saveAll(messages);
        }

        
        boolean isGroupChat = "RESERVASI".equals(room.getChatType()) || room.getParticipants().size() > 2;
        User other = null;
        if (!isGroupChat && room.getParticipants().size() == 2) {
            other = room.getParticipants().get(0).getId() == me.getId()
                    ? room.getParticipants().get(1) : room.getParticipants().get(0);
        }

        
        if (me instanceof PencariHunian pencariMe && other instanceof PencariHunian otherPencari) {
            Optional<RoommateRequest> reqOpt = requestRepository.findAcceptedBetween(pencariMe, otherPencari);
            if (reqOpt.isPresent()) {
                RoommateRequest req = reqOpt.get();
                if (req.getPencariHunian().getId() == me.getId() && !req.isSenderRead()) {
                    req.setSenderRead(true);
                    requestRepository.save(req);
                }
            }
        }

        
        List<User> otherParticipants = room.getParticipants().stream()
                .filter(p -> p.getId() != me.getId())
                .collect(Collectors.toList());

        String chatTitle;
        if (isGroupChat && room.getHunian() != null) {
            chatTitle = room.getHunian().getNamaHunian();
        } else if (other != null) {
            chatTitle = other.getNama();
        } else {
            chatTitle = "Chat";
        }

        
        int inviteReservasiId = -1;
        if ("RESERVASI".equals(room.getChatType()) && me instanceof PencariHunian) {
            List<Reservasi> reservasiList = reservasiRepository.findByHunianAndStatus(room.getHunian(), "ACCEPTED");
            for (Reservasi r : reservasiList) {
                if (r.getPencariHunian().getId() == me.getId() && r.getChatRoom() != null
                        && r.getChatRoom().getIdChat() == room.getIdChat()) {
                    inviteReservasiId = r.getIdReservasi();
                    break;
                }
            }
        }

        model.addAttribute("loggedInUser",      me);
        model.addAttribute("room",              room);
        model.addAttribute("messages",          messages);
        model.addAttribute("other",             other);
        model.addAttribute("isGroupChat",       isGroupChat);
        model.addAttribute("otherParticipants", otherParticipants);
        model.addAttribute("chatTitle",         chatTitle);
        model.addAttribute("inviteReservasiId", inviteReservasiId);
        return "chatroom";
    }

    

    @PostMapping("/chat/{chatId}/send")
    public String sendMessage(@PathVariable int chatId,
                              @RequestParam(required = false) String isiPesan,
                              @RequestParam(value = "replyToId", required = false) Long replyToId,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              HttpSession session) {
        User me = getMeGeneric(session);
        if (me == null) return "redirect:/login";

        ChatRoom room = chatRoomRepository.findById(chatId).orElse(null);
        if (room == null || !room.isParticipant(me)) return "redirect:/";

        Message msg = new Message();
        msg.setSender(me);
        msg.setTimestamp(new Date());
        msg.setChatRoom(room);
        msg.setReplyToId(replyToId);

        boolean hasContent = false;

        if (isiPesan != null && !isiPesan.trim().isEmpty()) {
            msg.setIsiPesan(isiPesan.trim());
            hasContent = true;
        }

        if (file != null && !file.isEmpty()) {
            try {
                String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file.jpg";
                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : ".jpg";

                java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads", "chat");
                java.nio.file.Files.createDirectories(uploadDir);
                String fileName = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + ext;
                java.nio.file.Path filePath = uploadDir.resolve(fileName);
                java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                msg.setAttachmentUrl("/uploads/chat/" + fileName);
                hasContent = true;
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        if (hasContent) {
            messageRepository.save(msg);
        }

        return "redirect:/chat/" + chatId;
    }

    @PostMapping("/chat/message/{messageId}/reaction")
    public String reactMessage(@PathVariable int messageId,
                               @RequestParam String emoji,
                               HttpSession session) {
        User me = getMeGeneric(session);
        if (me == null) return "redirect:/login";

        Message msg = messageRepository.findById(messageId).orElse(null);
        if (msg == null) return "redirect:/";
        if (!msg.getChatRoom().isParticipant(me)) return "redirect:/";

        msg.setReaction(emoji);
        messageRepository.save(msg);

        return "redirect:/chat/" + msg.getChatRoom().getIdChat();
    }

    @PostMapping("/chat/message/{messageId}/delete")
    public String deleteMessage(@PathVariable int messageId,
                                HttpSession session) {
        User me = getMeGeneric(session);
        if (me == null) return "redirect:/login";

        Message msg = messageRepository.findById(messageId).orElse(null);
        if (msg == null) return "redirect:/";
        if (msg.getSender().getId() != me.getId()) return "redirect:/chat/" + msg.getChatRoom().getIdChat();

        msg.setIsDeleted(true);
        messageRepository.save(msg);

        return "redirect:/chat/" + msg.getChatRoom().getIdChat();
    }

    @PostMapping("/chat/{chatId}/leave")
    public String leaveGroup(@PathVariable int chatId,
                             HttpSession session) {
        User me = getMeGeneric(session);
        if (me == null) return "redirect:/login";

        ChatRoom room = chatRoomRepository.findById(chatId).orElse(null);
        if (room != null) {
            room.removeParticipant(me);
            chatRoomRepository.save(room);
        }
        return "redirect:/roommate/inbox";
    }

    @PostMapping("/chat/tanya/{hunianId}")
    public String tanyaPemilik(@PathVariable int hunianId, HttpSession session) {
        User me = getMeGeneric(session);
        if (me == null) return "redirect:/login";

        Hunian hunian = hunianRepository.findById(hunianId).orElse(null);
        if (hunian == null) return "redirect:/";

        PemilikProperti pemilik = hunian.getPemilik();
        if (pemilik == null || pemilik.getId() == me.getId()) {
            return "redirect:/hunian/" + hunianId;
        }

        // Find existing chat room of type 'RESERVASI' for this hunian and user
        Optional<ChatRoom> existingRoom = chatRoomRepository.findReservasiRoom(hunian, me);
        if (existingRoom.isPresent()) {
            return "redirect:/chat/" + existingRoom.get().getIdChat();
        }

        // Create new ChatRoom
        ChatRoom room = new ChatRoom();
        room.setChatType("RESERVASI");
        room.setHunian(hunian);
        room.addParticipant(me);
        room.addParticipant(pemilik);
        room.setCreatedAt(new Date());
        room = chatRoomRepository.save(room);

        return "redirect:/chat/" + room.getIdChat();
    }
}
// #/naufal(ChatRoom & Message)
