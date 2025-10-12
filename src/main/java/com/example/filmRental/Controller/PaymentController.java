package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Validated
@Tag(name = "Payments", description = "Create payments and revenue analytics")
public class PaymentController {

    private final PaymentService service;

    @Autowired
    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new payment")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto create(@Valid @RequestBody PaymentCreateRequestDto request) {
        return service.create(request);
    }

    @Operation(summary = "Revenue (date-wise) across all stores")
    @GetMapping("/revenue/datewise")
    public List<RevenueByDateDto> revenueDatewiseAll() {
        return service.revenueDatewiseAll();
    }

    @Operation(summary = "Revenue (date-wise) for a store")
    @GetMapping("/revenue/datewise/store/{storeId}")
    public List<RevenueByDateDto> revenueDatewiseByStore(@PathVariable("storeId") @Min(1) short storeId) {
        return service.revenueDatewiseByStore(storeId);
    }

    @Operation(summary = "Revenue per store for a specific film")
    @GetMapping("/revenue/film/{filmId}")
    public List<RevenueByStoreDto> revenueStorewiseByFilm(@PathVariable("filmId") @Min(1) int filmId) {
        return service.revenueStorewiseByFilm(filmId);
    }

    @Operation(summary = "Revenue (film-wise) across all stores")
    @GetMapping("/revenue/filmwise")
    public List<RevenueByFilmDto> revenueFilmwiseAll() {
        return service.revenueFilmwiseAll();
    }

    @Operation(summary = "Revenue (film-wise) for a store")
    @GetMapping("/revenue/films/store/{storeId}")
    public List<RevenueByFilmDto> revenueFilmsByStore(@PathVariable("storeId") @Min(1) short storeId) {
        return service.revenueFilmsByStore(storeId);
    }
}
