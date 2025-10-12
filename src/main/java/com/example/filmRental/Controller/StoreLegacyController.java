package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
@Tag(name = "Stores (Legacy)", description = "Legacy endpoints required by the rubric")
public class StoreLegacyController {

    private final StoreService service;

    @Autowired
    public StoreLegacyController(StoreService service) {
        this.service = service;
    }

    @Operation(summary = "Add new Store object to DB (legacy)")
    @PostMapping("/post")
    public StoreResponseDto post(@Valid @RequestBody StoreCreateRequestDto request) {
        return service.create(request);
    }

    @Operation(summary = "Assign Address to a Store (legacy)")
    @PutMapping("/{storeId}/address/{addressId}")
    public StoreResponseDto assignAddress(@PathVariable("storeId") @Min(1) short storeId,
                                          @PathVariable("addressId") @Min(1) short addressId) {
        return service.assignAddress(storeId, addressId);
    }

    @Operation(summary = "Assign manager to a Store (legacy)")
    @PutMapping("/{storeId}/manager/{manager_staff_id}")
    public StoreResponseDto assignManager(@PathVariable("storeId") @Min(1) short storeId,
                                          @PathVariable("manager_staff_id") @Min(1) short managerStaffId) {
        return service.assignManager(storeId, managerStaffId);
    }

    @Operation(summary = "Update phone number of a Store (legacy)")
    @PutMapping("/update/{storeId}/{phone}")
    public StoreResponseDto updatePhone(@PathVariable("storeId") @Min(1) short storeId,
                                        @PathVariable("phone") @NotBlank String phone) {
        return service.updatePhone(storeId, phone);
    }

    @Operation(summary = "Search Store by phone number (legacy)")
    @GetMapping("/phone/{phone}")
    public List<StoreResponseDto> findByPhone(@PathVariable("phone") @NotBlank String phone) {
        return service.findByPhone(phone);
    }

    @Operation(summary = "Search Store by City (legacy)")
    @GetMapping("/city/{city}")
    public List<StoreResponseDto> findByCity(@PathVariable("city") @NotBlank String city) {
        return service.findByCity(city);
    }

    @Operation(summary = "Search Store by Country (legacy)")
    @GetMapping("/country/{country}")
    public List<StoreResponseDto> findByCountry(@PathVariable("country") @NotBlank String country) {
        return service.findByCountry(country);
    }

    @Operation(summary = "Display all Staff of a Store (legacy)")
    @GetMapping("/staff/{storeId}")
    public List<StaffSummaryDto> staff(@PathVariable("storeId") @Min(1) short storeId) {
        return service.staffByStore(storeId);
    }

    @Operation(summary = "Display all Customers of a Store (legacy)")
    @GetMapping("/customer/{storeId}")
    public List<CustomerSummaryDto> customers(@PathVariable("storeId") @Min(1) short storeId) {
        return service.customersByStore(storeId);
    }

    @Operation(summary = "Manager + Store details of all stores (legacy)")
    @GetMapping("/managers")
    public List<StoreManagerDetailsDto> managers() {
        return service.managers();
    }
}