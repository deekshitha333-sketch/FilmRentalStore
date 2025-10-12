package com.example.filmRental.Controller;
import com.example.filmRental.Dto.DateAmountDto;
import com.example.filmRental.Service.AnalyticsService;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentAnalyticsController.class)
@Import(GlobalExceptionHandler.class)
class PaymentAnalyticsControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AnalyticsService service;

    @Test @DisplayName("GET /api/v1/analytics/payments/revenue/datewise -> 200")
    void datewise_ok() throws Exception {
        Mockito.when(service.revenueDatewiseAll()).thenReturn(List.of(new DateAmountDto("2005-05-25", new java.math.BigDecimal("123.45"))));
        mockMvc.perform(get("/api/v1/analytics/payments/revenue/datewise"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date", is("2005-05-25")));
    }
}
