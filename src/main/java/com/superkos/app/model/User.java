package com.superkos.app.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    protected String nama;
    protected String email;
    protected String password;

    // Profile customization fields
    @Column(columnDefinition = "TEXT")
    protected String biodata;
    protected Integer umur;
    protected String lokasi;
    protected String gender;
    protected String pekerjaan;

    // Based on the UML Methods
    public void dashboard() {}
    public void login() {}
    public void logout() {}
    public void registrasi() {}
    public void popnotif() {}

    // Getters and Setters
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
}
