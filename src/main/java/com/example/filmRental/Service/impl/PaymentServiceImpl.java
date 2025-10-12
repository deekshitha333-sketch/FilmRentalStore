package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Entity.Payment;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Mapper.PaymentMapper;
import com.example.filmRental.Repository.*;
import com.example.filmRental.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final RentalRepository rentalRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            StaffRepository staffRepository,
            RentalRepository rentalRepository,
            NamedParameterJdbcTemplate jdbc
    ) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.rentalRepository = rentalRepository;
        this.jdbc = jdbc;
    }

    @Override
    public PaymentResponseDto create(PaymentCreateRequestDto request) {
        // FK checks
        customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.getCustomerId()));
        staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new NotFoundException("Staff not found: " + request.getStaffId()));
        if (request.getRentalId() != null) {
            rentalRepository.findById(request.getRentalId())
                    .orElseThrow(() -> new NotFoundException("Rental not found: " + request.getRentalId()));
            // (Optional) ensure rental belongs to customer
            Integer rid = request.getRentalId();
            short cid = request.getCustomerId();
            long count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM rental WHERE rental_id = :rid AND customer_id = :cid",
                    Map.of("rid", rid, "cid", cid),
                    Long.class
            );
            if (count == 0) throw new BadRequestException("Rental " + rid + " does not belong to customer " + cid);
        }
        Payment saved = paymentRepository.save(PaymentMapper.toEntity(request));
        return PaymentMapper.toDto(saved);
    }

    // -------- Revenue Queries (native via JDBC) --------

    @Override
    public List<RevenueByDateDto> revenueDatewiseAll() {
        String sql =
            "SELECT DATE(p.payment_date) AS d, SUM(p.amount) AS amt " +
            "FROM payment p " +
            "GROUP BY DATE(p.payment_date) " +
            "ORDER BY DATE(p.payment_date) ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<RevenueByDateDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Date date = (Date) m.get("d");
            BigDecimal amt = (BigDecimal) m.get("amt");
            list.add(new RevenueByDateDto(date.toLocalDate(), amt));
        }
        return list;
    }

    @Override
    public List<RevenueByDateDto> revenueDatewiseByStore(short storeId) {
        String sql =
            "SELECT DATE(p.payment_date) AS d, SUM(p.amount) AS amt " +
            "FROM payment p " +
            "JOIN rental r ON p.rental_id = r.rental_id " +
            "JOIN inventory i ON r.inventory_id = i.inventory_id " +
            "WHERE i.store_id = :storeId " +
            "GROUP BY DATE(p.payment_date) " +
            "ORDER BY DATE(p.payment_date) ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("storeId", storeId));
        List<RevenueByDateDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Date date = (Date) m.get("d");
            BigDecimal amt = (BigDecimal) m.get("amt");
            list.add(new RevenueByDateDto(date.toLocalDate(), amt));
        }
        return list;
    }

    @Override
    public List<RevenueByStoreDto> revenueStorewiseByFilm(int filmId) {
        String sql =
            "SELECT s.store_id AS sid, CONCAT(a.address, ', ', c.city) AS saddr, SUM(p.amount) AS amt " +
            "FROM payment p " +
            "JOIN rental r ON p.rental_id = r.rental_id " +
            "JOIN inventory i ON r.inventory_id = i.inventory_id " +
            "JOIN store s ON i.store_id = s.store_id " +
            "JOIN address a ON s.address_id = a.address_id " +
            "JOIN city c ON a.city_id = c.city_id " +
            "WHERE i.film_id = :filmId " +
            "GROUP BY s.store_id, saddr " +
            "ORDER BY s.store_id ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("filmId", filmId));
        List<RevenueByStoreDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Short sid = ((Number)m.get("sid")).shortValue();
            String addr = (String) m.get("saddr");
            BigDecimal amt = (BigDecimal) m.get("amt");
            list.add(new RevenueByStoreDto(sid, addr, amt));
        }
        return list;
    }

    @Override
    public List<RevenueByFilmDto> revenueFilmwiseAll() {
        String sql =
            "SELECT f.film_id AS fid, f.title AS title, SUM(p.amount) AS amt " +
            "FROM payment p " +
            "JOIN rental r ON p.rental_id = r.rental_id " +
            "JOIN inventory i ON r.inventory_id = i.inventory_id " +
            "JOIN film f ON i.film_id = f.film_id " +
            "GROUP BY f.film_id, f.title " +
            "ORDER BY amt DESC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<RevenueByFilmDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Integer fid = ((Number)m.get("fid")).intValue();
            String title = (String) m.get("title");
            BigDecimal amt = (BigDecimal) m.get("amt");
            list.add(new RevenueByFilmDto(fid, title, amt));
        }
        return list;
    }

    @Override
    public List<RevenueByFilmDto> revenueFilmsByStore(short storeId) {
        String sql =
            "SELECT f.film_id AS fid, f.title AS title, SUM(p.amount) AS amt " +
            "FROM payment p " +
            "JOIN rental r ON p.rental_id = r.rental_id " +
            "JOIN inventory i ON r.inventory_id = i.inventory_id " +
            "JOIN film f ON i.film_id = f.film_id " +
            "WHERE i.store_id = :storeId " +
            "GROUP BY f.film_id, f.title " +
            "ORDER BY amt DESC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("storeId", storeId));
        List<RevenueByFilmDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Integer fid = ((Number)m.get("fid")).intValue();
            String title = (String) m.get("title");
            BigDecimal amt = (BigDecimal) m.get("amt");
            list.add(new RevenueByFilmDto(fid, title, amt));
        }
        return list;
    }
}
