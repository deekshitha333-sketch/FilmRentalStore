package com.example.filmRental.Controller;
import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment Analytics (Legacy)", description = "Legacy revenue endpoints from rubric")
public class PaymentAnalyticsLegacyController {
    private final AnalyticsService service;
    @Autowired public PaymentAnalyticsLegacyController(AnalyticsService service){ this.service = service; }

    @Operation(summary = "Calculate Cumulative revenue of all Stores (legacy)")
    @GetMapping("/revenue/datewise")
    public List<DateAmountDto> revenueDatewiseAll(){ return service.revenueDatewiseAll(); }

    @Operation(summary = "Cumulative revenue of a Store (legacy)")
    @GetMapping("/revenue/datewise/store/{id}")
    public List<DateAmountDto> revenueDatewiseByStore(@PathVariable("id") @Min(1) short storeId){
        return service.revenueDatewiseByStore(storeId);
    }

    @Operation(summary = "Cumulative revenue of a Film store wise (legacy)")
    @GetMapping("/revenue/film/{id}")
    public List<StoreAmountDto> revenueByFilmAcrossStores(@PathVariable("id") @Min(1) int filmId){
        return service.revenueByFilmAcrossStores(filmId);
    }

    @Operation(summary = "Cumulative revenue of all Films by Store (legacy)")
    @GetMapping("/revenue/films/store/{id}")
    public List<FilmAmountDto> revenueAllFilmsByStore(@PathVariable("id") @Min(1) short storeId){
        return service.revenueAllFilmsByStore(storeId);
    }

    @Operation(summary = "Cumulative revenue filmwise across all Stores (legacy)")
    @GetMapping("/revenue/filmwise/")
    public List<FilmAmountDto> revenueFilmwiseAll(){ return service.revenueFilmwiseAll(); }
    
}
