package com.superkos.app;

import com.superkos.app.model.Hunian;
import com.superkos.app.model.PencariHunian;
import com.superkos.app.repository.HunianRepository;
import com.superkos.app.repository.PencariHunianRepository;
import com.superkos.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SuperkosApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuperkosApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDummyFacilities(HunianRepository hunianRepository) {
        return args -> {
            try {
                List<Hunian> allHunian = hunianRepository.findAll();
                int updatedCount = 0;
                for (Hunian h : allHunian) {
                    if (h.getFasilitas() == null || h.getFasilitas().isEmpty()) {
                        h.setFasilitas(Arrays.asList("WiFi", "AC", "Kasur", "Kamar Mandi Dalam"));
                        hunianRepository.save(h);
                        updatedCount++;
                    }
                }
                System.out.println(">>> Initialized dummy facilities for " + updatedCount + " properties.");
            } catch (Exception e) {
                System.err.println(">>> Failed to initialize dummy facilities: " + e.getMessage());
            }
        };
    }

    @Bean
    public CommandLineRunner cleanupOrphanedUsers(UserRepository userRepository) {
        return args -> {
            try {
                userRepository.deleteOrphanedUsers();
                System.out.println(">>> Cleaned up orphaned users from database.");
            } catch (Exception e) {
                System.err.println(">>> Failed to clean up orphaned users: " + e.getMessage());
            }
        };
    }

    @Bean
    public CommandLineRunner initDummyRoommates(PencariHunianRepository repo) {
        return args -> {
            try {
                List<PencariHunian> allPencari = repo.findAll();
                String[] locations = {"Bandung", "Jakarta", "Surabaya", "Yogyakarta"};
                String[] jobs = {"Mahasiswa", "Desainer Grafis", "Software Engineer", "Content Creator"};
                String[] genders = {"Pria", "Wanita"};
                int i = 0;
                for (PencariHunian p : allPencari) {
                    boolean updated = false;
                    if (p.getLokasi() == null || p.getLokasi().isEmpty()) {
                        p.setLokasi(locations[i % locations.length]);
                        updated = true;
                    }
                    if (p.getUmur() == null || p.getUmur() == 0) {
                        p.setUmur(20 + (i % 5));
                        updated = true;
                    }
                    if (p.getPekerjaan() == null || p.getPekerjaan().isEmpty()) {
                        p.setPekerjaan(jobs[i % jobs.length]);
                        updated = true;
                    }
                    if (p.getGender() == null || p.getGender().isEmpty()) {
                        p.setGender(genders[i % genders.length]);
                        updated = true;
                    }
                    if (updated) {
                        repo.save(p);
                    }
                    i++;
                }
                System.out.println(">>> Initialized dummy roommate personal details for " + allPencari.size() + " users.");
            } catch (Exception e) {
                System.err.println(">>> Failed to initialize dummy roommate details: " + e.getMessage());
            }
        };
    }
}
