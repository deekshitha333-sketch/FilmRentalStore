package com.example.filmRental.Dto;
import java.math.BigDecimal;
public class StoreAmountDto {
    private Short storeId;
    private String storeAddress; // address, city
    private BigDecimal amount;
    public StoreAmountDto(){}
    public StoreAmountDto(Short storeId, String storeAddress, BigDecimal amount){ this.storeId=storeId; this.storeAddress=storeAddress; this.amount=amount; }
    public Short getStoreId(){ return storeId; }
    public void setStoreId(Short v){ this.storeId=v; }
    public String getStoreAddress(){ return storeAddress; }
    public void setStoreAddress(String v){ this.storeAddress=v; }
    public BigDecimal getAmount(){ return amount; }
    public void setAmount(BigDecimal v){ this.amount=v; }
}

