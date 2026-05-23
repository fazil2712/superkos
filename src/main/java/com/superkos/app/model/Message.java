package com.superkos.app.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMessage;

    private String isiPesan;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private boolean isRead = false;

    public int getIdMessage() { return idMessage; }
    public void setIdMessage(int idMessage) { this.idMessage = idMessage; }

    public String getIsiPesan() { return isiPesan; }
    public void setIsiPesan(String isiPesan) { this.isiPesan = isiPesan; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public ChatRoom getChatRoom() { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
