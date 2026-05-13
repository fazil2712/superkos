package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class RoommateSurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSurvey;

    @ElementCollection
    private List<String> jawaban;

    private String kategoriGayaHidup;

    @OneToOne(mappedBy = "roommateSurvey")
    private PencariHunian pencariHunian;

    public String getKategori() {
        return kategoriGayaHidup;
    }

    public void isiSurvey() {}

    public double hitungKecocokan(RoommateSurvey target) {
        return 0.0;
    }
}
