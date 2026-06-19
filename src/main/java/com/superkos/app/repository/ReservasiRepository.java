package com.superkos.app.repository;

import com.superkos.app.model.Reservasi;
import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.PemilikProperti;
import com.superkos.app.model.Hunian;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// #fazil(Daftar Reservasi)
public interface ReservasiRepository extends JpaRepository<Reservasi, Integer> {

    
    List<Reservasi> findByPencariHunian(PencariHunian pencariHunian);

    
    List<Reservasi> findByHunian(Hunian hunian);

    
    List<Reservasi> findByStatus(String status);

    
    List<Reservasi> findByPencariHunianAndHunian(
            PencariHunian pencariHunian,
            Hunian hunian
    );

    

    
    List<Reservasi> findByPemilikOrderByIdReservasiDesc(PemilikProperti pemilik);

    
    List<Reservasi> findByPemilikAndStatusOrderByIdReservasiDesc(PemilikProperti pemilik, String status);

    
    long countByPemilikAndStatus(PemilikProperti pemilik, String status);

    
    long countByPemilikAndPemilikRead(PemilikProperti pemilik, Boolean pemilikRead);

    
    List<Reservasi> findByHunianAndStatus(Hunian hunian, String status);
}
// #/fazil(Daftar Reservasi)
// #/pajil