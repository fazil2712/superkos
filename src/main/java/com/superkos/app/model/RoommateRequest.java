package com.superkos.app.model;

import jakarta.persistence.*;
// #babas(RoommateRequest)
@Entity
public class RoommateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRequest;

    
    private String status;

    
    @ManyToOne
    @JoinColumn(name = "pencari_hunian_id")
    private PencariHunian pencariHunian;

    
    @ManyToOne
    @JoinColumn(name = "target_pencari_id")
    private PencariHunian targetPencari;

    
    @OneToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private boolean senderRead = false;

    
    public void terima() { this.status = "ACCEPTED"; }

    
    public void tolak()  { this.status = "REJECTED"; }

    
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
// #/babas(RoommateRequest)
