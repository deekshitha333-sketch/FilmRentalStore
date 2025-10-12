package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Entity.Customer;
import com.example.filmRental.Entity.Payment;
import com.example.filmRental.Entity.Rental;
import com.example.filmRental.Entity.Staff;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.*;
import com.example.filmRental.Service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private StaffRepository staffRepository;
    @Mock private RentalRepository rentalRepository;
    @Mock private NamedParameterJdbcTemplate jdbc;

    private PaymentService service;

    @BeforeEach
    void init() {
        service = new PaymentServiceImpl(paymentRepository, customerRepository, staffRepository, rentalRepository, jdbc);
    }

    @Test
    @DisplayName("create(): happy path saves payment")
    void create_ok() {
        PaymentCreateRequestDto req = new PaymentCreateRequestDto(
                (short) 1, (short) 1, 1000, new BigDecimal("9.99"), LocalDateTime.now()
        );

        // IMPORTANT: return correct entity types to satisfy generics
        when(customerRepository.findById((short)1))
                .thenReturn(Optional.of(mock(Customer.class)));
        when(staffRepository.findById((short)1))
                .thenReturn(Optional.of(mock(Staff.class)));
        when(rentalRepository.findById(1000))
                .thenReturn(Optional.of(mock(Rental.class)));

        // rental belongs to customer
        when(jdbc.queryForObject(anyString(), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        Payment saved = new Payment();
        saved.setPayment_id(5000);
        saved.setCustomer_id((short)1);
        saved.setStaff_id((short)1);
        saved.setRental_id(1000);
        saved.setAmount(new BigDecimal("9.99"));
        saved.setPayment_date(LocalDateTime.now());

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        PaymentResponseDto dto = service.create(req);
        assertEquals(5000, dto.getId());
        assertEquals(new BigDecimal("9.99"), dto.getAmount());
    }

    @Test
    @DisplayName("create(): throws 404 when staff not found")
    void create_staffNotFound() {
        PaymentCreateRequestDto req = new PaymentCreateRequestDto(
                (short) 1, (short) 9, null, new BigDecimal("1.00"), LocalDateTime.now()
        );

        when(customerRepository.findById((short)1))
                .thenReturn(Optional.of(mock(Customer.class)));
        when(staffRepository.findById((short)9))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(req));
    }

    @Test
    @DisplayName("create(): throws 400 when rental does not belong to customer")
    void create_rentalMismatch() {
        PaymentCreateRequestDto req = new PaymentCreateRequestDto(
                (short) 1, (short) 1, 9999, new BigDecimal("1.00"), LocalDateTime.now()
        );

        when(customerRepository.findById((short)1))
                .thenReturn(Optional.of(mock(Customer.class)));
        when(staffRepository.findById((short)1))
                .thenReturn(Optional.of(mock(Staff.class)));
        when(rentalRepository.findById(9999))
                .thenReturn(Optional.of(mock(Rental.class)));

        // rental does NOT belong to customer
        when(jdbc.queryForObject(anyString(), anyMap(), eq(Long.class)))
                .thenReturn(0L);

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    @Test
    @DisplayName("revenueDatewiseAll(): maps JDBC rows to DTOs")
    void revenueDatewiseAll_ok() {
        Map<String,Object> r1 = new HashMap<>();
        r1.put("d", Date.valueOf(LocalDate.now().minusDays(1)));
        r1.put("amt", new BigDecimal("19.98"));

        Map<String,Object> r2 = new HashMap<>();
        r2.put("d", Date.valueOf(LocalDate.now()));
        r2.put("amt", new BigDecimal("29.97"));

        when(jdbc.queryForList(anyString(), anyMap()))
                .thenReturn(List.of(r1, r2));

        var list = service.revenueDatewiseAll();
        assertEquals(2, list.size());
        assertEquals(new BigDecimal("29.97"), list.get(1).getAmount());
    }

    @Test
    @DisplayName("revenueFilmwiseAll(): maps JDBC rows to DTOs")
    void revenueFilmwiseAll_ok() {
        Map<String,Object> row = new HashMap<>();
        row.put("fid", 10);
        row.put("title", "ACADEMY DINOSAUR");
        row.put("amt", new BigDecimal("123.45"));

        when(jdbc.queryForList(anyString(), anyMap()))
                .thenReturn(List.of(row));

        var list = service.revenueFilmwiseAll();
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).getFilmId().intValue());
        assertEquals("ACADEMY DINOSAUR", list.get(0).getTitle());
    }
}
