package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.NotFoundException;
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

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock NamedParameterJdbcTemplate jdbc;
    @InjectMocks StoreServiceImpl service;

    @Test
    void create_ok_inserts_store_and_aligns_manager_storeId() {
        // exists(staff)=true, exists(address)=true
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class))).thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM address"), anyMap(), eq(Long.class))).thenReturn(1L);
        // manager uniqueness count = 0
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store WHERE manager_staff_id"), anyMap(), eq(Long.class)))
                .thenReturn(0L);
        // insert store -> set generated key
        when(jdbc.update(anyString(), any(MapSqlParameterSource.class), any(KeyHolder.class), any(String[].class)))
                .thenAnswer(inv -> {
                    KeyHolder kh = inv.getArgument(2);
                    kh.getKeyList().add(Map.of("store_id", 1));
                    return 1;
                });
        // update staff.store_id (alignment)
        when(jdbc.update(startsWith("UPDATE staff SET store_id = :sid"), anyMap())).thenReturn(1);

        // fetchStore read-back
        when(jdbc.queryForList(startsWith("SELECT st.store_id"), anyMap()))
                .thenReturn(List.of(Map.of(
                        "store_id", 1, "manager_staff_id", 2, "address_id", 1,
                        "address", "47 MySakila Drive", "city", "Lethbridge", "country", "Canada", "phone", ""
                )));

        StoreResponseDto dto = service.create(new StoreCreateRequestDto((short)2, (short)1));
        assertThat(dto.getStoreId()).isEqualTo((short)1);
        assertThat(dto.getManagerStaffId()).isEqualTo((short)2);
    }

    @Test
    void create_manager_already_assigned_conflict() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM staff"), anyMap(), eq(Long.class))).thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM address"), anyMap(), eq(Long.class))).thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store WHERE manager_staff_id"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        assertThatThrownBy(() -> service.create(new StoreCreateRequestDto((short)2,(short)1)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void findByCity_returns_rows() {
        when(jdbc.queryForList(startsWith("SELECT st.store_id"), anyMap()))
                .thenReturn(List.of(Map.of(
                        "store_id", 1, "manager_staff_id", 2, "address_id", 1,
                        "address", "47 MySakila Drive", "city", "Lethbridge", "country", "Canada", "phone", ""
                )));
        List<StoreResponseDto> list = service.findByCity("Lethbridge");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getCity()).isEqualTo("Lethbridge");
    }

    @Test
    void customersByStore_404_when_store_missing() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class))).thenReturn(0L);
        assertThatThrownBy(() -> service.customersByStore((short)5))
                .isInstanceOf(NotFoundException.class);
    }
}
