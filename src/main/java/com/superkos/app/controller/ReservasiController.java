package com.superkos.app.controller;

import com.superkos.app.model.*;
import com.superkos.app.repository.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@Controller
public class ReservasiController {

    @Autowired
    private ReservasiRepository reservasiRepository;

    @Autowired
    private HunianRepository hunianRepository;

    @Autowired
    private PencariHunianRepository pencariHunianRepository;

    @PostMapping("/reservasi/ajukan/{hunianId}")
    public String ajukanSewa(
            @PathVariable int hunianId,
            HttpSession session
    ) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        // BELUM LOGIN
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // HARUS PENCARI HUNIAN
        if (!(loggedInUser instanceof PencariHunian)) {
            return "redirect:/?error=only-pencari";
        }

        PencariHunian pencari =
                pencariHunianRepository
                        .findById(loggedInUser.getId())
                        .orElse(null);

        if (pencari == null) {
            return "redirect:/?error=user-not-found";
        }

        // AMBIL HUNIAN
        Optional<Hunian> hunianOpt =
                hunianRepository.findById(hunianId);

        if (hunianOpt.isEmpty()) {
            return "redirect:/?error=hunian-not-found";
        }

        Hunian hunian = hunianOpt.get();

        // VALIDASI PROFILE
        if (
                pencari.getGender() == null ||
                pencari.getGender().trim().isEmpty() ||

                pencari.getKontak() == null ||
                pencari.getKontak().trim().isEmpty() ||

                pencari.getUmur() == null
        ) {

            return "redirect:/profile?error=lengkapi-profile";
        }
        // =========================
        // CEK RESERVASI SEBELUMNYA
        // =========================

        var reservasiLama =
                reservasiRepository.findByPencariHunianAndHunian(
                        pencari,
                        hunian
                );

        for (Reservasi r : reservasiLama) {

        if ("ACCEPTED".equalsIgnoreCase(r.getStatus())) {

                return "redirect:/hunian/"
                + hunianId
                + "?info=sudah-diterima";
        }
                
        if ("PENDING".equalsIgnoreCase(r.getStatus())) {

                return "redirect:/hunian/"
                + hunianId
                + "?warning=masih-pending";
        }

        
        }
        
        /// =========================
        // DEBUG
        // =========================
        System.out.println("=== DEBUG GENDER ===");
        System.out.println("User Gender   : " + pencari.getGender());
        System.out.println("Hunian Gender : " + hunian.getTipeGender());
        System.out.println("====================");

        // =========================
        // VALIDASI GENDER
        // =========================
        String genderUser =
        pencari.getGender() == null
                ? ""
                : pencari.getGender().trim().toLowerCase();

        String genderHunian =
        hunian.getTipeGender() == null
                ? ""
                : hunian.getTipeGender().trim().toLowerCase();

        boolean cocok = false;

        // CAMPUR
        if (genderHunian.equals("campur")) {
        cocok = true;
        }

        // PUTRI
        else if (
                genderHunian.equals("putri") ||
                genderHunian.equals("wanita") ||
                genderHunian.equals("perempuan")
        ) {

        cocok =
                genderUser.equals("wanita") ||
                genderUser.equals("perempuan");
        }

        // PUTRA
        else if (
                genderHunian.equals("putra") ||
                genderHunian.equals("pria") ||
                genderHunian.equals("laki-laki")
        ) {

        cocok =
                genderUser.equals("pria") ||
                genderUser.equals("laki-laki");
        }

        // DEFAULT
        else {
        cocok =
                genderHunian.equalsIgnoreCase(genderUser);
        }

        if (!cocok) {

        return "redirect:/hunian/"
                + hunianId
                + "?error=gender-tidak-cocok";
        }

        // BUAT RESERVASI
        Reservasi reservasi = new Reservasi();

        reservasi.setPencariHunian(pencari);

        reservasi.setHunian(hunian);

        reservasi.setPemilik(hunian.getPemilik());

        reservasi.setStatus("PENDING");

        reservasi.setTanggalPengajuan(new Date());

        reservasiRepository.save(reservasi);

        return "redirect:/hunian/" +
                hunianId +
                "?success=reservasi-berhasil";
    }
}