package com.superkos.app.controller;

import com.superkos.app.model.Admin;
import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.PemilikProperti;
import com.superkos.app.model.User;
import com.superkos.app.repository.UserRepository;
import com.superkos.app.repository.PemilikPropertiRepository;
import com.superkos.app.repository.PencariHunianRepository;
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

    @Autowired
    private PemilikPropertiRepository pemilikPropertiRepository;

    @Autowired
    private PencariHunianRepository pencariHunianRepository;
    // #naufal(User)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, Model model) {
        // Intercept admin login
        if ("admin".equals(email) && "admin12345".equals(password)) {
            User adminUser = userRepository.findByEmail("admin");
            if (adminUser == null || !(adminUser instanceof Admin)) {
                Admin newAdmin = new Admin();
                newAdmin.setEmail("admin");
                newAdmin.setPassword("admin12345");
                newAdmin.setNama("Administrator");
                adminUser = userRepository.save(newAdmin);
            }
            session.setAttribute("loggedInUser", adminUser);
            return "redirect:/admin/dashboard";
        }

        User user = null;
        try {
            user = userRepository.findByEmail(email);
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan data. Coba hubungi admin.");
            return "login";
        }

        if (user != null && user.login(password)) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/";
        }

        model.addAttribute("error", "Email atau password salah!");
        return "login";
    }
    // #/naufal(User)
    // #fazil(Registrasi)
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

        
        User existingUser = null;
        try {
            existingUser = userRepository.findByEmail(email);
        } catch (Exception e) {
            
            userRepository.deleteOrphanedByEmail(email);
        }

        if (existingUser != null) {
            model.addAttribute("error", "Email sudah terdaftar!");
            return "register";
        }

        
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

        try {
            if (newUser instanceof PemilikProperti) {
                pemilikPropertiRepository.save((PemilikProperti) newUser);
            } else if (newUser instanceof PencariHunian) {
                pencariHunianRepository.save((PencariHunian) newUser);
            } else {
                newUser.registrasi(userRepository);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Gagal registrasi: " + e.getMessage());
            return "register";
        }
        session.setAttribute("loggedInUser", newUser);

        
        if (newUser instanceof PencariHunian) {
            session.setAttribute("pendingQuizSetup", true);
            return "redirect:/quiz/setup";
        }

        
        return "redirect:/";
    }
    // #/fazil(Registrasi)
    // #naufal(User)
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    // #/naufal(User)
}
