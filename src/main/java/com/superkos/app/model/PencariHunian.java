package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import com.superkos.app.repository.HunianRepository;
import com.superkos.app.repository.RoommateRequestRepository;
import com.superkos.app.repository.LaporanReviewRepository;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "pencariHunian", cascade = CascadeType.ALL)
    private List<LaporanReview> laporanReviews = new ArrayList<>();

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

    @Override
    public Map<String, Object> dashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("wishlistCount", (long) this.wishlist.size());
        long pendingCount = this.receivedRequests.stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()))
                .count();
        stats.put("pendingRequestsCount", pendingCount);
        return stats;
    }
    
    public List<Hunian> cariHunian(HunianRepository repository, String lokasi) {
        if (lokasi == null || lokasi.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByLokasiContainingIgnoreCase(lokasi);
    }
    
    public void kirimRoommateRequest(PencariHunian target, RoommateRequestRepository repo) {
        RoommateRequest request = new RoommateRequest();
        request.setPencariHunian(this);
        request.setTargetPencari(target);
        request.setStatus("PENDING");
        repo.save(request);
    }
    
    public void buatLaporanReview(Hunian hunian, String reviewText, int rating, LaporanReviewRepository repo) {
        LaporanReview review = new LaporanReview();
        review.setPencariHunian(this);
        review.setHunian(hunian);
        review.setIsiText(reviewText);
        review.setRating(rating);
        review.setTanggal(new Date());
        repo.save(review);
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
