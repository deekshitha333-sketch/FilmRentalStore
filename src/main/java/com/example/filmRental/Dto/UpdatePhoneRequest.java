package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePhoneRequest {
    @NotBlank(message = "phone is required")
    @Size(max = 20, message = "phone max 20 chars")
    private String phone;
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

