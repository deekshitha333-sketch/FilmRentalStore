package com.example.filmRental.Controller;

import com.example.filmRental.Dto.ActorRequestDto;
import com.example.filmRental.Dto.ActorResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.ActorService;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ActorController.class)
@Import(GlobalExceptionHandler.class)
class ActorControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean ActorService service;

    ActorResponseDto sample;

    @BeforeEach
    void setup() {
        sample = new ActorResponseDto();
        sample.setId((short)1);
        sample.setFirstName("PENELOPE");
        sample.setLastName("GUINESS");
        sample.setLastUpdate(Timestamp.from(Instant.now()));
    }

    @Test
    void create_ok() throws Exception {
        ActorRequestDto req = new ActorRequestDto();
        req.setFirstName("John");
        req.setLastName("Doe");
        Mockito.when(service.create(any())).thenReturn(sample);

        mvc.perform(post("/api/v1/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_validation_error() throws Exception {
        ActorRequestDto req = new ActorRequestDto();
        req.setFirstName("");
        req.setLastName("");
        mvc.perform(post("/api/v1/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_ok() throws Exception {
        Mockito.when(service.getById((short)1)).thenReturn(sample);
        mvc.perform(get("/api/v1/actors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("PENELOPE"));
    }

    @Test
    void getById_notFound() throws Exception {
        Mockito.when(service.getById((short)999)).thenThrow(new NotFoundException("Actor not found: 999"));
        mvc.perform(get("/api/v1/actors/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_ok() throws Exception {
        PagedResponse<ActorResponseDto> page = PagedResponse.<ActorResponseDto>builder()
                .setContent(List.of(sample))
                .setTotalElements(1)
                .setTotalPages(1)
                .setPageNumber(0)
                .setPageSize(10)
                .setHasNext(false)
                .setHasPrevious(false)
                .build();

        Mockito.when(service.list(any(), any(), any())).thenReturn(page);

        mvc.perform(get("/api/v1/actors").param("firstName", "PE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void update_ok() throws Exception {
        Mockito.when(service.update(eq((short)1), any())).thenReturn(sample);
        ActorRequestDto req = new ActorRequestDto();
        req.setFirstName("Jane");
        req.setLastName("Smith");
        mvc.perform(put("/api/v1/actors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void patch_ok() throws Exception {
        Mockito.when(service.patch(eq((short)1), any())).thenReturn(sample);
        ActorRequestDto req = new ActorRequestDto();
        req.setFirstName("OnlyFirst");
        mvc.perform(patch("/api/v1/actors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_ok() throws Exception {
        mvc.perform(delete("/api/v1/actors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void exists_ok() throws Exception {
        Mockito.when(service.exists((short)1)).thenReturn(true);
        mvc.perform(get("/api/v1/actors/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void count_ok() throws Exception {
        Mockito.when(service.count()).thenReturn(42L);
        mvc.perform(get("/api/v1/actors/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }
}
