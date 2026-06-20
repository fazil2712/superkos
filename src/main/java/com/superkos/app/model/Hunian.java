package com.superkos.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

// #fazil(Hunian)
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
    protected Date availableDateStart;
    protected Date availableDateEnd;

    @Column(columnDefinition = "TEXT")
    protected String deskripsi;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "hunian_kategori", 
        joinColumns = @JoinColumn(name = "hunian_id"),
        foreignKey = @ForeignKey(
            name = "fk_hunian_kategori_hunian",
            foreignKeyDefinition = "FOREIGN KEY (hunian_id) REFERENCES hunian (id_hunian) ON DELETE CASCADE"
        )
    )
    @Column(name = "kategori")
    private List<String> kategoriSewa = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "hunian_foto", 
        joinColumns = @JoinColumn(name = "hunian_id"),
        foreignKey = @ForeignKey(
            name = "fk_hunian_foto_hunian",
            foreignKeyDefinition = "FOREIGN KEY (hunian_id) REFERENCES hunian (id_hunian) ON DELETE CASCADE"
        )
    )
    @Column(name = "foto_url")
    private List<String> fotoHunian = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "hunian_fasilitas", 
        joinColumns = @JoinColumn(name = "hunian_id"),
        foreignKey = @ForeignKey(
            name = "fk_hunian_fasilitas_hunian",
            foreignKeyDefinition = "FOREIGN KEY (hunian_id) REFERENCES hunian (id_hunian) ON DELETE CASCADE"
        )
    )
    @Column(name = "fasilitas")
    private List<String> fasilitas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "pemilik_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PemilikProperti pemilik;



    
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

     public Date getAvailableDateStart() { return availableDateStart; }
     public void setAvailableDateStart(Date availableDateStart) { this.availableDateStart = availableDateStart; }
     public Date getAvailableDateEnd() { return availableDateEnd; }
     public void setAvailableDateEnd(Date availableDateEnd) { this.availableDateEnd = availableDateEnd; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public PemilikProperti getPemilik() { return pemilik; }
    public void setPemilik(PemilikProperti pemilik) { this.pemilik = pemilik; }


    public List<String> getKategoriSewa() { return kategoriSewa; }
    public void setKategoriSewa(List<String> kategoriSewa) { this.kategoriSewa = kategoriSewa; }

    public List<String> getFotoHunian() { return fotoHunian; }
    public void setFotoHunian(List<String> fotoHunian) { this.fotoHunian = fotoHunian; }

    public List<String> getFasilitas() { return fasilitas; }
    public void setFasilitas(List<String> fasilitas) { this.fasilitas = fasilitas; }
}
// #/fazil(Hunian)
