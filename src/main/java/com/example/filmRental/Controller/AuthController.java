
package com.example.filmRental.Controller;

import com.example.filmRental.Dto.ChangePasswordRequestDto;
import com.example.filmRental.Dto.SignupRequestDto;
import com.example.filmRental.Entity.UserCredential;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.UserCredentialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "Auth", description = "Create staff login & manage password")
public class AuthController {

    private final UserCredentialRepository repo;
    private final PasswordEncoder encoder;

    @Autowired
    public AuthController(UserCredentialRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Operation(summary = "Create login for an existing Staff (OPEN)")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@Valid @RequestBody SignupRequestDto req) {
        if (repo.existsById(req.getStaffId())) {
            // update username/password for same staff if needed? Keep simple: block duplicate staff
            return "Credential already exists for staff: " + req.getStaffId();
        }
        if (repo.existsByUsername(req.getUsername())) {
            return "Username already taken";
        }
        String hash = encoder.encode(req.getPassword());
        UserCredential uc = new UserCredential(req.getStaffId(), req.getUsername(), hash,
                Timestamp.from(Instant.now()));
        repo.save(uc);
        return "Credential created";
    }

    @Operation(summary = "Change password (Basic Auth required)")
    @PostMapping("/password")
    public String changePassword(@Valid @RequestBody ChangePasswordRequestDto req,
                                 @RequestHeader("Authorization") String authHeader) {
        // Basic <base64 username:password>
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new NotFoundException("Authorization required");
        }
        String base64 = authHeader.substring("Basic ".length());
        String decoded = new String(java.util.Base64.getDecoder().decode(base64));
        String username = decoded.split(":", 2)[0];

        UserCredential uc = repo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
        uc.setPasswordHash(encoder.encode(req.getNewPassword()));
        uc.setLastUpdate(Timestamp.from(Instant.now()));
        repo.save(uc);
        return "Password updated";
    }
}
