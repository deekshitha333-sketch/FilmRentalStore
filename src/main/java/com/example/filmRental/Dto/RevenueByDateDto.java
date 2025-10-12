package com.example.filmRental.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Date-wise revenue (all stores or a store)
public class RevenueByDateDto {
    private LocalDate date;
    private BigDecimal amount;

    public RevenueByDateDto() { }
    public RevenueByDateDto(LocalDate date, BigDecimal amount) {
        this.date = date; this.amount = amount;
    }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}