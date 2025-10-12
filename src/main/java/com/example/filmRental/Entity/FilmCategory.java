package com.example.filmRental.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "film_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FilmCategoryId.class)
public class FilmCategory {
    @Id
    private Short filmId;

    @Id
    private Byte categoryId;

    private Timestamp lastUpdate;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class FilmCategoryId implements Serializable {
    private Short filmId;
    private Byte categoryId;
}