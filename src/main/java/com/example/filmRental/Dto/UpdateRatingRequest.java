package com.example.filmRental.Dto;

import jakarta.validation.constraints.Pattern;

public class UpdateRatingRequest {
    @Pattern(regexp = "^(G|PG|PG-13|R|NC-17)$", message = "rating must be one of G, PG, PG-13, R, NC-17")
    private String rating;
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
}