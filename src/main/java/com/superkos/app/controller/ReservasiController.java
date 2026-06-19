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
    // #adam(ajukansewa)
    @PostMapping("/reservasi/ajukan/{hunianId}")
    public String ajukanSewa(
            @PathVariable int hunianId,
            HttpSession session
    ) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        
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

        
        Optional<Hunian> hunianOpt =
                hunianRepository.findById(hunianId);

        if (hunianOpt.isEmpty()) {
            return "redirect:/?error=hunian-not-found";
        }

        Hunian hunian = hunianOpt.get();

        
        if (
                pencari.getGender() == null ||
                pencari.getGender().trim().isEmpty() ||

                pencari.getKontak() == null ||
                pencari.getKontak().trim().isEmpty() ||

                pencari.getUmur() == null
        ) {

            return "redirect:/profile?error=lengkapi-profile";
        }
        
        
        

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
        
        
        
        
        System.out.println("=== DEBUG GENDER ===");
        System.out.println("User Gender   : " + pencari.getGender());
        System.out.println("Hunian Gender : " + hunian.getTipeGender());
        System.out.println("====================");

        
        
        
        String genderUser =
        pencari.getGender() == null
                ? ""
                : pencari.getGender().trim().toLowerCase();

        String genderHunian =
        hunian.getTipeGender() == null
                ? ""
                : hunian.getTipeGender().trim().toLowerCase();

        boolean cocok = false;

        
        if (genderHunian.equals("campur")) {
        cocok = true;
        }

        
        else if (
                genderHunian.equals("putri") ||
                genderHunian.equals("wanita") ||
                genderHunian.equals("perempuan")
        ) {

        cocok =
                genderUser.equals("wanita") ||
                genderUser.equals("perempuan");
        }

        
        else if (
                genderHunian.equals("putra") ||
                genderHunian.equals("pria") ||
                genderHunian.equals("laki-laki")
        ) {

        cocok =
                genderUser.equals("pria") ||
                genderUser.equals("laki-laki");
        }

        
        else {
        cocok =
                genderHunian.equalsIgnoreCase(genderUser);
        }

        if (!cocok) {

        return "redirect:/hunian/"
                + hunianId
                + "?error=gender-tidak-cocok";
        }

        
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
    // #/adam(ajukansewa)
}