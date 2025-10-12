package com.example.filmRental.Dto;
import java.math.BigDecimal;
public class DateAmountDto {
    private String date; // yyyy-MM-dd
    private BigDecimal amount;
    public DateAmountDto() {}
    public DateAmountDto(String date, BigDecimal amount) { this.date = date; this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
