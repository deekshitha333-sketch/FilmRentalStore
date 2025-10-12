package com.example.filmRental.Dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CustomerResponseDto {
    private Short id;
    private Short storeId;
    private String firstName;
    private String lastName;
    private String email;
    private Short addressId;
    private Boolean active;
    private LocalDateTime createDate;
    private Timestamp lastUpdate;

    public CustomerResponseDto() {}

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Short getAddressId() { return addressId; }
    public void setAddressId(Short addressId) { this.addressId = addressId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }
    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }
}