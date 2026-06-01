package com.superkos.app.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Reservasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReservasi;

    @ManyToOne
    @JoinColumn(name = "pencari_hunian_id")
    private PencariHunian pencariHunian;

    @ManyToOne
    @JoinColumn(name = "hunian_id")
    private Hunian hunian;

    // TAMBAHAN PEMILIK
    @ManyToOne
    @JoinColumn(name = "pemilik_id")
    private PemilikProperti pemilik;

    /*
        PENDING
        ACCEPTED
        REJECTED
    */
    private String status;

    private String alasanPenolakan;

    /** Chat room created when this reservasi is ACCEPTED */
    @OneToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    /** Whether the pemilik has seen this request */
    private Boolean pemilikRead = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date tanggalPengajuan;

    // =========================
    // AUTO SET DATE
    // =========================

    @PrePersist
    protected void onCreate() {
        tanggalPengajuan = new Date();
    }

    // =========================
    // METHOD
    // =========================

    public void terima() {
        this.status = "ACCEPTED";
    }

    public void tolak(String alasan) {
        this.status = "REJECTED";
        this.alasanPenolakan = alasan;
    }

    // =========================
    // GETTER SETTER
    // =========================

    public int getIdReservasi()                         { return idReservasi; }
    public void setIdReservasi(int idReservasi)         { this.idReservasi = idReservasi; }

    public PencariHunian getPencariHunian()             { return pencariHunian; }
    public void setPencariHunian(PencariHunian p)       { this.pencariHunian = p; }

    public Hunian getHunian()                          { return hunian; }
    public void setHunian(Hunian hunian)               { this.hunian = hunian; }

    public PemilikProperti getPemilik()                 { return pemilik; }
    public void setPemilik(PemilikProperti pemilik)     { this.pemilik = pemilik; }

    public String getStatus()                          { return status; }
    public void setStatus(String status)               { this.status = status; }

    public String getAlasanPenolakan()                 { return alasanPenolakan; }
    public void setAlasanPenolakan(String a)           { this.alasanPenolakan = a; }

    public Date getTanggalPengajuan()                  { return tanggalPengajuan; }
    public void setTanggalPengajuan(Date d)            { this.tanggalPengajuan = d; }

    public ChatRoom getChatRoom()                      { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom)         { this.chatRoom = chatRoom; }

    public Boolean getPemilikRead()                     { return pemilikRead; }
    public void setPemilikRead(Boolean pemilikRead)    { this.pemilikRead = pemilikRead; }
    
    // For thymeleaf backward compatibility if it looks for isPemilikRead
    public Boolean isPemilikRead()                     { return pemilikRead != null && pemilikRead; }
}