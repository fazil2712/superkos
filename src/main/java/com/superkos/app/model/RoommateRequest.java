package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

    public void terima() {
        this.status = "ACCEPTED";
    }

    public void tolak() {
        this.status = "REJECTED";
    }
}
