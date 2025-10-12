package com.example.filmRental.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StoreCreateRequestDto {
    @NotNull @Min(1)
    private Short managerStaffId;
    @NotNull @Min(1)
    private Short addressId;

    public StoreCreateRequestDto() { }
    public StoreCreateRequestDto(Short managerStaffId, Short addressId) {
        this.managerStaffId = managerStaffId;
        this.addressId = addressId;
    }
    public Short getManagerStaffId() { return managerStaffId; }
    public void setManagerStaffId(Short managerStaffId) { this.managerStaffId = managerStaffId; }
    public Short getAddressId() { return addressId; }
    public void setAddressId(Short addressId) { this.addressId = addressId; }
}