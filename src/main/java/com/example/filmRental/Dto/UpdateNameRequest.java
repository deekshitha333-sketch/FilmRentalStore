package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateNameRequest {
    @NotBlank(message = "value is required")
    @Size(max = 45, message = "value max 45 chars")
    private String value;
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}