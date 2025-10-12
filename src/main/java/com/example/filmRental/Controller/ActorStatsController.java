package com.example.filmRental.Controller;
import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/actors")
@Tag(name = "Actor Stats (v1)", description = "Actor leaderboards")
public class ActorStatsController {
    private final AnalyticsService service;
    @Autowired public ActorStatsController(AnalyticsService s){ this.service=s; }

    @Operation(summary = "Top 10 Actors by Film Count")
    @GetMapping("/toptenbyfilmcount")
    public List<ActorFilmCountDto> topTenActors(){ return service.topTenActorsByFilmCount(); }
}
