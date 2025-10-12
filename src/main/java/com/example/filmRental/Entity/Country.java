package com.example.filmRental.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "country")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    public Short getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Short country_id) {
		this.country_id = country_id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Timestamp getLast_update() {
		return last_update;
	}

	public void setLast_update(Timestamp last_update) {
		this.last_update = last_update;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short country_id;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(nullable = false)
    private Timestamp last_update;
}
