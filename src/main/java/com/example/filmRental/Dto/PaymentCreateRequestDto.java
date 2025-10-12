package com.example.filmRental.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentCreateRequestDto {
    @NotNull @Min(1)
    private Short customerId;

    @NotNull @Min(1)
    private Short staffId;

    // Optional in DB, but if sent we validate
    private Integer rentalId;

    @NotNull @DecimalMin("0.00")
    private BigDecimal amount;

    // Optional; if null, service sets now()
    private LocalDateTime paymentDate;

    public PaymentCreateRequestDto() { }

    public PaymentCreateRequestDto(Short customerId, Short staffId, Integer rentalId,
                                   BigDecimal amount, LocalDateTime paymentDate) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

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