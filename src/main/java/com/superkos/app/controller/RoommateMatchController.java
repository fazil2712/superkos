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
// #yury(PencariHunian)
@Controller
public class RoommateMatchController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private PencariHunianRepository pencariHunianRepository;

    @GetMapping("/roommate/match")
    public String showMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minMatch,
            @RequestParam(required = false) String gender,
            HttpSession session,
            Model model) {

        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (!(loggedInUser instanceof PencariHunian)) return "redirect:/";

        
        PencariHunian me = pencariHunianRepository.findById(loggedInUser.getId()).orElse(null);
        if (me == null) return "redirect:/login";

        
        RoommateSurvey mySurvey = me.getRoommateSurvey();
        if (mySurvey == null || !mySurvey.isQuizComplete()) {
            model.addAttribute("loggedInUser", me);
            model.addAttribute("noSurvey", true);
            return "roommate_match";
        }

        // Fetch all candidates
        List<PencariHunian> allCandidates = pencariHunianRepository.findAllCandidates(me.getId());

        
        List<MatchResult> results = allCandidates.stream()
                .map(candidate -> {
                    RoommateSurvey theirSurvey = candidate.getRoommateSurvey();

                    double overall = mySurvey.hitungKecocokan(theirSurvey);
                    if (overall < 0) return null; 

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
                            candidate.getFotoProfil(),
                            overall,
                            breakdown[0],   
                            breakdown[1],   
                            breakdown[2],   
                            RoommateSurvey.fuzzyLabel(overall)
                    );
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());

        // Logika Pencarian: Gunakan keyword untuk memfilter list user berdasarkan kecocokan nama atau lokasi
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            results = results.stream()
                    .filter(r -> (r.getNama() != null && r.getNama().toLowerCase().contains(kw))
                            || (r.getLokasi() != null && r.getLokasi().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        // Logika Filter Gender: Filter list user agar hanya menampilkan gender yang dipilih
        if (gender != null && !gender.trim().isEmpty()) {
            results = results.stream()
                    .filter(r -> r.getGender() != null && r.getGender().equalsIgnoreCase(gender))
                    .collect(Collectors.toList());
        }

        // Logika Filter Kecocokan: Tampilkan hanya yang skornya >= minMatch
        if (minMatch != null) {
            results = results.stream()
                    .filter(r -> r.getOverallScore() >= minMatch)
                    .collect(Collectors.toList());
        }

        // Sort by overall score descending
        results.sort(Comparator.comparingDouble(MatchResult::getOverallScore).reversed());

        // In-memory pagination
        int totalResults = results.size();
        int totalPages = (int) Math.ceil((double) totalResults / PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalResults);

        List<MatchResult> paginatedResults;
        if (start < totalResults) {
            paginatedResults = results.subList(start, end);
        } else {
            paginatedResults = java.util.Collections.emptyList();
        }

        
        model.addAttribute("loggedInUser",  me);
        model.addAttribute("results",       paginatedResults);
        model.addAttribute("currentPage",   page);
        model.addAttribute("totalPages",    totalPages);
        model.addAttribute("totalResults",  totalResults);
        model.addAttribute("hasPrevious",   page > 0);
        model.addAttribute("hasNext",       page < totalPages - 1);
        model.addAttribute("noSurvey",      false);

        // Keep filter values in model
        model.addAttribute("keyword",       keyword);
        model.addAttribute("minMatch",      minMatch);
        model.addAttribute("gender",        gender);

        return "roommate_match";
    }
}
// #/yury(PencariHunian)
