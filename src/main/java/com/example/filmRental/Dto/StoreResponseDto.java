package com.example.filmRental.Dto;

public class StoreResponseDto {
    private Short storeId;
    private Short managerStaffId;
    private Short addressId;
    private String address;
    private String city;
    private String country;
    private String phone;

    public StoreResponseDto() { }
    public StoreResponseDto(Short storeId, Short managerStaffId, Short addressId,
                            String address, String city, String country, String phone) {
        this.storeId = storeId; this.managerStaffId = managerStaffId; this.addressId = addressId;
        this.address = address; this.city = city; this.country = country; this.phone = phone;
    }

    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public Short getManagerStaffId() { return managerStaffId; }
    public void setManagerStaffId(Short managerStaffId) { this.managerStaffId = managerStaffId; }
    public Short getAddressId() { return addressId; }
    public void setAddressId(Short addressId) { this.addressId = addressId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
