package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin extends User {
    
    @Override
    public void dashboard() {}
    
    public void moderasiKonten() {}
    
    public void kelolaUser() {}
    
    public void kelolaPolicy() {}
}
