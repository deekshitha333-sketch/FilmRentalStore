package com.example.filmRental.Controller;
import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/films")
@Tag(name = "Film Stats (v1)", description = "Film counts and groupings")
public class FilmStatsController {
    private final AnalyticsService service;
    @Autowired public FilmStatsController(AnalyticsService s){ this.service=s; }

    @Operation(summary = "Display number of Films released by each Year")
    @GetMapping("/countbyyear")
    public List<YearCountDto> countByYear(){ return service.filmsCountByYear(); }
}
