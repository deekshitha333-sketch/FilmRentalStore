package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.StoreService;
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

/**
 * MockMvc slice tests for StoreController (v1).
 */
@WebMvcTest(controllers = StoreController.class)
@Import(GlobalExceptionHandler.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/stores -> 201 Created")
    void createStore_created201() throws Exception {
        StoreCreateRequestDto req = new StoreCreateRequestDto((short)1, (short)2);
        StoreResponseDto resp = new StoreResponseDto((short)5, (short)1, (short)2,
                "47 MySakila Drive", "A Coruna (La Coruna)", "Spain", "044-123456");

        Mockito.when(storeService.create(Mockito.any(StoreCreateRequestDto.class)))
               .thenReturn(resp);

        mockMvc.perform(post("/api/v1/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.storeId", is(5)))
               .andExpect(jsonPath("$.managerStaffId", is(1)))
               .andExpect(jsonPath("$.address", containsString("MySakila")));
    }

    @Test
    @DisplayName("PUT /api/v1/stores/{id}/address/{addr} -> 200 OK")
    void assignAddress_ok() throws Exception {
        StoreResponseDto resp = new StoreResponseDto((short)5, (short)1, (short)3,
                "28 MySQL Boulevard", "QLD", "Australia", "044-123456");
        Mockito.when(storeService.assignAddress((short)5, (short)3)).thenReturn(resp);

        mockMvc.perform(put("/api/v1/stores/{storeId}/address/{addressId}", 5, 3))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.addressId", is(3)))
               .andExpect(jsonPath("$.city", is("QLD")));
    }

    @Test
    @DisplayName("PUT /api/v1/stores/{id}/manager/{mgr} -> 404 when store not found")
    void assignManager_notFound() throws Exception {
        Mockito.when(storeService.assignManager((short)999, (short)1))
               .thenThrow(new NotFoundException("Store not found: 999"));

        mockMvc.perform(put("/api/v1/stores/{storeId}/manager/{managerStaffId}", 999, 1))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.status", is(404)))
               .andExpect(jsonPath("$.message", containsString("Store not found")));
    }

    @Test
    @DisplayName("PUT /api/v1/stores/{id}/phone -> 200 OK")
    void updatePhone_ok() throws Exception {
        StoreUpdatePhoneRequestDto body = new StoreUpdatePhoneRequestDto("044-777777");
        StoreResponseDto resp = new StoreResponseDto((short)5, (short)1, (short)2,
                "47 MySakila Drive", "A Coruna (La Coruna)", "Spain", "044-777777");

        Mockito.when(storeService.updatePhone((short)5, "044-777777")).thenReturn(resp);

        mockMvc.perform(put("/api/v1/stores/{storeId}/phone", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.phone", is("044-777777")));
    }

    @Test
    @DisplayName("PUT /api/v1/stores/{id}/phone -> 400 BadRequest (service)")
    void updatePhone_badRequest() throws Exception {
        StoreUpdatePhoneRequestDto body = new StoreUpdatePhoneRequestDto("bad-phone");
        Mockito.when(storeService.updatePhone((short)1, "bad-phone"))
               .thenThrow(new BadRequestException("Invalid phone"));

        mockMvc.perform(put("/api/v1/stores/{storeId}/phone", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("GET /api/v1/stores/{id}/staff -> 200 OK with list")
    void staff_ok() throws Exception {
        List<StaffSummaryDto> list = List.of(
                new StaffSummaryDto((short)1, "Mike", "Hillyer", "mike@example.com", "111-111"),
                new StaffSummaryDto((short)2, "Jon", "Stephens", "jon@example.com", "222-222")
        );
        Mockito.when(storeService.staffByStore((short)1)).thenReturn(list);

        mockMvc.perform(get("/api/v1/stores/{storeId}/staff", 1))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].staffId", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/stores/managers -> 200 OK with list")
    void managers_ok() throws Exception {
        List<StoreManagerDetailsDto> list = List.of(
                new StoreManagerDetailsDto((short)1, "Mike", "Hillyer", "mike@example.com", "111-111",
                        "47 MySakila Drive", "A Coruna (La Coruna)")
        );
        Mockito.when(storeService.managers()).thenReturn(list);

        mockMvc.perform(get("/api/v1/stores/managers"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].managerFirstName", is("Mike")))
               .andExpect(jsonPath("$[0].storeId", is(1)));
    }
}