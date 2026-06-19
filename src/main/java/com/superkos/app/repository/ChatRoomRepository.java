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
// #naufal(ChatRoom & Message)
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    
    @Query("""
            SELECT DISTINCT c FROM ChatRoom c
            JOIN c.participants p
            WHERE p = :user
            ORDER BY c.createdAt DESC
            """)
    List<ChatRoom> findByParticipant(@Param("user") User user);

    
    @Query("""
            SELECT c FROM ChatRoom c
            JOIN c.participants p1
            JOIN c.participants p2
            WHERE p1 = :a AND p2 = :b
            AND SIZE(c.participants) = 2
            """)
    Optional<ChatRoom> findBetween(@Param("a") User a, @Param("b") User b);

    
    @Query("""
            SELECT c FROM ChatRoom c
            JOIN c.participants p
            WHERE c.hunian = :hunian AND c.chatType = 'RESERVASI' AND p = :user
            """)
    Optional<ChatRoom> findReservasiRoom(@Param("hunian") Hunian hunian, @Param("user") User user);

    
    @Query("""
            SELECT DISTINCT c FROM ChatRoom c
            JOIN c.participants p
            WHERE p = :user AND c.chatType = 'RESERVASI'
            ORDER BY c.createdAt DESC
            """)
    List<ChatRoom> findReservasiRoomsByParticipant(@Param("user") User user);
}
// #/naufal(ChatRoom & Message)
