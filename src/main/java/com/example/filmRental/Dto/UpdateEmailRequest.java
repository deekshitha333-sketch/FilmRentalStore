package com.example.filmRental.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateEmailRequest {
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 50, message = "email max 50 chars")
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}