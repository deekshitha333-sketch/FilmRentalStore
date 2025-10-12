package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import java.util.List;

public interface StaffService {
    StaffResponseDto create(StaffCreateRequestDto request);

    // updates
    StaffResponseDto updateEmail(short staffId, StaffUpdateEmailRequestDto req);
    StaffResponseDto updatePhone(short staffId, StaffUpdatePhoneRequestDto req);
    StaffResponseDto updateFirstName(short staffId, StaffUpdateFirstNameRequestDto req);
    StaffResponseDto updateLastName(short staffId, StaffUpdateLastNameRequestDto req);
    StaffResponseDto assignAddress(short staffId, short addressId);
    StaffResponseDto assignStore(short staffId, StaffAssignStoreRequestDto req);

    // searches
    List<StaffResponseDto> findByFirstName(String firstName);
    List<StaffResponseDto> findByLastName(String lastName);
    List<StaffResponseDto> findByEmail(String email);
    StaffResponseDto findByPhone(String phone); // per rubric returns Staff with Address
    List<StaffResponseDto> findByCity(String city);
    List<StaffResponseDto> findByCountry(String country);
}
