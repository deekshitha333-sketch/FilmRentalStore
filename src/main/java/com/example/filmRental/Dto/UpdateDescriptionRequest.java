package com.example.filmRental.Dto;

import jakarta.validation.constraints.Size;

public class UpdateDescriptionRequest {
    @Size(max = 10000, message = "description too long")
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}