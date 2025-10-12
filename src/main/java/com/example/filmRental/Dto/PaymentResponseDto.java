package com.example.filmRental.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDto {
    private Integer id;
    private Short customerId;
    private Short staffId;
    private Integer rentalId; // can be null
    private BigDecimal amount;
    private LocalDateTime paymentDate;

    public PaymentResponseDto() { }

    public PaymentResponseDto(Integer id, Short customerId, Short staffId,
                              Integer rentalId, BigDecimal amount, LocalDateTime paymentDate) {
        this.id = id;
        this.customerId = customerId;
        this.staffId = staffId;
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Short getCustomerId() { return customerId; }
    public void setCustomerId(Short customerId) { this.customerId = customerId; }
    public Short getStaffId() { return staffId; }
    public void setStaffId(Short staffId) { this.staffId = staffId; }
    public Integer getRentalId() { return rentalId; }
    public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}