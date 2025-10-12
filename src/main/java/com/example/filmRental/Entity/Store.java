package com.example.filmRental.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "store")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    public Byte getStore_id() {
		return store_id;
	}

	public void setStore_id(Byte store_id) {
		this.store_id = store_id;
	}

	public Staff getManager() {
		return manager;
	}

	public void setManager(Staff manager) {
		this.manager = manager;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Timestamp getLast_update() {
		return last_update;
	}

	public void setLast_update(Timestamp last_update) {
		this.last_update = last_update;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte store_id;

    @ManyToOne
    @JoinColumn(name = "manager_staff_id", nullable = false)
    private Staff manager;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false)
    private Timestamp last_update;
}