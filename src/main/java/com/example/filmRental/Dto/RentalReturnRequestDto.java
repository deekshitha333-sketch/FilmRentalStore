// src/main/java/com/example/filmRental/Dto/RentalReturnRequestDto.java
package com.example.filmRental.Dto;

import java.time.LocalDateTime;

public class RentalReturnRequestDto {
    // optional override; if null, service sets now()
    private LocalDateTime returnDate;

    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
}