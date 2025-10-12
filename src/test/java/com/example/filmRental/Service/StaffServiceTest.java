package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.StaffRepository;
import com.example.filmRental.Service.impl.StaffServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock private NamedParameterJdbcTemplate jdbc;
    @Mock private StaffRepository repo;

    private StaffService service;

    @BeforeEach void init() { service = new StaffServiceImpl(jdbc, repo); }

    @Test
    @DisplayName("create(): inserts and reads back")
    void create_ok() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM address"), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        // simulate insert returning staff_id = 7
        doAnswer(inv -> {
            var kh = inv.getArgument(2, org.springframework.jdbc.support.KeyHolder.class);
            kh.getKeyList().add(Map.of("staff_id", 7)); // single pair => OK with Map.of
            return 1;
        }).when(jdbc).update(startsWith("INSERT INTO staff"), any(), any(), any());

        // Build the result row without Map.of(...)'s 10-entry limit
        Map<String, Object> row = new java.util.HashMap<>();
        row.put("staff_id", 7);
        row.put("first_name", "Al");
        row.put("last_name", "Green");
        row.put("email", "a@x");
        row.put("store_id", 1);
        row.put("address_id", 1);
        row.put("address", "addr");
        row.put("city", "cty");
        row.put("country", "co");
        row.put("phone", "ph");
        row.put("picture", "url");

        when(jdbc.queryForList(startsWith("SELECT s.staff_id"), anyMap()))
                .thenReturn(List.of(row));

        StaffCreateRequestDto req = new StaffCreateRequestDto("Al", "Green", "a@x", (short) 1, (short) 1, "url");
        StaffResponseDto dto = service.create(req);
        assertEquals(7, dto.getStaffId().intValue());
        assertEquals("Al", dto.getFirstName());
    }


    @Test @DisplayName("updatePhone(): 404 when staff missing")
    void updatePhone_404() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class))).thenReturn(0L);
        assertThrows(NotFoundException.class, () -> service.updatePhone((short)99, new StaffUpdatePhoneRequestDto("x")));
    }
}
