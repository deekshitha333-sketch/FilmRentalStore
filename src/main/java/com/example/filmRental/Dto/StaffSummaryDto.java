package com.example.filmRental.Dto;

public class StaffSummaryDto {
    private Short staffId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public StaffSummaryDto() { }
    public StaffSummaryDto(Short staffId, String firstName, String lastName, String email, String phone) {
        this.staffId = staffId; this.firstName = firstName; this.lastName = lastName; this.email = email; this.phone = phone;
    }
    public Short getStaffId() { return staffId; }
    public void setStaffId(Short staffId) { this.staffId = staffId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
