package com.superkos.app.controller;

import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.PemilikProperti;
import com.superkos.app.model.User;
import com.superkos.app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, Model model) {
        User user = null;
        try {
            user = userRepository.findByEmail(email);
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan data. Coba hubungi admin.");
            return "login";
        }

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/";
        }

        model.addAttribute("error", "Email atau password salah!");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String nama,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String kontak,
            HttpSession session,
            Model model) {

        // Check if email already exists — wrapped in try-catch for orphaned DB records
        User existingUser = null;
        try {
            existingUser = userRepository.findByEmail(email);
        } catch (Exception e) {
            // Orphaned user row — delete it so registration can proceed cleanly
            userRepository.deleteOrphanedByEmail(email);
        }

        if (existingUser != null) {
            model.addAttribute("error", "Email sudah terdaftar!");
            return "register";
        }

        // Validate kontak is required for PemilikProperti
        if ("PEMILIK".equals(role) && (kontak == null || kontak.trim().isEmpty())) {
            model.addAttribute("error", "Kontak wajib diisi untuk Pemilik Properti!");
            return "register";
        }

        User newUser;
        if ("PEMILIK".equals(role)) {
            newUser = new PemilikProperti();
        } else {
            newUser = new PencariHunian();
        }

        newUser.setNama(nama);
        newUser.setEmail(email);
        newUser.setPassword(password);
        if (kontak != null && !kontak.trim().isEmpty()) {
            newUser.setKontak(kontak.trim());
        }

        userRepository.save(newUser);
        session.setAttribute("loggedInUser", newUser);

        // Only PencariHunian needs to complete the preference quiz
        if (newUser instanceof PencariHunian) {
            session.setAttribute("pendingQuizSetup", true);
            return "redirect:/quiz/setup";
        }

        // PemilikProperti goes directly to the dashboard
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
