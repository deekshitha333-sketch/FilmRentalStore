package com.example.filmRental.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActorRequestDto {
    @NotBlank(message = "firstName is required")
    @Size(max = 45, message = "firstName must be at most 45 characters")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Size(max = 45, message = "lastName must be at most 45 characters")
    private String lastName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
