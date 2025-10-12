package com.example.filmRental.Repository;

import com.example.filmRental.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
   
}