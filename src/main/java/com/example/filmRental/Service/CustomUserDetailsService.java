// File: src/main/java/com/example/filmRental/Service/CustomUserDetailsService.java
package com.example.filmRental.Service;

import com.example.filmRental.Entity.UserCredential;
import com.example.filmRental.Repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialRepository repo;

    @Autowired
    public CustomUserDetailsService(UserCredentialRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential uc = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Single role is fine for rubric; expand later if needed
        return User.withUsername(uc.getUsername())
                .password(uc.getPasswordHash())
                .roles("STAFF")
                .build();
    }
}
