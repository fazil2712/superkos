package com.superkos.app.repository;

import com.superkos.app.model.ChatRoom;
import com.superkos.app.model.Message;
import com.superkos.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /** All messages in a chat room, ordered oldest first. */
    List<Message> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    /** Count all unread messages across all chats for a given user. */
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.chatRoom IN (
            SELECT DISTINCT c FROM ChatRoom c JOIN c.participants p WHERE p = :user
        )
        AND m.sender != :user
        AND m.isRead = false
    """)
    long countUnreadMessages(@Param("user") User user);
}
