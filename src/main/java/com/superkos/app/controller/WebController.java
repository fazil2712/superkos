package com.superkos.app.controller;

import com.superkos.app.model.Hunian;
import com.superkos.app.repository.HunianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.superkos.app.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @Autowired
    private HunianRepository hunianRepository;

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean roommate,
            @RequestParam(required = false) String tipeGender,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) List<String> kategoriSewa,
            @RequestParam(required = false) String sortBy,
            HttpSession session,
            Model model) {
        
        // Pass session user to the template if logged in
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);

        // Define sorting strategy based on the parameter
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            if (sortBy.equals("price_asc")) {
                sort = Sort.by(Sort.Direction.ASC, "harga");
            } else if (sortBy.equals("price_desc")) {
                sort = Sort.by(Sort.Direction.DESC, "harga");
            } else if (sortBy.equals("capacity_desc")) {
                sort = Sort.by(Sort.Direction.DESC, "jumlahKamar");
            } else if (sortBy.equals("capacity_asc")) {
                sort = Sort.by(Sort.Direction.ASC, "jumlahKamar");
            }
        }

        // Clean up empty keyword
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        // Handle empty list for kategoriSewa so the repository logic works correctly
        List<String> filteredKategori = (kategoriSewa != null && !kategoriSewa.isEmpty()) ? kategoriSewa : null;

        // Query the database
        List<Hunian> daftarHunian = hunianRepository.searchAndFilter(
                searchKeyword, minPrice, maxPrice, roommate, tipeGender, startDate, endDate, filteredKategori, sort);
        
        // Pass data and search filters back to the view
        model.addAttribute("daftarHunian", daftarHunian);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("roommate", roommate);
        model.addAttribute("tipeGender", tipeGender);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("kategoriSewa", kategoriSewa);
        model.addAttribute("sortBy", sortBy);
        
        return "index";
    }

    @GetMapping("/hunian/{id}")
    public String detailHunian(@PathVariable int id, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);

        Optional<Hunian> hunianOpt = hunianRepository.findById(id);
        if (hunianOpt.isPresent()) {
            model.addAttribute("hunian", hunianOpt.get());
            return "detail";
        } else {
            return "redirect:/"; // Redirect to home if property not found
        }
    }
}
