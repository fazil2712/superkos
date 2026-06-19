package com.superkos.app.repository;

import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.RoommateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
// #babas(RoommateRequest)
@Repository
public interface RoommateRequestRepository extends JpaRepository<RoommateRequest, Integer> {

    
    List<RoommateRequest> findByTargetPencariOrderByIdRequestDesc(PencariHunian target);

    
    List<RoommateRequest> findByPencariHunianOrderByIdRequestDesc(PencariHunian sender);

    
    @Query("""
            SELECT r FROM RoommateRequest r
            WHERE r.status = 'PENDING'
              AND ((r.pencariHunian = :a AND r.targetPencari = :b)
                OR (r.pencariHunian = :b AND r.targetPencari = :a))
            """)
    Optional<RoommateRequest> findPendingBetween(
            @Param("a") PencariHunian a,
            @Param("b") PencariHunian b);

    
    @Query("""
            SELECT r FROM RoommateRequest r
            WHERE r.status = 'ACCEPTED'
              AND ((r.pencariHunian = :a AND r.targetPencari = :b)
                OR (r.pencariHunian = :b AND r.targetPencari = :a))
            """)
    Optional<RoommateRequest> findAcceptedBetween(
            @Param("a") PencariHunian a,
            @Param("b") PencariHunian b);

    
    long countByTargetPencariAndStatus(PencariHunian target, String status);

    
    long countByPencariHunianAndStatusAndSenderRead(PencariHunian sender, String status, boolean senderRead);

    
    @Modifying
    @Transactional
    @Query("""
        UPDATE RoommateRequest r SET r.senderRead = true
        WHERE r.pencariHunian = :sender AND r.status = 'ACCEPTED' AND r.senderRead = false
    """)
    void markAcceptedRequestsAsRead(@Param("sender") PencariHunian sender);
}
// #/babas(RoommateRequest)
