package com.superkos.app.repository;

import com.superkos.app.model.ChatRoom;
import com.superkos.app.model.Hunian;
import com.superkos.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    /** Find all chat rooms where the user is a participant. */
    @Query("""
            SELECT DISTINCT c FROM ChatRoom c
            JOIN c.participants p
            WHERE p = :user
            ORDER BY c.createdAt DESC
            """)
    List<ChatRoom> findByParticipant(@Param("user") User user);

    /** Find the specific 1-on-1 room between two users (either direction). */
    @Query("""
            SELECT c FROM ChatRoom c
            JOIN c.participants p1
            JOIN c.participants p2
            WHERE p1 = :a AND p2 = :b
            AND SIZE(c.participants) = 2
            """)
    Optional<ChatRoom> findBetween(@Param("a") User a, @Param("b") User b);

    /** Find a RESERVASI-type chat room for a specific hunian that the user is in. */
    @Query("""
            SELECT c FROM ChatRoom c
            JOIN c.participants p
            WHERE c.hunian = :hunian AND c.chatType = 'RESERVASI' AND p = :user
            """)
    Optional<ChatRoom> findReservasiRoom(@Param("hunian") Hunian hunian, @Param("user") User user);

    /** Find all RESERVASI chat rooms for a user. */
    @Query("""
            SELECT DISTINCT c FROM ChatRoom c
            JOIN c.participants p
            WHERE p = :user AND c.chatType = 'RESERVASI'
            ORDER BY c.createdAt DESC
            """)
    List<ChatRoom> findReservasiRoomsByParticipant(@Param("user") User user);
}
