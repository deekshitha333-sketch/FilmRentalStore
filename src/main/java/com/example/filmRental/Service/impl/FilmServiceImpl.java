package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.FilmRequestDto;
import com.example.filmRental.Dto.FilmResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Entity.Film;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Mapper.FilmMapper;
import com.example.filmRental.Repository.FilmRepository;
import com.example.filmRental.Service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmRepository repository;

    @Autowired
    public FilmServiceImpl(FilmRepository repository) {
        this.repository = repository;
    }

    @Override
    public FilmResponseDto create(FilmRequestDto request) {
        Film saved = repository.save(FilmMapper.toEntity(request));
        return FilmMapper.toDto(saved);
    }

    @Override
    public FilmResponseDto getById(short id) {
        Film film = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Film not found: " + id));
        return FilmMapper.toDto(film);
    }

    @Override
    public PagedResponse<FilmResponseDto> list(String title, String rating, Short languageId, Pageable pageable) {
        Page<Film> page = repository.advancedSearch(
                empty(title), empty(rating), languageId,
                null, null, null, null, null, null, null, null,
                pageable);
        return toPaged(page, pageable);
    }

    @Override
    public PagedResponse<FilmResponseDto> advancedSearch(
            String title, String rating, Short languageId,
            Short releaseYearMin, Short releaseYearMax,
            Short lengthMin, Short lengthMax,
            BigDecimal rentalRateMin, BigDecimal rentalRateMax,
            Short rentalDurationMin, Short rentalDurationMax,
            Pageable pageable) {
        Page<Film> page = repository.advancedSearch(
                empty(title), empty(rating), languageId,
                releaseYearMin, releaseYearMax,
                lengthMin, lengthMax,
                rentalRateMin, rentalRateMax,
                rentalDurationMin, rentalDurationMax,
                pageable);
        return toPaged(page, pageable);
    }

    @Override
    public FilmResponseDto update(short id, FilmRequestDto request) {
        Film film = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Film not found: " + id));
        FilmMapper.updateEntity(film, request);
        Film saved = repository.save(film);
        return FilmMapper.toDto(saved);
    }

    @Override
    public FilmResponseDto patch(short id, FilmRequestDto request) {
        return update(id, request);
    }

    @Override
    public void delete(short id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Film not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean exists(short id) { return repository.existsById(id); }

    @Override
    public long count() { return repository.count(); }

    @Override
    public List<String> distinctRatings() { return repository.distinctRatings(); }

    @Override
    public List<Short> distinctReleaseYears() { return repository.distinctReleaseYears(); }

    @Override
    public PagedResponse<FilmResponseDto> recent(Pageable pageable) {
        Page<Film> page = repository.findRecent(pageable);
        return toPaged(page, pageable);
    }

    @Override
    public List<FilmResponseDto> bulkCreate(List<FilmRequestDto> requests) {
        List<Film> toSave = new ArrayList<>();
        for (FilmRequestDto r : requests) {
            toSave.add(FilmMapper.toEntity(r));
        }
        List<Film> saved = repository.saveAll(toSave);
        return saved.stream().map(FilmMapper::toDto).toList();
    }

    @Override
    public void bulkDelete(List<Short> ids) {
        repository.deleteAllByIdInBatch(ids);
    }

    @Override
    public String getDescription(short id) {
        Film film = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Film not found: " + id));
        return film.getDescription();
    }

    @Override
    public FilmResponseDto updateDescription(short id, String description) {
        Film film = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Film not found: " + id));
        film.setDescription(description);
        film.setLast_update(Timestamp.from(Instant.now()));
        Film saved = repository.save(film);
        return FilmMapper.toDto(saved);
    }

    @Override
    public FilmResponseDto updateRating(short id, String rating) {
        Film film = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Film not found: " + id));
        film.setRating(rating);
        film.setLast_update(Timestamp.from(Instant.now()));
        Film saved = repository.save(film);
        return FilmMapper.toDto(saved);
    }

    @Override
    public Timestamp getLastUpdate(short id) {
        Film film = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Film not found: " + id));
        return film.getLast_update();
    }

    private PagedResponse<FilmResponseDto> toPaged(Page<Film> page, Pageable pageable) {
        return PagedResponse.<FilmResponseDto>builder()
                .setContent(page.getContent().stream().map(FilmMapper::toDto).toList())
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setPageNumber(pageable.getPageNumber())
                .setPageSize(pageable.getPageSize())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
    }

    private String empty(String s) { return (s == null || s.isBlank()) ? null : s; }
}
