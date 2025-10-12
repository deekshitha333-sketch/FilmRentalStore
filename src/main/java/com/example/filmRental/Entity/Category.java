package com.example.filmRental.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    public Byte getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Byte category_id) {
		this.category_id = category_id;
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
    private Byte category_id;

    @Column(nullable = false, length = 25)
    private String name;

    @Column(nullable = false)
    private Timestamp last_update;
}
