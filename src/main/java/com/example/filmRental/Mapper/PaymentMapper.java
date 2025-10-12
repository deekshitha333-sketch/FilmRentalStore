package com.example.filmRental.Mapper;

import com.example.filmRental.Dto.PaymentCreateRequestDto;
import com.example.filmRental.Dto.PaymentResponseDto;
import com.example.filmRental.Entity.Payment;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public final class PaymentMapper {
    private PaymentMapper() { }

    public static Payment toEntity(PaymentCreateRequestDto req) {
        Payment p = new Payment();
        p.setCustomer_id(req.getCustomerId());
        p.setStaff_id(req.getStaffId());
        p.setRental_id(req.getRentalId());
        p.setAmount(req.getAmount());
        LocalDateTime when = req.getPaymentDate() != null ? req.getPaymentDate() : LocalDateTime.now();
        p.setPayment_date(when);
        p.setLast_update(Timestamp.from(Instant.now()));
        return p;
    }

    public static PaymentResponseDto toDto(Payment p) {
        return new PaymentResponseDto(
                p.getPayment_id(),
                p.getCustomer_id(),
                p.getStaff_id(),
                p.getRental_id(),
                p.getAmount(),
                p.getPayment_date()
        );
    }
}