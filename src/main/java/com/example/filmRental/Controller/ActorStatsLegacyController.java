package com.example.filmRental.Controller;
import com.example.filmRental.Dto.*;
import com.example.filmRental.Service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/actors")
@Tag(name = "Actor Stats (Legacy)", description = "Legacy actor stats endpoints")
public class ActorStatsLegacyController {
    private final AnalyticsService service;
    @Autowired public ActorStatsLegacyController(AnalyticsService s){ this.service=s; }

    @Operation(summary = "Find top 10 Actors by Film Count (legacy)")
    @GetMapping("/toptenbyfilmcount")
    public List<ActorFilmCountDto> topTenActors(){ return service.topTenActorsByFilmCount(); }
}
