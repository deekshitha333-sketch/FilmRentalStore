package com.example.filmRental.Dto;

import java.time.OffsetDateTime;

public class RentalDueDto {
    private Integer rentalId;
    private Short storeId;
    private Integer customerId;
    private String customerName;
    private Integer filmId;
    private String title;
    private String rentalDate; // ISO string for portability
    private Integer daysOutstanding;

    public RentalDueDto() {}

    public RentalDueDto(Integer rentalId, Short storeId, Integer customerId, String customerName,
                        Integer filmId, String title, String rentalDate, Integer daysOutstanding) {
        this.rentalId = rentalId;
        this.storeId = storeId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.filmId = filmId;
        this.title = title;
        this.rentalDate = rentalDate;
        this.daysOutstanding = daysOutstanding;
    }

    public Integer getRentalId() { return rentalId; }
    public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRentalDate() { return rentalDate; }
    public void setRentalDate(String rentalDate) { this.rentalDate = rentalDate; }
    public Integer getDaysOutstanding() { return daysOutstanding; }
    public void setDaysOutstanding(Integer daysOutstanding) { this.daysOutstanding = daysOutstanding; }
}
