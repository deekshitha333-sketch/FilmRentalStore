package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@Tag(name = "Staff (Legacy)", description = "Legacy endpoints required by the rubric")
public class StaffLegacyController {

    private final StaffService service;

    @Autowired
    public StaffLegacyController(StaffService service) { this.service = service; }

    @Operation(summary = "Add new Staff object to DB (legacy)")
    @PostMapping("/post")
    public String createLegacy(@Valid @RequestBody StaffCreateRequestDto req) {
        service.create(req);
        return "Record Created Successfully";
    }

    @Operation(summary = "Search Staff by last name (legacy)")
    @GetMapping("/lastname/{ln}")
    public List<StaffResponseDto> lastNameLegacy(@PathVariable("ln") String ln) {
        return service.findByLastName(ln);
    }

    @Operation(summary = "Search Staff by first name (legacy)")
    @GetMapping("/firstname/{fn}")
    public List<StaffResponseDto> firstNameLegacy(@PathVariable("fn") String fn) {
        return service.findByFirstName(fn);
    }

    @Operation(summary = "Search Staff by phone (legacy)")
    @GetMapping("/phone/{phone}")
    public StaffResponseDto byPhoneLegacy(@PathVariable("phone") String phone) {
        return service.findByPhone(phone);
    }

    @Operation(summary = "Search Staff by email (legacy)")
    @GetMapping("/email/{email}")
    public List<StaffResponseDto> byEmailLegacy(@PathVariable("email") String email) {
        return service.findByEmail(email);
    }

    @Operation(summary = "Search Staff by City (legacy)")
    @GetMapping("/city/{city}")
    public List<StaffResponseDto> byCityLegacy(@PathVariable("city") String city) {
        return service.findByCity(city);
    }

    @Operation(summary = "Search Staff by Country (legacy)")
    @GetMapping("/country/{country}")
    public List<StaffResponseDto> byCountryLegacy(@PathVariable("country") String country) {
        return service.findByCountry(country);
    }

    @Operation(summary = "Update phone number of a Staff (legacy)")
    @PutMapping("/update/phone/{id}")
    public StaffResponseDto updatePhoneLegacy(@PathVariable("id") @Min(1) short staffId,
                                              @Valid @RequestBody StaffUpdatePhoneRequestDto req) {
        return service.updatePhone(staffId, req);
    }

    @Operation(summary = "Update email of a Staff (legacy)")
    @PutMapping("/update/email/{id}")
    public StaffResponseDto updateEmailLegacy(@PathVariable("id") @Min(1) short staffId,
                                              @Valid @RequestBody StaffUpdateEmailRequestDto req) {
        return service.updateEmail(staffId, req);
    }

    @Operation(summary = "Update first name of Staff (legacy)")
    @PutMapping("/update/fn/{id}")
    public StaffResponseDto updateFnLegacy(@PathVariable("id") @Min(1) short staffId,
                                           @Valid @RequestBody StaffUpdateFirstNameRequestDto req) {
        return service.updateFirstName(staffId, req);
    }

    @Operation(summary = "Update last name of Staff (legacy)")
    @PutMapping("/update/ln/{id}")
    public StaffResponseDto updateLnLegacy(@PathVariable("id") @Min(1) short staffId,
                                           @Valid @RequestBody StaffUpdateLastNameRequestDto req) {
        return service.updateLastName(staffId, req);
    }

    @Operation(summary = "Assign Store to a Staff (legacy)")
    @PutMapping("/update/store/{id}")
    public StaffResponseDto assignStoreLegacy(@PathVariable("id") @Min(1) short staffId,
                                              @Valid @RequestBody StaffAssignStoreRequestDto req) {
        return service.assignStore(staffId, req);
    }

    // Special casing of capital 'S' in path required by rubric
    @Operation(summary = "Assign Address to a Staff (legacy)")
    @PutMapping(path = "/../Staff/{id}/address", consumes = "application/json", produces = "application/json")
    public StaffResponseDto assignAddressLegacy(@PathVariable("id") @Min(1) short staffId,
                                                @RequestParam("addressId") @Min(1) short addressId) {
        return service.assignAddress(staffId, addressId);
    }
}
