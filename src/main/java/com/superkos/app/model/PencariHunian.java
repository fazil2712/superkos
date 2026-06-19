package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import com.superkos.app.repository.HunianRepository;
import com.superkos.app.repository.RoommateRequestRepository;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.stream.Collectors;
// #yury(PencariHunian)
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

    @OneToMany(mappedBy = "targetPencari", cascade = CascadeType.ALL)
    private List<RoommateRequest> receivedRequests = new ArrayList<>();


    @ManyToMany
    @JoinTable(
        name = "wishlist",
        joinColumns = @JoinColumn(name = "pencari_hunian_id"),
        inverseJoinColumns = @JoinColumn(name = "hunian_id")
    )
    private List<Hunian> wishlist = new ArrayList<>();

    public List<Hunian> getWishlist() { return wishlist; }
    public void setWishlist(List<Hunian> wishlist) { this.wishlist = wishlist; }

    public List<RoommateRequest> getReceivedRequests() { return receivedRequests; }
    public void setReceivedRequests(List<RoommateRequest> receivedRequests) { this.receivedRequests = receivedRequests; }

    // Returns notification messages for pending roommate requests (used by GlobalModelAdvice)
    public List<String> popnotif(RoommateRequestRepository reqRepo) {
        List<String> notifications = new ArrayList<>();
        long pendingCount = reqRepo.countByTargetPencariAndStatus(this, "PENDING");
        if (pendingCount > 0) {
            notifications.add("Anda memiliki " + pendingCount + " permintaan roommate baru.");
        }
        long acceptedCount = reqRepo.countByPencariHunianAndStatusAndSenderRead(this, "ACCEPTED", false);
        if (acceptedCount > 0) {
            notifications.add(acceptedCount + " permintaan roommate Anda telah diterima!");
        }
        return notifications;
    }

    public void kirimRoommateRequest(PencariHunian target, RoommateRequestRepository repo) {
        RoommateRequest request = new RoommateRequest();
        request.setPencariHunian(this);
        request.setTargetPencari(target);
        request.setStatus("PENDING");
        repo.save(request);
    }

    public void tambahKeWishlist(Hunian h) {
        if (!wishlist.contains(h)) {
            wishlist.add(h);
        }
    }
    
    public void hapusDariWishlist(Hunian h) {
        wishlist.remove(h);
    }
}
// #/yury(PencariHunian)
