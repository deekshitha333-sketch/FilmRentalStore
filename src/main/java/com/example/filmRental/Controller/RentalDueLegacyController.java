package com.example.filmRental.Controller;

import com.example.filmRental.Dto.RentalDueDto;
import com.example.filmRental.Service.RentalDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rental")
@Tag(name = "Rentals Due (Legacy)", description = "Legacy due rentals endpoints")
public class RentalDueLegacyController {

    private final RentalDashboardService service;

    @Autowired
    public RentalDueLegacyController(RentalDashboardService service) { this.service = service; }

    @Operation(summary = "Display due rentals of a Store (legacy)")
    @GetMapping("/due/store/{id}")
    public List<RentalDueDto> dueByStoreLegacy(@PathVariable("id") @Min(1) short storeId) {
        return service.dueByStore(storeId);
    }
}
