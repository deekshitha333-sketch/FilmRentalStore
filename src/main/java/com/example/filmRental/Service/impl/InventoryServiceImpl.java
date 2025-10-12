package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public InventoryServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private boolean exists(String table, String idCol, Number id) {
        Long n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE " + idCol + " = :id",
                Map.of("id", id),
                Long.class
        );
        return n != null && n > 0;
    }

    @Override
    @Transactional
    public InventoryResponseDto add(InventoryAddRequestDto request) {
        int filmId = request.getFilmId();
        short storeId = request.getStoreId();

        if (!exists("film", "film_id", filmId))
            throw new NotFoundException("Film not found: " + filmId);
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);

        String ins = "INSERT INTO inventory (film_id, store_id, last_update) VALUES (:fid, :sid, CURRENT_TIMESTAMP)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(ins, new MapSqlParameterSource().addValue("fid", filmId).addValue("sid", storeId),
                kh, new String[]{"inventory_id"});

        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("Failed to create inventory");
        return new InventoryResponseDto(key.intValue(), filmId, storeId);
    }

    @Override
    public List<FilmInventoryCountDto> allFilmsAllStores() {
        String sql =
            "SELECT f.film_id AS fid, f.title AS title, COUNT(i.inventory_id) AS copies " +
            "FROM inventory i JOIN film f ON f.film_id = i.film_id " +
            "GROUP BY f.film_id, f.title ORDER BY f.title ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<FilmInventoryCountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            list.add(new FilmInventoryCountDto(
                ((Number)m.get("fid")).intValue(),
                (String)m.get("title"),
                ((Number)m.get("copies")).longValue()
            ));
        }
        return list;
    }

    @Override
    public List<StoreInventoryCountDto> filmAllStores(int filmId) {
        if (!exists("film", "film_id", filmId))
            throw new NotFoundException("Film not found: " + filmId);

        String sql =
            "SELECT s.store_id AS sid, CONCAT(a.address, ', ', c.city) AS saddr, COUNT(i.inventory_id) AS copies " +
            "FROM inventory i " +
            "JOIN store s ON s.store_id = i.store_id " +
            "JOIN address a ON a.address_id = s.address_id " +
            "JOIN city c ON c.city_id = a.city_id " +
            "WHERE i.film_id = :fid " +
            "GROUP BY s.store_id, saddr ORDER BY s.store_id ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("fid", filmId));
        List<StoreInventoryCountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            list.add(new StoreInventoryCountDto(
                ((Number)m.get("sid")).shortValue(),
                (String)m.get("saddr"),
                ((Number)m.get("copies")).longValue()
            ));
        }
        return list;
    }

    @Override
    public List<FilmInventoryCountDto> storeAllFilms(short storeId) {
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);

        String sql =
            "SELECT f.film_id AS fid, f.title AS title, COUNT(i.inventory_id) AS copies " +
            "FROM inventory i JOIN film f ON f.film_id = i.film_id " +
            "WHERE i.store_id = :sid " +
            "GROUP BY f.film_id, f.title ORDER BY f.title ASC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        List<FilmInventoryCountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            list.add(new FilmInventoryCountDto(
                ((Number)m.get("fid")).intValue(),
                (String)m.get("title"),
                ((Number)m.get("copies")).longValue()
            ));
        }
        return list;
    }

    @Override
    public StoreFilmInventoryCountDto filmInStore(int filmId, short storeId) {
        if (!exists("film", "film_id", filmId))
            throw new NotFoundException("Film not found: " + filmId);
        if (!exists("store", "store_id", storeId))
            throw new NotFoundException("Store not found: " + storeId);

        String sql =
            "SELECT s.store_id AS sid, CONCAT(a.address, ', ', c.city) AS saddr, " +
            "       f.film_id AS fid, f.title AS title, COUNT(i.inventory_id) AS copies " +
            "FROM inventory i " +
            "JOIN store s ON s.store_id = i.store_id " +
            "JOIN address a ON a.address_id = s.address_id " +
            "JOIN city c ON c.city_id = a.city_id " +
            "JOIN film f ON f.film_id = i.film_id " +
            "WHERE i.film_id = :fid AND i.store_id = :sid " +
            "GROUP BY s.store_id, saddr, f.film_id, f.title";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("fid", filmId, "sid", storeId));
        long copies = 0L;
        String saddr = null, title = null;
        if (!rows.isEmpty()) {
            Map<String,Object> m = rows.get(0);
            copies = ((Number)m.get("copies")).longValue();
            saddr = (String)m.get("saddr");
            title = (String)m.get("title");
        } else {
            // No rows means zero copies in that store; still return the structure
            String titleSql = "SELECT title FROM film WHERE film_id = :fid";
            String addrSql = "SELECT CONCAT(a.address, ', ', c.city) FROM store s " +
                             "JOIN address a ON a.address_id = s.address_id " +
                             "JOIN city c ON c.city_id = a.city_id WHERE s.store_id = :sid";
            title = jdbc.queryForObject(titleSql, Map.of("fid", filmId), String.class);
            saddr = jdbc.queryForObject(addrSql, Map.of("sid", storeId), String.class);
        }
        return new StoreFilmInventoryCountDto(storeId, saddr, filmId, title, copies);
    }
}