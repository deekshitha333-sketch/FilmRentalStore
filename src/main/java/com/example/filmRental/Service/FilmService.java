package com.example.filmRental.Service;

import com.example.filmRental.Dto.FilmRequestDto;
import com.example.filmRental.Dto.FilmResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface FilmService {
    FilmResponseDto create(FilmRequestDto request);
    FilmResponseDto getById(short id);
    PagedResponse<FilmResponseDto> list(String title, String rating, Short languageId, Pageable pageable);

    // Extended search (ranges)
    PagedResponse<FilmResponseDto> advancedSearch(
            String title, String rating, Short languageId,
            Short releaseYearMin, Short releaseYearMax,
            Short lengthMin, Short lengthMax,
            BigDecimal rentalRateMin, BigDecimal rentalRateMax,
            Short rentalDurationMin, Short rentalDurationMax,
            Pageable pageable);

    FilmResponseDto update(short id, FilmRequestDto request);
    FilmResponseDto patch(short id, FilmRequestDto request);
    void delete(short id);
    boolean exists(short id);
    long count();

    // Distincts + recent
    List<String> distinctRatings();
    List<Short> distinctReleaseYears();
    PagedResponse<FilmResponseDto> recent(Pageable pageable);

    // Bulk ops
    List<FilmResponseDto> bulkCreate(List<FilmRequestDto> requests);
    void bulkDelete(List<Short> ids);

    // Targeted partials
    String getDescription(short id);
    FilmResponseDto updateDescription(short id, String description);
    FilmResponseDto updateRating(short id, String rating);
    Timestamp getLastUpdate(short id);
}
