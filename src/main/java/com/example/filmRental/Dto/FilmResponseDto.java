package com.example.filmRental.Dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FilmResponseDto {
    private Short id;
    private String title;
    private String description;
    private Short releaseYear;
    private Short languageId;
    private Short rentalDuration;
    private BigDecimal rentalRate;
    private Short length;
    private BigDecimal replacementCost;
    private String rating;
    private String specialFeatures;
    private Timestamp lastUpdate;

    public FilmResponseDto() { }

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }
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
    public BigDecimal getRentalRate() { return rentalRate; }
    public void setRentalRate(BigDecimal rentalRate) { this.rentalRate = rentalRate; }
    public Short getLength() { return length; }
    public void setLength(Short length) { this.length = length; }
    public BigDecimal getReplacementCost() { return replacementCost; }
    public void setReplacementCost(BigDecimal replacementCost) { this.replacementCost = replacementCost; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getSpecialFeatures() { return specialFeatures; }
    public void setSpecialFeatures(String specialFeatures) { this.specialFeatures = specialFeatures; }
    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }
}
