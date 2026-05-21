package com.superkos.app.controller;

import com.superkos.app.dto.MatchResult;
import com.superkos.app.model.*;
import com.superkos.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Handles the profile view, roommate requests, and chat rooms.
 *
 * Routes:
 *  GET  /roommate/profile/{id}         — candidate's detailed profile (from match list)
 *  POST /roommate/request/{targetId}   — send a chat request
 *  GET  /roommate/inbox                — view received requests + accepted chats
 *  POST /roommate/request/{id}/accept  — accept a pending request (creates ChatRoom)
 *  POST /roommate/request/{id}/reject  — reject a pending request
 *  GET  /chat/{chatId}                 — open the chat room
 *  POST /chat/{chatId}/send            — send a message
 */
@Controller
public class ChatController {

    @Autowired private PencariHunianRepository pencariHunianRepository;
    @Autowired private RoommateRequestRepository requestRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private UserRepository userRepository;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PencariHunian getMe(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || !(u instanceof PencariHunian)) return null;
        return pencariHunianRepository.findById(u.getId()).orElse(null);
    }

    private String initial(String nama) {
        return (nama != null && !nama.isEmpty())
                ? String.valueOf(nama.charAt(0)).toUpperCase() : "?";
    }

    // ── Candidate Profile View ────────────────────────────────────────────────

    @GetMapping("/roommate/profile/{id}")
    public String candidateProfile(@PathVariable int id, HttpSession session, Model model) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        PencariHunian candidate = pencariHunianRepository.findById(id).orElse(null);
        if (candidate == null || candidate.getId() == me.getId()) return "redirect:/roommate/match";

        // Build MatchResult for score display (if both have surveys)
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
                    candidate.getPekerjaan(), candidate.getBiodata(), candidate.getUmur(),
                    overall, breakdown[0], breakdown[1], breakdown[2],
                    RoommateSurvey.fuzzyLabel(overall));
        }

        // Request / chat state
        Optional<RoommateRequest> pendingOpt  = requestRepository.findPendingBetween(me, candidate);
        Optional<RoommateRequest> acceptedOpt = requestRepository.findAcceptedBetween(me, candidate);

        String requestState; // NONE | PENDING_SENT | PENDING_RECEIVED | ACCEPTED
        int    chatRoomId = -1;
        int    requestId  = -1;

        if (acceptedOpt.isPresent()) {
            requestState = "ACCEPTED";
            RoommateRequest acc = acceptedOpt.get();
            requestId = acc.getIdRequest();
            if (acc.getChatRoom() != null) chatRoomId = acc.getChatRoom().getIdChat();
        } else if (pendingOpt.isPresent()) {
            RoommateRequest req = pendingOpt.get();
            requestId = req.getIdRequest();
            requestState = req.getPencariHunian().getId() == me.getId()
                    ? "PENDING_SENT" : "PENDING_RECEIVED";
        } else {
            requestState = "NONE";
        }

        // Pending inbox count for badge
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

    // ── Send Request ──────────────────────────────────────────────────────────

    @PostMapping("/roommate/request/{targetId}")
    public String sendRequest(@PathVariable int targetId, HttpSession session) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        PencariHunian target = pencariHunianRepository.findById(targetId).orElse(null);
        if (target == null || target.getId() == me.getId()) return "redirect:/roommate/match";

        // Idempotency: don't create if a PENDING or ACCEPTED already exists
        if (requestRepository.findPendingBetween(me, target).isPresent()
                || requestRepository.findAcceptedBetween(me, target).isPresent()) {
            return "redirect:/roommate/profile/" + targetId;
        }

        RoommateRequest req = new RoommateRequest();
        req.setPencariHunian(me);
        req.setTargetPencari(target);
        req.setStatus("PENDING");
        requestRepository.save(req);

        return "redirect:/roommate/profile/" + targetId + "?sent=true";
    }

    // ── Inbox ─────────────────────────────────────────────────────────────────

    @GetMapping("/roommate/inbox")
    public String showInbox(HttpSession session, Model model) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        List<RoommateRequest> received = requestRepository.findByTargetPencariOrderByIdRequestDesc(me);
        List<RoommateRequest> sent     = requestRepository.findByPencariHunianOrderByIdRequestDesc(me);
        List<ChatRoom>        chats    = chatRoomRepository.findByParticipant(me);
        long pendingCount              = requestRepository.countByTargetPencariAndStatus(me, "PENDING");

        model.addAttribute("loggedInUser",  me);
        model.addAttribute("received",      received);
        model.addAttribute("sent",          sent);
        model.addAttribute("chats",         chats);
        model.addAttribute("pendingCount",  pendingCount);
        return "chat_inbox";
    }

    // ── Accept Request ────────────────────────────────────────────────────────

    @PostMapping("/roommate/request/{requestId}/accept")
    public String acceptRequest(@PathVariable int requestId, HttpSession session) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        RoommateRequest req = requestRepository.findById(requestId).orElse(null);
        if (req == null || req.getTargetPencari().getId() != me.getId()) return "redirect:/roommate/inbox";
        if (!"PENDING".equals(req.getStatus())) return "redirect:/roommate/inbox";

        // Create the ChatRoom
        ChatRoom room = new ChatRoom();
        room.setParticipant1(req.getPencariHunian());
        room.setParticipant2(req.getTargetPencari());
        room.setCreatedAt(new Date());
        room = chatRoomRepository.save(room);

        // Accept and link
        req.terima();
        req.setChatRoom(room);
        requestRepository.save(req);

        return "redirect:/chat/" + room.getIdChat();
    }

    // ── Reject Request ────────────────────────────────────────────────────────

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

    // ── Chat Room ─────────────────────────────────────────────────────────────

    @GetMapping("/chat/{chatId}")
    public String showChat(@PathVariable int chatId, HttpSession session, Model model) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        ChatRoom room = chatRoomRepository.findById(chatId).orElse(null);
        if (room == null) return "redirect:/roommate/inbox";

        // Only participants may view the chat
        boolean isParticipant = room.getParticipant1().getId() == me.getId()
                || room.getParticipant2().getId() == me.getId();
        if (!isParticipant) return "redirect:/roommate/inbox";

        List<Message> messages = messageRepository.findByChatRoomOrderByTimestampAsc(room);
        User other = room.getParticipant1().getId() == me.getId()
                ? room.getParticipant2() : room.getParticipant1();

        long pendingCount = requestRepository.countByTargetPencariAndStatus(me, "PENDING");

        model.addAttribute("loggedInUser",  me);
        model.addAttribute("room",          room);
        model.addAttribute("messages",      messages);
        model.addAttribute("other",         other);
        model.addAttribute("pendingCount",  pendingCount);
        return "chatroom";
    }

    // ── Send Message ──────────────────────────────────────────────────────────

    @PostMapping("/chat/{chatId}/send")
    public String sendMessage(@PathVariable int chatId,
                              @RequestParam String isiPesan,
                              HttpSession session) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        ChatRoom room = chatRoomRepository.findById(chatId).orElse(null);
        if (room == null) return "redirect:/roommate/inbox";

        boolean isParticipant = room.getParticipant1().getId() == me.getId()
                || room.getParticipant2().getId() == me.getId();
        if (!isParticipant) return "redirect:/roommate/inbox";

        if (isiPesan != null && !isiPesan.trim().isEmpty()) {
            Message msg = new Message();
            msg.setIsiPesan(isiPesan.trim());
            msg.setSender(me);
            msg.setTimestamp(new Date());
            msg.setChatRoom(room);
            messageRepository.save(msg);
        }

        return "redirect:/chat/" + chatId;
    }
}
