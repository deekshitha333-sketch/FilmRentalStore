// src/main/java/com/example/filmRental/Service/RentalService.java
package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import org.springframework.data.domain.Pageable;

public interface RentalService {

    RentalResponseDto create(RentalCreateRequestDto request);

    RentalResponseDto returnRental(int rentalId, RentalReturnRequestDto request);

    PagedResponse<RentalResponseDto> openByCustomer(short customerId, Pageable pageable);

    PagedResponse<RentalOverdueResponseDto> overdueByStore(short storeId, Pageable pageable);
    PagedResponse<RentalResponseDto> historyByCustomer(short customerId, Pageable pageable); 
    RentalStatusResponseDto statusByInventory(int inventoryId);     
    PagedResponse<TopFilmDto> topTenFilms(Short storeId, Pageable pageable);
}