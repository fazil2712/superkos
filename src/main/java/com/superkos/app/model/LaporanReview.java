package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
public class LaporanReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRecord;

    private String tipeRecord;
    private int rating;
    private String isiText;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date tanggal;
    
    private String statusPenyelesaian;

    @ManyToOne
    @JoinColumn(name = "pencari_hunian_id")
    private PencariHunian pencariHunian;

    @ManyToOne
    @JoinColumn(name = "hunian_id")
    private Hunian hunian;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public void prosesLaporan() {}
}
