package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.impl.StoreServiceImpl;
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

/**
 * Pure unit tests for StoreServiceImpl by mocking NamedParameterJdbcTemplate.
 */
@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private NamedParameterJdbcTemplate jdbc;

    private StoreService service;

    @BeforeEach
    void setup() {
        service = new StoreServiceImpl(jdbc);
    }

    @Test
    @DisplayName("create(): inserts store and returns hydrated StoreResponseDto")
    void create_ok() {
        StoreCreateRequestDto req = new StoreCreateRequestDto((short) 1, (short) 2);

        // exists(staff, ...) => 1
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // exists(address, ...) => 1
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM address"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // manager already assigned? => 0
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store WHERE manager_staff_id"), anyMap(), eq(Long.class)))
                .thenReturn(0L);

        // INSERT store: set generated key (store_id = 5)
        doAnswer(inv -> {
            KeyHolder kh = inv.getArgument(2);
            // Simulate JDBC-generated key name "store_id"
            Map<String, Object> keyRow = new HashMap<>();
            keyRow.put("store_id", Short.valueOf((short)5));
            // Populate KeyHolder
            kh.getKeyList().add(keyRow);
            return 1; // rows updated
        }).when(jdbc).update(anyString(), any(MapSqlParameterSource.class), any(KeyHolder.class), any(String[].class));

        // UPDATE staff.store_id (consistency) => 1 row
        when(jdbc.update(startsWith("UPDATE staff SET store_id"), anyMap())).thenReturn(1);

        // fetchStore: return one row for store_id=5
        Map<String,Object> storeRow = new HashMap<>();
        storeRow.put("store_id", (short)5);
        storeRow.put("manager_staff_id", (short)1);
        storeRow.put("address_id", (short)2);
        storeRow.put("address", "47 MySakila Drive");
        storeRow.put("city", "A Coruna (La Coruna)");
        storeRow.put("country", "Spain");
        storeRow.put("phone", "044-123456");

        when(jdbc.queryForList(startsWith("SELECT st.store_id"), anyMap()))
                .thenReturn(List.of(storeRow));

        StoreResponseDto dto = service.create(req);
        assertEquals((short)5, dto.getStoreId());
        assertEquals("Spain", dto.getCountry());
        assertEquals("044-123456", dto.getPhone());
    }

    @Test
    @DisplayName("create(): 404 when manager staff missing")
    void create_managerMissing() {
        StoreCreateRequestDto req = new StoreCreateRequestDto((short) 10, (short) 2);

        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(0L); // manager not found

        assertThrows(NotFoundException.class, () -> service.create(req));
    }

    @Test
    @DisplayName("assignAddress(): ok → updates and returns StoreResponseDto")
    void assignAddress_ok() {
        // store exists
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // address exists
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM address"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.update(startsWith("UPDATE store SET address_id"), anyMap()))
                .thenReturn(1);

        Map<String,Object> row = new HashMap<>();
        row.put("store_id", (short)5);
        row.put("manager_staff_id", (short)1);
        row.put("address_id", (short)3);
        row.put("address", "28 MySQL Boulevard");
        row.put("city", "QLD");
        row.put("country", "Australia");
        row.put("phone", "044-123456");

        when(jdbc.queryForList(startsWith("SELECT st.store_id"), anyMap()))
                .thenReturn(List.of(row));

        StoreResponseDto dto = service.assignAddress((short)5, (short)3);
        assertEquals((short)3, dto.getAddressId());
        assertEquals("Australia", dto.getCountry());
    }

    @Test
    @DisplayName("assignManager(): 400 when manager already assigned elsewhere")
    void assignManager_conflict() {
        // store exists
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store WHERE store_id"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // staff exists
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        // others count > 0
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store WHERE manager_staff_id ="), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        assertThrows(BadRequestException.class, () -> service.assignManager((short)5, (short)1));
    }

    @Test
    @DisplayName("updatePhone(): 404 when store not found")
    void updatePhone_notFound() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(0L);

        assertThrows(NotFoundException.class, () -> service.updatePhone((short)999, "044-999999"));
    }

    @Test
    @DisplayName("findByPhone(): returns list")
    void findByPhone_ok() {
        Map<String,Object> row = new HashMap<>();
        row.put("store_id", (short)1);
        row.put("manager_staff_id", (short)1);
        row.put("address_id", (short)1);
        row.put("address", "47 MySakila Drive");
        row.put("city", "A Coruna (La Coruna)");
        row.put("country", "Spain");
        row.put("phone", "044-777777");

        when(jdbc.queryForList(startsWith("SELECT st.store_id"), anyMap()))
                .thenReturn(List.of(row));

        var list = service.findByPhone("044-777777");
        assertEquals(1, list.size());
        assertEquals("Spain", list.get(0).getCountry());
    }

    @Test
    @DisplayName("staffByStore(): 404 when store missing")
    void staffByStore_notFound() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(0L);

        assertThrows(NotFoundException.class, () -> service.staffByStore((short)9));
    }

    @Test
    @DisplayName("managers(): returns list of manager + store details")
    void managers_ok() {
        Map<String,Object> row = new HashMap<>();
        row.put("store_id", (short)1);
        row.put("fn", "Mike");
        row.put("ln", "Hillyer");
        row.put("em", "mike@example.com");
        row.put("ph", "111-111");
        row.put("addr", "47 MySakila Drive");
        row.put("city", "A Coruna (La Coruna)");

        when(jdbc.queryForList(startsWith("SELECT st.store_id, mgr.first_name"), anyMap()))
                .thenReturn(List.of(row));

        var list = service.managers();
        assertEquals(1, list.size());
        assertEquals("Mike", list.get(0).getManagerFirstName());
        assertEquals((short)1, list.get(0).getStoreId());
    }
}