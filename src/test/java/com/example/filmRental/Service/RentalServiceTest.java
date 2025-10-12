package com.example.filmRental.Service;

import com.example.filmRental.Dto.RentalCreateRequestDto;
import com.example.filmRental.Dto.RentalReturnRequestDto;
import com.example.filmRental.Dto.RentalStatusResponseDto;
import com.example.filmRental.Entity.Inventory;
import com.example.filmRental.Entity.Rental;
import com.example.filmRental.Entity.Customer;
import com.example.filmRental.Entity.Staff;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.CustomerRepository;
import com.example.filmRental.Repository.InventoryRepository;
import com.example.filmRental.Repository.RentalRepository;
import com.example.filmRental.Repository.StaffRepository;
import com.example.filmRental.Service.impl.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock private RentalRepository rentalRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private NamedParameterJdbcTemplate jdbc;

    private RentalService service;

    @BeforeEach
    void init() {
        service = new RentalServiceImpl(rentalRepository, inventoryRepository, customerRepository, staffRepository, jdbc);
    }

    @Test
    @DisplayName("create(): happy path saves and returns DTO")
    void create_ok() {
        // Arrange
        RentalCreateRequestDto req = new RentalCreateRequestDto();
        req.setInventoryId(200);
        req.setCustomerId((short) 1);
        req.setStaffId((short) 1);

        Inventory inv = Mockito.mock(Inventory.class);
        Mockito.when(inventoryRepository.findById(200)).thenReturn(Optional.of(inv));
        Mockito.when(inv.getInventory_id()).thenReturn(200);

        // IMPORTANT: return correct entity types to satisfy Optional<T> generics
        Mockito.when(customerRepository.findById((short)1))
                .thenReturn(Optional.of(Mockito.mock(Customer.class)));
        Mockito.when(staffRepository.findById((short)1))
                .thenReturn(Optional.of(Mockito.mock(Staff.class)));

        Mockito.when(rentalRepository.countOpenByInventory(200)).thenReturn(0L);

        Rental saved = new Rental();
        saved.setRental_id(5555);
        saved.setInventory_id(200);
        saved.setCustomer_id((short)1);
        saved.setStaff_id((short)1);
        saved.setRental_date(LocalDateTime.now());

        Mockito.when(rentalRepository.save(Mockito.any(Rental.class))).thenReturn(saved);

        // Act
        var dto = service.create(req);

        // Assert
        assertNotNull(dto);
        assertEquals(5555, dto.getId());
        assertEquals(200, dto.getInventoryId());
        assertEquals((short)1, dto.getCustomerId());
        assertNull(dto.getReturnDate());
    }

    @Test
    @DisplayName("create(): throws 404 when inventory missing")
    void create_inventoryNotFound() {
        RentalCreateRequestDto req = new RentalCreateRequestDto();
        req.setInventoryId(999);
        req.setCustomerId((short) 1);
        req.setStaffId((short) 1);

        Mockito.when(inventoryRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(req));
    }

    @Test
    @DisplayName("create(): throws conflict when inventory not available")
    void create_inventoryNotAvailable() {
        RentalCreateRequestDto req = new RentalCreateRequestDto();
        req.setInventoryId(200);
        req.setCustomerId((short) 1);
        req.setStaffId((short) 1);

        Inventory inv = Mockito.mock(Inventory.class);
        Mockito.when(inventoryRepository.findById(200)).thenReturn(Optional.of(inv));
        Mockito.when(inv.getInventory_id()).thenReturn(200);

        Mockito.when(customerRepository.findById((short)1))
                .thenReturn(Optional.of(Mockito.mock(Customer.class)));
        Mockito.when(staffRepository.findById((short)1))
                .thenReturn(Optional.of(Mockito.mock(Staff.class)));

        Mockito.when(rentalRepository.countOpenByInventory(200)).thenReturn(1L);

        assertThrows(IllegalStateException.class, () -> service.create(req));
    }

    @Test
    @DisplayName("returnRental(): happy path sets return_date")
    void returnRental_ok() {
        Rental entity = new Rental();
        entity.setRental_id(7000);
        entity.setInventory_id(200);
        entity.setCustomer_id((short)1);
        entity.setStaff_id((short)1);
        entity.setRental_date(LocalDateTime.now().minusDays(1));
        entity.setReturn_date(null);

        Mockito.when(rentalRepository.findById(7000)).thenReturn(Optional.of(entity));
        Mockito.when(rentalRepository.save(Mockito.any(Rental.class)))
               .thenAnswer(a -> a.getArgument(0));

        RentalReturnRequestDto req = new RentalReturnRequestDto();
        req.setReturnDate(LocalDateTime.now());

        var dto = service.returnRental(7000, req);
        assertNotNull(dto.getReturnDate());
        assertEquals(7000, dto.getId());
    }

    @Test
    @DisplayName("returnRental(): conflict when already returned")
    void returnRental_alreadyReturned() {
        Rental entity = new Rental();
        entity.setRental_id(7000);
        entity.setReturn_date(LocalDateTime.now());

        Mockito.when(rentalRepository.findById(7000)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> service.returnRental(7000, null));
    }

    @Test
    @DisplayName("statusByInventory(): available path")
    void status_available() {
        Mockito.when(inventoryRepository.findById(200)).thenReturn(Optional.of(Mockito.mock(Inventory.class)));
        Mockito.when(rentalRepository.countOpenByInventory(200)).thenReturn(0L);

        RentalStatusResponseDto dto = service.statusByInventory(200);
        assertTrue(dto.isAvailable());
        assertNull(dto.getOpenRentalId());
        assertNull(dto.getCustomerId());
    }

    @Test
    @DisplayName("statusByInventory(): not available path maps jdbc row")
    void status_notAvailable() {
        Mockito.when(inventoryRepository.findById(200)).thenReturn(Optional.of(Mockito.mock(Inventory.class)));
        Mockito.when(rentalRepository.countOpenByInventory(200)).thenReturn(1L);

        Map<String,Object> row = new HashMap<>();
        row.put("rental_id", 8888);
        row.put("customer_id", (short) 1);
        row.put("rental_date", Timestamp.from(Instant.now()));
        row.put("due_date", Timestamp.from(Instant.now()));

        Mockito.when(jdbc.queryForList(Mockito.anyString(), Mockito.anyMap()))
               .thenReturn(List.of(row));

        RentalStatusResponseDto dto = service.statusByInventory(200);
        assertFalse(dto.isAvailable());
        assertEquals(8888, dto.getOpenRentalId());
        assertEquals((short)1, dto.getCustomerId());
        assertNotNull(dto.getRentalDate());
        assertNotNull(dto.getDueDate());
    }
}