package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import java.util.List;

public interface StoreService {
    StoreResponseDto create(StoreCreateRequestDto request);
    StoreResponseDto assignAddress(short storeId, short addressId);
    StoreResponseDto assignManager(short storeId, short managerStaffId);
    StoreResponseDto updatePhone(short storeId, String phone);

    List<StoreResponseDto> findByPhone(String phone);
    List<StoreResponseDto> findByCity(String city);
    List<StoreResponseDto> findByCountry(String country);

    List<StaffSummaryDto> staffByStore(short storeId);
    List<CustomerSummaryDto> customersByStore(short storeId);
    List<StoreManagerDetailsDto> managers();
}
