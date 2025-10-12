package com.example.filmRental.Dto;

public class StoreInventoryCountDto {
    private Short storeId;
    private String storeAddress;
    private Long copies;

    public StoreInventoryCountDto() { }
    public StoreInventoryCountDto(Short storeId, String storeAddress, Long copies) {
        this.storeId = storeId; this.storeAddress = storeAddress; this.copies = copies;
    }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
    public Long getCopies() { return copies; }
    public void setCopies(Long copies) { this.copies = copies; }
}
