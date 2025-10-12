package com.example.filmRental.Dto;

public class StoreFilmInventoryCountDto {
    private Short storeId;
    private String storeAddress;
    private Integer filmId;
    private String title;
    private Long copies;

    public StoreFilmInventoryCountDto() { }
    public StoreFilmInventoryCountDto(Short storeId, String storeAddress, Integer filmId, String title, Long copies) {
        this.storeId = storeId; this.storeAddress = storeAddress;
        this.filmId = filmId; this.title = title; this.copies = copies;
    }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getCopies() { return copies; }
    public void setCopies(Long copies) { this.copies = copies; }
}