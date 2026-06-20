package com.superkos.app.config;

import com.superkos.app.model.*;
import com.superkos.app.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

@Configuration
public class DummyDataLoader {

    @Bean
    CommandLineRunner initDatabase(
            HunianRepository repository, 
            PemilikPropertiRepository pemilikRepository,
            PencariHunianRepository pencariRepository,
            UserRepository userRepository) {
        return args -> {
            
            // 1. UPDATE EXISTING PEMILIKPROPERTI OR CREATE IF NOT EXIST
            PemilikProperti dummyPemilik = pemilikRepository.findByEmail("pemilik@superkos.com");
            if (dummyPemilik == null) {
                dummyPemilik = new PemilikProperti();
                dummyPemilik.setEmail("pemilik@superkos.com");
            }
            dummyPemilik.setNama("Budi Santoso");
            dummyPemilik.setPassword("pemilik123");
            dummyPemilik.setKontak("08123456789");
            dummyPemilik.setLokasi("Bandung");
            dummyPemilik.setBiodata("Pemilik kos ramah dan profesional, siap melayani penyewa dengan sepenuh hati.");
            dummyPemilik.setGender("Pria");
            dummyPemilik.setUmur(45);
            dummyPemilik.setPekerjaan("Wiraswasta");
            dummyPemilik.setFotoProfil("/uploads/profile/avatar_budi.png");
            dummyPemilik = pemilikRepository.save(dummyPemilik);

            // 2. CREATE NEW PEMILIKPROPERTI
            PemilikProperti newPemilik = pemilikRepository.findByEmail("hendra@superkos.com");
            if (newPemilik == null) {
                newPemilik = new PemilikProperti();
                newPemilik.setEmail("hendra@superkos.com");
                newPemilik.setNama("Hendra Wijaya");
                newPemilik.setPassword("hendra123");
                newPemilik.setKontak("08987654321");
                newPemilik.setLokasi("Jakarta");
                newPemilik.setBiodata("Menyediakan kamar kos bersih, tenang, nyaman, dan strategis dekat dengan berbagai kampus utama.");
                newPemilik.setGender("Pria");
                newPemilik.setUmur(38);
                newPemilik.setPekerjaan("Pensiunan BUMN");
                newPemilik.setFotoProfil("/uploads/profile/avatar_hendra.png");
                newPemilik = pemilikRepository.save(newPemilik);
            }

            // 3. CREATE 2 NEW PENCARIHUNIAN
            User user1 = userRepository.findByEmail("ahmad@superkos.com");
            if (user1 == null) {
                PencariHunian ph1 = new PencariHunian();
                ph1.setEmail("ahmad@superkos.com");
                ph1.setNama("Ahmad Dhani");
                ph1.setPassword("ahmad123");
                ph1.setKontak("08112233445");
                ph1.setLokasi("Bandung");
                ph1.setBiodata("Mahasiswa tingkat akhir ITB Jurusan Teknik Informatika. Senang bermain musik, main game, dan diskusi santai.");
                ph1.setGender("Pria");
                ph1.setUmur(22);
                ph1.setPekerjaan("Mahasiswa");
                ph1.setFotoProfil("/uploads/profile/avatar_ahmad.png");
                ph1.setBio("Mencari hunian bersama roommate yang bersih dan toleran.");
                ph1.setKriteriaRoommate("Rapi, tidak merokok, dan jadwal tidur teratur.");

                RoommateSurvey survey1 = new RoommateSurvey();
                survey1.isiSurvey(List.of(8, 7, 9, 8, 7, 8, 9, 8, 9, 8, 7, 8, 9, 8, 6, 7, 6, 7, 6, 7)); // 20 answers
                survey1.setKategoriGayaHidup("Rapi & Tenang");
                ph1.setRoommateSurvey(survey1);
                pencariRepository.save(ph1);
            }

            User user2 = userRepository.findByEmail("sarah@superkos.com");
            if (user2 == null) {
                PencariHunian ph2 = new PencariHunian();
                ph2.setEmail("sarah@superkos.com");
                ph2.setNama("Siti Sarah");
                ph2.setPassword("sarah123");
                ph2.setKontak("08556677889");
                ph2.setLokasi("Jakarta");
                ph2.setBiodata("Karyawan swasta yang bekerja di bidang marketing agency. Tenang, menyukai kebersihan, dan hobi membaca buku.");
                ph2.setGender("Wanita");
                ph2.setUmur(24);
                ph2.setPekerjaan("Karyawan Swasta");
                ph2.setFotoProfil("/uploads/profile/avatar_sarah.png");
                ph2.setBio("Senang bersosialisasi tetapi tetap saling menghormati batas privasi masing-masing.");
                ph2.setKriteriaRoommate("Wanita, bekerja, sopan, bersih, dan ramah.");

                RoommateSurvey survey2 = new RoommateSurvey();
                survey2.isiSurvey(List.of(5, 6, 5, 6, 5, 6, 5, 9, 9, 9, 8, 9, 9, 8, 8, 9, 8, 9, 8, 9)); // 20 answers
                survey2.setKategoriGayaHidup("Sangat Bersih & Teratur");
                ph2.setRoommateSurvey(survey2);
                pencariRepository.save(ph2);
            }

            // 4. PREPARE DATES FOR HUNIAN
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.JANUARY, 1);
            Date start = cal.getTime();
            cal.set(2026, Calendar.DECEMBER, 31);
            Date end = cal.getTime();

            // 5. UPDATE OR CREATE HUNIAN DATA WITH COMPLETE COLUMNS
            if (repository.count() <= 3) {
                // Clear existing simple entries to populate complete entries
                repository.deleteAll();

                Hunian h1 = new Hunian();
                h1.setNamaHunian("Kost Superkos Mas Yono Tipe H");
                h1.setTipeHunian("Kost Putri");
                h1.setLokasi("Coblong, Bandung");
                h1.setHarga(600000);
                h1.setTipeGender("Putri");
                h1.setJumlahKamar(2);
                h1.setStatusTersedia(true);
                h1.setDeskripsi("Kost putri eksklusif di Coblong dengan lingkungan yang aman dan sejuk. Kamar mandi dalam dengan pancuran air hangat.");
                h1.setAvailableDateStart(start);
                h1.setAvailableDateEnd(end);
                h1.setKategoriSewa(List.of("Bulanan", "6 Bulanan", "Tahunan"));
                h1.setFotoHunian(List.of("/uploads/hunian/hunian_yono.png"));
                h1.setFasilitas(List.of("WiFi 100Mbps", "AC", "Kasur Springbed", "Lemari Pakaian", "Kamar Mandi Dalam"));
                h1.setPemilik(dummyPemilik);
                repository.save(h1);

                Hunian h2 = new Hunian();
                h2.setNamaHunian("Kost GMI46 Tipe C");
                h2.setTipeHunian("Kost Campur");
                h2.setLokasi("Andir, Bandung");
                h2.setHarga(1100000);
                h2.setTipeGender("Campur");
                h2.setJumlahKamar(1);
                h2.setStatusTersedia(false);
                h2.setDeskripsi("Kost campur minimalis modern dekat pusat perbelanjaan dan stasiun. Sangat cocok bagi pekerja aktif maupun mahasiswa.");
                h2.setAvailableDateStart(start);
                h2.setAvailableDateEnd(end);
                h2.setKategoriSewa(List.of("Harian", "Mingguan", "Bulanan"));
                h2.setFotoHunian(List.of("/uploads/hunian/hunian_gmi.png"));
                h2.setFasilitas(List.of("WiFi", "Kipas Angin", "Kasur Busa", "Meja Belajar"));
                h2.setPemilik(dummyPemilik);
                repository.save(h2);
                
                Hunian h3 = new Hunian();
                h3.setNamaHunian("Kost Dago Highland Tipe A");
                h3.setTipeHunian("Kost Putra");
                h3.setLokasi("Dago, Bandung");
                h3.setHarga(750000);
                h3.setTipeGender("Putra");
                h3.setJumlahKamar(2);
                h3.setStatusTersedia(true);
                h3.setDeskripsi("Kost khusus putra berlokasi di daerah premium Dago. Pemandangan indah perbukitan Bandung dengan udara sejuk alami.");
                h3.setAvailableDateStart(start);
                h3.setAvailableDateEnd(end);
                h3.setKategoriSewa(List.of("Bulanan", "Tahunan"));
                h3.setFotoHunian(List.of("/uploads/hunian/hunian_dago.png"));
                h3.setFasilitas(List.of("WiFi", "AC", "Kasur Queen Size", "Lemari", "Meja Kerja", "Balkon"));
                h3.setPemilik(dummyPemilik);
                repository.save(h3);

                // Add a new property listing for the new owner Hendra Wijaya
                Hunian h4 = new Hunian();
                h4.setNamaHunian("Apartemen Gateway Mas Hendra Tipe B");
                h4.setTipeHunian("Apartemen Studio");
                h4.setLokasi("Cibeunying, Bandung");
                h4.setHarga(2500000);
                h4.setTipeGender("Campur");
                h4.setJumlahKamar(3);
                h4.setStatusTersedia(true);
                h4.setDeskripsi("Unit apartemen tipe studio disewakan bulanan/tahunan. Fasilitas lengkap gedung termasuk kolam renang, gym, dan keamanan 24 jam.");
                h4.setAvailableDateStart(start);
                h4.setAvailableDateEnd(end);
                h4.setKategoriSewa(List.of("Bulanan", "Tahunan"));
                h4.setFotoHunian(List.of("/uploads/hunian/hunian_gateway.png"));
                h4.setFasilitas(List.of("WiFi", "AC Smart", "Dapur Mini", "TV Kabel", "Kulkas", "Kolam Renang"));
                h4.setPemilik(newPemilik);
                repository.save(h4);
            }

            // 6. ENSURE NO ORPHANED HUNIAN IN DATABASE
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
