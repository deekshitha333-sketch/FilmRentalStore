// File: src/main/java/com/example/filmRental/Dto/SignupRequestDto.java
package com.example.filmRental.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignupRequestDto {
    @NotNull @Min(1)
    private Short staffId;

    @NotBlank @Size(min = 4, max = 64)
    private String username;

    @NotBlank @Size(min = 6, max = 64)
    private String password;

    public SignupRequestDto() { }

    public SignupRequestDto(Short staffId, String username, String password) {
        this.staffId = staffId; this.username = username; this.password = password;
    }
    public Short getStaffId() { return staffId; }
    public void setStaffId(Short v) { this.staffId = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
}