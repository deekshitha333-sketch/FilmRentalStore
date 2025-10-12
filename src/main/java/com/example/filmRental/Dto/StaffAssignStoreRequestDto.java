package com.example.filmRental.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StaffAssignStoreRequestDto {
    @NotNull @Min(1)
    private Short storeId;

    public StaffAssignStoreRequestDto() { }
    public StaffAssignStoreRequestDto(Short storeId) { this.storeId = storeId; }

    public Short getStoreId() { return storeId; }
    public void setStoreId(Short v) { this.storeId = v; }
}
