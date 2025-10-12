package com.example.filmRental.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short film_id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Short release_year;

    @Column(nullable = false)
    private Short language_id;

    @Column(nullable = false)
    private Short rental_duration;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal rental_rate;

    @Column
    private Short length;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal replacement_cost;

    @Column(length = 10)
    private String rating;

    @Column(length = 255)
    private String special_features;

    @Column(nullable = false)
    private Timestamp last_update;

    public Short getFilm_id() { return film_id; }
    public void setFilm_id(Short film_id) { this.film_id = film_id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Short getRelease_year() { return release_year; }
    public void setRelease_year(Short release_year) { this.release_year = release_year; }
    public Short getLanguage_id() { return language_id; }
    public void setLanguage_id(Short language_id) { this.language_id = language_id; }
    public Short getRental_duration() { return rental_duration; }
    public void setRental_duration(Short rental_duration) { this.rental_duration = rental_duration; }
    public java.math.BigDecimal getRental_rate() { return rental_rate; }
    public void setRental_rate(java.math.BigDecimal rental_rate) { this.rental_rate = rental_rate; }
    public Short getLength() { return length; }
    public void setLength(Short length) { this.length = length; }
    public java.math.BigDecimal getReplacement_cost() { return replacement_cost; }
    public void setReplacement_cost(java.math.BigDecimal replacement_cost) { this.replacement_cost = replacement_cost; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getSpecial_features() { return special_features; }
    public void setSpecial_features(String special_features) { this.special_features = special_features; }
    public Timestamp getLast_update() { return last_update; }
    public void setLast_update(Timestamp last_update) { this.last_update = last_update; }
}
