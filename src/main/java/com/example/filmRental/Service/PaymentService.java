package com.example.filmRental.Service;

import com.example.filmRental.Dto.*;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto create(PaymentCreateRequestDto request);

    List<RevenueByDateDto> revenueDatewiseAll();
    List<RevenueByDateDto> revenueDatewiseByStore(short storeId);

    List<RevenueByStoreDto> revenueStorewiseByFilm(int filmId);
    List<RevenueByFilmDto> revenueFilmwiseAll();
    List<RevenueByFilmDto> revenueFilmsByStore(short storeId);
}
