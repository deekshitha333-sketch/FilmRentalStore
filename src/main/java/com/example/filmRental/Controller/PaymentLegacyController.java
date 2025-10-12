package com.example.filmRental.Controller;

import com.example.filmRental.Dto.PaymentCreateRequestDto;
import com.example.filmRental.Dto.PaymentResponseDto;
import com.example.filmRental.Service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payments (Legacy)", description = "Legacy non-analytics payment endpoints")
public class PaymentLegacyController {

    private final PaymentService service;

    @Autowired
    public PaymentLegacyController(PaymentService service) {
        this.service = service;
    }

    @Operation(summary = "Make one Payment entry (legacy PUT)")
    @PutMapping("/add")
    public PaymentResponseDto add(@Valid @RequestBody PaymentCreateRequestDto request) {
        return service.create(request);
    }

}