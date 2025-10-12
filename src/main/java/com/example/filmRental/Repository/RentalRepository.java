// src/main/java/com/example/filmRental/Repository/RentalRepository.java
package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Rental;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    // Open rentals by customer (native to avoid snake_case property parsing)
    @Query(
        value =
        "SELECT * FROM rental r WHERE r.customer_id = :cid AND r.return_date IS NULL ORDER BY r.rental_id DESC",
        countQuery =
        "SELECT COUNT(*) FROM rental r WHERE r.customer_id = :cid AND r.return_date IS NULL",
        nativeQuery = true
    )
    Page<Rental> findOpenByCustomer(@Param("cid") short customerId, Pageable pageable);

    // Is an inventory item currently rented (unreturned)?
    @Query(value = "SELECT COUNT(*) FROM rental WHERE inventory_id = :invId AND return_date IS NULL", nativeQuery = true)
    long countOpenByInventory(@Param("invId") int inventoryId);

@Query
(value=
        "SELECT * FROM rental r WHERE r.customer_id = :cid ORDER BY r.rental_id DESC",
        countQuery =
        "SELECT COUNT(*) FROM rental r WHERE r.customer_id = :cid",
        nativeQuery = true)
    Page<Rental> findHistoryByCustomer(@Param("cid") short customerId, Pageable pageable);


@Query(
        value = "SELECT * FROM rental WHERE inventory_id = :invId AND return_date IS NULL ORDER BY rental_id DESC LIMIT 1",
        nativeQuery = true
    )
    Optional<Rental> findOpenSingleByInventory(@Param("invId") int inventoryId);
    }

