// src/main/java/com/example/filmRental/Dto/RentalCreateRequestDto.java
package com.example.filmRental.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class RentalCreateRequestDto {
    @NotNull @Min(1)
    private Integer inventoryId;
    @NotNull @Min(1)
    private Short customerId;
    @NotNull @Min(1)
    private Short staffId;
    // Optional; if null, service uses now()
    private LocalDateTime rentalDate;

    public RentalCreateRequestDto() { }

    public RentalCreateRequestDto(Integer inventoryId, Short customerId, Short staffId, LocalDateTime rentalDate) {
        this.inventoryId = inventoryId;
        this.customerId = customerId;
        this.staffId = staffId;
        this.rentalDate = rentalDate;
    }

    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }
    public Short getCustomerId() { return customerId; }
    public void setCustomerId(Short customerId) { this.customerId = customerId; }
    public Short getStaffId() { return staffId; }
    public void setStaffId(Short staffId) { this.staffId = staffId; }
    public LocalDateTime getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDateTime rentalDate) { this.rentalDate = rentalDate; }
}

