package com.example.filmRental.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StaffCreateRequestDto {
    @NotBlank @Size(max = 45)
    private String firstName;

    @NotBlank @Size(max = 45)
    private String lastName;

    @Email @Size(max = 50)
    private String email; // optional

    @NotNull @Min(1)
    private Short storeId;

    @NotNull @Min(1)
    private Short addressId;

    // pictureUrl is optional; per rubric, picture is now a URL (VARCHAR)
    @Size(max = 255)
    private String pictureUrl;

    public StaffCreateRequestDto() { }

    public StaffCreateRequestDto(String firstName, String lastName, String email,
                                 Short storeId, Short addressId, String pictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.storeId = storeId;
        this.addressId = addressId;
        this.pictureUrl = pictureUrl;
    }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public Short getStoreId() { return storeId; }
    public void setStoreId(Short v) { this.storeId = v; }
    public Short getAddressId() { return addressId; }
    public void setAddressId(Short v) { this.addressId = v; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String v) { this.pictureUrl = v; }
}
