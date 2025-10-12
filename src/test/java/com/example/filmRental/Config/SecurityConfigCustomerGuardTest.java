// File: src/test/java/com/example/filmRental/Config/SecurityConfigCustomerGuardTest.java
package com.example.filmRental.Config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestController
@RequestMapping("/api/customers")
class CustomerProbeController {
    @GetMapping("/active") public String active() { return "OK"; }
    @PostMapping(value="/post", consumes=MediaType.APPLICATION_JSON_VALUE) public String post(@RequestBody String s) { return "OK"; }
}

@SpringBootTest(classes = {SecurityConfig.class, CustomerProbeController.class})
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.main.allow-bean-definition-overriding=true" })
class SecurityConfigCustomerGuardTest {

    @Autowired private MockMvc mockMvc;

    @Test @DisplayName("GET /api/customers/** requires auth -> 401")
    void customers_get_requires_auth() throws Exception {
        mockMvc.perform(get("/api/customers/active"))
                .andExpect(status().isUnauthorized());
    }

    @Test @DisplayName("POST /api/customers/** requires auth -> 401")
    void customers_post_requires_auth() throws Exception {
        mockMvc.perform(post("/api/customers/post").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}