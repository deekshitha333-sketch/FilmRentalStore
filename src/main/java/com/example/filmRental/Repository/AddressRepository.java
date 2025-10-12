package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Short> {
}