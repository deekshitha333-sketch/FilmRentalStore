package com.example.filmRental.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "actor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

    public Short getActor_id() {
		return actor_id;
	}

	public void setActor_id(Short actor_id) {
		this.actor_id = actor_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public Timestamp getLast_update() {
		return last_update;
	}

	public void setLast_update(Timestamp last_update) {
		this.last_update = last_update;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short actor_id;

    @Column(nullable = false, length = 45)
    private String first_name;

    @Column(nullable = false, length = 45)
    private String last_name;

    @Column(nullable = false)
    private Timestamp last_update;
}
