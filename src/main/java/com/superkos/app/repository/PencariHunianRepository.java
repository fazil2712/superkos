package com.superkos.app.repository;

import com.superkos.app.model.PencariHunian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PencariHunianRepository extends JpaRepository<PencariHunian, Integer> {

    /**
     * Fetches all PencariHunian who have a completed survey (all 3 scores non-null),
     * excluding the currently logged-in user. Uses JOIN FETCH to prevent N+1 on surveys.
     */
    @Query("""
            SELECT p FROM PencariHunian p
            JOIN FETCH p.roommateSurvey s
            WHERE p.id <> :currentUserId
              AND s.socialScore IS NOT NULL
              AND s.cleanlinessScore IS NOT NULL
              AND s.sleepScore IS NOT NULL
            """)
    Page<PencariHunian> findCandidates(@Param("currentUserId") int currentUserId, Pageable pageable);
}
