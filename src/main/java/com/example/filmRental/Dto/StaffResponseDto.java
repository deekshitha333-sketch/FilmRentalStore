package com.example.filmRental.Dto;

public class StaffResponseDto {
    private Short staffId;
    private String firstName;
    private String lastName;
    private String email;
    private Short storeId;
    private Short addressId;
    private String address;
    private String city;
    private String country;
    private String phone;
    private String pictureUrl;

    public StaffResponseDto() { }

    public StaffResponseDto(Short staffId, String firstName, String lastName, String email,
                            Short storeId, Short addressId, String address, String city,
                            String country, String phone, String pictureUrl) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.storeId = storeId;
        this.addressId = addressId;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.pictureUrl = pictureUrl;
    }
    public Short getStaffId() { return staffId; }
    public void setStaffId(Short v) { this.staffId = v; }
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
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getCountry() { return country; }
    public void setCountry(String v) { this.country = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String v) { this.pictureUrl = v; }
}
