// File: src/main/java/com/example/filmRental/Dto/ChangePasswordRequestDto.java
package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequestDto {
    @NotBlank @Size(min = 6, max = 64)
    private String newPassword;

    public ChangePasswordRequestDto() { }
    public ChangePasswordRequestDto(String newPassword) { this.newPassword = newPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String v) { this.newPassword = v; }
}
