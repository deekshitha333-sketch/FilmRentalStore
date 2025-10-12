package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Service.StaffService;
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

@WebMvcTest(controllers = StaffController.class)
@Import(GlobalExceptionHandler.class)
class StaffControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private StaffService service;

    @Test @DisplayName("POST /api/v1/staff -> 201")
    void create_ok() throws Exception {
        StaffCreateRequestDto req = new StaffCreateRequestDto("Alice","Brown","alice@x.com",(short)1,(short)10,"http://img");
        StaffResponseDto resp = new StaffResponseDto((short)5,"Alice","Brown","alice@x.com",(short)1,(short)10,
                "47 MySakila Drive","A Coruna (La Coruna)","Spain","555-000","http://img");
        Mockito.when(service.create(Mockito.any())).thenReturn(resp);

        mockMvc.perform(post("/api/v1/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.staffId", is(5)))
                .andExpect(jsonPath("$.firstName", is("Alice")));
    }

    @Test @DisplayName("GET /api/v1/staff/lastname/{lastName} -> array")
    void byLastName_ok() throws Exception {
        List<StaffResponseDto> list = List.of(
                new StaffResponseDto((short)1,"Mary","Smith","m@x",(short)1,(short)1,"a","c","co","p","u")
        );
        Mockito.when(service.findByLastName("Smith")).thenReturn(list);

        mockMvc.perform(get("/api/v1/staff/lastname/{lastName}", "Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lastName", is("Smith")));
    }

    @Test @DisplayName("PUT /api/v1/staff/update/phone/{id} -> 200")
    void updatePhone_ok() throws Exception {
        StaffUpdatePhoneRequestDto req = new StaffUpdatePhoneRequestDto("999");
        StaffResponseDto resp = new StaffResponseDto((short)2,"Eva","White","e@x",(short)1,(short)2,"a","c","co","999","u");
        Mockito.when(service.updatePhone((short)2, req)).thenReturn(resp);

        mockMvc.perform(put("/api/v1/staff/update/phone/{staffId}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone", is("999")));
    }
}
