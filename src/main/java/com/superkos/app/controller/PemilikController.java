package com.superkos.app.controller;

import com.superkos.app.model.*;
import com.superkos.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/pemilik")
public class PemilikController {

    @Autowired private PemilikPropertiRepository pemilikRepository;
    @Autowired private HunianRepository hunianRepository;
    @Autowired private ReservasiRepository reservasiRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private MessageRepository messageRepository;

    
    // #yury(PemilikProperti)
    private PemilikProperti getMe(HttpSession session) {
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null || !(u instanceof PemilikProperti)) return null;
        return pemilikRepository.findById(u.getId()).orElse(null);
    }

    
    private List<String> saveHunianPhotos(List<MultipartFile> files, int hunianId, List<String> existing) {
        List<String> paths = new ArrayList<>(existing != null ? existing : new ArrayList<>());
        if (files == null || files.isEmpty()) return paths;
        try {
            Path dir = Paths.get("uploads", "hunian", String.valueOf(hunianId));
            Files.createDirectories(dir);
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;
                String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "foto.jpg";
                String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
                String fileName = System.currentTimeMillis() + ext;
                Path dest = dir.resolve(fileName);
                Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
                paths.add("/uploads/hunian/" + hunianId + "/" + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    

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
            @RequestParam(required = false) String deskripsi,
            @RequestParam(required = false) List<MultipartFile> fotoHunian,
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
        hunian.setDeskripsi(deskripsi);
        hunian.setPemilik(me);

        
        hunian = hunianRepository.save(hunian);
        hunian.setFotoHunian(saveHunianPhotos(fotoHunian, hunian.getIdHunian(), null));
        hunianRepository.save(hunian);
        return "redirect:/pemilik/properti?added=true";
    }

    

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
            @RequestParam(required = false) String deskripsi,
            @RequestParam(required = false) List<MultipartFile> fotoHunian,
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
        hunian.setDeskripsi(deskripsi);
        
        hunian.setFotoHunian(saveHunianPhotos(fotoHunian, hunian.getIdHunian(), hunian.getFotoHunian()));

        hunianRepository.save(hunian);
        return "redirect:/pemilik/properti?saved=true";
    }

    

    @PostMapping("/properti/{id}/foto/delete/{idx}")
    public String deleteHunianPhoto(@PathVariable int id, @PathVariable int idx, HttpSession session) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Hunian hunian = hunianRepository.findById(id).orElse(null);
        if (hunian == null || hunian.getPemilik().getId() != me.getId()) return "redirect:/pemilik/properti";

        List<String> photos = new ArrayList<>(hunian.getFotoHunian());
        if (idx >= 0 && idx < photos.size()) {
            photos.remove(idx);
            hunian.setFotoHunian(photos);
            hunianRepository.save(hunian);
        }
        return "redirect:/pemilik/properti/" + id + "/edit";
    }

    

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
            if (status == null) status = "PENDING"; 

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

    
    @GetMapping("/reservasi/{id}")
    public String reservasiDetail(@PathVariable int id, HttpSession session, Model model) {
        PemilikProperti me = getMe(session);
        if (me == null) return "redirect:/login";

        Reservasi reservasi = reservasiRepository.findById(id).orElse(null);
        if (reservasi == null || reservasi.getPemilik().getId() != me.getId()) {
            return "redirect:/pemilik/reservasi";
        }

        
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

    // #/yury(PemilikProperti)

    // #fazil(Daftar Reservasi)
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

        
        ChatRoom room = new ChatRoom();
        room.setChatType("RESERVASI");
        room.setHunian(reservasi.getHunian());
        room.addParticipant(me);
        room.addParticipant(reservasi.getPencariHunian());
        room.setCreatedAt(new Date());
        room = chatRoomRepository.save(room);

        
        reservasi.terima();
        reservasi.setChatRoom(room);
        reservasi.setPemilikRead(true);
        reservasiRepository.save(reservasi);

        return "redirect:/pemilik/reservasi/" + id + "?accepted=true";
    }

    // #/fazil(Daftar Reservasi)

    // #adam(ajukansewa)
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
    // #/adam(ajukansewa)
