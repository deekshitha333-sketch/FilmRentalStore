package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.RentalDueDto;
import com.example.filmRental.Exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalDashboardServiceImplTest {

    @Mock NamedParameterJdbcTemplate jdbc;
    @InjectMocks RentalDashboardServiceImpl service;

    @Test
    void dueByStore_ok_maps_aliases() {
        // exists("store", "store_id", :id)
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        Map<String,Object> row = new HashMap<>();
        row.put("rid", 101);
        row.put("sid", 1);
        row.put("cid", 7);
        row.put("cname", "MARY SMITH");
        row.put("fid", 55);
        row.put("title", "ACADEMY DINOSAUR");
        row.put("rdate", "2025-01-01 10:15:00");
        row.put("days_outstanding", 3);

        when(jdbc.queryForList(startsWith("SELECT r.rental_id rid"), anyMap()))
                .thenReturn(List.of(row));

        List<RentalDueDto> items = service.dueByStore((short)1);
        assertThat(items).singleElement().satisfies(d -> {
            assertThat(d.getRentalId()).isEqualTo(101);
            assertThat(d.getStoreId()).isEqualTo((short)1);
            assertThat(d.getCustomerId()).isEqualTo(7);
            assertThat(d.getCustomerName()).isEqualTo("MARY SMITH");
            assertThat(d.getFilmId()).isEqualTo(55);
            assertThat(d.getTitle()).isEqualTo("ACADEMY DINOSAUR");
            assertThat(d.getRentalDate()).isEqualTo("2025-01-01 10:15:00");
            assertThat(d.getDaysOutstanding()).isEqualTo(3);
        });
    }

    @Test
    void dueByStore_no_store_404() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(0L);
        assertThatThrownBy(() -> service.dueByStore((short)9))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Store not found: 9");
        verify(jdbc, never()).queryForList(startsWith("SELECT r.rental_id rid"), anyMap());
    }
}