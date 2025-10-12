package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/v1/films")
@Validated
@Tag(name = "Films", description = "CRUD & extended search endpoints for films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) { this.filmService = filmService; }

    // 1 Create
    @Operation(summary = "Create film")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponseDto create(@Valid @RequestBody FilmRequestDto request) {
        return filmService.create(request);
    }

    // 2 Get by id
    @Operation(summary = "Get film by id")
    @GetMapping("/{id}")
    public FilmResponseDto getById(@PathVariable("id") @Min(1) short id) {
        return filmService.getById(id);
    }

    // 3 Basic list/search with paging & sorting
    @Operation(summary = "List/search films (title/rating/language) with paging & sorting")
    @GetMapping
    public PagedResponse<FilmResponseDto> list(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "rating", required = false) String rating,
            @RequestParam(name = "languageId", required = false) Short languageId,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort", defaultValue = "film_id,asc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return filmService.list(title, rating, languageId, pageable);
    }

    // 4 Full update
    @Operation(summary = "Full update (PUT)")
    @PutMapping("/{id}")
    public FilmResponseDto update(@PathVariable("id") @Min(1) short id, @Valid @RequestBody FilmRequestDto request) {
        return filmService.update(id, request);
    }

    // 5 Partial update
    @Operation(summary = "Partial update (PATCH)")
    @PatchMapping("/{id}")
    public FilmResponseDto patch(@PathVariable("id") @Min(1) short id, @RequestBody FilmRequestDto request) {
        return filmService.patch(id, request);
    }

    // 6 Delete
    @Operation(summary = "Delete film")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Min(1) short id) { filmService.delete(id); }

    // 7 Exists
    @Operation(summary = "Check existence")
    @GetMapping("/{id}/exists")
    public boolean exists(@PathVariable("id") @Min(1) short id) { return filmService.exists(id); }

    // 8 Count
    @Operation(summary = "Count films")
    @GetMapping("/count")
    public long count() { return filmService.count(); }

    // 9 Extended search: title/rating/language + ranges
    @Operation(summary = "Advanced search with ranges (releaseYear/length/rentalRate/rentalDuration)")
    @GetMapping("/search/advanced")
    public PagedResponse<FilmResponseDto> advancedSearch(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "rating", required = false) String rating,
            @RequestParam(name = "languageId", required = false) Short languageId,
            @RequestParam(name = "releaseYearMin", required = false) Short releaseYearMin,
            @RequestParam(name = "releaseYearMax", required = false) Short releaseYearMax,
            @RequestParam(name = "lengthMin", required = false) Short lengthMin,
            @RequestParam(name = "lengthMax", required = false) Short lengthMax,
            @RequestParam(name = "rentalRateMin", required = false) BigDecimal rentalRateMin,
            @RequestParam(name = "rentalRateMax", required = false) BigDecimal rentalRateMax,
            @RequestParam(name = "rentalDurationMin", required = false) Short rentalDurationMin,
            @RequestParam(name = "rentalDurationMax", required = false) Short rentalDurationMax,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort", defaultValue = "film_id,asc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return filmService.advancedSearch(title, rating, languageId,
                releaseYearMin, releaseYearMax,
                lengthMin, lengthMax,
                rentalRateMin, rentalRateMax,
                rentalDurationMin, rentalDurationMax,
                pageable);
    }

    // 10 Distinct ratings
    @Operation(summary = "List distinct ratings")
    @GetMapping("/distinct/ratings")
    public List<String> distinctRatings() { return filmService.distinctRatings(); }

    // 11 Distinct release years
    @Operation(summary = "List distinct release years")
    @GetMapping("/distinct/release-years")
    public List<Short> distinctReleaseYears() { return filmService.distinctReleaseYears(); }

    // 12 Recent (by last_update desc)
    @Operation(summary = "Recently updated films (sorted by last_update desc)")
    @GetMapping("/recent")
    public PagedResponse<FilmResponseDto> recent(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("last_update").descending());
        return filmService.recent(pageable);
    }

    // 13 Bulk create
    @Operation(summary = "Bulk create films")
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<FilmResponseDto> bulkCreate(@Valid @RequestBody List<FilmRequestDto> requests) {
        return filmService.bulkCreate(requests);
    }

    // 14 Bulk delete (ids in body)
    @Operation(summary = "Bulk delete by ids")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bulkDelete(@RequestBody List<Short> ids) { filmService.bulkDelete(ids); }

    // 15 Get description only
    @Operation(summary = "Get film description")
    @GetMapping("/{id}/description")
    public String getDescription(@PathVariable("id") @Min(1) short id) { return filmService.getDescription(id); }

    // 16 Update description only
    @Operation(summary = "Update film description")
    @PutMapping("/{id}/description")
    public FilmResponseDto updateDescription(@PathVariable("id") @Min(1) short id,
                                             @Valid @RequestBody UpdateDescriptionRequest body) {
        return filmService.updateDescription(id, body.getDescription());
    }

    // 17 Update rating only
    @Operation(summary = "Update film rating")
    @PutMapping("/{id}/rating")
    public FilmResponseDto updateRating(@PathVariable("id") @Min(1) short id,
                                        @Valid @RequestBody UpdateRatingRequest body) {
        return filmService.updateRating(id, body.getRating());
    }

    // 18 Get last_update timestamp
    @Operation(summary = "Get last_update timestamp")
    @GetMapping("/{id}/last-update")
    public Timestamp getLastUpdate(@PathVariable("id") @Min(1) short id) { return filmService.getLastUpdate(id); }

    // 19 Title-only search (alias to basic list with title filter)
    @Operation(summary = "Quick search by title (alias)")
    @GetMapping("/search/title")
    public PagedResponse<FilmResponseDto> searchTitle(
            @RequestParam(name = "q") String q,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return filmService.list(q, null, null, pageable);
    }

    // 20 Filter by rating only
    @Operation(summary = "Filter by rating (alias)")
    @GetMapping("/search/rating")
    public PagedResponse<FilmResponseDto> searchRating(
            @RequestParam(name = "rating") String rating,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("film_id").ascending());
        return filmService.list(null, rating, null, pageable);
    }

    // 21 Filter by language only
    @Operation(summary = "Filter by languageId (alias)")
    @GetMapping("/search/language")
    public PagedResponse<FilmResponseDto> searchLanguage(
            @RequestParam(name = "languageId") Short languageId,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("film_id").ascending());
        return filmService.list(null, null, languageId, pageable);
    }

    // 22 Release year range only
    @Operation(summary = "Filter by release year range (alias)")
    @GetMapping("/search/release-year")
    public PagedResponse<FilmResponseDto> searchReleaseYearRange(
            @RequestParam(name = "min", required = false) Short min,
            @RequestParam(name = "max", required = false) Short max,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("release_year").ascending());
        return filmService.advancedSearch(null, null, null, min, max, null, null, null, null, null, null, pageable);
    }

    // 23 Length range only
    @Operation(summary = "Filter by length range (alias)")
    @GetMapping("/search/length")
    public PagedResponse<FilmResponseDto> searchLengthRange(
            @RequestParam(name = "min", required = false) Short min,
            @RequestParam(name = "max", required = false) Short max,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("length").ascending());
        return filmService.advancedSearch(null, null, null, null, null, min, max, null, null, null, null, pageable);
    }

    // 24 Rental rate range only
    @Operation(summary = "Filter by rental rate range (alias)")
    @GetMapping("/search/rental-rate")
    public PagedResponse<FilmResponseDto> searchRentalRateRange(
            @RequestParam(name = "min", required = false) BigDecimal min,
            @RequestParam(name = "max", required = false) BigDecimal max,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rental_rate").ascending());
        return filmService.advancedSearch(null, null, null, null, null, null, null, min, max, null, null, pageable);
    }

    // 25 Rental duration range only
    @Operation(summary = "Filter by rental duration range (alias)")
    @GetMapping("/search/rental-duration")
    public PagedResponse<FilmResponseDto> searchRentalDurationRange(
            @RequestParam(name = "min", required = false) Short min,
            @RequestParam(name = "max", required = false) Short max,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rental_duration").ascending());
        return filmService.advancedSearch(null, null, null, null, null, null, null, null, null, min, max, pageable);
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        String dir = parts.length > 1 ? parts[1] : "asc";
        return "desc".equalsIgnoreCase(dir) ? Sort.by(field).descending() : Sort.by(field).ascending();
    }
}
