package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;

public class StoreUpdatePhoneRequestDto {
    @NotBlank
    private String phone;

    public StoreUpdatePhoneRequestDto() { }
    public StoreUpdatePhoneRequestDto(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}