package com.superkos.app.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class Hunian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int idHunian;

    protected String namaHunian;
    protected String tipeHunian;
    protected double harga;
    protected String lokasi;
    protected boolean statusTersedia;
    protected String tipeGender;
    protected int jumlahKamar;
    protected String tipeUnit;
    protected Date availableDateStart;
    protected Date availableDateEnd;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "hunian_kategori", joinColumns = @JoinColumn(name = "hunian_id"))
    @Column(name = "kategori")
    private List<String> kategoriSewa = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "pemilik_id")
    private PemilikProperti pemilik;

    @OneToMany(mappedBy = "hunian", cascade = CascadeType.ALL)
    private List<LaporanReview> laporanReviews = new ArrayList<>();

    public void tampilkanDetail() {}

    // Getters and Setters
    public int getIdHunian() { return idHunian; }
    public void setIdHunian(int idHunian) { this.idHunian = idHunian; }

    public String getNamaHunian() { return namaHunian; }
    public void setNamaHunian(String namaHunian) { this.namaHunian = namaHunian; }

    public String getTipeHunian() { return tipeHunian; }
    public void setTipeHunian(String tipeHunian) { this.tipeHunian = tipeHunian; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public boolean isStatusTersedia() { return statusTersedia; }
    public void setStatusTersedia(boolean statusTersedia) { this.statusTersedia = statusTersedia; }

    public String getTipeGender() { return tipeGender; }
    public void setTipeGender(String tipeGender) { this.tipeGender = tipeGender; }

    public int getJumlahKamar() { return jumlahKamar; }
    public void setJumlahKamar(int jumlahKamar) { this.jumlahKamar = jumlahKamar; }

    public String getTipeUnit() { return tipeUnit; }
    public void setTipeUnit(String tipeUnit) { this.tipeUnit = tipeUnit; }
     public Date getAvailableDateStart() { return availableDateStart; }
     public void setAvailableDateStart(Date availableDateStart) { this.availableDateStart = availableDateStart; }
     public Date getAvailableDateEnd() { return availableDateEnd; }
     public void setAvailableDateEnd(Date availableDateEnd) { this.availableDateEnd = availableDateEnd; }

    public PemilikProperti getPemilik() { return pemilik; }
    public void setPemilik(PemilikProperti pemilik) { this.pemilik = pemilik; }

    public List<LaporanReview> getLaporanReviews() { return laporanReviews; }
    public void setLaporanReviews(List<LaporanReview> laporanReviews) { this.laporanReviews = laporanReviews; }

    public List<String> getKategoriSewa() { return kategoriSewa; }
    public void setKategoriSewa(List<String> kategoriSewa) { this.kategoriSewa = kategoriSewa; }
}
