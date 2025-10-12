package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@Validated
@Tag(name = "Stores", description = "Manage stores, search and reporting")
public class StoreController {

    private final StoreService service;

    @Autowired
    public StoreController(StoreService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new store")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreResponseDto create(@Valid @RequestBody StoreCreateRequestDto request) {
        return service.create(request);
    }

    @Operation(summary = "Assign address to store")
    @PutMapping("/{storeId}/address/{addressId}")
    public StoreResponseDto assignAddress(@PathVariable("storeId") @Min(1) short storeId,
                                          @PathVariable("addressId") @Min(1) short addressId) {
        return service.assignAddress(storeId, addressId);
    }

    @Operation(summary = "Assign manager to store")
    @PutMapping("/{storeId}/manager/{managerStaffId}")
    public StoreResponseDto assignManager(@PathVariable("storeId") @Min(1) short storeId,
                                          @PathVariable("managerStaffId") @Min(1) short managerStaffId) {
        return service.assignManager(storeId, managerStaffId);
    }

    @Operation(summary = "Update store phone")
    @PutMapping("/{storeId}/phone")
    public StoreResponseDto updatePhone(@PathVariable("storeId") @Min(1) short storeId,
                                        @Valid @RequestBody StoreUpdatePhoneRequestDto request) {
        return service.updatePhone(storeId, request.getPhone());
    }

    @Operation(summary = "Find stores by phone")
    @GetMapping("/phone/{phone}")
    public List<StoreResponseDto> findByPhone(@PathVariable("phone") @NotBlank String phone) {
        return service.findByPhone(phone);
    }

    @Operation(summary = "Find stores by city")
    @GetMapping("/city/{city}")
    public List<StoreResponseDto> findByCity(@PathVariable("city") @NotBlank String city) {
        return service.findByCity(city);
    }

    @Operation(summary = "Find stores by country")
    @GetMapping("/country/{country}")
    public List<StoreResponseDto> findByCountry(@PathVariable("country") @NotBlank String country) {
        return service.findByCountry(country);
    }

    @Operation(summary = "List staff of a store")
    @GetMapping("/{storeId}/staff")
    public List<StaffSummaryDto> staff(@PathVariable("storeId") @Min(1) short storeId) {
        return service.staffByStore(storeId);
    }

    @Operation(summary = "List customers of a store")
    @GetMapping("/{storeId}/customers")
    public List<CustomerSummaryDto> customers(@PathVariable("storeId") @Min(1) short storeId) {
        return service.customersByStore(storeId);
    }

    @Operation(summary = "Managers details of all stores")
    @GetMapping("/managers")
    public List<StoreManagerDetailsDto> managers() {
        return service.managers();
    }
}
