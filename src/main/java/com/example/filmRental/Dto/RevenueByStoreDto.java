package com.example.filmRental.Dto;

import java.math.BigDecimal;

public class RevenueByStoreDto {
    private Short storeId;
    private String storeAddress;
    private BigDecimal amount;

    public RevenueByStoreDto() { }
    public RevenueByStoreDto(Short storeId, String storeAddress, BigDecimal amount) {
        this.storeId = storeId; this.storeAddress = storeAddress; this.amount = amount;
    }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}