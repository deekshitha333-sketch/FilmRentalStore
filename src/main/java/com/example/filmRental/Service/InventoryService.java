package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import java.util.List;

public interface InventoryService {
    InventoryResponseDto add(InventoryAddRequestDto request);

    // analytics/listing
    List<FilmInventoryCountDto> allFilmsAllStores();
    List<StoreInventoryCountDto> filmAllStores(int filmId);
    List<FilmInventoryCountDto> storeAllFilms(short storeId);
    StoreFilmInventoryCountDto filmInStore(int filmId, short storeId);
}//package com.example.filmRental.Service;
