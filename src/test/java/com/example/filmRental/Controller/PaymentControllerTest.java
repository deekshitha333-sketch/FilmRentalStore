package com.example.filmRental.Controller;

import com.example.filmRental.Dto.PaymentCreateRequestDto;
import com.example.filmRental.Dto.PaymentResponseDto;
import com.example.filmRental.Dto.RevenueByDateDto;
import com.example.filmRental.Dto.RevenueByFilmDto;
import com.example.filmRental.Exception.BadRequestException;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Service.PaymentService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/payments -> 201 Created")
    void createPayment_returns201() throws Exception {
        PaymentCreateRequestDto req = new PaymentCreateRequestDto(
                (short) 1, (short) 1, 1000, new BigDecimal("9.99"), LocalDateTime.now()
        );

        PaymentResponseDto resp = new PaymentResponseDto(
                5000, (short) 1, (short) 1, 1000, new BigDecimal("9.99"), LocalDateTime.now()
        );

        // Use Mockito.when(...) explicitly to avoid ambiguity
        Mockito.when(paymentService.create(Mockito.any(PaymentCreateRequestDto.class)))
               .thenReturn(resp);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id", is(5000)))
               .andExpect(jsonPath("$.amount", is(9.99)));
    }

    @Test
    @DisplayName("POST /api/v1/payments -> 400 when service throws BadRequestException")
    void createPayment_400() throws Exception {
        Mockito.when(paymentService.create(Mockito.any(PaymentCreateRequestDto.class)))
               .thenThrow(new BadRequestException("Rental does not belong to customer"));

        PaymentCreateRequestDto req = new PaymentCreateRequestDto(
                (short) 1, (short) 1, 9999, new BigDecimal("9.99"), LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("GET /api/v1/payments/revenue/datewise -> 200 with array")
    void revenueDatewiseAll_ok() throws Exception {
        List<RevenueByDateDto> list = List.of(
                new RevenueByDateDto(LocalDate.now().minusDays(1), new BigDecimal("19.98")),
                new RevenueByDateDto(LocalDate.now(), new BigDecimal("29.97"))
        );
        Mockito.when(paymentService.revenueDatewiseAll()).thenReturn(list);

        mockMvc.perform(get("/api/v1/payments/revenue/datewise"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].amount", is(19.98)));
    }

    @Test
    @DisplayName("GET /api/v1/payments/revenue/filmwise -> 200 with array")
    void revenueFilmwiseAll_ok() throws Exception {
        List<RevenueByFilmDto> list = List.of(
                new RevenueByFilmDto(10, "ACADEMY DINOSAUR", new BigDecimal("123.45"))
        );
        Mockito.when(paymentService.revenueFilmwiseAll()).thenReturn(list);

        mockMvc.perform(get("/api/v1/payments/revenue/filmwise"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].filmId", is(10)))
               .andExpect(jsonPath("$[0].title", is("ACADEMY DINOSAUR")));
    }
}
