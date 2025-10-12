package com.example.filmRental.Controller;

import com.example.filmRental.Exception.GlobalExceptionHandler;
import com.example.filmRental.Service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;  // <-- import
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;      // <-- import
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false) // <-- disables the Spring Security filter chain in this test
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired MockMvc mvc;
    @Mock CustomerService service;

    @Test
    void active_ok() throws Exception {
        // no authentication needed; filters are disabled
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/customers/active"))
           .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    }
}
