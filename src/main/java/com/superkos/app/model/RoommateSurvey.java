package com.superkos.app.model;

import com.superkos.app.converter.IntegerListConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// #nadia(Roommate Survey)
@Entity
public class RoommateSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSurvey;

    
    @Convert(converter = IntegerListConverter.class)
    @Column(name = "raw_answers", columnDefinition = "TEXT")
    private List<Integer> jawaban = new ArrayList<>();

    
    @Column(name = "social_score")
    private Float socialScore;

    
    @Column(name = "cleanliness_score")
    private Float cleanlinessScore;

    
    @Column(name = "sleep_score")
    private Float sleepScore;

    
    private String kategoriGayaHidup;

    
    private LocalDateTime lastQuizTaken;

    @OneToOne(mappedBy = "roommateSurvey")
    private PencariHunian pencariHunian;

    

    
    public void isiSurvey(List<Integer> answers) {
        if (answers == null || answers.size() != 20) {
            throw new IllegalArgumentException("Quiz harus memiliki tepat 20 jawaban.");
        }
        for (int i = 0; i < answers.size(); i++) {
            Integer v = answers.get(i);
            if (v == null || v < 1 || v > 10) {
                throw new IllegalArgumentException(
                        "Jawaban ke-" + (i + 1) + " tidak valid. Harus antara 1 dan 10.");
            }
        }

        this.jawaban = new ArrayList<>(answers);

        
        this.socialScore      = roundAvg(answers.subList(0,  7));   
        this.cleanlinessScore = roundAvg(answers.subList(7,  14));  
        this.sleepScore       = roundAvg(answers.subList(14, 20));  

        this.lastQuizTaken = LocalDateTime.now();
    }

    
    public double hitungKecocokan(RoommateSurvey target) {
        if (!this.isQuizComplete() || target == null || !target.isQuizComplete()) return -1;

        double socialComp = (1.0 - Math.abs(this.socialScore      - target.getSocialScore())      / 9.0) * 100.0;
        double cleanComp  = (1.0 - Math.abs(this.cleanlinessScore - target.getCleanlinessScore()) / 9.0) * 100.0;
        double sleepComp  = (1.0 - Math.abs(this.sleepScore       - target.getSleepScore())       / 9.0) * 100.0;

        
        socialComp = Math.max(0, Math.min(100, socialComp));
        cleanComp  = Math.max(0, Math.min(100, cleanComp));
        sleepComp  = Math.max(0, Math.min(100, sleepComp));

        return (socialComp + cleanComp + sleepComp) / 3.0;
    }

    
    public double[] getBreakdown(RoommateSurvey target) {
        if (!this.isQuizComplete() || target == null || !target.isQuizComplete()) return null;

        double socialComp = Math.max(0, Math.min(100,
                (1.0 - Math.abs(this.socialScore      - target.getSocialScore())      / 9.0) * 100.0));
        double cleanComp  = Math.max(0, Math.min(100,
                (1.0 - Math.abs(this.cleanlinessScore - target.getCleanlinessScore()) / 9.0) * 100.0));
        double sleepComp  = Math.max(0, Math.min(100,
                (1.0 - Math.abs(this.sleepScore       - target.getSleepScore())       / 9.0) * 100.0));

        return new double[]{ socialComp, cleanComp, sleepComp };
    }

    
    public static String fuzzyLabel(double overallPct) {
        if (overallPct >= 90) return "Sangat Cocok";
        if (overallPct >= 75) return "Cocok";
        if (overallPct >= 60) return "Lumayan Cocok";
        return "Kurang Cocok";
    }

    public String getKategori() { return kategoriGayaHidup; }

    

    
    public boolean isQuizComplete() {
        return socialScore != null && cleanlinessScore != null && sleepScore != null;
    }

    
    public int getAnswer(int index) {
        if (jawaban == null || index >= jawaban.size()) return 5;
        Integer val = jawaban.get(index);
        return val != null ? val : 5;
    }

    

    private float roundAvg(List<Integer> values) {
        double avg = values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        return Math.round(avg * 100f) / 100f; 
    }

    

    public int getIdSurvey() { return idSurvey; }
    public void setIdSurvey(int idSurvey) { this.idSurvey = idSurvey; }

    public List<Integer> getJawaban() { return jawaban; }
    public void setJawaban(List<Integer> jawaban) { this.jawaban = jawaban; }

    public Float getSocialScore() { return socialScore; }
    public void setSocialScore(Float socialScore) { this.socialScore = socialScore; }

    public Float getCleanlinessScore() { return cleanlinessScore; }
    public void setCleanlinessScore(Float cleanlinessScore) { this.cleanlinessScore = cleanlinessScore; }

    public Float getSleepScore() { return sleepScore; }
    public void setSleepScore(Float sleepScore) { this.sleepScore = sleepScore; }

    public String getKategoriGayaHidup() { return kategoriGayaHidup; }
    public void setKategoriGayaHidup(String k) { this.kategoriGayaHidup = k; }

    public LocalDateTime getLastQuizTaken() { return lastQuizTaken; }
    public void setLastQuizTaken(LocalDateTime lastQuizTaken) { this.lastQuizTaken = lastQuizTaken; }

    public PencariHunian getPencariHunian() { return pencariHunian; }
    public void setPencariHunian(PencariHunian p) { this.pencariHunian = p; }
}
// #/nadia(Roommate Survey)
