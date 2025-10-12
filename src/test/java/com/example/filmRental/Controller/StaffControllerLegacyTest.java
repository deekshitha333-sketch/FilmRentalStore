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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StaffLegacyController.class)
@Import(GlobalExceptionHandler.class)
class StaffLegacyControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private StaffService service;

    @Test @DisplayName("POST /api/staff/post -> legacy success string")
    void post_legacy_ok() throws Exception {
        StaffCreateRequestDto req = new StaffCreateRequestDto("Bob","King","b@x",(short)1,(short)1,null);
        Mockito.when(service.create(Mockito.any())).thenReturn(
                new StaffResponseDto((short)10,"Bob","King","b@x",(short)1,(short)1,"a","c","co","p",null)
        );

        mockMvc.perform(post("/api/staff/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Record Created Successfully"));
    }

    @Test @DisplayName("PUT /api/staff/update/email/{id} -> 200")
    void updateEmail_legacy_ok() throws Exception {
        StaffUpdateEmailRequestDto req = new StaffUpdateEmailRequestDto("new@x.com");
        Mockito.when(service.updateEmail((short)3, req)).thenReturn(
                new StaffResponseDto((short)3,"Amy","Ray","new@x.com",(short)1,(short)1,"a","c","co","p",null)
        );

        mockMvc.perform(put("/api/staff/update/email/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("new@x.com")));
    }
}
