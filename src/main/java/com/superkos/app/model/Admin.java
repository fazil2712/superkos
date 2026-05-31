package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin extends User {
    
    @Override
    public java.util.Map<String, Object> dashboard() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("role", "ADMIN");
        return stats;
    }
    
    public void moderasiKonten(LaporanReview review, String statusBaru, com.superkos.app.repository.LaporanReviewRepository repo) {
        review.setStatusPenyelesaian(statusBaru);
        repo.save(review);
    }
    
    public void kelolaUser(User targetUser, String action, com.superkos.app.repository.UserRepository repo) {
        if ("DELETE".equalsIgnoreCase(action)) {
            repo.delete(targetUser);
        }
    }
    
    public void kelolaPolicy() {
        System.out.println("System policy updated by admin: " + this.getNama());
    }
}
