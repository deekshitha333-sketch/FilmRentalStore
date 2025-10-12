package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StaffUpdateLastNameRequestDto {
    @NotBlank @Size(max = 45)
    private String lastName;

    public StaffUpdateLastNameRequestDto() { }
    public StaffUpdateLastNameRequestDto(String lastName) { this.lastName = lastName; }

    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
}
