package com.example.filmRental.Dto;

public class StoreManagerDetailsDto {
    private Short storeId;
    private String managerFirstName;
    private String managerLastName;
    private String managerEmail;
    private String managerPhone;
    private String storeAddress;
    private String city;

    public StoreManagerDetailsDto() { }
    public StoreManagerDetailsDto(Short storeId, String managerFirstName, String managerLastName,
                                  String managerEmail, String managerPhone, String storeAddress, String city) {
        this.storeId = storeId; this.managerFirstName = managerFirstName; this.managerLastName = managerLastName;
        this.managerEmail = managerEmail; this.managerPhone = managerPhone; this.storeAddress = storeAddress; this.city = city;
    }

    public Short getStoreId() { return storeId; }
    public void setStoreId(Short storeId) { this.storeId = storeId; }
    public String getManagerFirstName() { return managerFirstName; }
    public void setManagerFirstName(String managerFirstName) { this.managerFirstName = managerFirstName; }
    public String getManagerLastName() { return managerLastName; }
    public void setManagerLastName(String managerLastName) { this.managerLastName = managerLastName; }
    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
    public String getManagerPhone() { return managerPhone; }
    public void setManagerPhone(String managerPhone) { this.managerPhone = managerPhone; }
    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}