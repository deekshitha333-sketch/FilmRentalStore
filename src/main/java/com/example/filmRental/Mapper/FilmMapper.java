package com.example.filmRental.Mapper;

import com.example.filmRental.Dto.FilmRequestDto;
import com.example.filmRental.Dto.FilmResponseDto;
import com.example.filmRental.Entity.Film;

import java.sql.Timestamp;
import java.time.Instant;

public final class FilmMapper {
    private FilmMapper() { }

    public static Film toEntity(FilmRequestDto dto) {
        Film f = new Film();
        f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setRelease_year(dto.getReleaseYear());
        f.setLanguage_id(dto.getLanguageId());
        f.setRental_duration(dto.getRentalDuration());
        f.setRental_rate(dto.getRentalRate());
        f.setLength(dto.getLength());
        f.setReplacement_cost(dto.getReplacementCost());
        f.setRating(dto.getRating());
        f.setSpecial_features(dto.getSpecialFeatures());
        f.setLast_update(Timestamp.from(Instant.now()));
        return f;
    }

    public static void updateEntity(Film entity, FilmRequestDto dto) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getReleaseYear() != null) entity.setRelease_year(dto.getReleaseYear());
        if (dto.getLanguageId() != null) entity.setLanguage_id(dto.getLanguageId());
        if (dto.getRentalDuration() != null) entity.setRental_duration(dto.getRentalDuration());
        if (dto.getRentalRate() != null) entity.setRental_rate(dto.getRentalRate());
        if (dto.getLength() != null) entity.setLength(dto.getLength());
        if (dto.getReplacementCost() != null) entity.setReplacement_cost(dto.getReplacementCost());
        if (dto.getRating() != null) entity.setRating(dto.getRating());
        if (dto.getSpecialFeatures() != null) entity.setSpecial_features(dto.getSpecialFeatures());
        entity.setLast_update(Timestamp.from(Instant.now()));
    }

    public static FilmResponseDto toDto(Film film) {
        FilmResponseDto dto = new FilmResponseDto();
        dto.setId(film.getFilm_id());
        dto.setTitle(film.getTitle());
        dto.setDescription(film.getDescription());
        dto.setReleaseYear(film.getRelease_year());
        dto.setLanguageId(film.getLanguage_id());
        dto.setRentalDuration(film.getRental_duration());
        dto.setRentalRate(film.getRental_rate());
        dto.setLength(film.getLength());
        dto.setReplacementCost(film.getReplacement_cost());
        dto.setRating(film.getRating());
        dto.setSpecialFeatures(film.getSpecial_features());
        dto.setLastUpdate(film.getLast_update());
        return dto;
    }
}
