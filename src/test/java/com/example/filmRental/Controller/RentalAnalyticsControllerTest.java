package com.example.filmRental.Controller;
import com.example.filmRental.Dto.FilmRentCountDto;
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

@WebMvcTest(controllers = RentalAnalyticsController.class)
@Import(GlobalExceptionHandler.class)
class RentalAnalyticsControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AnalyticsService service;

    @Test @DisplayName("GET /api/v1/analytics/rentals/toptenfilms -> 200")
    void topTen_ok() throws Exception {
        Mockito.when(service.topTenFilmsAllStores()).thenReturn(List.of(new FilmRentCountDto(10, "ACADEMY DINOSAUR", 100L)));
        mockMvc.perform(get("/api/v1/analytics/rentals/toptenfilms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("ACADEMY DINOSAUR")));
    }
}
