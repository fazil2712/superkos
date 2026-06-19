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

        
        Page<PencariHunian> candidatePage = pencariHunianRepository
                .findCandidates(me.getId(), PageRequest.of(page, PAGE_SIZE));

        
        List<MatchResult> results = candidatePage.getContent().stream()
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
                            overall,
                            breakdown[0],   
                            breakdown[1],   
                            breakdown[2],   
                            RoommateSurvey.fuzzyLabel(overall)
                    );
                })
                .filter(r -> r != null)
                
                .sorted(Comparator.comparingDouble(MatchResult::getOverallScore).reversed())
                .collect(Collectors.toList());

        
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
// #/yury(PencariHunian)
