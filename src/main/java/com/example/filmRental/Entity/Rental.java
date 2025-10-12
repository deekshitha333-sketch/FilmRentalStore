package com.example.filmRental.Entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental")
public class Rental implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id", nullable = false)
    private Integer rental_id;

    @Column(name = "rental_date", nullable = false)
    private LocalDateTime rental_date;

    @Column(name = "inventory_id", nullable = false)
    private Integer inventory_id;

    @Column(name = "customer_id", nullable = false)
    private Short customer_id;

    @Column(name = "return_date")
    private LocalDateTime return_date;

    @Column(name = "staff_id", nullable = false)
    private Short staff_id;

    @Column(name = "last_update", nullable = false)
    private Timestamp last_update;

    // --- Getters / Setters (snake_case to match your existing code) ---

    public Integer getRental_id() { return rental_id; }
    public void setRental_id(Integer rental_id) { this.rental_id = rental_id; }

    public LocalDateTime getRental_date() { return rental_date; }
    public void setRental_date(LocalDateTime rental_date) { this.rental_date = rental_date; }

    public Integer getInventory_id() { return inventory_id; }
    public void setInventory_id(Integer inventory_id) { this.inventory_id = inventory_id; }

    public Short getCustomer_id() { return customer_id; }
    public void setCustomer_id(Short customer_id) { this.customer_id = customer_id; }

    public LocalDateTime getReturn_date() { return return_date; }
    public void setReturn_date(LocalDateTime return_date) { this.return_date = return_date; }

    public Short getStaff_id() { return staff_id; }
    public void setStaff_id(Short staff_id) { this.staff_id = staff_id; }

    public Timestamp getLast_update() { return last_update; }
    public void setLast_update(Timestamp last_update) { this.last_update = last_update; }
}
