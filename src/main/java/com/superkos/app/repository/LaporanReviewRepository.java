package com.superkos.app.repository;

import com.superkos.app.model.LaporanReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaporanReviewRepository extends JpaRepository<LaporanReview, Integer> {
}
