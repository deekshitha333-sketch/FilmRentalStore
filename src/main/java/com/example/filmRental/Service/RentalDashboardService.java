package com.example.filmRental.Service;

import com.example.filmRental.Dto.RentalDueDto;
import java.util.List;

public interface RentalDashboardService {
    List<RentalDueDto> dueByStore(short storeId);
}
