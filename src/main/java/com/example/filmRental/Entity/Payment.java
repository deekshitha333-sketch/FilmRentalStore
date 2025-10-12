package com.example.filmRental.Entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Integer payment_id;

    @Column(name = "customer_id", nullable = false)
    private Short customer_id;

    @Column(name = "staff_id", nullable = false)
    private Short staff_id;

    @Column(name = "rental_id")
    private Integer rental_id; // nullable

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime payment_date;

    @Column(name = "last_update")
    private Timestamp last_update;

    public Payment() { }

    // getters/setters snake_case like your style
    public Integer getPayment_id() { return payment_id; }
    public void setPayment_id(Integer payment_id) { this.payment_id = payment_id; }

    public Short getCustomer_id() { return customer_id; }
    public void setCustomer_id(Short customer_id) { this.customer_id = customer_id; }

    public Short getStaff_id() { return staff_id; }
    public void setStaff_id(Short staff_id) { this.staff_id = staff_id; }

    public Integer getRental_id() { return rental_id; }
    public void setRental_id(Integer rental_id) { this.rental_id = rental_id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getPayment_date() { return payment_date; }
    public void setPayment_date(LocalDateTime payment_date) { this.payment_date = payment_date; }

    public Timestamp getLast_update() { return last_update; }
    public void setLast_update(Timestamp last_update) { this.last_update = last_update; }
}