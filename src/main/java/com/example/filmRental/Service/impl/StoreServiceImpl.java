package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Service
public class StoreServiceImpl implements StoreService {

    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public StoreServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private boolean exists(String table, String idColumn, Number id) {
        Long n = jdbc.queryForObject(
            "SELECT COUNT(*) FROM " + table + " WHERE " + idColumn + " = :id",
            Map.of("id", id),
            Long.class
        );
        return n != null && n > 0;
    }

    private StoreResponseDto fetchStore(short storeId) {
        String sql =
            "SELECT st.store_id, st.manager_staff_id, st.address_id, " +
            "       a.address, ci.city, co.country, a.phone " +
            "FROM store st " +
            "JOIN address a ON a.address_id = st.address_id " +
            "JOIN city ci ON ci.city_id = a.city_id " +
            "JOIN country co ON co.country_id = ci.country_id " +
            "WHERE st.store_id = :sid";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        if (rows.isEmpty()) throw new NotFoundException("Store not found: " + storeId);
        Map<String,Object> m = rows.get(0);
        return new StoreResponseDto(
            ((Number)m.get("store_id")).shortValue(),
            ((Number)m.get("manager_staff_id")).shortValue(),
            ((Number)m.get("address_id")).shortValue(),
            (String)m.get("address"),
            (String)m.get("city"),
            (String)m.get("country"),
            (String)m.get("phone")
        );
    }

    @Override
    @Transactional
    public StoreResponseDto create(StoreCreateRequestDto request) {
        short managerId = request.getManagerStaffId();
        short addressId = request.getAddressId();

        if (!exists("staff", "staff_id", managerId))
            throw new NotFoundException("Staff (manager) not found: " + managerId);
        if (!exists("address", "address_id", addressId))
            throw new NotFoundException("Address not found: " + addressId);

        // ensure the manager is not already assigned to another store (unique manager)
        Long cnt = jdbc.queryForObject(
            "SELECT COUNT(*) FROM store WHERE manager_staff_id = :mid",
            Map.of("mid", managerId), Long.class);
        if (cnt != null && cnt > 0) throw new BadRequestException("Manager already assigned to another store: " + managerId);

        // insert store
        String ins = "INSERT INTO store (manager_staff_id, address_id, last_update) " +
                     "VALUES (:mid, :aid, CURRENT_TIMESTAMP)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(ins, new MapSqlParameterSource()
                .addValue("mid", managerId)
                .addValue("aid", addressId), kh, new String[]{"store_id"});
        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("Failed to create store");

        short storeId = key.shortValue();

        // make sure the manager.staff.store_id points to this store for consistency
        jdbc.update("UPDATE staff SET store_id = :sid, last_update = CURRENT_TIMESTAMP WHERE staff_id = :mid",
                Map.of("sid", storeId, "mid", managerId));

