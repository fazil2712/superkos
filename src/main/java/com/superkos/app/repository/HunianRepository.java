package com.superkos.app.repository;

import com.superkos.app.model.Hunian;
import com.superkos.app.model.PemilikProperti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import java.util.Date;
import java.util.List;

@Repository
public interface HunianRepository extends JpaRepository<Hunian, Integer> {

    /** All properties owned by a specific pemilik, newest first. */
    List<Hunian> findByPemilikOrderByIdHunianDesc(PemilikProperti pemilik);

    List<Hunian> findByLokasiContainingIgnoreCase(String lokasi);

    
    @Query("SELECT DISTINCT h FROM Hunian h LEFT JOIN h.kategoriSewa k WHERE " +
           "(:keyword IS NULL OR LOWER(h.namaHunian) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(h.lokasi) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:minPrice IS NULL OR h.harga >= :minPrice) " +
           "AND (:maxPrice IS NULL OR h.harga <= :maxPrice) " +
           "AND (:roommate IS NULL OR (:roommate = true AND h.jumlahKamar > 1) OR (:roommate = false AND h.jumlahKamar = 1)) " +
           "AND (:tipeGender IS NULL OR :tipeGender = '' OR h.tipeGender = :tipeGender) " +
           "AND (:startDate IS NULL OR h.availableDateStart <= :startDate) " +
           "AND (:endDate IS NULL OR h.availableDateEnd >= :endDate) " +
           "AND (:kategoriSewa IS NULL OR k IN :kategoriSewa)")
    List<Hunian> searchAndFilter(
        @Param("keyword") String keyword,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("roommate") Boolean roommate,
        @Param("tipeGender") String tipeGender,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate,
        @Param("kategoriSewa") List<String> kategoriSewa,
        Sort sort
    );
}
