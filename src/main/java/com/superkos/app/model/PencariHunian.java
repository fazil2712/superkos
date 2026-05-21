package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class PencariHunian extends User {
    private String bio;
    private String kriteriaRoommate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id", referencedColumnName = "idSurvey")
    private RoommateSurvey roommateSurvey;

    public RoommateSurvey getRoommateSurvey() { return roommateSurvey; }
    public void setRoommateSurvey(RoommateSurvey s) { this.roommateSurvey = s; }

    @OneToMany(mappedBy = "pencariHunian", cascade = CascadeType.ALL)
    private List<RoommateRequest> roommateRequests = new ArrayList<>();

    @OneToMany(mappedBy = "pencariHunian", cascade = CascadeType.ALL)
    private List<LaporanReview> laporanReviews = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "wishlist",
        joinColumns = @JoinColumn(name = "pencari_hunian_id"),
        inverseJoinColumns = @JoinColumn(name = "hunian_id")
    )
    private List<Hunian> wishlist = new ArrayList<>();

    @Override
    public void dashboard() {}
    
    public void cariHunian() {}
    
    public void kirimRoommateRequest(PencariHunian target) {}
    
    public void buatLaporanReview() {}
    
    public void tambahKeWishlist(Hunian h) {
        if (!wishlist.contains(h)) {
            wishlist.add(h);
        }
    }
    
    public void hapusDariWishlist(Hunian h) {
        wishlist.remove(h);
    }
}
