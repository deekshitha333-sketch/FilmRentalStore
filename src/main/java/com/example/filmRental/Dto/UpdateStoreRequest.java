package com.example.filmRental.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateStoreRequest {
    @NotNull(message = "storeId is required")
    @Min(value = 1, message = "storeId must be >= 1")
    private Short storeId;
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
}
