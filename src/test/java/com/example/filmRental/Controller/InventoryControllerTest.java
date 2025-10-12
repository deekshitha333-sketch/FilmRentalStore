package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InventoryController.class)
@Import(GlobalExceptionHandler.class)
class InventoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private InventoryService inventoryService;

    @Test
    @DisplayName("POST /api/v1/inventory -> 201 Created")
    void add_created() throws Exception {
        InventoryAddRequestDto req = new InventoryAddRequestDto(10, (short)1);
        InventoryResponseDto resp = new InventoryResponseDto(500, 10, (short)1);

        Mockito.when(inventoryService.add(Mockito.any(InventoryAddRequestDto.class)))
               .thenReturn(resp);

        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.inventoryId", is(500)))
               .andExpect(jsonPath("$.filmId", is(10)));
    }

    @Test
    @DisplayName("GET /api/v1/inventory/films -> 200 with array")
    void filmsAll_ok() throws Exception {
        List<FilmInventoryCountDto> list = List.of(
                new FilmInventoryCountDto(10, "ACADEMY DINOSAUR", 7L),
                new FilmInventoryCountDto(25, "ALABAMA DEVIL", 5L)
        );
        Mockito.when(inventoryService.allFilmsAllStores()).thenReturn(list);

        mockMvc.perform(get("/api/v1/inventory/films"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].title", is("ACADEMY DINOSAUR")));
    }

    @Test
    @DisplayName("GET /api/v1/inventory/film/{filmId} -> 404 when film not found")
    void filmAllStores_404() throws Exception {
        Mockito.when(inventoryService.filmAllStores(999))
               .thenThrow(new NotFoundException("Film not found: 999"));

        mockMvc.perform(get("/api/v1/inventory/film/{filmId}", 999))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", containsString("Film not found")));
    }

    @Test
    @DisplayName("GET /api/v1/inventory/film/{filmId}/store/{storeId} -> 200 one row")
    void filmInStore_ok() throws Exception {
        StoreFilmInventoryCountDto dto = new StoreFilmInventoryCountDto((short)1, "47 MySakila Drive, A Coruna (La Coruna)",
                10, "ACADEMY DINOSAUR", 7L);
        Mockito.when(inventoryService.filmInStore(10, (short)1)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/inventory/film/{filmId}/store/{storeId}", 10, 1))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.storeId", is(1)))
               .andExpect(jsonPath("$.title", is("ACADEMY DINOSAUR")))
               .andExpect(jsonPath("$.copies", is(7)));
    }
}