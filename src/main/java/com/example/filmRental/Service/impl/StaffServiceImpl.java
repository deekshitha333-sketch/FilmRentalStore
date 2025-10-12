package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Mapper.StaffMapper;
import com.example.filmRental.Repository.StaffRepository;
import com.example.filmRental.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StaffServiceImpl implements StaffService {

    private final NamedParameterJdbcTemplate jdbc;
    private final StaffRepository repo;

    @Autowired
    public StaffServiceImpl(NamedParameterJdbcTemplate jdbc, StaffRepository repo) {
        this.jdbc = jdbc;
        this.repo = repo;
    }

    private boolean exists(String table, String idCol, Number id) {
        Long n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE " + idCol + " = :id",
                Map.of("id", id), Long.class);
        return n != null && n > 0;
    }

    // Reusable SELECT with joins to return consistent StaffResponseDto structure
    private static final String BASE_SELECT =
            "SELECT s.staff_id, s.first_name, s.last_name, s.email, s.store_id, s.address_id, " +
            "       a.address, ci.city, co.country, a.phone, s.picture " +
            "FROM staff s " +
            "JOIN address a ON a.address_id = s.address_id " +
            "JOIN city ci ON ci.city_id = a.city_id " +
            "JOIN country co ON co.country_id = ci.country_id ";

    private StaffResponseDto oneOr404(List<Map<String,Object>> rows, String notFoundMessage) {
        if (rows.isEmpty()) throw new NotFoundException(notFoundMessage);
        return StaffMapper.mapRow(rows.get(0));
    }

    private List<StaffResponseDto> many(List<Map<String,Object>> rows) {
        List<StaffResponseDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) list.add(StaffMapper.mapRow(m));
        return list;
    }

    @Override
    @Transactional
    public StaffResponseDto create(StaffCreateRequestDto request) {
        short storeId = request.getStoreId();
        short addressId = request.getAddressId();

        if (!exists("store", "store_id", storeId)) throw new NotFoundException("Store not found: " + storeId);
        if (!exists("address", "address_id", addressId)) throw new NotFoundException("Address not found: " + addressId);

        String sql = "INSERT INTO staff (first_name, last_name, address_id, email, store_id, active, picture, last_update) " +
                     "VALUES (:fn, :ln, :addr, :email, :sid, TRUE, :pic, CURRENT_TIMESTAMP)";
        KeyHolder kh = new GeneratedKeyHolder();
        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("fn", request.getFirstName())
                .addValue("ln", request.getLastName())
                .addValue("addr", addressId)
                .addValue("email", request.getEmail())
                .addValue("sid", storeId)
                .addValue("pic", request.getPictureUrl());
        jdbc.update(sql, ps, kh, new String[] { "staff_id" });
        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("Failed to create staff");

        String read = BASE_SELECT + " WHERE s.staff_id = :id";
        List<Map<String,Object>> rows = jdbc.queryForList(read, Map.of("id", key.shortValue()));
        return oneOr404(rows, "Staff not found: " + key.shortValue());
    }

    @Override
    @Transactional
    public StaffResponseDto updateEmail(short staffId, StaffUpdateEmailRequestDto req) {
        if (!exists("staff", "staff_id", staffId)) throw new NotFoundException("Staff not found: " + staffId);
        jdbc.update("UPDATE staff SET email = :email, last_update = CURRENT_TIMESTAMP WHERE staff_id = :id",
                Map.of("email", req.getEmail(), "id", staffId));
        List<Map<String,Object>> rows = jdbc.queryForList(BASE_SELECT + " WHERE s.staff_id = :id",
                Map.of("id", staffId));
        return oneOr404(rows, "Staff not found after update: " + staffId);
    }

    @Override
    @Transactional
    public StaffResponseDto updatePhone(short staffId, StaffUpdatePhoneRequestDto req) {
        if (!exists("staff", "staff_id", staffId)) throw new NotFoundException("Staff not found: " + staffId);
        Short addrId = jdbc.queryForObject("SELECT address_id FROM staff WHERE staff_id = :id",
                Map.of("id", staffId), Short.class);
        if (addrId == null) throw new NotFoundException("Address not linked for staff: " + staffId);

        jdbc.update("UPDATE address SET phone = :ph, last_update = CURRENT_TIMESTAMP WHERE address_id = :aid",
                Map.of("ph", req.getPhone(), "aid", addrId));
        List<Map<String,Object>> rows = jdbc.queryForList(BASE_SELECT + " WHERE s.staff_id = :id",
                Map.of("id", staffId));
        return oneOr404(rows, "Staff not found after update: " + staffId);
    }

    @Override
    @Transactional
    public StaffResponseDto updateFirstName(short staffId, StaffUpdateFirstNameRequestDto req) {
        if (!exists("staff", "staff_id", staffId)) throw new NotFoundException("Staff not found: " + staffId);
        jdbc.update("UPDATE staff SET first_name = :fn, last_update = CURRENT_TIMESTAMP WHERE staff_id = :id",
                Map.of("fn", req.getFirstName(), "id", staffId));
        List<Map<String,Object>> rows = jdbc.queryForList(BASE_SELECT + " WHERE s.staff_id = :id",
                Map.of("id", staffId));
        return oneOr404(rows, "Staff not found after update: " + staffId);
    }

    @Override
    @Transactional
    public StaffResponseDto updateLastName(short staffId, StaffUpdateLastNameRequestDto req) {
        if (!exists("staff", "staff_id", staffId)) throw new NotFoundException("Staff not found: " + staffId);
        jdbc.update("UPDATE staff SET last_name = :ln, last_update = CURRENT_TIMESTAMP WHERE staff_id = :id",
                Map.of("ln", req.getLastName(), "id", staffId));
        List<Map<String,Object>> rows = jdbc.queryForList(BASE_SELECT + " WHERE s.staff_id = :id",
                Map.of("id", staffId));
        return oneOr404(rows, "Staff not found after update: " + staffId);
    }

    @Override
    @Transactional
    public StaffResponseDto assignAddress(short staffId, short addressId) {
        if (!exists("staff", "staff_id", staffId)) throw new NotFoundException("Staff not found: " + staffId);
        if (!exists("address", "address_id", addressId)) throw new NotFoundException("Address not found: " + addressId);
        jdbc.update("UPDATE staff SET address_id = :aid, last_update = CURRENT_TIMESTAMP WHERE staff_id = :id",
                Map.of("aid", addressId, "id", staffId));
        List<Map<String,Object>> rows = jdbc.queryForList(BASE_SELECT + " WHERE s.staff_id = :id",
                Map.of("id", staffId));
        return oneOr404(rows, "Staff not found after assign address: " + staffId);
    }

    @Override
    @Transactional
    public StaffResponseDto assignStore(short staffId, StaffAssignStoreRequestDto req) {
        if (!exists("staff", "staff_id", staffId)) throw new NotFoundException("Staff not found: " + staffId);
        short storeId = req.getStoreId();
        if (!exists("store", "store_id", storeId)) throw new NotFoundException("Store not found: " + storeId);
        jdbc.update("UPDATE staff SET store_id = :sid, last_update = CURRENT_TIMESTAMP WHERE staff_id = :id",
                Map.of("sid", storeId, "id", staffId));
        List<Map<String,Object>> rows = jdbc.queryForList(BASE_SELECT + " WHERE s.staff_id = :id",
                Map.of("id", staffId));
        return oneOr404(rows, "Staff not found after assign store: " + staffId);
    }

    @Override
    public List<StaffResponseDto> findByFirstName(String firstName) {
        List<Map<String,Object>> rows = jdbc.queryForList(
                BASE_SELECT + " WHERE s.first_name = :fn ORDER BY s.last_name ASC",
                Map.of("fn", firstName));
        return many(rows);
    }

    @Override
    public List<StaffResponseDto> findByLastName(String lastName) {
        List<Map<String,Object>> rows = jdbc.queryForList(
                BASE_SELECT + " WHERE s.last_name = :ln ORDER BY s.first_name ASC",
                Map.of("ln", lastName));
        return many(rows);
    }

    @Override
    public List<StaffResponseDto> findByEmail(String email) {
        List<Map<String,Object>> rows = jdbc.queryForList(
                BASE_SELECT + " WHERE s.email = :em",
                Map.of("em", email));
        return many(rows);
    }

    @Override
    public StaffResponseDto findByPhone(String phone) {
        List<Map<String,Object>> rows = jdbc.queryForList(
                BASE_SELECT + " WHERE a.phone = :ph",
                Map.of("ph", phone));
        return oneOr404(rows, "Staff not found by phone: " + phone);
    }

    @Override
    public List<StaffResponseDto> findByCity(String city) {
        List<Map<String,Object>> rows = jdbc.queryForList(
                BASE_SELECT + " WHERE ci.city = :ct",
                Map.of("ct", city));
        return many(rows);
    }

    @Override
    public List<StaffResponseDto> findByCountry(String country) {
        List<Map<String,Object>> rows = jdbc.queryForList(
                BASE_SELECT + " WHERE co.country = :co",
                Map.of("co", country));
        return many(rows);
    }
}
