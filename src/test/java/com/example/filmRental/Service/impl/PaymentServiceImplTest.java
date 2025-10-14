package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Entity.Payment;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock PaymentRepository paymentRepository;
    @Mock CustomerRepository customerRepository;
    @Mock StaffRepository staffRepository;
    @Mock RentalRepository rentalRepository;
    @Mock NamedParameterJdbcTemplate jdbc;

    @InjectMocks PaymentServiceImpl service;

    @Test
    void create_ok_with_rental_belongs_to_customer() {
        when(customerRepository.findById((short)2)).thenReturn(Optional.of(new com.example.filmRental.Entity.Customer()));
        when(staffRepository.findById((short)3)).thenReturn(Optional.of(new com.example.filmRental.Entity.Staff()));
        when(rentalRepository.findById(100)).thenReturn(Optional.of(new com.example.filmRental.Entity.Rental()));
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM rental WHERE rental_id = :rid"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentCreateRequestDto req = new PaymentCreateRequestDto((short)2,(short)3,100,new BigDecimal("4.99"), LocalDate.now().atStartOfDay());
        PaymentResponseDto dto = service.create(req);
        assertThat(dto).isNotNull();
    }

    @Test
    void create_badRequest_when_rental_not_of_customer() {
        when(customerRepository.findById((short)2)).thenReturn(Optional.of(new com.example.filmRental.Entity.Customer()));
        when(staffRepository.findById((short)3)).thenReturn(Optional.of(new com.example.filmRental.Entity.Staff()));
        when(rentalRepository.findById(100)).thenReturn(Optional.of(new com.example.filmRental.Entity.Rental()));
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM rental WHERE rental_id = :rid"), anyMap(), eq(Long.class)))
                .thenReturn(0L);

        PaymentCreateRequestDto req = new PaymentCreateRequestDto((short)2,(short)3,100,new BigDecimal("4.99"), LocalDate.now().atStartOfDay());
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void revenueDatewiseAll_ok() {
        when(jdbc.queryForList(startsWith("SELECT DATE(p.payment_date) AS d"), anyMap()))
                .thenReturn(List.of(Map.of("d", Date.valueOf("2025-01-01"), "amt", new BigDecimal("100.00"))));
        List<RevenueByDateDto> out = service.revenueDatewiseAll();
        assertThat(out).hasSize(1);
        assertThat(out.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void revenueStorewiseByFilm_ok() {
        when(jdbc.queryForList(startsWith("SELECT s.store_id AS sid"), anyMap()))
                .thenReturn(List.of(Map.of("sid", 1, "saddr", "47 MySakila Drive, Lethbridge", "amt", new BigDecimal("5.50"))));
        List<RevenueByStoreDto> out = service.revenueStorewiseByFilm(5);
        assertThat(out).hasSize(1);
        assertThat(out.get(0).getAmount()).isEqualByComparingTo("5.50");
    }
}