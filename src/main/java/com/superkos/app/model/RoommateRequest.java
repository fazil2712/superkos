package com.superkos.app.model;

import jakarta.persistence.*;

@Entity
public class RoommateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRequest;

    /** PENDING, ACCEPTED, REJECTED */
    private String status;

    /** The user who sent the request */
    @ManyToOne
    @JoinColumn(name = "pencari_hunian_id")
    private PencariHunian pencariHunian;

    /** The user who received the request */
    @ManyToOne
    @JoinColumn(name = "target_pencari_id")
    private PencariHunian targetPencari;

    /** Created when the request is ACCEPTED */
    @OneToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private boolean senderRead = false;

    /** Accepts the request and updates status. ChatRoom is linked by the controller. */
    public void terima() { this.status = "ACCEPTED"; }

    /** Rejects the request. */
    public void tolak()  { this.status = "REJECTED"; }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int getIdRequest()                      { return idRequest; }
    public void setIdRequest(int idRequest)        { this.idRequest = idRequest; }

    public String getStatus()                      { return status; }
    public void setStatus(String status)           { this.status = status; }

    public PencariHunian getPencariHunian()              { return pencariHunian; }
    public void setPencariHunian(PencariHunian p)        { this.pencariHunian = p; }

    public PencariHunian getTargetPencari()              { return targetPencari; }
    public void setTargetPencari(PencariHunian t)        { this.targetPencari = t; }

    public ChatRoom getChatRoom()                  { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom)     { this.chatRoom = chatRoom; }

    public boolean isSenderRead()                  { return senderRead; }
    public void setSenderRead(boolean senderRead)  { this.senderRead = senderRead; }
}
