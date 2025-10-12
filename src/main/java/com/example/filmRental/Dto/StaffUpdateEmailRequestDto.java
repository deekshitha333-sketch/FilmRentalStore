package com.example.filmRental.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StaffUpdateEmailRequestDto {
    @Email @NotBlank @Size(max = 50)
    private String email;

    public StaffUpdateEmailRequestDto() { }
    public StaffUpdateEmailRequestDto(String email) { this.email = email; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
}
