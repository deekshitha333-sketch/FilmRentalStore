// File: src/main/java/com/example/filmRental/Entity/UserCredential.java
package com.example.filmRental.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_credential")
public class UserCredential {
    @Id
    @Column(name = "staff_id", nullable = false)
    private Short staffId;

    @Column(name = "username", nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "last_update", nullable = false)
    private Timestamp lastUpdate;

    public UserCredential() { }

    public UserCredential(Short staffId, String username, String passwordHash, Timestamp lastUpdate) {
        this.staffId = staffId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.lastUpdate = lastUpdate;
    }

    public Short getStaffId() { return staffId; }
    public void setStaffId(Short staffId) { this.staffId = staffId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }
}