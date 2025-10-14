package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Repository.StaffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StaffServiceImpl search/update flows.
 * StaffMapper.mapRow expects keys:
 *  staff_id, first_name, last_name, email, store_id, address_id, address, city, country, phone, picture
 */
@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock private NamedParameterJdbcTemplate jdbc;
    @Mock private StaffRepository staffRepository; // not used by impl for CRUD in current code, but injected
    @InjectMocks private StaffServiceImpl service;

    private static Map<String,Object> row(
            Object id, String fn, String ln, String email,
            Object storeId, Object addressId, String addr,
            String city, String country, String phone, String picture) {
        Map<String,Object> m = new HashMap<>();
        m.put("staff_id", id);
        m.put("first_name", fn);
        m.put("last_name", ln);
        m.put("email", email);
        m.put("store_id", storeId);
        m.put("address_id", addressId);
        m.put("address", addr);
        m.put("city", city);
        m.put("country", country);
        m.put("phone", phone);
        m.put("picture", picture);
        return m;
    }

    // ---------- Search APIs (BASE_SELECT + WHERE ...) ----------

    @Test
    void findByFirst_last_email_phone_city_country_map_correctly() {
        // All finders use BASE_SELECT starting with "SELECT s.staff_id"
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)1,"A","B","a@b.com",(short)1,(short)1,"Addr","City","Country","999","pic")));

        assertThat(service.findByFirstName("A")).hasSize(1);
        assertThat(service.findByLastName("B")).hasSize(1);
        assertThat(service.findByEmail("a@b.com")).hasSize(1);
        assertThat(service.findByCity("City")).hasSize(1);
        assertThat(service.findByCountry("Country")).hasSize(1);

        // For byPhone, impl returns a single StaffResponseDto via oneOr404(...)
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)2,"X","Y","x@y.com",(short)1,(short)1,"Addr2","City2","Country2","555","pic2")));
        StaffResponseDto byPhone = service.findByPhone("555");
        assertThat(byPhone.getPhone()).isEqualTo("555");
        assertThat(byPhone.getFirstName()).isEqualTo("X");
    }

    // ---------- Create (FK checks + insert + read-back) ----------

    @Test
    void create_ok_inserts_and_reads_back() {
        // exists(store)=true, exists(address)=true
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM address"), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        // INSERT with KeyHolder -> populate generated staff_id
        when(jdbc.update(anyString(), any(MapSqlParameterSource.class), any(KeyHolder.class), any(String[].class)))
                .thenAnswer(inv -> {
                    KeyHolder kh = inv.getArgument(2);
                    // Put the generated key into KeyHolder
                    kh.getKeyList().add(Map.of("staff_id", 7));
                    return 1;
                });

        // Read-back row (BASE_SELECT ... WHERE s.staff_id = :id)
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)7,"F","L","f@l.com",(short)1,(short)1,"Addr","City","Country","14033335568","http://pic")));

        StaffCreateRequestDto req = new StaffCreateRequestDto("F","L","f@l.com",(short)1,(short)1,"http://pic");
        StaffResponseDto created = service.create(req);

        assertThat(created.getStaffId()).isEqualTo((short)7);
        assertThat(created.getEmail()).isEqualTo("f@l.com");
        assertThat(created.getAddress()).isEqualTo("Addr");
    }

    // ---------- Update Email ----------

    @Test
    void updateEmail_updates_and_reads_back() {
        // exists(staff)=true
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // UPDATE staff SET email = ...
        when(jdbc.update(startsWith("UPDATE staff SET email"), anyMap())).thenReturn(1);
        // Read-back
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)7,"F","L","new@mail.com",(short)1,(short)1,"Addr","City","Country","777","pic")));

        StaffResponseDto dto = service.updateEmail((short)7, new StaffUpdateEmailRequestDto("new@mail.com"));
        assertThat(dto.getEmail()).isEqualTo("new@mail.com");
    }

    // ---------- Update Phone (needs address_id lookup) ----------

    @Test
    void updatePhone_updates_address_and_reads_back() {
        // exists(staff)=true
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // SELECT address_id FROM staff WHERE staff_id = :id
        when(jdbc.queryForObject(startsWith("SELECT address_id FROM staff"), anyMap(), eq(Short.class)))
                .thenReturn((short)5);
        // UPDATE address SET phone = :ph ...
        when(jdbc.update(startsWith("UPDATE address SET phone"), anyMap())).thenReturn(1);
        // Read-back
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)7,"F","L","f@l.com",(short)1,(short)5,"Addr","City","Country","777","pic")));

        StaffResponseDto dto = service.updatePhone((short)7, new StaffUpdatePhoneRequestDto("777"));
        assertThat(dto.getPhone()).isEqualTo("777");
    }

    // ---------- Update First Name ----------

    @Test
    void updateFirstName_updates_and_reads_back() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.update(startsWith("UPDATE staff SET first_name"), anyMap())).thenReturn(1);
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)7,"NF","L","f@l.com",(short)1,(short)1,"Addr","City","Country","999","pic")));

        StaffResponseDto dto = service.updateFirstName((short)7, new StaffUpdateFirstNameRequestDto("NF"));
        assertThat(dto.getFirstName()).isEqualTo("NF");
    }

    // ---------- Update Last Name ----------

    @Test
    void updateLastName_updates_and_reads_back() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.update(startsWith("UPDATE staff SET last_name"), anyMap())).thenReturn(1);
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)7,"F","NL","f@l.com",(short)1,(short)1,"Addr","City","Country","999","pic")));

        StaffResponseDto dto = service.updateLastName((short)7, new StaffUpdateLastNameRequestDto("NL"));
        assertThat(dto.getLastName()).isEqualTo("NL");
    }

    // ---------- Assign Address (FK checks) ----------

    @Test
    void assignAddress_updates_and_reads_back() {
        // exists(staff)=true, exists(address)=true
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM address"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // UPDATE staff SET address_id = ...
        when(jdbc.update(startsWith("UPDATE staff SET address_id"), anyMap())).thenReturn(1);
        // Read-back
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)7,"F","L","f@l.com",(short)1,(short)5,"Addr","City","Country","999","pic")));

        StaffResponseDto dto = service.assignAddress((short)7, (short)5);
        assertThat(dto.getAddressId()).isEqualTo((short)5);
    }

    // ---------- Assign Store (FK checks) ----------

    @Test
    void assignStore_updates_and_reads_back() {
        // exists(staff)=true, exists(store)=true
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // UPDATE staff SET store_id = ...
        when(jdbc.update(startsWith("UPDATE staff SET store_id"), anyMap())).thenReturn(1);
        // Read-back
        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row((short)7,"F","L","f@l.com",(short)2,(short)1,"Addr","City","Country","999","pic")));

        StaffResponseDto dto = service.assignStore((short)7, new StaffAssignStoreRequestDto((short)2));
        assertThat(dto.getStoreId()).isEqualTo((short)2);
    }
}