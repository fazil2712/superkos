package com.superkos.app.model;

import com.superkos.app.converter.IntegerListConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a PencariHunian's lifestyle preference quiz results.
 *
 * The 20 raw answers are stored as a JSON array in a single TEXT column
 * so we keep granular data for future dealbreaker / fuzzy-logic features.
 *
 * Answer layout (0-based index):
 *   [ 0.. 6]  Social & Kepribadian   (7 questions)
 *   [ 7..13]  Kebersihan & Kerapian  (7 questions)
 *   [14..19]  Waktu Tidur & Rutinitas (6 questions)
 *
 * On every quiz submission, three category averages are pre-calculated
 * and stored as floats (1.00 – 10.00) so matching queries can use indexed
 * numeric comparisons instead of parsing JSON.
 */
@Entity
public class RoommateSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSurvey;

    /**
     * All 20 raw quiz answers stored as a JSON array string.
     * e.g. "[5,7,3,8,9,4,6,7,8,5,6,7,9,3,4,6,7,8,5,7]"
     */
    @Convert(converter = IntegerListConverter.class)
    @Column(name = "raw_answers", columnDefinition = "TEXT")
    private List<Integer> jawaban = new ArrayList<>();

    /** Average of Social answers (indices 0–6). Range: 1.00 – 10.00 */
    @Column(name = "social_score")
    private Float socialScore;

    /** Average of Cleanliness answers (indices 7–13). Range: 1.00 – 10.00 */
    @Column(name = "cleanliness_score")
    private Float cleanlinessScore;

    /** Average of Sleep answers (indices 14–19). Range: 1.00 – 10.00 */
    @Column(name = "sleep_score")
    private Float sleepScore;

    /** Lifestyle category label set by fuzzy logic (to be implemented). */
    private String kategoriGayaHidup;

    /** Timestamp of last quiz submission — used to enforce the 7-day cooldown. */
    private LocalDateTime lastQuizTaken;

    @OneToOne(mappedBy = "roommateSurvey")
    private PencariHunian pencariHunian;

    // ── Core Domain Method ────────────────────────────────────────────────────

    /**
     * Primary domain method. Validates all 20 answers, pre-calculates the
     * three category averages, stores the raw JSON data, and records the
     * submission timestamp. Call this whenever a quiz form is submitted.
     *
     * @param answers Ordered list of exactly 20 integers, each strictly 1–10.
     * @throws IllegalArgumentException if size != 20 or any value is out of range.
     */
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

        // Pre-calculate category averages — rounded to 2 decimal places
        this.socialScore      = roundAvg(answers.subList(0,  7));   // 7 social questions
        this.cleanlinessScore = roundAvg(answers.subList(7,  14));  // 7 cleanliness questions
        this.sleepScore       = roundAvg(answers.subList(14, 20));  // 6 sleep questions

        this.lastQuizTaken = LocalDateTime.now();
    }

    /**
     * Calculates compatibility between this survey and a target survey.
     *
     * Formula (per category):
     *   compatibility = (1 - |myScore - targetScore| / 9) * 100
     * Overall score:
     *   (socialComp + cleanComp + sleepComp) / 3
     *
     * @return overall compatibility 0–100, or -1 if either survey is incomplete.
     */
    public double hitungKecocokan(RoommateSurvey target) {
        if (!this.isQuizComplete() || target == null || !target.isQuizComplete()) return -1;

        double socialComp = (1.0 - Math.abs(this.socialScore      - target.getSocialScore())      / 9.0) * 100.0;
        double cleanComp  = (1.0 - Math.abs(this.cleanlinessScore - target.getCleanlinessScore()) / 9.0) * 100.0;
        double sleepComp  = (1.0 - Math.abs(this.sleepScore       - target.getSleepScore())       / 9.0) * 100.0;

        // Clamp each to [0, 100]
        socialComp = Math.max(0, Math.min(100, socialComp));
        cleanComp  = Math.max(0, Math.min(100, cleanComp));
        sleepComp  = Math.max(0, Math.min(100, sleepComp));

        return (socialComp + cleanComp + sleepComp) / 3.0;
    }

    /**
     * Per-category compatibility breakdown (social, cleanliness, sleep).
     * Returns an array of 3 doubles [socialComp, cleanComp, sleepComp], each 0–100.
     * Returns null if either survey is incomplete.
     */
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

    /** Fuzzy label based on overall compatibility percentage. */
    public static String fuzzyLabel(double overallPct) {
        if (overallPct >= 90) return "Sangat Cocok";
        if (overallPct >= 75) return "Cocok";
        if (overallPct >= 60) return "Lumayan Cocok";
        return "Kurang Cocok";
    }

    public String getKategori() { return kategoriGayaHidup; }

    // ── Convenience Helpers ───────────────────────────────────────────────────

    /** Returns true when all 3 category scores have been calculated. */
    public boolean isQuizComplete() {
        return socialScore != null && cleanlinessScore != null && sleepScore != null;
    }

    /**
     * Safe single-answer getter by 0-based index (for pre-filling quiz sliders).
     * Returns 5 (midpoint) when the list is empty or index is out of bounds.
     */
    public int getAnswer(int index) {
        if (jawaban == null || index >= jawaban.size()) return 5;
        Integer val = jawaban.get(index);
        return val != null ? val : 5;
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private float roundAvg(List<Integer> values) {
        double avg = values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        return Math.round(avg * 100f) / 100f; // Round to 2 decimal places
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

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
