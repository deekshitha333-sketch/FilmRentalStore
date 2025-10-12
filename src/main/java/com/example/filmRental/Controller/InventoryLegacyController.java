package com.example.filmRental.Controller;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory (Legacy)", description = "Legacy endpoints required by the rubric")
public class InventoryLegacyController {

    private final InventoryService service;

    @Autowired
    public InventoryLegacyController(InventoryService service) {
        this.service = service;
    }

    @Operation(summary = "Add one more Film to a Store (legacy)")
    @PostMapping("/add")
    public InventoryResponseDto addLegacy(@Valid @RequestBody InventoryAddRequestDto request) {
        return service.add(request);
    }

    @Operation(summary = "Inventory of all Films in all Stores (legacy)")
    @GetMapping("/films")
    public List<FilmInventoryCountDto> filmsAllStores() {
        return service.allFilmsAllStores();
    }

    @Operation(summary = "Inventory of a Film in all Stores (legacy)")
    @GetMapping("/film/{id}")
    public List<StoreInventoryCountDto> filmAllStoresLegacy(@PathVariable("id") @Min(1) int filmId) {
        return service.filmAllStores(filmId);
    }

    @Operation(summary = "Inventory of all Films by a Store (legacy)")
    @GetMapping("/store/{id}")
    public List<FilmInventoryCountDto> storeAllFilmsLegacy(@PathVariable("id") @Min(1) short storeId) {
        return service.storeAllFilms(storeId);
    }

    @Operation(summary = "Inventory count of a Film in a Store (legacy)")
    @GetMapping("/film/{filmId}/store/{storeId}")
    public StoreFilmInventoryCountDto filmInStoreLegacy(@PathVariable("filmId") @Min(1) int filmId,
                                                        @PathVariable("storeId") @Min(1) short storeId) {
        return service.filmInStore(filmId, storeId);
    }
}