        return fetchStore(storeId);
    }

    @Override
    @Transactional
    public StoreResponseDto assignAddress(short storeId, short addressId) {
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);
        if (!exists("address", "address_id", addressId))
            throw new NotFoundException("Address not found: " + addressId);

        jdbc.update("UPDATE store SET address_id = :aid, last_update = CURRENT_TIMESTAMP WHERE store_id = :sid",
                Map.of("aid", addressId, "sid", storeId));

        return fetchStore(storeId);
    }

    @Override
    @Transactional
    public StoreResponseDto assignManager(short storeId, short managerStaffId) {
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);
        if (!exists("staff", "staff_id", managerStaffId))
            throw new NotFoundException("Staff (manager) not found: " + managerStaffId);

        Long others = jdbc.queryForObject(
            "SELECT COUNT(*) FROM store WHERE manager_staff_id = :mid AND store_id <> :sid",
            Map.of("mid", managerStaffId, "sid", storeId), Long.class);
        if (others != null && others > 0)
            throw new BadRequestException("Manager already assigned to another store: " + managerStaffId);

        jdbc.update("UPDATE store SET manager_staff_id = :mid, last_update = CURRENT_TIMESTAMP WHERE store_id = :sid",
                Map.of("mid", managerStaffId, "sid", storeId));

        // align staff.store_id with the store (Sakila consistency)
        jdbc.update("UPDATE staff SET store_id = :sid, last_update = CURRENT_TIMESTAMP WHERE staff_id = :mid",
                Map.of("sid", storeId, "mid", managerStaffId));

        return fetchStore(storeId);
    }

    @Override
    @Transactional
    public StoreResponseDto updatePhone(short storeId, String phone) {
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);

        int updated = jdbc.update(
            "UPDATE address a JOIN store s ON s.address_id = a.address_id " +
            "SET a.phone = :ph, a.last_update = CURRENT_TIMESTAMP " +
            "WHERE s.store_id = :sid",
            Map.of("ph", phone, "sid", storeId));
        if (updated == 0) throw new IllegalStateException("Failed to update phone for store " + storeId);

        return fetchStore(storeId);
    }

    private List<StoreResponseDto> findByWhere(String where, Map<String,Object> params) {
        String sql =
            "SELECT st.store_id, st.manager_staff_id, st.address_id, a.address, ci.city, co.country, a.phone " +
            "FROM store st " +
            "JOIN address a ON a.address_id = st.address_id " +
            "JOIN city ci ON ci.city_id = a.city_id " +
            "JOIN country co ON co.country_id = ci.country_id " +
            where +
            " ORDER BY st.store_id ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, params);
        List<StoreResponseDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            list.add(new StoreResponseDto(
                ((Number)m.get("store_id")).shortValue(),
                ((Number)m.get("manager_staff_id")).shortValue(),
                ((Number)m.get("address_id")).shortValue(),
                (String)m.get("address"),
                (String)m.get("city"),
                (String)m.get("country"),
                (String)m.get("phone")
            ));
        }
        return list;
    }

    @Override
    public List<StoreResponseDto> findByPhone(String phone) {
        return findByWhere(" WHERE a.phone = :ph", Map.of("ph", phone));
    }

    @Override
    public List<StoreResponseDto> findByCity(String city) {
        return findByWhere(" WHERE ci.city = :cty", Map.of("cty", city));
    }

    @Override
    public List<StoreResponseDto> findByCountry(String country) {
        return findByWhere(" WHERE co.country = :cn", Map.of("cn", country));
    }

    @Override
    public List<StaffSummaryDto> staffByStore(short storeId) {
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);

        String sql =
            "SELECT s.staff_id, s.first_name, s.last_name, s.email, a.phone " +
            "FROM staff s " +
            "JOIN address a ON a.address_id = s.address_id " +
            "WHERE s.store_id = :sid " +
            "ORDER BY s.staff_id ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        List<StaffSummaryDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            list.add(new StaffSummaryDto(
                ((Number)m.get("staff_id")).shortValue(),
                (String)m.get("first_name"),
                (String)m.get("last_name"),
                (String)m.get("email"),
                (String)m.get("phone")
            ));
        }
        return list;
    }

    @Override
    public List<CustomerSummaryDto> customersByStore(short storeId) {
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);

        String sql =
            "SELECT c.customer_id, c.first_name, c.last_name, c.email, a.phone " +
            "FROM customer c " +
            "JOIN address a ON a.address_id = c.address_id " +
            "WHERE c.store_id = :sid " +
            "ORDER BY c.customer_id ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        List<CustomerSummaryDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            list.add(new CustomerSummaryDto(
                ((Number)m.get("customer_id")).shortValue(),
                (String)m.get("first_name"),
                (String)m.get("last_name"),
                (String)m.get("email"),
                (String)m.get("phone")
            ));
        }
        return list;
    }

    @Override
    public List<StoreManagerDetailsDto> managers() {
        String sql =
            "SELECT st.store_id, mgr.first_name AS fn, mgr.last_name AS ln, mgr.email AS em, " +
            "       am.phone AS ph, aa.address AS addr, ci.city AS city " +
            "FROM store st " +
            "JOIN staff mgr ON mgr.staff_id = st.manager_staff_id " +
            "JOIN address aa ON aa.address_id = st.address_id " +
            "JOIN city ci ON ci.city_id = aa.city_id " +
            "LEFT JOIN address am ON am.address_id = mgr.address_id " +
            "ORDER BY st.store_id ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<StoreManagerDetailsDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            list.add(new StoreManagerDetailsDto(
                ((Number)m.get("store_id")).shortValue(),
                (String)m.get("fn"),
                (String)m.get("ln"),
                (String)m.get("em"),
                (String)m.get("ph"),
                (String)m.get("addr"),
                (String)m.get("city")
            ));
        }
        return list;
    }
}
