package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.StaffService;
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
@RequestMapping("/api/v1/staff")
@Validated
@Tag(name = "Staff (v1)", description = "Modernized Staff endpoints")
public class StaffController {

    private final StaffService service;

    @Autowired
    public StaffController(StaffService service) { this.service = service; }

    @Operation(summary = "Create new Staff")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StaffResponseDto create(@Valid @RequestBody StaffCreateRequestDto req) {
        return service.create(req);
    }

    @Operation(summary = "Update Staff email")
    @PutMapping("/update/email/{staffId}")
    public StaffResponseDto updateEmail(@PathVariable("staffId") @Min(1) short staffId,
                                        @Valid @RequestBody StaffUpdateEmailRequestDto req) {
        return service.updateEmail(staffId, req);
    }

    @Operation(summary = "Update Staff phone")
    @PutMapping("/update/phone/{staffId}")
    public StaffResponseDto updatePhone(@PathVariable("staffId") @Min(1) short staffId,
                                        @Valid @RequestBody StaffUpdatePhoneRequestDto req) {
        return service.updatePhone(staffId, req);
    }

    @Operation(summary = "Update Staff first name")
    @PutMapping("/update/fn/{staffId}")
    public StaffResponseDto updateFirstName(@PathVariable("staffId") @Min(1) short staffId,
                                            @Valid @RequestBody StaffUpdateFirstNameRequestDto req) {
        return service.updateFirstName(staffId, req);
    }

    @Operation(summary = "Update Staff last name")
    @PutMapping("/update/ln/{staffId}")
    public StaffResponseDto updateLastName(@PathVariable("staffId") @Min(1) short staffId,
                                           @Valid @RequestBody StaffUpdateLastNameRequestDto req) {
        return service.updateLastName(staffId, req);
    }

    @Operation(summary = "Assign Address to Staff")
    @PutMapping("/{staffId}/address/{addressId}")
    public StaffResponseDto assignAddress(@PathVariable("staffId") @Min(1) short staffId,
                                          @PathVariable("addressId") @Min(1) short addressId) {
        return service.assignAddress(staffId, addressId);
    }

    @Operation(summary = "Assign Store to Staff")
    @PutMapping("/update/store/{staffId}")
    public StaffResponseDto assignStore(@PathVariable("staffId") @Min(1) short staffId,
                                        @Valid @RequestBody StaffAssignStoreRequestDto req) {
        return service.assignStore(staffId, req);
    }

    // Searches

    @Operation(summary = "Search Staff by first name")
    @GetMapping("/firstname/{firstName}")
    public List<StaffResponseDto> byFirstName(@PathVariable("firstName") String firstName) {
        return service.findByFirstName(firstName);
    }

    @Operation(summary = "Search Staff by last name")
    @GetMapping("/lastname/{lastName}")
    public List<StaffResponseDto> byLastName(@PathVariable("lastName") String lastName) {
        return service.findByLastName(lastName);
    }

    @Operation(summary = "Search Staff by email")
    @GetMapping("/email/{email}")
    public List<StaffResponseDto> byEmail(@PathVariable("email") String email) {
        return service.findByEmail(email);
    }

    @Operation(summary = "Search Staff by phone")
    @GetMapping("/phone/{phone}")
    public StaffResponseDto byPhone(@PathVariable("phone") String phone) {
        return service.findByPhone(phone);
    }

    @Operation(summary = "Search Staff by City")
    @GetMapping("/city/{city}")
    public List<StaffResponseDto> byCity(@PathVariable("city") String city) {
        return service.findByCity(city);
    }

    @Operation(summary = "Search Staff by Country")
    @GetMapping("/country/{country}")
    public List<StaffResponseDto> byCountry(@PathVariable("country") String country) {
        return service.findByCountry(country);
    }
}
