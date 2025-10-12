package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.InventoryService;
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
@RequestMapping("/api/v1/inventory")
@Validated
@Tag(name = "Inventory", description = "Add copies and view inventory counts")
public class InventoryController {

    private final InventoryService service;

    @Autowired
    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @Operation(summary = "Add one more copy of a film to a store")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponseDto add(@Valid @RequestBody InventoryAddRequestDto request) {
        return service.add(request);
    }

    @Operation(summary = "Inventory of all films across all stores")
    @GetMapping("/films")
    public List<FilmInventoryCountDto> allFilmsAllStores() {
        return service.allFilmsAllStores();
    }

    @Operation(summary = "Inventory of a film across all stores")
    @GetMapping("/film/{filmId}")
    public List<StoreInventoryCountDto> filmAllStores(@PathVariable("filmId") @Min(1) int filmId) {
        return service.filmAllStores(filmId);
    }

    @Operation(summary = "Inventory of all films by a store")
    @GetMapping("/store/{storeId}")
    public List<FilmInventoryCountDto> storeAllFilms(@PathVariable("storeId") @Min(1) short storeId) {
        return service.storeAllFilms(storeId);
    }

    @Operation(summary = "Inventory count of a film in a store")
    @GetMapping("/film/{filmId}/store/{storeId}")
    public StoreFilmInventoryCountDto filmInStore(@PathVariable("filmId") @Min(1) int filmId,
                                                  @PathVariable("storeId") @Min(1) short storeId) {
        return service.filmInStore(filmId, storeId);
    }
}