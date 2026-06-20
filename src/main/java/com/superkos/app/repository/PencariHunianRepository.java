package com.superkos.app.repository;

import com.superkos.app.model.PencariHunian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

// #yury(PencariHunian)
@Repository
public interface PencariHunianRepository extends JpaRepository<PencariHunian, Integer> {

    
    @Query("""
            SELECT p FROM PencariHunian p
            JOIN FETCH p.roommateSurvey s
            WHERE p.id <> :currentUserId
              AND s.socialScore IS NOT NULL
              AND s.cleanlinessScore IS NOT NULL
              AND s.sleepScore IS NOT NULL
            """)
    Page<PencariHunian> findCandidates(@Param("currentUserId") int currentUserId, Pageable pageable);

    @Query("""
            SELECT p FROM PencariHunian p
            JOIN FETCH p.roommateSurvey s
            WHERE p.id <> :currentUserId
              AND s.socialScore IS NOT NULL
              AND s.cleanlinessScore IS NOT NULL
              AND s.sleepScore IS NOT NULL
            """)
    List<PencariHunian> findAllCandidates(@Param("currentUserId") int currentUserId);
}
// #/yury(PencariHunian)
