package com.superkos.app.config;

import com.superkos.app.model.Hunian;
import com.superkos.app.model.PemilikProperti;
import com.superkos.app.repository.HunianRepository;
import com.superkos.app.repository.PemilikPropertiRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DummyDataLoader {

    @Bean
    CommandLineRunner initDatabase(HunianRepository repository, PemilikPropertiRepository pemilikRepository) {
        return args -> {
            // Seed a dummy owner first so the dummy properties have an owner
            PemilikProperti dummyPemilik = pemilikRepository.findByEmail("pemilik@superkos.com");
            if (dummyPemilik == null) {
                dummyPemilik = new PemilikProperti();
                dummyPemilik.setNama("Budi Santoso");
                dummyPemilik.setEmail("pemilik@superkos.com");
                dummyPemilik.setPassword("pemilik123");
                dummyPemilik.setKontak("08123456789");
                dummyPemilik.setLokasi("Bandung");
                dummyPemilik.setBiodata("Pemilik kos ramah, siap melayani penyewa dengan sepenuh hati.");
                dummyPemilik = pemilikRepository.save(dummyPemilik);
            }

            // Only seed data if the table is empty
            if (repository.count() == 0) {
                Hunian h1 = new Hunian();
                h1.setNamaHunian("Kost Superkos Mas Yono Tipe H");
                h1.setLokasi("Coblong, Bandung");
                h1.setHarga(600000);
                h1.setTipeGender("Putri");
                h1.setJumlahKamar(2);
                h1.setStatusTersedia(true);
                h1.setKategoriSewa(List.of("Bulanan", "6 Bulanan", "Tahunan"));
                h1.setPemilik(dummyPemilik);
                repository.save(h1);

                Hunian h2 = new Hunian();
                h2.setNamaHunian("Kost GMI46 Tipe C");
                h2.setLokasi("Andir, Bandung");
                h2.setHarga(1100000);
                h2.setTipeGender("Campur");
                h2.setJumlahKamar(1);
                h2.setStatusTersedia(false);
                h2.setKategoriSewa(List.of("Harian", "Mingguan", "Bulanan"));
                h2.setPemilik(dummyPemilik);
                repository.save(h2);
                
                Hunian h3 = new Hunian();
                h3.setNamaHunian("Kost Dago Highland Tipe A");
                h3.setLokasi("Dago, Bandung");
                h3.setHarga(750000);
                h3.setTipeGender("Putra");
                h3.setJumlahKamar(2);
                h3.setStatusTersedia(true);
                h3.setKategoriSewa(List.of("Bulanan", "Tahunan"));
                h3.setPemilik(dummyPemilik);
                repository.save(h3);
            }

            // Link existing orphan properties to the dummy owner
            List<Hunian> orphans = repository.findAll();
            for (Hunian h : orphans) {
                if (h.getPemilik() == null) {
                    h.setPemilik(dummyPemilik);
                    repository.save(h);
                }
            }
        };
    }
}
