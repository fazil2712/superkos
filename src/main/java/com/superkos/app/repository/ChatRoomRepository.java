package com.superkos.app.repository;

import com.superkos.app.model.ChatRoom;
import com.superkos.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    /** Find a chat room where the user is either participant. */
    @Query("""
            SELECT c FROM ChatRoom c
            WHERE c.participant1 = :user OR c.participant2 = :user
            ORDER BY c.createdAt DESC
            """)
    List<ChatRoom> findByParticipant(@Param("user") User user);

    /** Find the specific room between two users (either direction). */
    @Query("""
            SELECT c FROM ChatRoom c
            WHERE (c.participant1 = :a AND c.participant2 = :b)
               OR (c.participant1 = :b AND c.participant2 = :a)
            """)
    Optional<ChatRoom> findBetween(@Param("a") User a, @Param("b") User b);
}
