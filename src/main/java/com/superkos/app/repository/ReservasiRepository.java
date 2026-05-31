package com.superkos.app.repository;

import com.superkos.app.model.Reservasi;
import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.Hunian;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservasiRepository extends JpaRepository<Reservasi, Integer> {

    // Ambil semua reservasi milik pencari hunian
    List<Reservasi> findByPencariHunian(PencariHunian pencariHunian);

    // Ambil semua reservasi berdasarkan hunian
    List<Reservasi> findByHunian(Hunian hunian);

    // Ambil berdasarkan status
    List<Reservasi> findByStatus(String status);

    // Cari reservasi user pada hunian tertentu
    List<Reservasi> findByPencariHunianAndHunian(
            PencariHunian pencariHunian,
            Hunian hunian
    );
}