package com.superkos.app.controller;

import com.superkos.app.dto.MatchResult;
import com.superkos.app.model.*;
import com.superkos.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles the roommate invite flow for accepted reservasi.
 *
 * Routes:
 *  GET  /reservasi/{id}/invite-roommate       — show roommate invite page
 *  POST /reservasi/{id}/invite-roommate/{uid}  — invite a roommate (adds to group chat)
 */
@Controller
public class RoommateInviteController {

    @Autowired private PencariHunianRepository pencariHunianRepository;
    @Autowired private RoommateRequestRepository requestRepository;
    @Autowired private ReservasiRepository reservasiRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;

    private PencariHunian getMe(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || !(u instanceof PencariHunian)) return null;
        return pencariHunianRepository.findById(u.getId()).orElse(null);
    }

    /**
     * Show the roommate invite page.
     * Lists: (1) accepted roommates, (2) recommended candidates (70%+ compatibility).
     */
    @GetMapping("/reservasi/{id}/invite-roommate")
    public String showInvitePage(@PathVariable int id, HttpSession session, Model model) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        Reservasi reservasi = reservasiRepository.findById(id).orElse(null);
        if (reservasi == null || reservasi.getPencariHunian().getId() != me.getId()) {
            return "redirect:/";
        }
        if (!"ACCEPTED".equals(reservasi.getStatus()) || reservasi.getChatRoom() == null) {
            return "redirect:/";
        }

        ChatRoom room = reservasi.getChatRoom();
        Set<Integer> existingParticipantIds = room.getParticipants().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        RoommateSurvey mySurvey = me.getRoommateSurvey();

        // ── 1. Accepted Roommates ─────────────────────────────────────────────
        // Find all users who have an ACCEPTED roommate request with me
        List<RoommateRequest> sentAccepted = requestRepository.findByPencariHunianOrderByIdRequestDesc(me);
        List<RoommateRequest> receivedAccepted = requestRepository.findByTargetPencariOrderByIdRequestDesc(me);

        List<Map<String, Object>> acceptedRoommates = new ArrayList<>();
        Set<Integer> acceptedIds = new HashSet<>();

        for (RoommateRequest req : sentAccepted) {
            if ("ACCEPTED".equals(req.getStatus())) {
                PencariHunian other = req.getTargetPencari();
                if (!existingParticipantIds.contains(other.getId())) {
                    Map<String, Object> entry = buildRoommateEntry(me, other, mySurvey);
                    acceptedRoommates.add(entry);
                    acceptedIds.add(other.getId());
                }
            }
        }
        for (RoommateRequest req : receivedAccepted) {
            if ("ACCEPTED".equals(req.getStatus())) {
                PencariHunian other = req.getPencariHunian();
                if (!existingParticipantIds.contains(other.getId()) && !acceptedIds.contains(other.getId())) {
                    Map<String, Object> entry = buildRoommateEntry(me, other, mySurvey);
                    acceptedRoommates.add(entry);
                    acceptedIds.add(other.getId());
                }
            }
        }

        // ── 2. Recommended Candidates (70%+) ─────────────────────────────────
        List<Map<String, Object>> recommended = new ArrayList<>();

        if (mySurvey != null && mySurvey.isQuizComplete()) {
            Page<PencariHunian> candidates = pencariHunianRepository.findCandidates(
                    me.getId(), PageRequest.of(0, 200));

            for (PencariHunian candidate : candidates) {
                // Skip if already in chat, already accepted roommate, or is me
                if (existingParticipantIds.contains(candidate.getId())
                        || acceptedIds.contains(candidate.getId())
                        || candidate.getId() == me.getId()) {
                    continue;
                }

                RoommateSurvey theirSurvey = candidate.getRoommateSurvey();
                if (theirSurvey != null && theirSurvey.isQuizComplete()) {
                    double score = mySurvey.hitungKecocokan(theirSurvey);
                    if (score >= 70.0) {
                        Map<String, Object> entry = buildRoommateEntry(me, candidate, mySurvey);
                        recommended.add(entry);
                    }
                }
            }

            // Sort by score descending
            recommended.sort((a, b) -> Double.compare(
                    (double) b.get("score"), (double) a.get("score")));
        }

        model.addAttribute("loggedInUser",       me);
        model.addAttribute("reservasi",          reservasi);
        model.addAttribute("hunian",             reservasi.getHunian());
        model.addAttribute("acceptedRoommates",  acceptedRoommates);
        model.addAttribute("recommended",        recommended);
        model.addAttribute("currentParticipants", room.getParticipants());
        return "invite_roommate";
    }

    /**
     * Invite a roommate to the group chat.
     * For recommended (non-accepted) users, also auto-send a roommate request.
     */
    @PostMapping("/reservasi/{reservasiId}/invite-roommate/{userId}")
    public String inviteRoommate(@PathVariable int reservasiId,
                                  @PathVariable int userId,
                                  HttpSession session) {
        PencariHunian me = getMe(session);
        if (me == null) return "redirect:/login";

        Reservasi reservasi = reservasiRepository.findById(reservasiId).orElse(null);
        if (reservasi == null || reservasi.getPencariHunian().getId() != me.getId()) {
            return "redirect:/";
        }
        if (!"ACCEPTED".equals(reservasi.getStatus()) || reservasi.getChatRoom() == null) {
            return "redirect:/";
        }

        PencariHunian target = pencariHunianRepository.findById(userId).orElse(null);
        if (target == null || target.getId() == me.getId()) {
            return "redirect:/reservasi/" + reservasiId + "/invite-roommate";
        }

        ChatRoom room = chatRoomRepository.findById(reservasi.getChatRoom().getIdChat()).orElse(null);
        if (room == null) return "redirect:/";

        // Add to group chat
        room.addParticipant(target);
        chatRoomRepository.save(room);

        // Auto-send roommate request if not already connected
        Optional<RoommateRequest> pendingOpt = requestRepository.findPendingBetween(me, target);
        Optional<RoommateRequest> acceptedOpt = requestRepository.findAcceptedBetween(me, target);
        if (pendingOpt.isEmpty() && acceptedOpt.isEmpty()) {
            me.kirimRoommateRequest(target, requestRepository);
        }

        return "redirect:/reservasi/" + reservasiId + "/invite-roommate?invited=" + target.getNama();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Map<String, Object> buildRoommateEntry(PencariHunian me, PencariHunian other, RoommateSurvey mySurvey) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("user", other);
        entry.put("initial", other.getNama() != null && !other.getNama().isEmpty()
                ? String.valueOf(other.getNama().charAt(0)).toUpperCase() : "?");

        double score = 0;
        String label = "";
        if (mySurvey != null && mySurvey.isQuizComplete()) {
            RoommateSurvey theirSurvey = other.getRoommateSurvey();
            if (theirSurvey != null && theirSurvey.isQuizComplete()) {
                score = mySurvey.hitungKecocokan(theirSurvey);
                label = RoommateSurvey.fuzzyLabel(score);
            }
        }
        entry.put("score", Math.round(score * 10.0) / 10.0);
        entry.put("label", label);
        return entry;
    }
}
