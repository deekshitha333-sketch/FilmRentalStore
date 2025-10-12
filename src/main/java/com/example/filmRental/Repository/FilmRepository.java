package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Short> {

    @Query(
        "SELECT f FROM Film f " +
        "WHERE (:title IS NULL OR LOWER(f.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
        "AND (:rating IS NULL OR f.rating = :rating) " +
        "AND (:languageId IS NULL OR f.language_id = :languageId) " +
        "AND (:releaseYearMin IS NULL OR f.release_year >= :releaseYearMin) " +
        "AND (:releaseYearMax IS NULL OR f.release_year <= :releaseYearMax) " +
        "AND (:lengthMin IS NULL OR f.length >= :lengthMin) " +
        "AND (:lengthMax IS NULL OR f.length <= :lengthMax) " +
        "AND (:rentalRateMin IS NULL OR f.rental_rate >= :rentalRateMin) " +
        "AND (:rentalRateMax IS NULL OR f.rental_rate <= :rentalRateMax) " +
        "AND (:rentalDurationMin IS NULL OR f.rental_duration >= :rentalDurationMin) " +
        "AND (:rentalDurationMax IS NULL OR f.rental_duration <= :rentalDurationMax)"
    )
    Page<Film> advancedSearch(
            @Param("title") String title,
            @Param("rating") String rating,
            @Param("languageId") Short languageId,
            @Param("releaseYearMin") Short releaseYearMin,
            @Param("releaseYearMax") Short releaseYearMax,
            @Param("lengthMin") Short lengthMin,
            @Param("lengthMax") Short lengthMax,
            @Param("rentalRateMin") BigDecimal rentalRateMin,
            @Param("rentalRateMax") BigDecimal rentalRateMax,
            @Param("rentalDurationMin") Short rentalDurationMin,
            @Param("rentalDurationMax") Short rentalDurationMax,
            Pageable pageable
    );

    @Query("SELECT DISTINCT f.rating FROM Film f WHERE f.rating IS NOT NULL ORDER BY f.rating")
    List<String> distinctRatings();

    @Query("SELECT DISTINCT f.release_year FROM Film f WHERE f.release_year IS NOT NULL ORDER BY f.release_year")
    List<Short> distinctReleaseYears();

    @Query("SELECT f FROM Film f ORDER BY f.last_update DESC")
    Page<Film> findRecent(Pageable pageable);
}
