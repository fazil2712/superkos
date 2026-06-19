package com.superkos.app.model;

import jakarta.persistence.*;
import com.superkos.app.repository.UserRepository;
import com.superkos.app.repository.RoommateRequestRepository;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract // #naufal
// #naufal(User)
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    protected String nama;
    protected String email;
    protected String password;

    
    @Column(columnDefinition = "TEXT")
    protected String biodata;
    protected Integer umur;
    protected String lokasi;
    protected String gender;
    protected String pekerjaan;
    
    protected String kontak;
    
    protected String fotoProfil;

    // Checks if input password matches stored password (used by AuthController login)
    public boolean login(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    // Registers a new user via the repository (used by AuthController register fallback)
    public void registrasi(UserRepository repository) {
        if (repository.findByEmail(this.email) != null) {
            throw new IllegalArgumentException("Email sudah terdaftar!");
        }
        repository.save(this);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBiodata() { return biodata; }
    public void setBiodata(String biodata) { this.biodata = biodata; }

    public Integer getUmur() { return umur; }
    public void setUmur(Integer umur) { this.umur = umur; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPekerjaan() { return pekerjaan; }
    public void setPekerjaan(String pekerjaan) { this.pekerjaan = pekerjaan; }

    public String getKontak() { return kontak; }
    public void setKontak(String kontak) { this.kontak = kontak; }

    public String getFotoProfil() { return fotoProfil; }
    public void setFotoProfil(String fotoProfil) { this.fotoProfil = fotoProfil; }
}
// #/naufal(User)
