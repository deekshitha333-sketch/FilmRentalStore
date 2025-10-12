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
@RequestMapping("/api/rental")
@Tag(name = "Rentals Analytics (Legacy)", description = "Legacy rental dashboards")
public class RentalAnalyticsLegacyController {
    private final AnalyticsService service;
    @Autowired public RentalAnalyticsLegacyController(AnalyticsService service){ this.service = service; }

}
