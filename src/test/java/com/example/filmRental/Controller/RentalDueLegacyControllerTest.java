package com.example.filmRental.Controller;

import com.example.filmRental.Dto.RentalDueDto;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Service.RentalDashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(controllers = RentalDueLegacyController.class)
@Import(GlobalExceptionHandler.class)
class RentalDueLegacyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private RentalDashboardService service;

    @Test @DisplayName("GET /api/rental/due/store/{id} -> 200")
    void due_store_legacy_ok() throws Exception {
        List<RentalDueDto> list = List.of(new RentalDueDto(1002, (short)1, 6, "JOHN DOE", 11, "ALABAMA DEVIL", "2005-05-26 14:30:00", 3));
        Mockito.when(service.dueByStore((short)1)).thenReturn(list);

        mockMvc.perform(get("/api/rental/due/store/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("ALABAMA DEVIL")));
    }
}
