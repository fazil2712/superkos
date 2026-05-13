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
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        User user = userRepository.findByEmail(email);
        
        // Basic plain-text password check (In production, use Spring Security & BCrypt!)
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
            Model model) {
            
        // Check if email already exists
        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email sudah terdaftar!");
            return "register";
        }

        User newUser;
        // Check what role they are registering for
        if ("PEMILIK".equals(role)) {
            newUser = new PemilikProperti();
        } else {
            newUser = new PencariHunian();
        }
        
        newUser.setNama(nama);
        newUser.setEmail(email);
        newUser.setPassword(password); // Note: Store hashed passwords in a real app
        
        userRepository.save(newUser);
        
        return "redirect:/login"; // Redirect to login page after successful registration
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Destroy session
        return "redirect:/";
    }
}
