
package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rentals")
@Validated
@Tag(name = "Rentals", description = "Create, return, and search rentals")
public class RentalController {

    private final RentalService service;

    @Autowired
    public RentalController(RentalService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new rental (inventory must be available)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDto create(@Valid @RequestBody RentalCreateRequestDto request) {
        return service.create(request);
    }

    @Operation(summary = "Return a rental (set return_date)")
    @PutMapping("/{id}/return")
    public RentalResponseDto returnRental(
            @PathVariable("id") @Min(1) int id,
            @Valid @RequestBody(required = false) RentalReturnRequestDto request) {
        return service.returnRental(id, request);
    }

    @Operation(summary = "List open rentals for a customer")
    @GetMapping("/customer/{customerId}/open")
    public PagedResponse<RentalResponseDto> openByCustomer(
            @PathVariable("customerId") @Min(1) short customerId,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rental_id").descending());
        return service.openByCustomer(customerId, pageable);
    }

    @Operation(summary = "List overdue rentals for a store (with due date, days overdue)")
    @GetMapping("/store/{storeId}/overdue")
    public PagedResponse<RentalOverdueResponseDto> overdueByStore(
            @PathVariable("storeId") @Min(1) short storeId,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rental_id").ascending());
        return service.overdueByStore(storeId, pageable);
    }

    @Operation(summary = "List all rentals (history) for a customer")
    @GetMapping("/customer/{customerId}/history")
    public PagedResponse<RentalResponseDto> historyByCustomer(
        @PathVariable("customerId") @Min(1) short customerId,
        @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
        @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
      Pageable pageable = PageRequest.of(page, size, Sort.by("rental_id").descending());
      return service.historyByCustomer(customerId, pageable);
    }

    @Operation(summary = "Inventory status (availability, open rental, due date)")
    @GetMapping("/inventory/{inventoryId}/status")
    public RentalStatusResponseDto statusByInventory(
        @PathVariable("inventoryId") @Min(1) int inventoryId) {
      return service.statusByInventory(inventoryId);
    }

}