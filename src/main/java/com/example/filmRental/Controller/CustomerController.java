package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@Validated
@Tag(name = "Customers", description = "Customer queries and updates")
public class CustomerController {

    private final CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) { this.service = service; }

    // POST (secured)
    @Operation(summary = "Add new Customer")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDto create(@Valid @RequestBody CustomerRequestDto request) {
        return service.create(request);
    }

    // GET (secured) – inactive
    @Operation(summary = "Search all inactive Customers")
    @GetMapping("/inactive")
    public PagedResponse<CustomerResponseDto> inactive(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort", defaultValue = "customer_id,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return service.inactive(pageable);
    }

    // GET (secured) – active
    @Operation(summary = "Search all active Customers")
    @GetMapping("/active")
    public PagedResponse<CustomerResponseDto> active(
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort", defaultValue = "customer_id,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return service.active(pageable);
    }

    // GET (secured) – last name
    @Operation(summary = "Search Customers by last name")
    @GetMapping("/lastname/{ln}")
    public PagedResponse<CustomerResponseDto> byLastName(
            @PathVariable("ln") String ln,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort", defaultValue = "customer_id,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return service.byLastName(ln, pageable);
    }

    // GET (secured) – first name
    @Operation(summary = "Search Customers by first name")
    @GetMapping("/firstname/{fn}")
    public PagedResponse<CustomerResponseDto> byFirstName(
            @PathVariable("fn") String fn,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort", defaultValue = "customer_id,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return service.byFirstName(fn, pageable);
    }

    // GET (secured) – email (single)
    @Operation(summary = "Search Customer by email")
    @GetMapping("/email/{email}")
    public CustomerResponseDto byEmail(@PathVariable("email") String email) {
        return service.byEmail(email);
    }

    // GET (secured) – by phone (returns plain CustomerResponseDto page)
    @Operation(summary = "Search Customers by phone")
    @GetMapping("/phone/{phone}")
    public PagedResponse<CustomerResponseDto> byPhone(
            @PathVariable("phone") String phone,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort", defaultValue = "customer_id,asc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return service.byPhone(phone, pageable);
    }

    // GET (secured) – by city (with address)
    @Operation(summary = "Search Customers by City (with address)")
    @GetMapping("/city/{city}")
    public PagedResponse<CustomerWithAddressResponseDto> byCity(
            @PathVariable("city") String city,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("customer_id").ascending());
        return service.byCity(city, pageable);
    }

    // GET (secured) – by country (with address)
    @Operation(summary = "Search Customers by Country (with address)")
    @GetMapping("/country/{country}")
    public PagedResponse<CustomerWithAddressResponseDto> byCountry(
            @PathVariable("country") String country,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("customer_id").ascending());
        return service.byCountry(country, pageable);
    }

    // PUT – update email
    @Operation(summary = "Update email of Customer")
    @PutMapping("/{id}/email")
    public CustomerResponseDto updateEmail(
            @PathVariable("id") @Min(1) short id,
            @Valid @RequestBody UpdateEmailRequest body) {
        return service.updateEmail(id, body.getEmail());
    }

    // PUT – update phone (address.phone)
    @Operation(summary = "Update phone number of a Customer (address.phone)")
    @PutMapping("/{id}/phone")
    public CustomerResponseDto updatePhone(
            @PathVariable("id") @Min(1) short id,
            @Valid @RequestBody UpdatePhoneRequest body) {
        return service.updatePhone(id, body.getPhone());
    }

    // PUT – assign store
    @Operation(summary = "Assign Store to a Customer")
    @PutMapping("/{id}/store")
    public CustomerResponseDto updateStore(
            @PathVariable("id") @Min(1) short id,
            @Valid @RequestBody UpdateStoreRequest body) {
        return service.updateStore(id, body.getStoreId());
    }

    // PUT – assign address
    @Operation(summary = "Assign Address to a Customer")
    @PutMapping("/{id}/address/{addressId}")
    public CustomerResponseDto updateAddress(
            @PathVariable("id") @Min(1) short id,
            @PathVariable("addressId") @Min(1) short addressId) {
        return service.updateAddress(id, addressId);
    }

    // PUT – update first name
    @Operation(summary = "Update first name of Customer")
    @PutMapping("/{id}/firstname")
    public CustomerResponseDto updateFirstName(
            @PathVariable("id") @Min(1) short id,
            @Valid @RequestBody UpdateNameRequest body) {
        return service.updateFirstName(id, body.getValue());
    }

    // PUT – update last name
    @Operation(summary = "Update last name of Customer")
    @PutMapping("/{id}/lastname")
    public CustomerResponseDto updateLastName(
            @PathVariable("id") @Min(1) short id,
            @Valid @RequestBody UpdateNameRequest body) {
        return service.updateLastName(id, body.getValue());
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        String dir = parts.length > 1 ? parts[1] : "asc";
        return "desc".equalsIgnoreCase(dir) ? Sort.by(field).descending() : Sort.by(field).ascending();
    }
}
