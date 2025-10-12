package com.example.filmRental.Mapper;

import com.example.filmRental.Dto.CustomerRequestDto;
import com.example.filmRental.Dto.CustomerResponseDto;
import com.example.filmRental.Dto.CustomerWithAddressResponseDto;
import com.example.filmRental.Entity.Customer;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public final class CustomerMapper {
    private CustomerMapper() {}

    public static Customer toEntity(CustomerRequestDto dto) {
        Customer c = new Customer();
        c.setStore_id(dto.getStoreId());
        c.setFirst_name(dto.getFirstName());
        c.setLast_name(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setAddress_id(dto.getAddressId());
        c.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
        c.setCreate_date(LocalDateTime.now());
        c.setLast_update(Timestamp.from(Instant.now()));
        return c;
    }

    public static void applyFirstName(Customer c, String value) {
        c.setFirst_name(value);
        c.setLast_update(Timestamp.from(Instant.now()));
    }

    public static void applyLastName(Customer c, String value) {
        c.setLast_name(value);
        c.setLast_update(Timestamp.from(Instant.now()));
    }

    public static void applyEmail(Customer c, String email) {
        c.setEmail(email);
        c.setLast_update(Timestamp.from(Instant.now()));
    }

    public static void applyStore(Customer c, short storeId) {
        c.setStore_id(storeId);
        c.setLast_update(Timestamp.from(Instant.now()));
    }

    public static void applyAddress(Customer c, short addressId) {
        c.setAddress_id(addressId);
        c.setLast_update(Timestamp.from(Instant.now()));
    }

    public static CustomerResponseDto toDto(Customer c) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(c.getCustomer_id());
        dto.setStoreId(c.getStore_id());
        dto.setFirstName(c.getFirst_name());
        dto.setLastName(c.getLast_name());
        dto.setEmail(c.getEmail());
        dto.setAddressId(c.getAddress_id());
        dto.setActive(c.getActive());
        dto.setCreateDate(c.getCreate_date());
        dto.setLastUpdate(c.getLast_update());
        return dto;
    }

    // Mapping from a row map (native query)
    public static CustomerWithAddressResponseDto rowToWithAddressDto(Map<String, Object> row) {
        CustomerWithAddressResponseDto dto = new CustomerWithAddressResponseDto();
        dto.setId(((Number)row.get("customer_id")).shortValue());
        dto.setFirstName((String) row.get("first_name"));
        dto.setLastName((String) row.get("last_name"));
        dto.setEmail((String) row.get("email"));
        dto.setActive(((Number)row.get("active")).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE);
        dto.setAddress((String) row.get("address"));
        dto.setDistrict((String) row.get("district"));
        dto.setCity((String) row.get("city"));
        dto.setCountry((String) row.get("country"));
        dto.setPhone((String) row.get("phone"));
        return dto;
    }
}