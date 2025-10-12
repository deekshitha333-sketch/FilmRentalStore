package com.example.filmRental.Service;

import com.example.filmRental.Dto.ActorRequestDto;
import com.example.filmRental.Dto.ActorResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface ActorService {
    ActorResponseDto create(ActorRequestDto request);
    ActorResponseDto getById(short id);
    PagedResponse<ActorResponseDto> list(String firstName, String lastName, Pageable pageable);
    ActorResponseDto update(short id, ActorRequestDto request);
    ActorResponseDto patch(short id, ActorRequestDto request);
    void delete(short id);
    boolean exists(short id);
    long count();
}
