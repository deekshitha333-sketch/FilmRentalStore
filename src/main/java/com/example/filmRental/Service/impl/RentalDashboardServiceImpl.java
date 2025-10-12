package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.RentalDueDto;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.RentalDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RentalDashboardServiceImpl implements RentalDashboardService {

    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public RentalDashboardServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private boolean exists(String table, String idCol, Number id) {
        Long n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE " + idCol + " = :id",
                Map.of("id", id), Long.class);
        return n != null && n > 0;
    }

    @Override
    public List<RentalDueDto> dueByStore(short storeId) {
        if (!exists("store", "store_id", storeId)) {
            throw new NotFoundException("Store not found: " + storeId);
        }

        String sql = "SELECT r.rental_id rid, i.store_id sid, c.customer_id cid, " +
                "CONCAT(c.first_name, ' ', c.last_name) cname, f.film_id fid, f.title, " +
                "r.rental_date rdate, DATEDIFF(CURRENT_TIMESTAMP, r.rental_date) days_outstanding " +
                "FROM rental r " +
                "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                "JOIN customer c ON r.customer_id = c.customer_id " +
                "JOIN film f ON i.film_id = f.film_id " +
                "WHERE r.return_date IS NULL AND i.store_id = :sid " +
                "ORDER BY days_outstanding DESC, r.rental_date ASC";

        List<Map<String, Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        List<RentalDueDto> list = new ArrayList<>();
        for (Map<String, Object> m : rows) {
            Integer rid = ((Number) m.get("rid")).intValue();
            Short sid = ((Number) m.get("sid")).shortValue();
            Integer cid = ((Number) m.get("cid")).intValue();
            String cname = (String) m.get("cname");
            Integer fid = ((Number) m.get("fid")).intValue();
            String title = (String) m.get("title");
            String rdate = String.valueOf(m.get("rdate"));
            Integer days = ((Number) m.get("days_outstanding")).intValue();
            list.add(new RentalDueDto(rid, sid, cid, cname, fid, title, rdate, days));
        }
        return list;
    }
}
