package com.example.filmRental.Dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class FilmRequestDto {
    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be at most 255 chars")
    private String title;

    @Size(max = 10000, message = "description too long")
    private String description;

    @Min(value = 1900, message = "releaseYear must be >= 1900")
    @Max(value = 2150, message = "releaseYear must be reasonable")
    private Short releaseYear; // optional

    @NotNull(message = "languageId is required")
    @Min(value = 1, message = "languageId must be >= 1")
    private Short languageId;

    @NotNull(message = "rentalDuration is required")
    @Min(value = 0, message = "rentalDuration must be >= 0")
    private Short rentalDuration;

    @NotNull(message = "rentalRate is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "rentalRate must be >= 0.00")
    private java.math.BigDecimal rentalRate;

    @Min(value = 0, message = "length must be >= 0")
    private Short length; // optional

    @NotNull(message = "replacementCost is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "replacementCost must be >= 0.00")
    private java.math.BigDecimal replacementCost;

    @Pattern(regexp = "^(G|PG|PG-13|R|NC-17)$", message = "rating must be one of G, PG, PG-13, R, NC-17")
    private String rating; // optional

    @Size(max = 255, message = "specialFeatures too long")
    private String specialFeatures; // optional, comma-separated

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Short getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Short releaseYear) { this.releaseYear = releaseYear; }
    public Short getLanguageId() { return languageId; }
    public void setLanguageId(Short languageId) { this.languageId = languageId; }
    public Short getRentalDuration() { return rentalDuration; }
    public void setRentalDuration(Short rentalDuration) { this.rentalDuration = rentalDuration; }
    public java.math.BigDecimal getRentalRate() { return rentalRate; }
    public void setRentalRate(java.math.BigDecimal rentalRate) { this.rentalRate = rentalRate; }
    public Short getLength() { return length; }
    public void setLength(Short length) { this.length = length; }
    public java.math.BigDecimal getReplacementCost() { return replacementCost; }
    public void setReplacementCost(java.math.BigDecimal replacementCost) { this.replacementCost = replacementCost; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getSpecialFeatures() { return specialFeatures; }
    public void setSpecialFeatures(String specialFeatures) { this.specialFeatures = specialFeatures; }
}
