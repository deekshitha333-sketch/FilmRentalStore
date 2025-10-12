package com.example.filmRental.Service;
import com.example.filmRental.Dto.*;
import java.util.List;

public interface AnalyticsService {
    // Payments
    List<DateAmountDto> revenueDatewiseAll();
    List<DateAmountDto> revenueDatewiseByStore(short storeId);
    List<StoreAmountDto> revenueByFilmAcrossStores(int filmId);
    List<FilmAmountDto> revenueAllFilmsByStore(short storeId);
    List<FilmAmountDto> revenueFilmwiseAll();

    // Rentals
    List<FilmRentCountDto> topTenFilmsAllStores();
    List<FilmRentCountDto> topTenFilmsByStore(short storeId);

    // Films & Actors stats
    List<YearCountDto> filmsCountByYear();
    List<ActorFilmCountDto> topTenActorsByFilmCount();
}
