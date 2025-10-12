package com.example.filmRental.Service.impl;
import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public AnalyticsServiceImpl(NamedParameterJdbcTemplate jdbc){ this.jdbc = jdbc; }

    private boolean exists(String table, String idCol, Number id) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE " + idCol + " = :id",
                Map.of("id", id), Long.class);
        return n != null && n > 0;
    }

    @Override
    public List<DateAmountDto> revenueDatewiseAll() {
        String sql = "SELECT DATE(payment_date) d, SUM(amount) amt FROM payment GROUP BY DATE(payment_date) ORDER BY d";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<DateAmountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            String d = m.get("d").toString();
            BigDecimal amt = new BigDecimal(m.get("amt").toString());
            list.add(new DateAmountDto(d, amt));
        }
        return list;
    }

    @Override
    public List<DateAmountDto> revenueDatewiseByStore(short storeId) {
        if (!exists("store", "store_id", storeId)) throw new NotFoundException("Store not found: " + storeId);
        String sql = "SELECT DATE(p.payment_date) d, SUM(p.amount) amt FROM payment p " +
                "JOIN rental r ON p.rental_id = r.rental_id " +
                "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                "WHERE i.store_id = :sid GROUP BY DATE(p.payment_date) ORDER BY d";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        List<DateAmountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            String d = m.get("d").toString();
            BigDecimal amt = new BigDecimal(m.get("amt").toString());
            list.add(new DateAmountDto(d, amt));
        }
        return list;
    }

    @Override
    public List<StoreAmountDto> revenueByFilmAcrossStores(int filmId) {
        if (!exists("film", "film_id", filmId)) throw new NotFoundException("Film not found: " + filmId);
        String sql = "SELECT s.store_id sid, CONCAT(a.address, ', ', c.city) saddr, SUM(p.amount) amt FROM payment p " +
                "JOIN rental r ON p.rental_id = r.rental_id " +
                "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                "JOIN store s ON s.store_id = i.store_id " +
                "JOIN address a ON a.address_id = s.address_id " +
                "JOIN city c ON c.city_id = a.city_id " +
                "WHERE i.film_id = :fid GROUP BY s.store_id, saddr ORDER BY amt DESC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("fid", filmId));
        List<StoreAmountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Short sid = ((Number)m.get("sid")).shortValue();
            String saddr = (String)m.get("saddr");
            BigDecimal amt = new BigDecimal(m.get("amt").toString());
            list.add(new StoreAmountDto(sid, saddr, amt));
        }
        return list;
    }

    @Override
    public List<FilmAmountDto> revenueAllFilmsByStore(short storeId) {
        if (!exists("store", "store_id", storeId)) throw new NotFoundException("Store not found: " + storeId);
        String sql = "SELECT f.film_id fid, f.title, SUM(p.amount) amt FROM payment p " +
                "JOIN rental r ON p.rental_id = r.rental_id " +
                "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                "JOIN film f ON f.film_id = i.film_id " +
                "WHERE i.store_id = :sid GROUP BY f.film_id, f.title ORDER BY amt DESC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        List<FilmAmountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Integer fid = ((Number)m.get("fid")).intValue();
            String title = (String)m.get("title");
            BigDecimal amt = new BigDecimal(m.get("amt").toString());
            list.add(new FilmAmountDto(fid, title, amt));
        }
        return list;
    }

    @Override
    public List<FilmAmountDto> revenueFilmwiseAll() {
        String sql = "SELECT f.film_id fid, f.title, SUM(p.amount) amt FROM payment p " +
                "JOIN rental r ON p.rental_id = r.rental_id " +
                "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                "JOIN film f ON f.film_id = i.film_id " +
                "GROUP BY f.film_id, f.title ORDER BY amt DESC";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<FilmAmountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Integer fid = ((Number)m.get("fid")).intValue();
            String title = (String)m.get("title");
            BigDecimal amt = new BigDecimal(m.get("amt").toString());
            list.add(new FilmAmountDto(fid, title, amt));
        }
        return list;
    }

    @Override
    public List<FilmRentCountDto> topTenFilmsAllStores() {
        String sql = "SELECT f.film_id fid, f.title, COUNT(r.rental_id) cnt FROM rental r " +
                "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                "JOIN film f ON f.film_id = i.film_id " +
                "GROUP BY f.film_id, f.title ORDER BY cnt DESC, f.title ASC LIMIT 10";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<FilmRentCountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Integer fid = ((Number)m.get("fid")).intValue();
            String title = (String)m.get("title");
            Long cnt = ((Number)m.get("cnt")).longValue();
            list.add(new FilmRentCountDto(fid, title, cnt));
        }
        return list;
    }

    @Override
    public List<FilmRentCountDto> topTenFilmsByStore(short storeId) {
        if (!exists("store", "store_id", storeId)) throw new NotFoundException("Store not found: " + storeId);
        String sql = "SELECT f.film_id fid, f.title, COUNT(r.rental_id) cnt FROM rental r " +
                "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                "JOIN film f ON f.film_id = i.film_id " +
                "WHERE i.store_id = :sid GROUP BY f.film_id, f.title ORDER BY cnt DESC, f.title ASC LIMIT 10";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of("sid", storeId));
        List<FilmRentCountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Integer fid = ((Number)m.get("fid")).intValue();
            String title = (String)m.get("title");
            Long cnt = ((Number)m.get("cnt")).longValue();
            list.add(new FilmRentCountDto(fid, title, cnt));
        }
        return list;
    }

    @Override
    public List<YearCountDto> filmsCountByYear() {
        String sql = "SELECT release_year y, COUNT(*) c FROM film GROUP BY release_year ORDER BY y";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<YearCountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Integer y = m.get("y") == null ? null : ((Number)m.get("y")).intValue();
            Long c = ((Number)m.get("c")).longValue();
            list.add(new YearCountDto(y, c));
        }
        return list;
    }

    @Override
    public List<ActorFilmCountDto> topTenActorsByFilmCount() {
        String sql = "SELECT a.actor_id aid, CONCAT(a.first_name, ' ', a.last_name) nm, COUNT(fa.film_id) cnt " +
                "FROM actor a JOIN film_actor fa ON a.actor_id = fa.actor_id " +
                "GROUP BY a.actor_id, nm ORDER BY cnt DESC, nm ASC LIMIT 10";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of());
        List<ActorFilmCountDto> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            Short aid = ((Number)m.get("aid")).shortValue();
            String nm = (String)m.get("nm");
            Long cnt = ((Number)m.get("cnt")).longValue();
            list.add(new ActorFilmCountDto(aid, nm, cnt));
        }
        return list;
    }
}
