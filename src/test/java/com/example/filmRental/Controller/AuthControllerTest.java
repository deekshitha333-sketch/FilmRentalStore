// File: src/test/java/com/example/filmRental/Controller/AuthControllerTest.java
package com.example.filmRental.Controller;

import com.example.filmRental.Dto.ChangePasswordRequestDto;
import com.example.filmRental.Dto.SignupRequestDto;
import com.example.filmRental.Entity.UserCredential;
import com.example.filmRental.Repository.UserCredentialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = {AuthController.class})
class AuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserCredentialRepository repo;
    @MockBean private PasswordEncoder encoder;

    @Test @DisplayName("POST /api/v1/auth/signup -> 201")
    void signup_created() throws Exception {
        SignupRequestDto req = new SignupRequestDto((short)5, "alice", "secret123");
        Mockito.when(repo.existsById((short)5)).thenReturn(false);
        Mockito.when(repo.existsByUsername("alice")).thenReturn(false);
        Mockito.when(encoder.encode("secret123")).thenReturn("hash");

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Credential created")));
    }

    @Test @DisplayName("POST /api/v1/auth/password -> 200")
    void changePassword_ok() throws Exception {
        String basic = "Basic " + java.util.Base64.getEncoder()
                .encodeToString("alice:oldpass".getBytes());
        Mockito.when(repo.findByUsername("alice"))
                .thenReturn(Optional.of(new UserCredential((short)5, "alice", "oldhash",
                        Timestamp.from(Instant.now()))));
        Mockito.when(encoder.encode("newpass")).thenReturn("newhash");

        ChangePasswordRequestDto body = new ChangePasswordRequestDto("newpass");
        mockMvc.perform(post("/api/v1/auth/password")
                .header("Authorization", basic)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated"));
    }
}