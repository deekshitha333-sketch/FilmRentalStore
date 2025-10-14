package com.example.filmRental.Service.impl;


import com.example.filmRental.Dto.*;
import com.example.filmRental.Entity.Film;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.FilmRepository;
import com.example.filmRental.Service.impl.FilmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

 @Mock private FilmRepository repository;
 @InjectMocks private FilmServiceImpl service;

 private Film film;
 private Pageable pageable;

 @BeforeEach
 void setUp() {
     pageable = PageRequest.of(0, 10, Sort.by("film_id").ascending());
     film = new Film();
     film.setFilm_id((short) 1);
     film.setTitle("ACADEMY DINOSAUR");
     film.setDescription("desc");
     film.setRelease_year((short) 2006);
     film.setLanguage_id((short) 1);
     film.setRental_duration((short) 3);
     film.setRental_rate(new BigDecimal("4.99"));
     film.setLength((short) 90);
     film.setReplacement_cost(new BigDecimal("19.99"));
     film.setRating("PG");
     film.setSpecial_features("Deleted Scenes");
     film.setLast_update(Timestamp.from(Instant.now()));
 }

 @Test
 void create_and_get_ok() {
     when(repository.save(any(Film.class))).thenAnswer(inv -> {
         Film saved = inv.getArgument(0);
         saved.setFilm_id((short) 1);
         return saved;
     });
     FilmRequestDto req = new FilmRequestDto();
     req.setTitle("X");
     req.setLanguageId((short) 1);
     req.setRentalDuration((short) 3);
     req.setRentalRate(new BigDecimal("1.00"));
     req.setReplacementCost(new BigDecimal("2.00"));

     FilmResponseDto created = service.create(req);
     assertThat(created.getId()).isEqualTo((short) 1);

     when(repository.findById((short) 1)).thenReturn(Optional.of(film));
     FilmResponseDto got = service.getById((short) 1);
     assertThat(got.getTitle()).isEqualTo("ACADEMY DINOSAUR");
 }

 @Test
 void getById_notFound() {
     when(repository.findById((short) 9)).thenReturn(Optional.empty());
     assertThrows(NotFoundException.class, () -> service.getById((short) 9));
 }

 @Test
 void list_and_advancedSearch_ok() {
     Page<Film> page = new PageImpl<>(List.of(film), pageable, 1);
     when(repository.advancedSearch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(pageable)))
             .thenReturn(page);

     PagedResponse<FilmResponseDto> list = service.list("ACA", "PG", (short) 1, pageable);
     PagedResponse<FilmResponseDto> adv = service.advancedSearch("ACA", "PG", (short) 1,
             (short) 2000, (short) 2020, (short) 60, (short) 120,
             new BigDecimal("0.99"), new BigDecimal("9.99"),
             (short) 1, (short) 7, pageable);

     assertThat(list.getContent()).hasSize(1);
     assertThat(adv.getContent()).hasSize(1);
 }

 @Test
 void update_patch_delete_and_metadata() {
     when(repository.findById((short) 1)).thenReturn(Optional.of(film));
     when(repository.save(any(Film.class))).thenAnswer(inv -> inv.getArgument(0));

     FilmRequestDto req = new FilmRequestDto();
     req.setTitle("NEW TITLE");
     FilmResponseDto up = service.update((short) 1, req);
     assertThat(up.getTitle()).isEqualTo("NEW TITLE");

     FilmResponseDto pat = service.patch((short) 1, req);
     assertThat(pat.getTitle()).isEqualTo("NEW TITLE");

     assertThat(service.getDescription((short) 1)).isEqualTo("desc");
     FilmResponseDto updDesc = service.updateDescription((short) 1, "D2");
     assertThat(updDesc.getDescription()).isEqualTo("D2");
     FilmResponseDto updRating = service.updateRating((short) 1, "R");
     assertThat(updRating.getRating()).isEqualTo("R");
     assertThat(service.getLastUpdate((short) 1)).isNotNull();

     when(repository.existsById((short) 1)).thenReturn(true);
     service.delete((short) 1);
     verify(repository).deleteById((short) 1);

     when(repository.existsById((short) 9)).thenReturn(false);
     assertThrows(NotFoundException.class, () -> service.delete((short) 9));
 }

 @Test
 void distincts_recent_bulk() {
     when(repository.distinctRatings()).thenReturn(List.of("G", "PG"));
     when(repository.distinctReleaseYears()).thenReturn(List.of((short) 2006, (short) 2007));
     when(repository.findRecent(pageable)).thenReturn(new PageImpl<>(List.of(film), pageable, 1));

     assertThat(service.distinctRatings()).containsExactly("G", "PG");
     assertThat(service.distinctReleaseYears()).containsExactly((short) 2006, (short) 2007);
     assertThat(service.recent(pageable).getContent()).hasSize(1);

     // bulk
     FilmRequestDto minimal = new FilmRequestDto();
     minimal.setTitle("A"); minimal.setLanguageId((short) 1);
     minimal.setRentalDuration((short) 1);
     minimal.setRentalRate(new BigDecimal("0.99"));
     minimal.setReplacementCost(new BigDecimal("1.99"));

     when(repository.saveAll(anyList())).thenAnswer(inv -> {
         List<Film> toSave = inv.getArgument(0);
         short id = 1;
         for (Film f : toSave) f.setFilm_id(id++);
         return toSave;
     });

     List<FilmResponseDto> saved = service.bulkCreate(List.of(minimal, minimal));
     assertThat(saved).hasSize(2);

     service.bulkDelete(List.of((short) 1, (short) 2));
     verify(repository).deleteAllByIdInBatch(List.of((short) 1, (short) 2));
 }
}
