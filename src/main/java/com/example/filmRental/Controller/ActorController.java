package com.example.filmRental.Controller;

import com.example.filmRental.Dto.ActorRequestDto;
import com.example.filmRental.Dto.ActorResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Service.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/actors")
@Validated
@Tag(name = "Actors", description = "CRUD & search endpoints for actors")
public class ActorController {

    private final ActorService actorService;

    // EXPLICIT CONSTRUCTOR (no Lombok). This initializes the final field.
    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @Operation(summary = "Create actor")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActorResponseDto create(@Valid @RequestBody ActorRequestDto request) {
        return actorService.create(request);
    }

    @Operation(summary = "Get actor by id")
    @GetMapping("/{id}")
    public ActorResponseDto getById(@PathVariable("id") @Min(1) short id) {
        return actorService.getById(id);
    }

    @Operation(summary = "List/search actors with paging & sorting")
    @GetMapping
    public PagedResponse<ActorResponseDto> list(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName",  required = false) String lastName,
            @RequestParam(name = "page",      defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size",      defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "sort",      defaultValue = "actor_id,asc") String sort
    ) {
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return actorService.list(firstName, lastName, pageable);
    }

    @Operation(summary = "Full update (PUT)")
    @PutMapping("/{id}")
    public ActorResponseDto update(@PathVariable("id") @Min(1) short id,
                                   @Valid @RequestBody ActorRequestDto request) {
        return actorService.update(id, request);
    }

    @Operation(summary = "Partial update (PATCH)")
    @PatchMapping("/{id}")
    public ActorResponseDto patch(@PathVariable("id") @Min(1) short id,
                                  @RequestBody ActorRequestDto request) {
        return actorService.patch(id, request);
    }

    @Operation(summary = "Delete actor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Min(1) short id) {
        actorService.delete(id);
    }

    @Operation(summary = "Check existence")
    @GetMapping("/{id}/exists")
    public boolean exists(@PathVariable("id") @Min(1) short id) {
        return actorService.exists(id);
    }

    @Operation(summary = "Count actors")
    @GetMapping("/count")
    public long count() {
        return actorService.count();
    }

    private Sort parseSort(String sort) {
        // format: field,dir (e.g., actor_id,asc or first_name,desc)
        String[] parts = sort.split(",");
        String field = parts[0];
        String dir = parts.length > 1 ? parts[1] : "asc";
        return "desc".equalsIgnoreCase(dir) ? Sort.by(field).descending() : Sort.by(field).ascending();
    }
}
