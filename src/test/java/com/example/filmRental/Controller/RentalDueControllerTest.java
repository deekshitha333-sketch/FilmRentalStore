package com.example.filmRental.Controller;

import com.example.filmRental.Dto.RentalDueDto;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Service.RentalDashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RentalDueController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RentalDueControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private RentalDashboardService service;

    @Test @DisplayName("GET /api/v1/analytics/rentals/due/store/{storeId} -> 200")
    void due_store_ok() throws Exception {
        List<RentalDueDto> list = List.of(new RentalDueDto(1001, (short)1, 5, "MARY SMITH", 10, "ACADEMY DINOSAUR", "2005-05-25 12:00:00", 7));
        Mockito.when(service.dueByStore((short)1)).thenReturn(list);

        mockMvc.perform(get("/api/v1/analytics/rentals/due/store/{storeId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rentalId", is(1001)))
                .andExpect(jsonPath("$[0].daysOutstanding", is(7)));
    }
}
