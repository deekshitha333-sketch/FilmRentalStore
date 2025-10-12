package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StaffUpdateFirstNameRequestDto {
    @NotBlank @Size(max = 45)
    private String firstName;

    public StaffUpdateFirstNameRequestDto() { }
    public StaffUpdateFirstNameRequestDto(String firstName) { this.firstName = firstName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
}
