package com.superkos.app.config;

import com.superkos.app.model.Hunian;
import com.superkos.app.repository.HunianRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DummyDataLoader {

    @Bean
    CommandLineRunner initDatabase(HunianRepository repository) {
        return args -> {
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
                repository.save(h1);

                Hunian h2 = new Hunian();
                h2.setNamaHunian("Kost GMI46 Tipe C");
                h2.setLokasi("Andir, Bandung");
                h2.setHarga(1100000);
                h2.setTipeGender("Campur");
                h2.setJumlahKamar(1);
                h2.setStatusTersedia(false);
                h2.setKategoriSewa(List.of("Harian", "Mingguan", "Bulanan"));
                repository.save(h2);
                
                Hunian h3 = new Hunian();
                h3.setNamaHunian("Kost Dago Highland Tipe A");
                h3.setLokasi("Dago, Bandung");
                h3.setHarga(750000);
                h3.setTipeGender("Putra");
                h3.setJumlahKamar(2);
                h3.setStatusTersedia(true);
                h3.setKategoriSewa(List.of("Bulanan", "Tahunan"));
                repository.save(h3);
            }
        };
    }
}
