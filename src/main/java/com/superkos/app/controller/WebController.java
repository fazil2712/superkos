package com.superkos.app.controller;

import com.superkos.app.model.*;
import com.superkos.app.repository.HunianRepository;
import com.superkos.app.repository.PencariHunianRepository;
import com.superkos.app.repository.ReservasiRepository;
import com.superkos.app.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired private HunianRepository        hunianRepository;
    @Autowired private UserRepository          userRepository;
    @Autowired private PencariHunianRepository pencariHunianRepository;
    @Autowired private ReservasiRepository     reservasiRepository;
    // #adam(SearchEngine)
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
            HttpSession session, Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);

        
        if (loggedInUser instanceof PemilikProperti) {
            return "redirect:/pemilik/dashboard";
        }
        if (loggedInUser instanceof Admin) {
            return "redirect:/admin/dashboard";
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            if      (sortBy.equals("price_asc"))     sort = Sort.by(Sort.Direction.ASC,  "harga");
            else if (sortBy.equals("price_desc"))    sort = Sort.by(Sort.Direction.DESC, "harga");
            else if (sortBy.equals("capacity_desc")) sort = Sort.by(Sort.Direction.DESC, "jumlahKamar");
            else if (sortBy.equals("capacity_asc"))  sort = Sort.by(Sort.Direction.ASC,  "jumlahKamar");
        }

        String       searchKeyword = (keyword     != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        List<String> filteredKat   = (kategoriSewa != null && !kategoriSewa.isEmpty())  ? kategoriSewa  : null;

        List<Hunian> daftarHunian = hunianRepository.searchAndFilter(
                searchKeyword, minPrice, maxPrice, roommate, tipeGender,
                startDate, endDate, filteredKat, sort);

        model.addAttribute("daftarHunian",  daftarHunian);
        model.addAttribute("keyword",       keyword);
        model.addAttribute("minPrice",      minPrice);
        model.addAttribute("maxPrice",      maxPrice);
        model.addAttribute("roommate",      roommate);
        model.addAttribute("tipeGender",    tipeGender);
        model.addAttribute("startDate",     startDate);
        model.addAttribute("endDate",       endDate);
        model.addAttribute("kategoriSewa",  kategoriSewa);
        model.addAttribute("sortBy",        sortBy);

        
        Set<Integer> wishlistIds = new HashSet<>();
        int wishlistCount = 0;
        if (loggedInUser instanceof PencariHunian pencari) {
            PencariHunian fresh = pencariHunianRepository.findById(pencari.getId()).orElse(null);
            if (fresh != null && fresh.getWishlist() != null) {
                fresh.getWishlist().forEach(h -> wishlistIds.add(h.getIdHunian()));
                wishlistCount = fresh.getWishlist().size();
            }
        }
        model.addAttribute("wishlistIds", wishlistIds);
        model.addAttribute("wishlistCount", wishlistCount);

        return "index";
    }
    // #/adam(SearchEngine)
    // #fazil(Daftar Reservasi)
    @GetMapping("/hunian/{id}")
    public String detailHunian(@PathVariable int id, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);

        Optional<Hunian> hunianOpt = hunianRepository.findById(id);
        if (hunianOpt.isPresent()) {
            model.addAttribute("hunian", hunianOpt.get());

            
            boolean inWishlist = false;
            boolean isSewaAccepted = false;
            int chatRoomId = -1;
            if (loggedInUser instanceof PencariHunian pencari) {
                PencariHunian fresh = pencariHunianRepository.findById(pencari.getId()).orElse(null);
                if (fresh != null) {
                    if (fresh.getWishlist() != null) {
                        inWishlist = fresh.getWishlist().stream()
                                .anyMatch(h -> h.getIdHunian() == id);
                    }
                    List<Reservasi> reservasis = reservasiRepository.findByPencariHunianAndHunian(fresh, hunianOpt.get());
                    for (Reservasi r : reservasis) {
                        if ("ACCEPTED".equals(r.getStatus())) {
                            isSewaAccepted = true;
                            if (r.getChatRoom() != null) {
                                chatRoomId = r.getChatRoom().getIdChat();
                            }
                            break;
                        }
                    }
                }
            }
            model.addAttribute("inWishlist", inWishlist);
            model.addAttribute("isSewaAccepted", isSewaAccepted);
            model.addAttribute("chatRoomId", chatRoomId);
            return "detail";
        }
        return "redirect:/";
    }
    // #/fazil(Daftar Reservasi)

    

    // #fazil(Wishlist)
    @GetMapping("/wishlist")
    public String showWishlist(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (!(loggedInUser instanceof PencariHunian)) return "redirect:/";

        PencariHunian fresh = pencariHunianRepository.findById(loggedInUser.getId()).orElse(null);
        List<Hunian> wishlist = (fresh != null) ? fresh.getWishlist() : new ArrayList<>();

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("wishlist", wishlist);
        return "wishlist";
    }

    @PostMapping("/wishlist/toggle/{hunianId}")
    public String toggleWishlist(@PathVariable int hunianId,
                                  @RequestParam(defaultValue = "/") String returnUrl,
                                  HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (!(loggedInUser instanceof PencariHunian)) return "redirect:/";

        PencariHunian pencari = pencariHunianRepository.findById(loggedInUser.getId()).orElse(null);
        if (pencari == null) return "redirect:/";

        Optional<Hunian> hunianOpt = hunianRepository.findById(hunianId);
        if (hunianOpt.isEmpty()) return "redirect:" + returnUrl;

        Hunian hunian = hunianOpt.get();
        boolean alreadySaved = pencari.getWishlist().stream()
                .anyMatch(h -> h.getIdHunian() == hunianId);

        if (alreadySaved) {
            pencari.hapusDariWishlist(hunian);
        } else {
            pencari.tambahKeWishlist(hunian);
        }

        pencariHunianRepository.save(pencari);
        
        session.setAttribute("loggedInUser", pencariHunianRepository.findById(loggedInUser.getId()).orElse(pencari));

        return "redirect:" + returnUrl;
    }
    // #/fazil(Wishlist)
    // #fazil(Daftar Reservasi)
    @GetMapping("/reservasi-saya")
    public String reservasiSaya(
            HttpSession session,
            Model model
    ) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (!(loggedInUser instanceof PencariHunian)) {
            return "redirect:/";
        }

        PencariHunian pencari =
                pencariHunianRepository
                        .findById(loggedInUser.getId())
                        .orElse(null);

        if (pencari == null) {
            return "redirect:/";
        }

        List<Reservasi> reservasiList =
                reservasiRepository
                        .findByPencariHunian(pencari);

        model.addAttribute(
                "loggedInUser",
                pencari
        );

        model.addAttribute(
                "reservasiList",
                reservasiList
        );

        return "reservasi-saya";
    }
    // #/fazil(Daftar Reservasi)
    // #naufal(User)
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        User freshUser = userRepository.findById(loggedInUser.getId()).orElse(loggedInUser);
        session.setAttribute("loggedInUser", freshUser);
        model.addAttribute("loggedInUser", freshUser);
        return "profile";
    }

    @PostMapping("/profile")
    public String saveProfile(
            @RequestParam String nama,
            @RequestParam(required = false) String biodata,
            @RequestParam(required = false) Integer umur,
            @RequestParam(required = false) String lokasi,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String pekerjaan,
            @RequestParam(required = false) String kontak,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session) throws IOException {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        User userToUpdate = userRepository.findById(loggedInUser.getId()).orElse(null);
        if (userToUpdate == null) return "redirect:/login";

        userToUpdate.setNama(nama);
        userToUpdate.setBiodata(biodata);
        userToUpdate.setUmur(umur);
        userToUpdate.setLokasi(lokasi);
        userToUpdate.setGender(gender);
        userToUpdate.setPekerjaan(pekerjaan);
        userToUpdate.setKontak(kontak);

        if (file != null && !file.isEmpty()) {
            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "foto.jpg";
            String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : ".jpg";

            Path uploadDir = Paths.get("uploads", "profile");
            Files.createDirectories(uploadDir);
            String fileName = userToUpdate.getId() + "_foto" + ext;
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            userToUpdate.setFotoProfil("/uploads/profile/" + fileName);
        }

        userRepository.save(userToUpdate);
        session.setAttribute("loggedInUser", userToUpdate);
        return "redirect:/profile?success=true";
    }

    

    @PostMapping("/profile/photo")
    public String uploadProfilePhoto(
            @RequestParam("foto") MultipartFile foto,
            HttpSession session) throws IOException {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (foto.isEmpty()) return "redirect:/profile";

        User userToUpdate = userRepository.findById(loggedInUser.getId()).orElse(null);
        if (userToUpdate == null) return "redirect:/login";

        
        String originalName = foto.getOriginalFilename() != null ? foto.getOriginalFilename() : "foto.jpg";
        String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : ".jpg";

        
        Path uploadDir = Paths.get("uploads", "profiles");
        Files.createDirectories(uploadDir);
        String fileName = userToUpdate.getId() + "_foto" + ext;
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        userToUpdate.setFotoProfil("/uploads/profiles/" + fileName);
        userRepository.save(userToUpdate);
        session.setAttribute("loggedInUser", userToUpdate);
        return "redirect:/profile?photoSaved=true";
    }
    // #/naufal(User)
    // #nadia(Roommate Survey)
    @GetMapping("/quiz/setup")
    public String showQuizSetup(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        
        if (!(loggedInUser instanceof PencariHunian pencari)) return "redirect:/";

        
        if (pencari.getRoommateSurvey() != null && pencari.getRoommateSurvey().getLastQuizTaken() != null)
            return "redirect:/settings";

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("survey",       pencari.getRoommateSurvey());
        model.addAttribute("formAction",   "/quiz/setup");
        return "quiz";
    }

    @PostMapping("/quiz/setup")
    public String saveQuizSetup(
            @RequestParam Integer socialQ1, @RequestParam Integer socialQ2,
            @RequestParam Integer socialQ3, @RequestParam Integer socialQ4,
            @RequestParam Integer socialQ5, @RequestParam Integer socialQ6,
            @RequestParam Integer socialQ7,
            @RequestParam Integer cleanQ1,  @RequestParam Integer cleanQ2,
            @RequestParam Integer cleanQ3,  @RequestParam Integer cleanQ4,
            @RequestParam Integer cleanQ5,  @RequestParam Integer cleanQ6,
            @RequestParam Integer cleanQ7,
            @RequestParam Integer sleepQ1,  @RequestParam Integer sleepQ2,
            @RequestParam Integer sleepQ3,  @RequestParam Integer sleepQ4,
            @RequestParam Integer sleepQ5,  @RequestParam Integer sleepQ6,
            HttpSession session, Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (!(loggedInUser instanceof PencariHunian)) return "redirect:/";

        User user = userRepository.findById(loggedInUser.getId()).orElse(null);
        if (user == null) return "redirect:/login";

        try {
            saveAnswersToSurvey((PencariHunian) user,
                    buildAnswerList(socialQ1, socialQ2, socialQ3, socialQ4, socialQ5, socialQ6, socialQ7,
                                    cleanQ1,  cleanQ2,  cleanQ3,  cleanQ4,  cleanQ5,  cleanQ6,  cleanQ7,
                                    sleepQ1,  sleepQ2,  sleepQ3,  sleepQ4,  sleepQ5,  sleepQ6));
        } catch (IllegalArgumentException e) {
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("survey",       ((PencariHunian) loggedInUser).getRoommateSurvey());
            model.addAttribute("formAction",   "/quiz/setup");
            model.addAttribute("error",        e.getMessage());
            return "quiz";
        }

        session.setAttribute("loggedInUser", user);
        session.removeAttribute("pendingQuizSetup");
        return "redirect:/";
    }
    // #/nadia(Roommate Survey)
    // #nadia(Roommate Survey)
    @GetMapping("/settings")
    public String showSettings(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        User freshUser = userRepository.findById(loggedInUser.getId()).orElse(loggedInUser);
        session.setAttribute("loggedInUser", freshUser);
        model.addAttribute("loggedInUser", freshUser);

        boolean isPencariHunian = freshUser instanceof PencariHunian;
        model.addAttribute("isPencariHunian", isPencariHunian);

        RoommateSurvey survey = isPencariHunian
                ? ((PencariHunian) freshUser).getRoommateSurvey()
                : null;
        model.addAttribute("survey", survey);

        
        boolean canRetake   = true;
        long remainingHours = 0;
        long daysElapsed    = 0;

        if (survey != null && survey.getLastQuizTaken() != null) {
            long hoursElapsed  = Duration.between(survey.getLastQuizTaken(), LocalDateTime.now()).toHours();
            daysElapsed        = hoursElapsed / 24;
            if (hoursElapsed < 168) {
                canRetake      = false;
                remainingHours = 168 - hoursElapsed;
            }
        }

        model.addAttribute("canRetake",         canRetake);
        model.addAttribute("remainingDays",      remainingHours / 24);
        model.addAttribute("remainingHoursOnly", remainingHours % 24);
        model.addAttribute("daysElapsed",        daysElapsed);
        model.addAttribute("progressPct",        Math.min(100, (daysElapsed * 100) / 7));

        return "settings";
    }
    // #/nadia(Roommate Survey)
    // #nadia(Roommate Survey)
    @GetMapping("/settings/quiz")
    public String showQuizRetake(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (!(loggedInUser instanceof PencariHunian pencari)) return "redirect:/settings";

        RoommateSurvey survey = pencari.getRoommateSurvey();
        if (survey != null && survey.getLastQuizTaken() != null) {
            long hrs = Duration.between(survey.getLastQuizTaken(), LocalDateTime.now()).toHours();
            if (hrs < 168) return "redirect:/settings?cooldown=true";
        }

        PencariHunian fresh = (PencariHunian) userRepository.findById(loggedInUser.getId())
                .orElse(loggedInUser);
        model.addAttribute("loggedInUser", fresh);
        model.addAttribute("survey",       fresh.getRoommateSurvey());
        model.addAttribute("formAction",   "/settings/quiz");
        return "quiz";
    }

    @PostMapping("/settings/quiz")
    public String saveQuizRetake(
            @RequestParam Integer socialQ1, @RequestParam Integer socialQ2,
            @RequestParam Integer socialQ3, @RequestParam Integer socialQ4,
            @RequestParam Integer socialQ5, @RequestParam Integer socialQ6,
            @RequestParam Integer socialQ7,
            @RequestParam Integer cleanQ1,  @RequestParam Integer cleanQ2,
            @RequestParam Integer cleanQ3,  @RequestParam Integer cleanQ4,
            @RequestParam Integer cleanQ5,  @RequestParam Integer cleanQ6,
            @RequestParam Integer cleanQ7,
            @RequestParam Integer sleepQ1,  @RequestParam Integer sleepQ2,
            @RequestParam Integer sleepQ3,  @RequestParam Integer sleepQ4,
            @RequestParam Integer sleepQ5,  @RequestParam Integer sleepQ6,
            HttpSession session, Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        if (!(loggedInUser instanceof PencariHunian)) return "redirect:/settings";

        
        PencariHunian pencari = (PencariHunian) loggedInUser;
        RoommateSurvey existing = pencari.getRoommateSurvey();
        if (existing != null && existing.getLastQuizTaken() != null) {
            long hrs = Duration.between(existing.getLastQuizTaken(), LocalDateTime.now()).toHours();
            if (hrs < 168) return "redirect:/settings?cooldown=true";
        }

        User user = userRepository.findById(loggedInUser.getId()).orElse(null);
        if (user == null) return "redirect:/login";

        try {
            saveAnswersToSurvey((PencariHunian) user,
                    buildAnswerList(socialQ1, socialQ2, socialQ3, socialQ4, socialQ5, socialQ6, socialQ7,
                                    cleanQ1,  cleanQ2,  cleanQ3,  cleanQ4,  cleanQ5,  cleanQ6,  cleanQ7,
                                    sleepQ1,  sleepQ2,  sleepQ3,  sleepQ4,  sleepQ5,  sleepQ6));
        } catch (IllegalArgumentException e) {
            PencariHunian freshPencari = (PencariHunian) userRepository.findById(loggedInUser.getId())
                    .orElse(loggedInUser);
            model.addAttribute("loggedInUser", freshPencari);
            model.addAttribute("survey",       freshPencari.getRoommateSurvey());
            model.addAttribute("formAction",   "/settings/quiz");
            model.addAttribute("error",        e.getMessage());
            return "quiz";
        }

        session.setAttribute("loggedInUser", user);
        return "redirect:/settings?success=true";
    }

    

    
    private void saveAnswersToSurvey(PencariHunian pencari, List<Integer> answers) {
        RoommateSurvey survey = pencari.getRoommateSurvey();
        if (survey == null) survey = new RoommateSurvey();

        survey.isiSurvey(answers); 
        pencari.setRoommateSurvey(survey);

        userRepository.save(pencari); 
    }

    
    private List<Integer> buildAnswerList(
            Integer s1, Integer s2, Integer s3, Integer s4, Integer s5, Integer s6, Integer s7,
            Integer c1, Integer c2, Integer c3, Integer c4, Integer c5, Integer c6, Integer c7,
            Integer p1, Integer p2, Integer p3, Integer p4, Integer p5, Integer p6) {
        return new ArrayList<>(Arrays.asList(
                s1, s2, s3, s4, s5, s6, s7,
                c1, c2, c3, c4, c5, c6, c7,
                p1, p2, p3, p4, p5, p6));
    }
    // #/nadia(Roommate Survey)
}
