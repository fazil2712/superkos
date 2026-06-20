package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
// #yury(PemilikProperti)
@Entity
@Getter
@Setter
public class PemilikProperti extends User {

    @OneToMany(mappedBy = "pemilik", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hunian> daftarHunian = new ArrayList<>();

    @OneToMany(mappedBy = "pemilik", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservasi> daftarReservasi = new ArrayList<>();

    public java.util.Map<String, Object> dashboard() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalProperties", (long) this.daftarHunian.size());
        long availableCount = this.daftarHunian.stream().filter(Hunian::isStatusTersedia).count();
        stats.put("availableCount", availableCount);
        return stats;
    }

    public void tambahHunian(Hunian hunian) {
        daftarHunian.add(hunian);
        hunian.setPemilik(this);
    }
}
// #/yury(PemilikProperti)
