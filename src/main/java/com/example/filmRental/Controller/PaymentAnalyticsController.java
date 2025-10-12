package com.example.filmRental.Controller;
import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/payments")
@Validated
@Tag(name = "Payments Analytics (v1)", description = "Revenue analytics endpoints")
public class PaymentAnalyticsController {
    private final AnalyticsService service;
    @Autowired public PaymentAnalyticsController(AnalyticsService service){ this.service = service; }

    @Operation(summary = "Cumulative revenue datewise across all stores")
    @GetMapping("/revenue/datewise")
    public List<DateAmountDto> revenueDatewiseAll(){ return service.revenueDatewiseAll(); }

    @Operation(summary = "Cumulative revenue datewise for a store")
    @GetMapping("/revenue/datewise/store/{storeId}")
    public List<DateAmountDto> revenueDatewiseByStore(@PathVariable("storeId") @Min(1) short storeId){
        return service.revenueDatewiseByStore(storeId);
    }

    @Operation(summary = "Revenue of a Film across Stores")
    @GetMapping("/revenue/film/{filmId}")
    public List<StoreAmountDto> revenueByFilmAcrossStores(@PathVariable("filmId") @Min(1) int filmId){
        return service.revenueByFilmAcrossStores(filmId);
    }

    @Operation(summary = "Revenue of all Films by Store")
    @GetMapping("/revenue/films/store/{storeId}")
    public List<FilmAmountDto> revenueAllFilmsByStore(@PathVariable("storeId") @Min(1) short storeId){
        return service.revenueAllFilmsByStore(storeId);
    }

    @Operation(summary = "Revenue Filmwise across all Stores")
    @GetMapping("/revenue/filmwise")
    public List<FilmAmountDto> revenueFilmwiseAll(){ return service.revenueFilmwiseAll(); }
}
