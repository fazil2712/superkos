package com.superkos.app.controller;

import com.superkos.app.model.Admin;
import com.superkos.app.model.Hunian;
import com.superkos.app.model.User;
import com.superkos.app.repository.HunianRepository;
import com.superkos.app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

// #nadia(Admin)
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private HunianRepository hunianRepository;

    @Autowired
    private UserRepository userRepository;

    private Admin checkAdminStatus(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser instanceof Admin) {
            return (Admin) loggedInUser;
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Admin admin = checkAdminStatus(session);
        if (admin == null) return "redirect:/login";

        List<Hunian> allHunian = hunianRepository.findAll();
        List<User> allUsers = userRepository.findAll().stream()
                .filter(u -> u.getId() != admin.getId())
                .collect(Collectors.toList());

        model.addAttribute("loggedInUser", admin);
        model.addAttribute("daftarHunian", allHunian);
        model.addAttribute("daftarUser", allUsers);
        return "admin_dashboard";
    }

    @GetMapping("/hunian/{id}/edit")
    public String editHunian(@PathVariable int id, HttpSession session, Model model) {
        Admin admin = checkAdminStatus(session);
        if (admin == null) return "redirect:/login";

        Hunian hunian = hunianRepository.findById(id).orElse(null);
        if (hunian == null) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("loggedInUser", admin);
        model.addAttribute("hunian", hunian);
        return "admin_edit_hunian";
    }

    @PostMapping("/hunian/{id}/update")
    public String updateHunian(
            @PathVariable int id,
            @RequestParam String namaHunian,
            @RequestParam Double harga,
            @RequestParam String lokasi,
            @RequestParam String deskripsi,
            @RequestParam Integer jumlahKamar,
            @RequestParam String tipeGender,
            @RequestParam(required = false) List<String> fasilitas,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Admin admin = checkAdminStatus(session);
        if (admin == null) return "redirect:/login";

        Hunian hunian = hunianRepository.findById(id).orElse(null);
        if (hunian != null) {
            hunian.setNamaHunian(namaHunian);
            hunian.setHarga(harga);
            hunian.setLokasi(lokasi);
            hunian.setDeskripsi(deskripsi);
            hunian.setJumlahKamar(jumlahKamar);
            hunian.setTipeGender(tipeGender);
            hunian.setFasilitas(fasilitas != null ? fasilitas : new ArrayList<>());
            hunianRepository.save(hunian);
            redirectAttributes.addFlashAttribute("successMessage", "Data hunian berhasil diupdate.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = checkAdminStatus(session);
        if (admin == null) return "redirect:/login";

        User targetUser = userRepository.findById(id).orElse(null);
        if (targetUser != null && targetUser.getId() != admin.getId()) {
            try {
                admin.kelolaUser(targetUser, "DELETE", userRepository);
                redirectAttributes.addFlashAttribute("successMessage", "User berhasil dihapus.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus user: " + e.getMessage());
            }
        }
        return "redirect:/admin/dashboard";
    }
}
// #/nadia(Admin)
