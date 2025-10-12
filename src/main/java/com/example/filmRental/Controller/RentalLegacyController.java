package com.example.filmRental.Controller;

import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Dto.RentalCreateRequestDto;
import com.example.filmRental.Dto.RentalResponseDto;
import com.example.filmRental.Dto.RentalReturnRequestDto;
import com.example.filmRental.Service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Rentals (Legacy)", description = "Legacy endpoints required by the rubric")
public class RentalLegacyController {

    private final RentalService service;

    @Autowired
    public RentalLegacyController(RentalService service) {
        this.service = service;
    }

    @Operation(summary = "Rent a Film (legacy)")
    @PostMapping("/rental/add")
    public RentalResponseDto add(@Valid @RequestBody RentalCreateRequestDto request) {
        return service.create(request);
    }

    @Operation(summary = "Update return date (legacy)")
    @PostMapping("/rental/update/returndate/{id}")
    public RentalResponseDto updateReturnDate(
            @PathVariable("id") @Min(1) int id,
            @Valid @RequestBody(required = false) RentalReturnRequestDto request) {
        return service.returnRental(id, request);
    }

    @Operation(summary = "All films rented to a customer")
    @GetMapping("/rental/customer/{id}")
    public PagedResponse<RentalResponseDto> customerRentals(
            @PathVariable("id") @Min(1) short customerId,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rental_id").descending());
        return service.historyByCustomer(customerId, pageable);
    }

}