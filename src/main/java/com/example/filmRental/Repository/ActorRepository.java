package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActorRepository extends JpaRepository<Actor, Short> {

    @Query("SELECT a FROM Actor a " +
           "WHERE (:firstName IS NULL OR LOWER(a.first_name) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
           "AND   (:lastName  IS NULL OR LOWER(a.last_name)  LIKE LOWER(CONCAT('%', :lastName,  '%')))")
    Page<Actor> search(@Param("firstName") String firstName,
                       @Param("lastName") String lastName,
                       Pageable pageable);
}