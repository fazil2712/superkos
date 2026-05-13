package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class PemilikProperti extends User {
    
    @OneToMany(mappedBy = "pemilik", cascade = CascadeType.ALL)
    private List<Hunian> daftarHunian = new ArrayList<>();

    @Override
    public void dashboard() {}
    
    public void tambahHunian(Hunian hunian) {
        daftarHunian.add(hunian);
        hunian.setPemilik(this);
    }
    
    public void kelolaPenyewa() {}
}
