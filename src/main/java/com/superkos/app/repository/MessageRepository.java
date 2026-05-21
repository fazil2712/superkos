package com.superkos.app.repository;

import com.superkos.app.model.ChatRoom;
import com.superkos.app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /** All messages in a chat room, ordered oldest first. */
    List<Message> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);
}
