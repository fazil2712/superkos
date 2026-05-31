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

@Repository
public interface RoommateRequestRepository extends JpaRepository<RoommateRequest, Integer> {

    /** All requests received by a user (the inbox). */
    List<RoommateRequest> findByTargetPencariOrderByIdRequestDesc(PencariHunian target);

    /** All requests sent by a user. */
    List<RoommateRequest> findByPencariHunianOrderByIdRequestDesc(PencariHunian sender);

    /** Check if a PENDING request already exists between two users (either direction). */
    @Query("""
            SELECT r FROM RoommateRequest r
            WHERE r.status = 'PENDING'
              AND ((r.pencariHunian = :a AND r.targetPencari = :b)
                OR (r.pencariHunian = :b AND r.targetPencari = :a))
            """)
    Optional<RoommateRequest> findPendingBetween(
            @Param("a") PencariHunian a,
            @Param("b") PencariHunian b);

    /** Check if an ACCEPTED request (= active chat) exists between two users (either direction). */
    @Query("""
            SELECT r FROM RoommateRequest r
            WHERE r.status = 'ACCEPTED'
              AND ((r.pencariHunian = :a AND r.targetPencari = :b)
                OR (r.pencariHunian = :b AND r.targetPencari = :a))
            """)
    Optional<RoommateRequest> findAcceptedBetween(
            @Param("a") PencariHunian a,
            @Param("b") PencariHunian b);

    /** Count pending requests received by a user (for inbox badge). */
    long countByTargetPencariAndStatus(PencariHunian target, String status);

    /** Count accepted requests sent by a user that they haven't read yet. */
    long countByPencariHunianAndStatusAndSenderRead(PencariHunian sender, String status, boolean senderRead);

    /** Mark all accepted requests sent by a user as read in the database. */
    @Modifying
    @Transactional
    @Query("""
        UPDATE RoommateRequest r SET r.senderRead = true
        WHERE r.pencariHunian = :sender AND r.status = 'ACCEPTED' AND r.senderRead = false
    """)
    void markAcceptedRequestsAsRead(@Param("sender") PencariHunian sender);
}
