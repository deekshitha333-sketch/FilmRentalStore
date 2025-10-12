// File: src/main/java/com/example/filmRental/Repository/UserCredentialRepository.java
package com.example.filmRental.Repository;

import com.example.filmRental.Entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Short> {
    Optional<UserCredential> findByUsername(String username);
    boolean existsByUsername(String username);
}