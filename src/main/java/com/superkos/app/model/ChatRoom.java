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

    /** ROOMMATE = 1-on-1 roommate chat, RESERVASI = group chat for a property rental */
    private String chatType = "ROOMMATE";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "chat_room_participants",
        joinColumns = @JoinColumn(name = "chat_room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();

    /** Optional: which property this group chat is about (only for RESERVASI type) */
    @ManyToOne
    @JoinColumn(name = "hunian_id")
    private Hunian hunian;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC")
    private List<Message> messages = new ArrayList<>();

    // ── Convenience Methods ───────────────────────────────────────────────────

    public void tambahPesan(Message msg) {
        messages.add(msg);
        msg.setChatRoom(this);
    }

    public List<Message> getRiwayatPesan() { return messages; }

    /** Count unread messages for a given user (messages from others that are unread). */
    public long getUnreadCount(User user) {
        if (messages == null) return 0;
        return messages.stream()
                .filter(m -> m.getSender().getId() != user.getId() && !m.isRead())
                .count();
    }

    /** Check if a user is a participant in this chat room. */
    public boolean isParticipant(User user) {
        if (user == null || participants == null) return false;
        return participants.stream().anyMatch(p -> p.getId() == user.getId());
    }

    /** Add a participant if not already present. */
    public void addParticipant(User user) {
        if (!isParticipant(user)) {
            participants.add(user);
        }
    }

    // ── Backward Compatibility ────────────────────────────────────────────────
    // These delegate to the participants list for existing code that references
    // participant1/participant2 (roommate 1-on-1 chats).

    public User getParticipant1() {
        return participants.size() > 0 ? participants.get(0) : null;
    }
    public void setParticipant1(User p) {
        if (participants.isEmpty()) participants.add(p);
        else participants.set(0, p);
    }

    public User getParticipant2() {
        return participants.size() > 1 ? participants.get(1) : null;
    }
    public void setParticipant2(User p) {
        if (participants.size() < 1) participants.add(null); // placeholder
        if (participants.size() < 2) participants.add(p);
        else participants.set(1, p);
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int  getIdChat()                         { return idChat; }
    public void setIdChat(int idChat)               { this.idChat = idChat; }

    public String getChatType()                     { return chatType; }
    public void setChatType(String chatType)        { this.chatType = chatType; }

    public List<User> getParticipants()             { return participants; }
    public void setParticipants(List<User> p)       { this.participants = p; }

    public Hunian getHunian()                       { return hunian; }
    public void setHunian(Hunian hunian)            { this.hunian = hunian; }

    public Date getCreatedAt()                      { return createdAt; }
    public void setCreatedAt(Date createdAt)        { this.createdAt = createdAt; }

    public List<Message> getMessages()              { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}
