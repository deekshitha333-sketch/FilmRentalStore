package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice test for RentalController using MockMvc.
 * GlobalExceptionHandler imported to test error mappings.
 */
@WebMvcTest(controllers = RentalController.class)
@Import(GlobalExceptionHandler.class)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @Autowired
    private ObjectMapper objectMapper;

    private RentalResponseDto sampleRentalDto;

    @BeforeEach
    void setUp() {
        sampleRentalDto = new RentalResponseDto();
        sampleRentalDto.setId(1001);
        sampleRentalDto.setInventoryId(200);
        sampleRentalDto.setCustomerId((short) 1);
        sampleRentalDto.setStaffId((short) 1);
        sampleRentalDto.setRentalDate(LocalDateTime.now());
        sampleRentalDto.setReturnDate(null);
    }

    @Test
    @DisplayName("POST /api/v1/rentals -> 201 Created")
    void createRental_returns201() throws Exception {
        Mockito.when(rentalService.create(Mockito.any(RentalCreateRequestDto.class)))
                .thenReturn(sampleRentalDto);

        RentalCreateRequestDto req = new RentalCreateRequestDto();
        req.setInventoryId(200);
        req.setCustomerId((short)1);
        req.setStaffId((short)1);

        mockMvc.perform(post("/api/v1/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1001)))
                .andExpect(jsonPath("$.inventoryId", is(200)))
                .andExpect(jsonPath("$.customerId", is(1)));
    }

    @Test
    @DisplayName("PUT /api/v1/rentals/{id}/return -> 200 OK")
    void returnRental_returns200() throws Exception {
        RentalResponseDto returned = new RentalResponseDto();
        returned.setId(1001);
        returned.setInventoryId(200);
        returned.setCustomerId((short)1);
        returned.setStaffId((short)1);
        returned.setRentalDate(LocalDateTime.now().minusDays(1));
        returned.setReturnDate(LocalDateTime.now());

        Mockito.when(rentalService.returnRental(Mockito.eq(1001), Mockito.any(RentalReturnRequestDto.class)))
                .thenReturn(returned);

        RentalReturnRequestDto body = new RentalReturnRequestDto();
        body.setReturnDate(LocalDateTime.now());

        mockMvc.perform(put("/api/v1/rentals/{id}/return", 1001)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1001)))
                .andExpect(jsonPath("$.returnDate", notNullValue()));
    }

    @Test
    @DisplayName("PUT /api/v1/rentals/{id}/return -> 409 when already returned")
    void returnRental_conflict409() throws Exception {
        Mockito.when(rentalService.returnRental(Mockito.eq(9999), Mockito.any()))
                .thenThrow(new IllegalStateException("Rental already returned: 9999"));

        mockMvc.perform(put("/api/v1/rentals/{id}/return", 9999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already returned")));
    }

    @Test
    @DisplayName("GET /api/v1/rentals/customer/{id}/open -> 200 with PagedResponse")
    void openByCustomer_ok() throws Exception {
        RentalResponseDto r1 = new RentalResponseDto();
        r1.setId(7000);
        r1.setInventoryId(300);
        r1.setCustomerId((short)1);
        r1.setStaffId((short)1);
        r1.setRentalDate(LocalDateTime.now().minusHours(3));

        PagedResponse<RentalResponseDto> page = PagedResponse.<RentalResponseDto>builder()
                .setContent(List.of(r1))
                .setTotalElements(1L)
                .setTotalPages(1)
                .setPageNumber(0)
                .setPageSize(10)
                .setHasNext(false)
                .setHasPrevious(false)
                .build();

        // ✅ Disambiguate generics: use Mockito.any(Pageable.class)
        Mockito.when(rentalService.openByCustomer(Mockito.eq((short)1), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/rentals/customer/{customerId}/open", 1)
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(7000)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/rentals/customer/{id}/history -> 404 when customer not found")
    void historyByCustomer_404() throws Exception {
        Mockito.when(rentalService.historyByCustomer(Mockito.eq((short)9), Mockito.any(Pageable.class)))
                .thenThrow(new NotFoundException("Customer not found: 9"));

        mockMvc.perform(get("/api/v1/rentals/customer/{customerId}/history", 9)
                        .param("page","0").param("size","10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Customer not found")));
    }
}
}