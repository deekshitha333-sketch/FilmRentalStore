package com.example.filmRental.Dto;

public class InventoryResponseDto {
    private Integer inventoryId;
    private Integer filmId;
    private Short storeId;

    public InventoryResponseDto() { }
    public InventoryResponseDto(Integer inventoryId, Integer filmId, Short storeId) {
        this.inventoryId = inventoryId; this.filmId = filmId; this.storeId = storeId;
    }
    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
}