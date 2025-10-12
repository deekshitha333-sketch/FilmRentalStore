package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.FilmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FilmController.class)
@Import(GlobalExceptionHandler.class)
class FilmControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean FilmService service;

    FilmResponseDto sample;

    @BeforeEach
    void setup() {
        sample = new FilmResponseDto();
        sample.setId((short) 1);
        sample.setTitle("ACE GOLDFINGER");
        sample.setDescription("A spy action film");
        sample.setReleaseYear((short) 2006);
        sample.setLanguageId((short) 1);
        sample.setRentalDuration((short) 3);
        sample.setRentalRate(new BigDecimal("4.99"));
        sample.setLength((short) 120);
        sample.setReplacementCost(new BigDecimal("19.99"));
        sample.setRating("PG-13");
        sample.setSpecialFeatures("Trailers,Deleted Scenes");
        sample.setLastUpdate(Timestamp.from(Instant.now()));
    }

    @Test void create_ok() throws Exception {
        FilmRequestDto req = new FilmRequestDto();
        req.setTitle("NEW MOVIE"); req.setLanguageId((short)1);
        req.setRentalDuration((short)3); req.setRentalRate(new BigDecimal("2.99"));
        req.setReplacementCost(new BigDecimal("9.99"));
        Mockito.when(service.create(any())).thenReturn(sample);
        mvc.perform(post("/api/v1/films").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void get_ok() throws Exception {
        Mockito.when(service.getById((short)1)).thenReturn(sample);
        mvc.perform(get("/api/v1/films/1")).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("ACE GOLDFINGER"));
    }

    @Test void get_not_found() throws Exception {
        Mockito.when(service.getById((short)9)).thenThrow(new NotFoundException("Film not found: 9"));
        mvc.perform(get("/api/v1/films/9")).andExpect(status().isNotFound());
    }

    @Test void list_basic_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.list(any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films").param("title","ACE")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test void update_ok() throws Exception {
        Mockito.when(service.update(eq((short)1), any())).thenReturn(sample);
        FilmRequestDto req = new FilmRequestDto();
        req.setTitle("UPDATED"); req.setLanguageId((short)1);
        req.setRentalDuration((short)3); req.setRentalRate(new BigDecimal("4.99"));
        req.setReplacementCost(new BigDecimal("19.99"));
        mvc.perform(put("/api/v1/films/1").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void patch_ok() throws Exception {
        Mockito.when(service.patch(eq((short)1), any())).thenReturn(sample);
        FilmRequestDto req = new FilmRequestDto(); req.setTitle("PATCHED");
        mvc.perform(patch("/api/v1/films/1").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void delete_ok() throws Exception { mvc.perform(delete("/api/v1/films/1")).andExpect(status().isNoContent()); }

    @Test void exists_ok() throws Exception {
        Mockito.when(service.exists((short)1)).thenReturn(true);
        mvc.perform(get("/api/v1/films/1/exists")).andExpect(status().isOk()).andExpect(content().string("true"));
    }

    @Test void count_ok() throws Exception {
        Mockito.when(service.count()).thenReturn(42L);
        mvc.perform(get("/api/v1/films/count")).andExpect(status().isOk()).andExpect(content().string("42"));
    }

    @Test void advanced_search_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.advancedSearch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/advanced").param("releaseYearMin","2000")).andExpect(status().isOk());
    }

    @Test void distinct_ratings_ok() throws Exception {
        Mockito.when(service.distinctRatings()).thenReturn(List.of("G","PG"));
        mvc.perform(get("/api/v1/films/distinct/ratings")).andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("G"));
    }

    @Test void distinct_years_ok() throws Exception {
        Mockito.when(service.distinctReleaseYears()).thenReturn(List.of((short)2001,(short)2006));
        mvc.perform(get("/api/v1/films/distinct/release-years")).andExpect(status().isOk()).andExpect(jsonPath("$[0]").value(2001));
    }

    @Test void recent_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.recent(any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/recent")).andExpect(status().isOk());
    }

    @Test void bulk_create_ok() throws Exception {
        Mockito.when(service.bulkCreate(any())).thenReturn(List.of(sample));
        FilmRequestDto req = new FilmRequestDto();
        req.setTitle("BULK"); req.setLanguageId((short)1);
        req.setRentalDuration((short)3); req.setRentalRate(new BigDecimal("2.99"));
        req.setReplacementCost(new BigDecimal("9.99"));
        mvc.perform(post("/api/v1/films/bulk").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(List.of(req))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test void bulk_delete_ok() throws Exception {
        mvc.perform(delete("/api/v1/films").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(List.of(1,2,3))))
                .andExpect(status().isNoContent());
    }

    @Test void get_description_ok() throws Exception {
        Mockito.when(service.getDescription((short)1)).thenReturn("DESC");
        mvc.perform(get("/api/v1/films/1/description")).andExpect(status().isOk()).andExpect(content().string("DESC"));
    }

    @Test void update_description_ok() throws Exception {
        Mockito.when(service.updateDescription(eq((short)1), anyString())).thenReturn(sample);
        UpdateDescriptionRequest body = new UpdateDescriptionRequest(); body.setDescription("NEW");
        mvc.perform(put("/api/v1/films/1/description").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void update_rating_ok() throws Exception {
        Mockito.when(service.updateRating(eq((short)1), anyString())).thenReturn(sample);
        UpdateRatingRequest body = new UpdateRatingRequest(); body.setRating("PG");
        mvc.perform(put("/api/v1/films/1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test void get_last_update_ok() throws Exception {
        Mockito.when(service.getLastUpdate((short)1)).thenReturn(Timestamp.from(Instant.now()));
        mvc.perform(get("/api/v1/films/1/last-update")).andExpect(status().isOk());
    }

    @Test void title_alias_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.list(any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/title").param("q","ACE")).andExpect(status().isOk());
    }

    @Test void rating_alias_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.list(any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/rating").param("rating","PG")).andExpect(status().isOk());
    }

    @Test void language_alias_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.list(any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/language").param("languageId","1")).andExpect(status().isOk());
    }

    @Test void release_year_alias_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.advancedSearch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/release-year").param("min","2000")).andExpect(status().isOk());
    }

    @Test void length_alias_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.advancedSearch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/length").param("min","60")).andExpect(status().isOk());
    }

    @Test void rental_rate_alias_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.advancedSearch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/rental-rate").param("min","0.99")).andExpect(status().isOk());
    }

    @Test void rental_duration_alias_ok() throws Exception {
        PagedResponse<FilmResponseDto> page = PagedResponse.<FilmResponseDto>builder()
                .setContent(List.of(sample)).setTotalElements(1).setTotalPages(1)
                .setPageNumber(0).setPageSize(10).setHasNext(false).setHasPrevious(false).build();
        Mockito.when(service.advancedSearch(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);
        mvc.perform(get("/api/v1/films/search/rental-duration").param("min","1")).andExpect(status().isOk());
    }
} 