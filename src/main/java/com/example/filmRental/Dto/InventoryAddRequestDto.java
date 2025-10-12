package com.example.filmRental.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryAddRequestDto {
    @NotNull @Min(1)
    private Integer filmId;
    @NotNull @Min(1)
    private Short storeId;

    public InventoryAddRequestDto() { }
    public InventoryAddRequestDto(Integer filmId, Short storeId) {
        this.filmId = filmId; this.storeId = storeId;
    }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
}