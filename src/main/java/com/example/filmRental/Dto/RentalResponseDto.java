// src/main/java/com/example/filmRental/Dto/RentalResponseDto.java
package com.example.filmRental.Dto;

import java.time.LocalDateTime;

public class RentalResponseDto {
    private Integer id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private Integer inventoryId;
    private Short customerId;
    private Short staffId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDateTime rentalDate) { this.rentalDate = rentalDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }
    public Short getCustomerId() { return customerId; }
    public void setCustomerId(Short customerId) { this.customerId = customerId; }
    public Short getStaffId() { return staffId; }
    public void setStaffId(Short staffId) { this.staffId = staffId; }
}