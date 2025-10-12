// src/main/java/com/example/filmRental/Dto/RentalStatusResponseDto.java
package com.example.filmRental.Dto;

import java.time.LocalDateTime;

public class RentalStatusResponseDto {
    private Integer inventoryId;
    private boolean available;
    private Integer openRentalId;      // null if available
    private Short customerId;          // who currently holds it
    private LocalDateTime rentalDate;
    private LocalDateTime dueDate;     // computed from rental_duration

    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public Integer getOpenRentalId() { return openRentalId; }
    public void setOpenRentalId(Integer openRentalId) { this.openRentalId = openRentalId; }
    public Short getCustomerId() { return customerId; }
    public void setCustomerId(Short customerId) { this.customerId = customerId; }
    public LocalDateTime getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDateTime rentalDate) { this.rentalDate = rentalDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}