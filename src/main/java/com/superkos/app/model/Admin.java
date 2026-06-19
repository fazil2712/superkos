package com.superkos.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
// #nadia(Admin)
@Entity
@Getter
@Setter
public class Admin extends User {
    

    public void kelolaUser(User targetUser, String action, com.superkos.app.repository.UserRepository repo) {
        if ("DELETE".equalsIgnoreCase(action)) {
            repo.delete(targetUser);
        }
    }

}
// #/nadia(Admin)
