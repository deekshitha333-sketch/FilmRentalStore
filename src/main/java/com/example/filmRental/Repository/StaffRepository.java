// src/main/java/com/example/filmRental/Repository/StaffRepository.java
package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Short> {
    // For complex searches by city/country/phone, we use service-level SQL
}
