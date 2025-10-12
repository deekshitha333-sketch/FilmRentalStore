package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponseDto create(CustomerRequestDto request);

    PagedResponse<CustomerResponseDto> active(Pageable pageable);

    PagedResponse<CustomerResponseDto> inactive(Pageable pageable);

    PagedResponse<CustomerResponseDto> byLastName(String ln, Pageable pageable);

    PagedResponse<CustomerResponseDto> byFirstName(String fn, Pageable pageable);

    CustomerResponseDto byEmail(String email);

    PagedResponse<CustomerResponseDto> byPhone(String phone, Pageable pageable);

    PagedResponse<CustomerWithAddressResponseDto> byCity(String city, Pageable pageable);

    PagedResponse<CustomerWithAddressResponseDto> byCountry(String country, Pageable pageable);

    CustomerResponseDto updateEmail(short id, String email);

    CustomerResponseDto updatePhone(short id, String phone); // updates address of the customer

    CustomerResponseDto updateStore(short id, short storeId);

    CustomerResponseDto updateAddress(short id, short addressId);

    CustomerResponseDto updateFirstName(short id, String value);

    CustomerResponseDto updateLastName(short id, String value);
}