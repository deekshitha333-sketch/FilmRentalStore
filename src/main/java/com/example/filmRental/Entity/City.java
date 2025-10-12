package com.example.filmRental.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "city")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short city_id;

    public Short getCity_id() {
		return city_id;
	}

	public void setCity_id(Short city_id) {
		this.city_id = city_id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Timestamp getLast_update() {
		return last_update;
	}

	public void setLast_update(Timestamp last_update) {
		this.last_update = last_update;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false)
    private Timestamp last_update;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
}
