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
@RequestMapping("/api/v1/analytics/rentals")
@Validated
@Tag(name = "Rentals Analytics (v1)", description = "Top-N rental dashboards")
public class RentalAnalyticsController {
    private final AnalyticsService service;
    @Autowired public RentalAnalyticsController(AnalyticsService service){ this.service = service; }

    @Operation(summary = "Top 10 most rented Films")
    @GetMapping("/toptenfilms")
    public List<FilmRentCountDto> topTenFilmsAll(){ return service.topTenFilmsAllStores(); }

    @Operation(summary = "Top 10 most rented Films of a Store")
    @GetMapping("/toptenfilms/store/{storeId}")
    public List<FilmRentCountDto> topTenByStore(@PathVariable("storeId") @Min(1) short storeId){
        return service.topTenFilmsByStore(storeId);
    }
}
