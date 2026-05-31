package com.superkos.app.controller;

import com.superkos.app.dto.MatchResult;
import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.RoommateSurvey;
import com.superkos.app.model.User;
import com.superkos.app.repository.PencariHunianRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the Roommate Match feature.
 *
 * Route: GET /roommate/match?page=0
 *
 * Steps:
 *  1. Identify the logged-in PencariHunian and load their 3 category scores.
 *  2. Query the DB for candidates (paginated, 10 per page) — only users with a
 *     complete survey are included; the current user is always excluded.
 *  3. For each candidate, call RoommateSurvey.hitungKecocokan() with the exact
 *     formula: compatibility_i = (1 – |diff_i| / 9) × 100, overall = avg of 3.
 *  4. Discard any result where the score < 0 (incomplete survey guard).
 *  5. Attach a fuzzy label per RoommateSurvey.fuzzyLabel().
 *  6. Sort highest score first.
 *  7. Pass the sorted list + pagination metadata to the template.
 */
@Controller
public class RoommateMatchController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private PencariHunianRepository pencariHunianRepository;

    @GetMapping("/roommate/match")
    public String showMatches(
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        // ── 1. Auth & role guard ──────────────────────────────────────────────
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (!(loggedInUser instanceof PencariHunian)) return "redirect:/";

        // Load a fresh copy from DB to get the survey eagerly
        PencariHunian me = pencariHunianRepository.findById(loggedInUser.getId()).orElse(null);
        if (me == null) return "redirect:/login";

        // ── 2. Current user must have completed the quiz ──────────────────────
        RoommateSurvey mySurvey = me.getRoommateSurvey();
        if (mySurvey == null || !mySurvey.isQuizComplete()) {
            model.addAttribute("loggedInUser", me);
            model.addAttribute("noSurvey", true);
            return "roommate_match";
        }

        // ── 3. Paginated candidate query (DB-level exclusion of self + incomplete surveys) ──
        Page<PencariHunian> candidatePage = pencariHunianRepository
                .findCandidates(me.getId(), PageRequest.of(page, PAGE_SIZE));

        // ── 4 & 5. Calculate scores + build DTOs ─────────────────────────────
        List<MatchResult> results = candidatePage.getContent().stream()
                .map(candidate -> {
                    RoommateSurvey theirSurvey = candidate.getRoommateSurvey();

                    double overall = mySurvey.hitungKecocokan(theirSurvey);
                    if (overall < 0) return null; // filter guard (should not happen after DB filter)

                    double[] breakdown = mySurvey.getBreakdown(theirSurvey);

                    return new MatchResult(
                            candidate.getId(),
                            candidate.getNama(),
                            candidate.getEmail(),
                            candidate.getKontak(),
                            candidate.getLokasi(),
                            candidate.getGender(),
                            candidate.getPekerjaan(),
                            candidate.getBiodata(),
                            candidate.getUmur(),
                            overall,
                            breakdown[0],   // social compatibility %
                            breakdown[1],   // cleanliness compatibility %
                            breakdown[2],   // sleep compatibility %
                            RoommateSurvey.fuzzyLabel(overall)
                    );
                })
                .filter(r -> r != null)
                // ── 6. Sort: highest overall score first ──────────────────────
                .sorted(Comparator.comparingDouble(MatchResult::getOverallScore).reversed())
                .collect(Collectors.toList());

        // ── 7. Pass to template ───────────────────────────────────────────────
        model.addAttribute("loggedInUser",  me);
        model.addAttribute("results",       results);
        model.addAttribute("currentPage",   candidatePage.getNumber());
        model.addAttribute("totalPages",    candidatePage.getTotalPages());
        model.addAttribute("totalResults",  candidatePage.getTotalElements());
        model.addAttribute("hasPrevious",   candidatePage.hasPrevious());
        model.addAttribute("hasNext",       candidatePage.hasNext());
        model.addAttribute("noSurvey",      false);

        return "roommate_match";
    }
}
