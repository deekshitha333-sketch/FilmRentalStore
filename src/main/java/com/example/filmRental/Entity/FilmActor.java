package com.example.filmRental.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "film_actor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FilmActorId.class)
public class FilmActor {

    @Id
    @Column(name = "actor_id")
    private Short actorId;

    @Id
    @Column(name = "film_id")
    private Short filmId;

    @Column(name = "last_update")
    private Timestamp lastUpdate;
}