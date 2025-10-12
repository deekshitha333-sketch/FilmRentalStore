package com.example.filmRental.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "language")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    public Byte getLanguage_id() {
		return language_id;
	}

	public void setLanguage_id(Byte language_id) {
		this.language_id = language_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getLast_update() {
		return last_update;
	}

	public void setLast_update(Timestamp last_update) {
		this.last_update = last_update;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte language_id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Timestamp last_update;
}
