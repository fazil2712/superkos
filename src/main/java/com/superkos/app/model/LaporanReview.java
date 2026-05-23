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

    public void prosesLaporan(String status) {
        this.statusPenyelesaian = status;
    }

    // Getters and Setters
    public int getIdRecord() { return idRecord; }
    public void setIdRecord(int idRecord) { this.idRecord = idRecord; }

    public String getTipeRecord() { return tipeRecord; }
    public void setTipeRecord(String tipeRecord) { this.tipeRecord = tipeRecord; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getIsiText() { return isiText; }
    public void setIsiText(String isiText) { this.isiText = isiText; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public String getStatusPenyelesaian() { return statusPenyelesaian; }
    public void setStatusPenyelesaian(String statusPenyelesaian) { this.statusPenyelesaian = statusPenyelesaian; }

    public PencariHunian getPencariHunian() { return pencariHunian; }
    public void setPencariHunian(PencariHunian pencariHunian) { this.pencariHunian = pencariHunian; }

    public Hunian getHunian() { return hunian; }
    public void setHunian(Hunian hunian) { this.hunian = hunian; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
}
