package com.superkos.app.controller;

import com.superkos.app.model.*;
import com.superkos.app.repository.HunianRepository;
import com.superkos.app.repository.PemilikPropertiRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handles PemilikProperti-specific routes:
 *  GET  /pemilik/dashboard             — owner dashboard showing their properties
 *  GET  /pemilik/properti              — property management (list + add form)
 *  POST /pemilik/properti              — create a new hunian
 *  GET  /pemilik/properti/{id}/edit    — edit form for a specific hunian
 *  POST /pemilik/properti/{id}/edit    — save edits
 *  POST /pemilik/properti/{id}/delete  — delete a hunian
 */
@Controller
@RequestMapping("/pemilik")
public class PemilikController {

    @Autowired private PemilikPropertiRepository pemilikRepository;
    @Autowired private HunianRepository hunianRepository;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PemilikProperti getMe(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || !(u instanceof PemilikProperti)) return null;
        return pemilikRepository.findById(u.getId()).orElse(null);
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        List<Hunian> properties = hunianRepository.findByPemilikOrderByIdHunianDesc(me);

        java.util.Map<String, Object> stats = me.dashboard();

        model.addAttribute("loggedInUser",    me);
        model.addAttribute("properties",      properties);
        model.addAttribute("totalProperties", stats.get("totalProperties"));
        model.addAttribute("availableCount",  stats.get("availableCount"));
        return "pemilik_dashboard";
    }

    // ── Property Management (List + Add) ──────────────────────────────────────

    @GetMapping("/properti")
    public String manageProperties(HttpSession session, Model model) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        List<Hunian> properties = hunianRepository.findByPemilikOrderByIdHunianDesc(me);

        model.addAttribute("loggedInUser", me);
        model.addAttribute("properties",   properties);
        return "pemilik_properti";
    }

    @PostMapping("/properti")
    public String createProperty(
            @RequestParam String namaHunian,
            @RequestParam(required = false) String tipeHunian,
            @RequestParam double harga,
            @RequestParam String lokasi,
            @RequestParam(defaultValue = "true") boolean statusTersedia,
            @RequestParam(required = false) String tipeGender,
            @RequestParam(defaultValue = "1") int jumlahKamar,
            @RequestParam(required = false) String tipeUnit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date availableDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date availableDateEnd,
            @RequestParam(required = false) List<String> kategoriSewa,
            HttpSession session) {

        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Hunian hunian = new Hunian();
        hunian.setNamaHunian(namaHunian);
        hunian.setTipeHunian(tipeHunian);
        hunian.setHarga(harga);
        hunian.setLokasi(lokasi);
        hunian.setStatusTersedia(statusTersedia);
        hunian.setTipeGender(tipeGender);
        hunian.setJumlahKamar(jumlahKamar);
        hunian.setTipeUnit(tipeUnit);
        hunian.setAvailableDateStart(availableDateStart);
        hunian.setAvailableDateEnd(availableDateEnd);
        hunian.setKategoriSewa(kategoriSewa != null ? kategoriSewa : new ArrayList<>());
        hunian.setPemilik(me);

        hunianRepository.save(hunian);
        return "redirect:/pemilik/properti?added=true";
    }

    // ── Edit Property ─────────────────────────────────────────────────────────

    @GetMapping("/properti/{id}/edit")
    public String showEditForm(@PathVariable int id, HttpSession session, Model model) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Hunian hunian = hunianRepository.findById(id).orElse(null);
        if (hunian == null || hunian.getPemilik().getId() != me.getId()) {
            return "redirect:/pemilik/properti";
        }

        model.addAttribute("loggedInUser", me);
        model.addAttribute("hunian",       hunian);
        return "pemilik_properti_edit";
    }

    @PostMapping("/properti/{id}/edit")
    public String saveEdit(
            @PathVariable int id,
            @RequestParam String namaHunian,
            @RequestParam(required = false) String tipeHunian,
            @RequestParam double harga,
            @RequestParam String lokasi,
            @RequestParam(defaultValue = "true") boolean statusTersedia,
            @RequestParam(required = false) String tipeGender,
            @RequestParam(defaultValue = "1") int jumlahKamar,
            @RequestParam(required = false) String tipeUnit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date availableDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date availableDateEnd,
            @RequestParam(required = false) List<String> kategoriSewa,
            HttpSession session) {

        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Hunian hunian = hunianRepository.findById(id).orElse(null);
        if (hunian == null || hunian.getPemilik().getId() != me.getId()) {
            return "redirect:/pemilik/properti";
        }

        hunian.setNamaHunian(namaHunian);
        hunian.setTipeHunian(tipeHunian);
        hunian.setHarga(harga);
        hunian.setLokasi(lokasi);
        hunian.setStatusTersedia(statusTersedia);
        hunian.setTipeGender(tipeGender);
        hunian.setJumlahKamar(jumlahKamar);
        hunian.setTipeUnit(tipeUnit);
        hunian.setAvailableDateStart(availableDateStart);
        hunian.setAvailableDateEnd(availableDateEnd);
        hunian.setKategoriSewa(kategoriSewa != null ? kategoriSewa : new ArrayList<>());

        hunianRepository.save(hunian);
        return "redirect:/pemilik/properti?saved=true";
    }

    // ── Delete Property ───────────────────────────────────────────────────────

    @PostMapping("/properti/{id}/delete")
    public String deleteProperty(@PathVariable int id, HttpSession session) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Hunian hunian = hunianRepository.findById(id).orElse(null);
        if (hunian != null && hunian.getPemilik().getId() == me.getId()) {
            hunianRepository.delete(hunian);
        }

        return "redirect:/pemilik/properti?deleted=true";
    }
}
