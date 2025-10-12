package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private NamedParameterJdbcTemplate jdbc;
    private InventoryService service;

    @BeforeEach
    void init() {
        service = new InventoryServiceImpl(jdbc);
    }

    @Test
    @DisplayName("add(): inserts row and returns InventoryResponseDto")
    void add_ok() {
        InventoryAddRequestDto req = new InventoryAddRequestDto(10, (short)1);

        // film exists
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // store exists
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        // simulate insert key inventory_id = 500
        doAnswer(inv -> {
            KeyHolder kh = inv.getArgument(2);
            Map<String, Object> key = new HashMap<>();
            key.put("inventory_id", 500);
            kh.getKeyList().add(key);
            return 1;
        }).when(jdbc).update(anyString(), any(MapSqlParameterSource.class), any(KeyHolder.class), any(String[].class));

        InventoryResponseDto dto = service.add(req);
        assertEquals(500, dto.getInventoryId().intValue());
        assertEquals(10, dto.getFilmId().intValue());
        assertEquals((short)1, dto.getStoreId());
    }

    @Test
    @DisplayName("add(): 404 when film missing")
    void add_filmMissing() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
                .thenReturn(0L);
        assertThrows(NotFoundException.class, () -> service.add(new InventoryAddRequestDto(999, (short)1)));
    }

    @Test
    @DisplayName("allFilmsAllStores(): maps rows to DTOs")
    void allFilmsAllStores_ok() {
        Map<String,Object> r = new HashMap<>();
        r.put("fid", 10); r.put("title", "ACADEMY DINOSAUR"); r.put("copies", 7L);
        when(jdbc.queryForList(startsWith("SELECT f.film_id AS fid"), anyMap()))
                .thenReturn(List.of(r));

        var list = service.allFilmsAllStores();
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).getFilmId().intValue());
        assertEquals(7L, list.get(0).getCopies().longValue());
    }

    @Test
    @DisplayName("filmInStore(): 404 when store missing")
    void filmInStore_storeMissing() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(0L);
        assertThrows(NotFoundException.class, () -> service.filmInStore(10, (short)9));
    }
}