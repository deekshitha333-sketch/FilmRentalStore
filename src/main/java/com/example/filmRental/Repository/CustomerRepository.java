package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Short> {

    // Active / inactive (works with snake_case entity fields)
    Page<Customer> findByActive(Boolean active, Pageable pageable);

    // ---- First name (snake_case) using native SQL ----
    @Query(
        value =
            "SELECT * FROM customer c " +
            "WHERE LOWER(c.first_name) LIKE LOWER(CONCAT('%', :fn, '%'))",
        countQuery =
            "SELECT COUNT(*) FROM customer c " +
            "WHERE LOWER(c.first_name) LIKE LOWER(CONCAT('%', :fn, '%'))",
        nativeQuery = true
    )
    Page<Customer> findByFirst_nameContainingIgnoreCase(@Param("fn") String fn, Pageable pageable);

    // ---- Last name (snake_case) using native SQL ----
    @Query(
        value =
            "SELECT * FROM customer c " +
            "WHERE LOWER(c.last_name) LIKE LOWER(CONCAT('%', :ln, '%'))",
        countQuery =
            "SELECT COUNT(*) FROM customer c " +
            "WHERE LOWER(c.last_name) LIKE LOWER(CONCAT('%', :ln, '%'))",
        nativeQuery = true
    )
    Page<Customer> findByLast_nameContainingIgnoreCase(@Param("ln") String ln, Pageable pageable);

    // Email (single)
    Optional<Customer> findByEmail(String email);

    // ---- Phone via address join ----
    @Query(
        value =
            "SELECT c.* FROM customer c " +
            "JOIN address a ON a.address_id = c.address_id " +
            "WHERE a.phone = :phone",
        countQuery =
            "SELECT COUNT(*) FROM customer c " +
            "JOIN address a ON a.address_id = c.address_id " +
            "WHERE a.phone = :phone",
        nativeQuery = true
    )
    Page<Customer> findByPhone(@Param("phone") String phone, Pageable pageable);

    // ---- By city (with address join) ----
    @Query(
        value =
            "SELECT c.* FROM customer c " +
            "JOIN address a ON a.address_id = c.address_id " +
            "JOIN city ci ON ci.city_id = a.city_id " +
            "WHERE LOWER(ci.city) = LOWER(:city)",
        countQuery =
            "SELECT COUNT(*) FROM customer c " +
            "JOIN address a ON a.address_id = c.address_id " +
            "JOIN city ci ON ci.city_id = a.city_id " +
            "WHERE LOWER(ci.city) = LOWER(:city)",
        nativeQuery = true
    )
    Page<Customer> findByCity(@Param("city") String city, Pageable pageable);

    // ---- By country (with address/city/country join) ----
    @Query(
        value =
            "SELECT c.* FROM customer c " +
            "JOIN address a ON a.address_id = c.address_id " +
            "JOIN city ci ON ci.city_id = a.city_id " +
            "JOIN country co ON co.country_id = ci.country_id " +
            "WHERE LOWER(co.country) = LOWER(:country)",
        countQuery =
            "SELECT COUNT(*) FROM customer c " +
            "JOIN address a ON a.address_id = c.address_id " +
            "JOIN city ci ON ci.city_id = a.city_id " +
            "JOIN country co ON co.country_id = ci.country_id " +
            "WHERE LOWER(co.country) = LOWER(:country)",
        nativeQuery = true
    )
    Page<Customer> findByCountry(@Param("country") String country, Pageable pageable);

    
}
