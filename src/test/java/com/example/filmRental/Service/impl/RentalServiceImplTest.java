package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Entity.Inventory;
import com.example.filmRental.Entity.Rental;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.CustomerRepository;
import com.example.filmRental.Repository.InventoryRepository;
import com.example.filmRental.Repository.RentalRepository;
import com.example.filmRental.Repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    private static final short CUSTOMER_ID = 2;
    private static final short STAFF_ID = 3;
    private static final int INVENTORY_ID = 10;

    @Mock private RentalRepository rentalRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private NamedParameterJdbcTemplate jdbc;

    @InjectMocks private RentalServiceImpl service;

    private Pageable pageable;
    private Rental openRental;

    /** Ensure we return an Inventory with its ID set to avoid NPEs in service.create(...) */
    private static Inventory inv(int id) {
        Inventory i = new Inventory();
        i.setInventory_id(id);
        return i;
    }

    @BeforeEach
    void init() {
        pageable = PageRequest.of(0, 10, Sort.by("rental_id").descending());
        openRental = new Rental();
        openRental.setRental_id(1);
        openRental.setInventory_id(INVENTORY_ID);
        openRental.setCustomer_id(CUSTOMER_ID);
        openRental.setStaff_id(STAFF_ID);
        openRental.setRental_date(LocalDateTime.of(2025,1,1,10,0));
        openRental.setReturn_date(null);
    }

    @Test
    void create_ok_validates_FKs_and_availability() {
        // Return an Inventory with ID populated (fixes your NPE)
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inv(INVENTORY_ID)));
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(new com.example.filmRental.Entity.Customer()));
        when(staffRepository.findById(STAFF_ID)).thenReturn(Optional.of(new com.example.filmRental.Entity.Staff()));
        when(rentalRepository.countOpenByInventory(INVENTORY_ID)).thenReturn(0L);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> {
            Rental r = inv.getArgument(0);
            r.setRental_id(123);
            return r;
        });

        RentalResponseDto dto = service.create(new RentalCreateRequestDto(INVENTORY_ID, CUSTOMER_ID, STAFF_ID, null));
        assertThat(dto.getId()).isEqualTo(123);
        verify(rentalRepository).countOpenByInventory(INVENTORY_ID);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void create_when_inventory_missing_404() {
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(new RentalCreateRequestDto(INVENTORY_ID, CUSTOMER_ID, STAFF_ID, null)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Inventory not found");
        verifyNoInteractions(customerRepository, staffRepository, rentalRepository);
    }

    @Test
    void returnRental_sets_returnDate_and_saves() {
        when(rentalRepository.findById(1)).thenReturn(Optional.of(openRental));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        RentalReturnRequestDto body = new RentalReturnRequestDto();
        body.setReturnDate(LocalDateTime.of(2025,1,3,18,0));

        RentalResponseDto dto = service.returnRental(1, body);
        assertThat(dto.getReturnDate()).isEqualTo(LocalDateTime.of(2025,1,3,18,0));
    }

    @Test
    void openByCustomer_and_historyByCustomer_ok() {
        when(rentalRepository.findOpenByCustomer(CUSTOMER_ID, pageable))
                .thenReturn(new PageImpl<>(List.of(openRental), pageable, 1));
        PagedResponse<RentalResponseDto> open = service.openByCustomer(CUSTOMER_ID, pageable);
        assertThat(open.getContent()).hasSize(1);

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(new com.example.filmRental.Entity.Customer()));
        when(rentalRepository.findHistoryByCustomer(CUSTOMER_ID, pageable))
                .thenReturn(new PageImpl<>(List.of(openRental), pageable, 1));
        PagedResponse<RentalResponseDto> hist = service.historyByCustomer(CUSTOMER_ID, pageable);
        assertThat(hist.getContent()).hasSize(1);
    }

    @Test
    void overdueByStore_maps_mysql_projection() {
        Map<String,Object> row = new HashMap<>();
        row.put("rental_id", 1);
        row.put("customer_id", CUSTOMER_ID);
        row.put("inventory_id", INVENTORY_ID);
        row.put("store_id", 1);
        row.put("film_id", 55);
        row.put("title", "ACADEMY DINOSAUR");
        row.put("rental_date", Timestamp.valueOf(LocalDateTime.of(2025,1,1,10,0)));
        row.put("due_date", Timestamp.valueOf(LocalDateTime.of(2025,1,4,10,0)));
        row.put("days_overdue", 2);

        when(jdbc.queryForList(startsWith("SELECT r.rental_id"), anyMap())).thenReturn(List.of(row));
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*)"), anyMap(), eq(Long.class))).thenReturn(1L);

        PagedResponse<RentalOverdueResponseDto> page = service.overdueByStore((short)1, pageable);
        assertThat(page.getContent()).singleElement().satisfies(d -> {
            assertThat(d.getDaysOverdue()).isEqualTo(2);
            assertThat(d.getTitle()).isEqualTo("ACADEMY DINOSAUR");
        });
    }

    @Test
    void statusByInventory_available_branch() {
        // Service checks inventory exists up-front
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inv(INVENTORY_ID)));
        when(rentalRepository.countOpenByInventory(INVENTORY_ID)).thenReturn(0L);

        RentalStatusResponseDto s = service.statusByInventory(INVENTORY_ID);
        assertThat(s.isAvailable()).isTrue();
        assertThat(s.getOpenRentalId()).isNull();
    }

    @Test
    void statusByInventory_unavailable_branch_reads_open_row() {
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inv(INVENTORY_ID)));
        when(rentalRepository.countOpenByInventory(INVENTORY_ID)).thenReturn(1L);

        Map<String,Object> row = new HashMap<>();
        row.put("rental_id", 999);
        row.put("customer_id", CUSTOMER_ID);
        row.put("rental_date", Timestamp.valueOf(LocalDateTime.of(2025,1,1,10,0)));
        row.put("due_date", Timestamp.valueOf(LocalDateTime.of(2025,1,4,10,0)));

        when(jdbc.queryForList(startsWith("SELECT r.rental_id, r.customer_id, r.rental_date"), anyMap()))
                .thenReturn(List.of(row));

        RentalStatusResponseDto s = service.statusByInventory(INVENTORY_ID);
        assertThat(s.isAvailable()).isFalse();
        assertThat(s.getOpenRentalId()).isEqualTo(999);
        assertThat(s.getCustomerId()).isEqualTo(CUSTOMER_ID);
    }

    @Test
    void topTenFilms_all_and_byStore() {
        when(jdbc.queryForList(startsWith("SELECT f.film_id AS film_id"), anyMap()))
                .thenReturn(List.of(Map.of("film_id", 5, "title", "T", "rent_count", 9L)));
        PagedResponse<TopFilmDto> all = service.topTenFilms(null, pageable);
        assertThat(all.getContent()).hasSize(1);

        when(jdbc.queryForList(
                startsWith("SELECT f.film_id AS film_id"),
                ArgumentMatchers.<Map<String,Object>>argThat(m -> m.containsKey("storeId"))
        )).thenReturn(List.of(Map.of("film_id", 6, "title", "S", "rent_count", 7L)));

        PagedResponse<TopFilmDto> byStore = service.topTenFilms((short)1, pageable);
        assertThat(byStore.getContent()).hasSize(1);
    }
}