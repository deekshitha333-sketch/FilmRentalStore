package com.example.filmRental.Service;

import com.example.filmRental.Dto.FilmRequestDto;
import com.example.filmRental.Dto.FilmResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Entity.Film;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.FilmRepository;
import com.example.filmRental.Service.impl.FilmServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FilmServiceImplTest {

    private Film sampleFilm(short id) {
        Film f = new Film();
        f.setFilm_id(id);
        f.setTitle("SAMPLE-" + id);
        f.setDescription("Sample description " + id);
        f.setRelease_year((short) 2005);
        f.setLanguage_id((short) 1);
        f.setRental_duration((short) 3);
        f.setRental_rate(new BigDecimal("4.99"));
        f.setLength((short) 120);
        f.setReplacement_cost(new BigDecimal("19.99"));
        f.setRating("PG-13");
        f.setSpecial_features("Trailers,Deleted Scenes");
        f.setLast_update(Timestamp.from(Instant.now()));
        return f;
    }

    private FilmRequestDto minimalRequest() {
        FilmRequestDto req = new FilmRequestDto();
        req.setTitle("NEW");
        req.setLanguageId((short) 1);
        req.setRentalDuration((short) 3);
        req.setRentalRate(new BigDecimal("2.99"));
        req.setReplacementCost(new BigDecimal("9.99"));
        return req;
    }

    @Test
    void create_ok() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        when(repo.save(any())).thenAnswer(inv -> {
            Film f = inv.getArgument(0);
            f.setFilm_id((short) 10);
            f.setLast_update(Timestamp.from(Instant.now()));
            return f;
        });

        FilmRequestDto req = minimalRequest();
        FilmResponseDto created = service.create(req);

        assertEquals((short) 10, created.getId());
        assertEquals("NEW", created.getTitle());
        verify(repo, times(1)).save(any(Film.class));
    }

    @Test
    void getById_ok() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        Film f = sampleFilm((short) 1);
        when(repo.findById((short) 1)).thenReturn(Optional.of(f));

        FilmResponseDto dto = service.getById((short) 1);
        assertEquals((short) 1, dto.getId());
        assertEquals("SAMPLE-1", dto.getTitle());
    }

    @Test
    void getById_not_found() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        when(repo.findById((short) 9)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById((short) 9));
    }

    @Test
    void list_basic_ok() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        Film f = sampleFilm((short) 2);
        Page<Film> page = new PageImpl<>(List.of(f), PageRequest.of(0, 10), 1);
        when(repo.advancedSearch(any(), any(), any(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                any(Pageable.class)))
                .thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("film_id").ascending());
        PagedResponse<?> resp = service.list("ACE", null, null, pageable);

        assertEquals(1, resp.getContent().size());
        assertEquals(1, resp.getTotalElements());
        assertEquals(0, resp.getPageNumber());
        verify(repo, times(1)).advancedSearch(any(), any(), any(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(pageable));
    }

    @Test
    void advancedSearch_ok() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        Film f = sampleFilm((short) 3);
        Page<Film> page = new PageImpl<>(List.of(f), PageRequest.of(1, 5), 6);
        when(repo.advancedSearch(any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(),
                any(Pageable.class)))
                .thenReturn(page);

        Pageable pageable = PageRequest.of(1, 5, Sort.by("title").ascending());
        PagedResponse<?> resp = service.advancedSearch(
                "ACE", "PG", (short) 1,
                (short) 2000, (short) 2010,
                (short) 60, (short) 180,
                new BigDecimal("0.99"), new BigDecimal("4.99"),
                (short) 1, (short) 7,
                pageable
        );

        assertEquals(1, resp.getContent().size());
        assertEquals(6, resp.getTotalElements());
        assertEquals(1, resp.getPageNumber());
    }

    @Test
    void update_ok() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        Film f = sampleFilm((short) 4);
        when(repo.findById((short) 4)).thenReturn(Optional.of(f));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FilmRequestDto req = minimalRequest();
        req.setTitle("UPDATED");
        FilmResponseDto dto = service.update((short) 4, req);

        assertEquals((short) 4, dto.getId());
        assertEquals("UPDATED", dto.getTitle());
        verify(repo, times(1)).save(any(Film.class));
    }

    @Test
    void patch_ok() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        Film f = sampleFilm((short) 5);
        when(repo.findById((short) 5)).thenReturn(Optional.of(f));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FilmRequestDto req = new FilmRequestDto();
        req.setTitle("PATCHED");
        FilmResponseDto dto = service.patch((short) 5, req);

        assertEquals("PATCHED", dto.getTitle());
    }

    @Test
    void delete_ok() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        when(repo.existsById((short) 6)).thenReturn(true);
        service.delete((short) 6);

        verify(repo, times(1)).deleteById((short) 6);
    }

    @Test
    void delete_notFound() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        when(repo.existsById((short) 7)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete((short) 7));
        verify(repo, never()).deleteById(anyShort());
    }

    @Test
    void exists_and_count() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        when(repo.existsById((short) 8)).thenReturn(true);
        when(repo.count()).thenReturn(42L);

        assertTrue(service.exists((short) 8));
        assertEquals(42L, service.count());
    }

    @Test
    void distincts_and_recent() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        when(repo.distinctRatings()).thenReturn(List.of("G", "PG", "PG-13"));
        when(repo.distinctReleaseYears()).thenReturn(List.of((short) 2001, (short) 2006));

        Film f = sampleFilm((short) 9);
        Page<Film> recent = new PageImpl<>(List.of(f), PageRequest.of(0, 10), 1);
        when(repo.findRecent(any(Pageable.class))).thenReturn(recent);

        assertEquals(3, service.distinctRatings().size());
        assertEquals(2, service.distinctReleaseYears().size());

        PagedResponse<?> resp = service.recent(PageRequest.of(0, 10));
        assertEquals(1, resp.getContent().size());
    }

    @Test
    void bulkCreate_and_bulkDelete() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        List<Film> saved = new ArrayList<>();
        Film f1 = sampleFilm((short) 11);
        Film f2 = sampleFilm((short) 12);
        saved.add(f1);
        saved.add(f2);

        when(repo.saveAll(anyList())).thenReturn(saved);

        FilmRequestDto r1 = minimalRequest();
        FilmRequestDto r2 = minimalRequest();
        r2.setTitle("SECOND");

        List<FilmResponseDto> out = service.bulkCreate(List.of(r1, r2));
        assertEquals(2, out.size());
        assertEquals((short) 11, out.get(0).getId());
        assertEquals((short) 12, out.get(1).getId());

        service.bulkDelete(List.of((short) 11, (short) 12));
        verify(repo, times(1)).deleteAllByIdInBatch(anyList());
    }

    @Test
    void description_get_and_update() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        Film f = sampleFilm((short) 13);
        when(repo.findById((short) 13)).thenReturn(Optional.of(f));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // getDescription
        String desc = service.getDescription((short) 13);
        assertTrue(desc.startsWith("Sample description"));

        // updateDescription
        FilmResponseDto dto = service.updateDescription((short) 13, "NEW-DESC");
        assertEquals("NEW-DESC", dto.getDescription());
    }

    @Test
    void rating_update_and_lastUpdate_get() {
        FilmRepository repo = Mockito.mock(FilmRepository.class);
        FilmService service = new FilmServiceImpl(repo);

        Film f = sampleFilm((short) 14);
        when(repo.findById((short) 14)).thenReturn(Optional.of(f));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // updateRating
        FilmResponseDto dto = service.updateRating((short) 14, "PG");
        assertEquals("PG", dto.getRating());

        // getLastUpdate
        Timestamp ts = service.getLastUpdate((short) 14);
        assertNotNull(ts);
    }
}