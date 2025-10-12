package com.example.filmRental.Controller;

import com.example.filmRental.Dto.RentalDueDto;
import com.example.filmRental.Service.RentalDashboardService;
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
@Tag(name = "Rentals Due (v1)", description = "Due/Overdue rentals dashboards")
public class RentalDueController {

    private final RentalDashboardService service;

    @Autowired
    public RentalDueController(RentalDashboardService service) { this.service = service; }

    @Operation(summary = "List rentals due/overdue for a Store")
    @GetMapping("/due/store/{storeId}")
    public List<RentalDueDto> dueByStore(@PathVariable("storeId") @Min(1) short storeId) {
        return service.dueByStore(storeId);
    }
}
