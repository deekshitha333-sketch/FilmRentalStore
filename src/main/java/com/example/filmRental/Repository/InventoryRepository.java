// src/main/java/com/example/filmRental/Repository/InventoryRepository.java
//package com.example.filmRental.Repository;
//
//import com.example.filmRental.Entity.Inventory;
//import org.springframework.data.jpa.repository.JpaRepository;

package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    // All methods rewritten to native SQL to avoid property-path parsing on snake_case fields

    @Query(value = "SELECT * FROM inventory WHERE film_id = :filmId", nativeQuery = true)
    List<Inventory> findByFilmId(@Param("filmId") Short filmId);

    @Query(value = "SELECT * FROM inventory WHERE store_id = :storeId", nativeQuery = true)
    List<Inventory> findByStoreId(@Param("storeId") Short storeId);

    @Query(value = "SELECT * FROM inventory WHERE film_id = :filmId AND store_id = :storeId", nativeQuery = true)
    List<Inventory> findByFilmIdAndStoreId(@Param("filmId") Short filmId,
                                           @Param("storeId") Short storeId);

    // Kept the name you already exposed; now also native SQL
    @Query(value = "SELECT * FROM inventory WHERE film_id = :filmId AND store_id = :storeId", nativeQuery = true)
    List<Inventory> findInventoryByFilmAndStore(@Param("filmId") Short filmId,
                                                @Param("storeId") Short storeId);

    @Query(value = "SELECT COUNT(*) FROM inventory WHERE film_id = :filmId AND store_id = :storeId", nativeQuery = true)
    Long countByFilmAndStore(@Param("filmId") Short filmId,
                             @Param("storeId") Short storeId);
}