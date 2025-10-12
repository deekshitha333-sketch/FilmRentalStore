package com.example.filmRental.Mapper;

import com.example.filmRental.Dto.StaffResponseDto;

import java.util.Map;

public final class StaffMapper {
    private StaffMapper() { }

    public static StaffResponseDto mapRow(Map<String, Object> m) {
        Short staffId = ((Number) m.get("staff_id")).shortValue();
        String firstName = (String) m.get("first_name");
        String lastName  = (String) m.get("last_name");
        String email     = (String) m.get("email");
        Short storeId    = ((Number) m.get("store_id")).shortValue();
        Short addressId  = ((Number) m.get("address_id")).shortValue();
        String address   = (String) m.get("address");
        String city      = (String) m.get("city");
        String country   = (String) m.get("country");
        String phone     = (String) m.get("phone");
        String picture   = (String) m.get("picture"); // now URL string

        return new StaffResponseDto(staffId, firstName, lastName, email, storeId,
                addressId, address, city, country, phone, picture);
    }
}
