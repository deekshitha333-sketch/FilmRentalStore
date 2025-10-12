package com.example.filmRental.Dto;

import java.math.BigDecimal;

public class RevenueFilmByStoreDto {
    private Short storeId;
    private String storeAddress;
    private Integer filmId;
    private String title;
    private BigDecimal amount;

    public RevenueFilmByStoreDto() { }
    public RevenueFilmByStoreDto(Short storeId, String storeAddress, Integer filmId,
                                 String title, BigDecimal amount) {
        this.storeId = storeId; this.storeAddress = storeAddress;
        this.filmId = filmId; this.title = title; this.amount = amount;
    }

    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}