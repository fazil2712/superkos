package com.superkos.app.dto;


public class MatchResult {

    
    private final int    candidateId;
    private final String nama;
    private final String email;
    private final String kontak;
    private final String lokasi;
    private final String gender;
    private final String pekerjaan;
    private final String biodata;
    private final Integer umur;

    
    private final double overallScore;    
    private final double socialScore;     
    private final double cleanScore;      
    private final double sleepScore;      

    
    private final String fuzzyLabel;      

    public MatchResult(
            int candidateId, String nama, String email, String kontak,
            String lokasi, String gender, String pekerjaan, String biodata, Integer umur,
            double overallScore, double socialScore, double cleanScore, double sleepScore,
            String fuzzyLabel) {

        this.candidateId  = candidateId;
        this.nama         = nama;
        this.email        = email;
        this.kontak       = kontak;
        this.lokasi       = lokasi;
        this.gender       = gender;
        this.pekerjaan    = pekerjaan;
        this.biodata      = biodata;
        this.umur         = umur;
        this.overallScore = round1(overallScore);
        this.socialScore  = round1(socialScore);
        this.cleanScore   = round1(cleanScore);
        this.sleepScore   = round1(sleepScore);
        this.fuzzyLabel   = fuzzyLabel;
    }

    

    public int    getCandidateId()  { return candidateId;  }
    public String getNama()         { return nama;         }
    public String getEmail()        { return email;        }
    public String getKontak()       { return kontak;       }
    public String getLokasi()       { return lokasi;       }
    public String getGender()       { return gender;       }
    public String getPekerjaan()    { return pekerjaan;    }
    public String getBiodata()      { return biodata;      }
    public Integer getUmur()        { return umur;         }
    public double getOverallScore() { return overallScore; }
    public double getSocialScore()  { return socialScore;  }
    public double getCleanScore()   { return cleanScore;   }
    public double getSleepScore()   { return sleepScore;   }
    public String getFuzzyLabel()   { return fuzzyLabel;   }

    
    public String getInitial() {
        return (nama != null && !nama.isEmpty())
                ? String.valueOf(nama.charAt(0)).toUpperCase()
                : "?";
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
