package com.superkos.app.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idChat;

    @ManyToOne
    @JoinColumn(name = "participant1_id")
    private User participant1;

    @ManyToOne
    @JoinColumn(name = "participant2_id")
    private User participant2;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC")
    private List<Message> messages = new ArrayList<>();

    public void tambahPesan(Message msg) {
        messages.add(msg);
        msg.setChatRoom(this);
    }

    public List<Message> getRiwayatPesan() { return messages; }

    public long getUnreadCount(User user) {
        if (messages == null) return 0;
        return messages.stream()
                .filter(m -> m.getSender().getId() != user.getId() && !m.isRead())
                .count();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int  getIdChat()                         { return idChat; }
    public void setIdChat(int idChat)               { this.idChat = idChat; }

    public User getParticipant1()                   { return participant1; }
    public void setParticipant1(User p)             { this.participant1 = p; }

    public User getParticipant2()                   { return participant2; }
    public void setParticipant2(User p)             { this.participant2 = p; }

    public Date getCreatedAt()                      { return createdAt; }
    public void setCreatedAt(Date createdAt)        { this.createdAt = createdAt; }

    public List<Message> getMessages()              { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}
