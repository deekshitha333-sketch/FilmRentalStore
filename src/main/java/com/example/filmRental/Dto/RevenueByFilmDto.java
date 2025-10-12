package com.example.filmRental.Dto;

import java.math.BigDecimal;

public class RevenueByFilmDto {
    private Integer filmId;
    private String title;
    private BigDecimal amount;

    public RevenueByFilmDto() { }
    public RevenueByFilmDto(Integer filmId, String title, BigDecimal amount) {
        this.filmId = filmId; this.title = title; this.amount = amount;
    }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}