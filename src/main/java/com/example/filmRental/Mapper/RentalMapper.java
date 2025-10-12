// src/main/java/com/example/filmRental/Mapper/RentalMapper.java
package com.example.filmRental.Mapper;

import com.example.filmRental.Dto.RentalCreateRequestDto;
import com.example.filmRental.Dto.RentalResponseDto;
import com.example.filmRental.Entity.Rental;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public final class RentalMapper {

    private RentalMapper() {}

    public static Rental toEntity(RentalCreateRequestDto req) {
        Rental r = new Rental();
        // If your entity uses camelCase, rename to setInventoryId / setCustomerId / setStaffId / setRentalDate, etc.
        r.setInventory_id(req.getInventoryId());
        r.setCustomer_id(req.getCustomerId());
        r.setStaff_id(req.getStaffId());
        LocalDateTime rd = req.getRentalDate() != null ? req.getRentalDate() : LocalDateTime.now();
        r.setRental_date(rd);
        r.setLast_update(Timestamp.from(Instant.now()));
        return r;
    }

    public static void applyReturnDate(Rental r, LocalDateTime value) {
        LocalDateTime when = (value != null ? value : LocalDateTime.now());
        r.setReturn_date(when);
        r.setLast_update(Timestamp.from(Instant.now()));
    }

    public static RentalResponseDto toDto(Rental r) {
        RentalResponseDto dto = new RentalResponseDto();
        dto.setId(r.getRental_id());
        dto.setRentalDate(r.getRental_date());
        dto.setReturnDate(r.getReturn_date());
        dto.setInventoryId(r.getInventory_id());
        dto.setCustomerId(r.getCustomer_id());
        dto.setStaffId(r.getStaff_id());
        return dto;
    }
}
