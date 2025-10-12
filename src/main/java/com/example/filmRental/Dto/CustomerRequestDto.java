package com.example.filmRental.Dto;

import jakarta.validation.constraints.*;

public class CustomerRequestDto {
    @NotNull(message = "storeId is required")
    @Min(value = 1, message = "storeId must be >= 1")
    private Short storeId;

    @NotBlank(message = "firstName is required")
    @Size(max = 45, message = "firstName max 45 chars")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Size(max = 45, message = "lastName max 45 chars")
    private String lastName;

    @Email(message = "email must be valid")
    @Size(max = 50, message = "email max 50 chars")
    private String email;

    @NotNull(message = "addressId is required")
    @Min(value = 1, message = "addressId must be >= 1")
    private Short addressId;

    private Boolean active = Boolean.TRUE;

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
}