package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StaffUpdatePhoneRequestDto {
    @NotBlank @Size(max = 20)
    private String phone;

    public StaffUpdatePhoneRequestDto() { }
    public StaffUpdatePhoneRequestDto(String phone) { this.phone = phone; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
}
