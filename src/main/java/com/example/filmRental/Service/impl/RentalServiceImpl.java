// src/main/java/com/example/filmRental/Service/impl/RentalServiceImpl.java
// src/main/java/com/example/filmRental/Service/impl/RentalServiceImpl.java
package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Dto.RentalCreateRequestDto;
import com.example.filmRental.Dto.RentalOverdueResponseDto;
import com.example.filmRental.Dto.RentalResponseDto;
import com.example.filmRental.Dto.RentalReturnRequestDto;
import com.example.filmRental.Dto.RentalStatusResponseDto;
import com.example.filmRental.Dto.TopFilmDto;
import com.example.filmRental.Entity.Inventory;
import com.example.filmRental.Entity.Rental;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Mapper.RentalMapper;
import com.example.filmRental.Repository.CustomerRepository;
import com.example.filmRental.Repository.InventoryRepository;
import com.example.filmRental.Repository.RentalRepository;
import com.example.filmRental.Repository.StaffRepository;
import com.example.filmRental.Service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public RentalServiceImpl(
            RentalRepository rentalRepository,
            InventoryRepository inventoryRepository,
            CustomerRepository customerRepository,
            StaffRepository staffRepository,
            NamedParameterJdbcTemplate jdbc) {
        this.rentalRepository = rentalRepository;
        this.inventoryRepository = inventoryRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.jdbc = jdbc;
    }

    @Override
    public RentalResponseDto create(RentalCreateRequestDto request) {
        // FK presence checks
        Inventory inv = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new NotFoundException("Inventory not found: " + request.getInventoryId()));

        customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.getCustomerId()));

        staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new NotFoundException("Staff not found: " + request.getStaffId()));

        // Availability check
        long openCount = rentalRepository.countOpenByInventory(inv.getInventory_id());
        if (openCount > 0) {
            throw new IllegalStateException("Inventory ID " + inv.getInventory_id() + " is not available (already rented).");
        }

        Rental saved = rentalRepository.save(RentalMapper.toEntity(request));
        return RentalMapper.toDto(saved);
    }

    @Override
    public RentalResponseDto returnRental(int rentalId, RentalReturnRequestDto request) {
        Rental r = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found: " + rentalId));

        if (r.getReturn_date() != null) {
            throw new IllegalStateException("Rental already returned: " + rentalId);
        }

        RentalMapper.applyReturnDate(r, request != null ? request.getReturnDate() : null);
        return RentalMapper.toDto(rentalRepository.save(r));
    }

    @Override
    public PagedResponse<RentalResponseDto> openByCustomer(short customerId, Pageable pageable) {
        Page<Rental> page = rentalRepository.findOpenByCustomer(customerId, pageable);
        List<RentalResponseDto> content = page.getContent().stream().map(RentalMapper::toDto).toList();

        return PagedResponse.<RentalResponseDto>builder()
                .setContent(content)
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setPageNumber(pageable.getPageNumber())
                .setPageSize(pageable.getPageSize())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
    }

    @Override
    public PagedResponse<RentalResponseDto> historyByCustomer(short customerId, Pageable pageable) {
        // Ensure customer exists for clearer error
        customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));

        Page<Rental> page = rentalRepository.findHistoryByCustomer(customerId, pageable);
        List<RentalResponseDto> content = page.getContent().stream().map(RentalMapper::toDto).toList();

        return PagedResponse.<RentalResponseDto>builder()
                .setContent(content)
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setPageNumber(pageable.getPageNumber())
                .setPageSize(pageable.getPageSize())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
    }

    @Override
    public PagedResponse<RentalOverdueResponseDto> overdueByStore(short storeId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getPageNumber() * pageable.getPageSize());

        // Plain strings (no text blocks), MySQL-compatible date math
        String sql =
            "SELECT r.rental_id, r.customer_id, r.inventory_id, i.store_id, f.film_id, f.title, " +
            "       r.rental_date, (r.rental_date + INTERVAL f.rental_duration DAY) AS due_date, " +
            "       DATEDIFF(NOW(), (r.rental_date + INTERVAL f.rental_duration DAY)) AS days_overdue " +
            "FROM rental r " +
            "JOIN inventory i ON i.inventory_id = r.inventory_id " +
            "JOIN film f ON f.film_id = i.film_id " +
            "WHERE i.store_id = :storeId " +
            "  AND r.return_date IS NULL " +
            "  AND (r.rental_date + INTERVAL f.rental_duration DAY) < NOW() " +
            "ORDER BY r.rental_id ASC " +
            "LIMIT :limit OFFSET :offset";

        String countSql =
            "SELECT COUNT(*) " +
            "FROM rental r " +
            "JOIN inventory i ON i.inventory_id = r.inventory_id " +
            "JOIN film f ON f.film_id = i.film_id " +
            "WHERE i.store_id = :storeId " +
            "  AND r.return_date IS NULL " +
            "  AND (r.rental_date + INTERVAL f.rental_duration DAY) < NOW()";

        List<Map<String, Object>> rows = jdbc.queryForList(sql, params);
        Long total = jdbc.queryForObject(countSql, params, Long.class);
        long totalElements = (total != null ? total : 0L);

        List<RentalOverdueResponseDto> content = rows.stream().map(m -> {
            RentalOverdueResponseDto d = new RentalOverdueResponseDto();
            d.setRentalId(((Number) m.get("rental_id")).intValue());
            d.setCustomerId(((Number) m.get("customer_id")).shortValue());
            d.setInventoryId(((Number) m.get("inventory_id")).intValue());
            d.setStoreId(((Number) m.get("store_id")).shortValue());
            d.setFilmId(((Number) m.get("film_id")).intValue());
            d.setTitle((String) m.get("title"));
            d.setRentalDate(((Timestamp) m.get("rental_date")).toLocalDateTime());
            Timestamp due = (Timestamp) m.get("due_date");
            d.setDueDate(due != null ? due.toLocalDateTime() : null);
            Number days = (Number) m.get("days_overdue");
            d.setDaysOverdue(days != null ? days.intValue() : null);
            return d;
        }).toList();

        int totalPages = (int) Math.ceil(totalElements / (double) pageable.getPageSize());

        return PagedResponse.<RentalOverdueResponseDto>builder()
                .setContent(content)
                .setTotalElements(totalElements)
                .setTotalPages(totalPages)
                .setPageNumber(pageable.getPageNumber())
                .setPageSize(pageable.getPageSize())
                .setHasNext((pageable.getPageNumber() + 1) * pageable.getPageSize() < totalElements)
                .setHasPrevious(pageable.getPageNumber() > 0)
                .build();
    }

    @Override
    public RentalStatusResponseDto statusByInventory(int inventoryId) {
        // Ensure inventory exists
        inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new NotFoundException("Inventory not found: " + inventoryId));

        long open = rentalRepository.countOpenByInventory(inventoryId);

        RentalStatusResponseDto dto = new RentalStatusResponseDto();
        dto.setInventoryId(inventoryId);
        dto.setAvailable(open == 0);

        if (open == 0) {
            dto.setOpenRentalId(null);
            dto.setCustomerId(null);
            dto.setRentalDate(null);
            dto.setDueDate(null);
            return dto;
        }

        // Fetch the current open rental details + due date
        Map<String, Object> params = new HashMap<>();
        params.put("invId", inventoryId);

        String sql =
            "SELECT r.rental_id, r.customer_id, r.rental_date, " +
            "       (r.rental_date + INTERVAL f.rental_duration DAY) AS due_date " +
            "FROM rental r " +
            "JOIN inventory i ON i.inventory_id = r.inventory_id " +
            "JOIN film f ON f.film_id = i.film_id " +
            "WHERE r.inventory_id = :invId AND r.return_date IS NULL " +
            "ORDER BY r.rental_id DESC LIMIT 1";

        List<Map<String, Object>> rows = jdbc.queryForList(sql, params);

        // Guard race condition: a return could happen between count and query
        if (rows.isEmpty()) {
            dto.setAvailable(true);
            dto.setOpenRentalId(null);
            dto.setCustomerId(null);
            dto.setRentalDate(null);
            dto.setDueDate(null);
            return dto;
        }

        Map<String, Object> m = rows.get(0);
        dto.setOpenRentalId(((Number) m.get("rental_id")).intValue());
        dto.setCustomerId(((Number) m.get("customer_id")).shortValue());
        dto.setRentalDate(((Timestamp) m.get("rental_date")).toLocalDateTime());
        Timestamp due = (Timestamp) m.get("due_date");
        dto.setDueDate(due != null ? due.toLocalDateTime() : null);

        return dto;
    }
 // in RentalServiceImpl
    public PagedResponse<TopFilmDto> topTenFilms(Short storeId, Pageable pageable) {
        String base =
            "SELECT f.film_id AS film_id, f.title AS title, COUNT(r.rental_id) AS rent_count " +
            "FROM rental r " +
            "JOIN inventory i ON i.inventory_id = r.inventory_id " +
            "JOIN film f ON f.film_id = i.film_id ";

        String where = (storeId != null ? "WHERE i.store_id = :storeId " : "");
        String groupOrder = "GROUP BY f.film_id, f.title ORDER BY rent_count DESC LIMIT :limit OFFSET :offset";

        Map<String, Object> params = new HashMap<>();
        if (storeId != null) params.put("storeId", storeId);
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getPageNumber() * pageable.getPageSize());

        List<Map<String, Object>> rows = jdbc.queryForList(base + where + groupOrder, params);

        String countSql =
            "SELECT COUNT(*) FROM (" +
            "SELECT f.film_id FROM rental r " +
            "JOIN inventory i ON i.inventory_id = r.inventory_id " +
            "JOIN film f ON f.film_id = i.film_id " +
            (storeId != null ? "WHERE i.store_id = :storeId " : "") +
            "GROUP BY f.film_id) t";

        Long total = jdbc.queryForObject(countSql, params, Long.class);
        long totalElements = (total != null ? total : 0L);

        List<TopFilmDto> content = rows.stream().map(m ->
                new TopFilmDto(((Number)m.get("film_id")).intValue(),
                               (String) m.get("title"),
                               ((Number)m.get("rent_count")).longValue())
        ).toList();

        int totalPages = (int) Math.ceil(totalElements / (double) pageable.getPageSize());

        return PagedResponse.<TopFilmDto>builder()
            .setContent(content)
            .setTotalElements(totalElements)
            .setTotalPages(totalPages)
            .setPageNumber(pageable.getPageNumber())
            .setPageSize(pageable.getPageSize())
            .setHasNext((pageable.getPageNumber() + 1) * pageable.getPageSize() < totalElements)
            .setHasPrevious(pageable.getPageNumber() > 0)
            .build();
    }

}