package com.superkos.app.controller;

import com.superkos.app.model.*;
import com.superkos.app.repository.*;
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
 *  GET  /pemilik/reservasi             — reservasi inbox (pending/accepted/rejected)
 *  GET  /pemilik/reservasi/{id}        — single reservasi detail with pencari profile
 *  POST /pemilik/reservasi/{id}/accept — accept a reservasi (creates group chat)
 *  POST /pemilik/reservasi/{id}/reject — reject a reservasi
 */
@Controller
@RequestMapping("/pemilik")
public class PemilikController {

    @Autowired private PemilikPropertiRepository pemilikRepository;
    @Autowired private HunianRepository hunianRepository;
    @Autowired private ReservasiRepository reservasiRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private MessageRepository messageRepository;

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
        long pendingReservasi = reservasiRepository.countByPemilikAndStatus(me, "PENDING");

        java.util.Map<String, Object> stats = me.dashboard();

        model.addAttribute("loggedInUser",    me);
        model.addAttribute("properties",      properties);
        model.addAttribute("totalProperties", stats.get("totalProperties"));
        model.addAttribute("availableCount",  stats.get("availableCount"));
        model.addAttribute("pendingReservasi", pendingReservasi);
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

    // ═══════════════════════════════════════════════════════════════════════════
    //  RESERVASI (RENT REQUEST) MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    /** Reservasi inbox: lists all rent requests for the owner. */
    @GetMapping("/reservasi")
    public String reservasiInbox(HttpSession session, Model model) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        List<Reservasi> all = reservasiRepository.findByPemilikOrderByIdReservasiDesc(me);

        List<Reservasi> pending  = new ArrayList<>();
        List<Reservasi> accepted = new ArrayList<>();
        List<Reservasi> rejected = new ArrayList<>();
        for (Reservasi r : all) {
            String status = r.getStatus();
            if (status == null) status = "PENDING"; // Default to pending if null

            switch (status) {
                case "PENDING"  -> pending.add(r);
                case "ACCEPTED" -> accepted.add(r);
                case "REJECTED" -> rejected.add(r);
            }
        }

        model.addAttribute("loggedInUser", me);
        model.addAttribute("pending",      pending);
        model.addAttribute("accepted",     accepted);
        model.addAttribute("rejected",     rejected);
        model.addAttribute("totalPending", pending.size());
        return "pemilik_reservasi";
    }

    /** Reservasi detail: shows full PencariHunian profile + hunian info. */
    @GetMapping("/reservasi/{id}")
    public String reservasiDetail(@PathVariable int id, HttpSession session, Model model) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Reservasi reservasi = reservasiRepository.findById(id).orElse(null);
        if (reservasi == null || reservasi.getPemilik().getId() != me.getId()) {
            return "redirect:/pemilik/reservasi";
        }

        // Mark as read
        if (!reservasi.isPemilikRead()) {
            reservasi.setPemilikRead(true);
            reservasiRepository.save(reservasi);
        }

        model.addAttribute("loggedInUser", me);
        model.addAttribute("reservasi",    reservasi);
        model.addAttribute("pencari",      reservasi.getPencariHunian());
        model.addAttribute("hunian",       reservasi.getHunian());
        return "pemilik_reservasi_detail";
    }

    /** Accept a reservasi — creates a RESERVASI-type group ChatRoom. */
    @PostMapping("/reservasi/{id}/accept")
    public String acceptReservasi(@PathVariable int id, HttpSession session) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Reservasi reservasi = reservasiRepository.findById(id).orElse(null);
        if (reservasi == null || reservasi.getPemilik().getId() != me.getId()) {
            return "redirect:/pemilik/reservasi";
        }
        if (!"PENDING".equals(reservasi.getStatus())) {
            return "redirect:/pemilik/reservasi/" + id;
        }

        // Create group ChatRoom for this rental
        ChatRoom room = new ChatRoom();
        room.setChatType("RESERVASI");
        room.setHunian(reservasi.getHunian());
        room.addParticipant(me);
        room.addParticipant(reservasi.getPencariHunian());
        room.setCreatedAt(new Date());
        room = chatRoomRepository.save(room);

        // Accept and link
        reservasi.terima();
        reservasi.setChatRoom(room);
        reservasi.setPemilikRead(true);
        reservasiRepository.save(reservasi);

        return "redirect:/pemilik/reservasi/" + id + "?accepted=true";
    }

    /** Reject a reservasi with an optional reason. */
    @PostMapping("/reservasi/{id}/reject")
    public String rejectReservasi(@PathVariable int id,
                                  @RequestParam(required = false) String alasan,
                                  HttpSession session) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Reservasi reservasi = reservasiRepository.findById(id).orElse(null);
        if (reservasi == null || reservasi.getPemilik().getId() != me.getId()) {
            return "redirect:/pemilik/reservasi";
        }

        reservasi.tolak(alasan != null ? alasan.trim() : null);
        reservasi.setPemilikRead(true);
        reservasiRepository.save(reservasi);

        return "redirect:/pemilik/reservasi?rejected=true";
    }
}
