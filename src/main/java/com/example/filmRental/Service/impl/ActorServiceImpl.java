package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.ActorRequestDto;
import com.example.filmRental.Dto.ActorResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Entity.Actor;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Mapper.ActorMapper;
import com.example.filmRental.Repository.ActorRepository;
import com.example.filmRental.Service.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ActorServiceImpl implements ActorService {

    private final ActorRepository repository;

    @Autowired
    public ActorServiceImpl(ActorRepository repository) {
        this.repository = repository;
    }

    @Override
    public ActorResponseDto create(ActorRequestDto request) {
        Actor saved = repository.save(ActorMapper.toEntity(request));
        return ActorMapper.toDto(saved);
    }

    @Override
    public ActorResponseDto getById(short id) {
        Actor actor = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Actor not found: " + id));
        return ActorMapper.toDto(actor);
    }

    @Override
    public PagedResponse<ActorResponseDto> list(String firstName, String lastName, Pageable pageable) {
        Page<Actor> page = repository.search(emptyToNull(firstName), emptyToNull(lastName), pageable);
        return PagedResponse.<ActorResponseDto>builder()
                .setContent(page.getContent().stream().map(ActorMapper::toDto).toList())
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setPageNumber(pageable.getPageNumber())
                .setPageSize(pageable.getPageSize())
                .setHasNext(page.hasNext())
                .setHasPrevious(page.hasPrevious())
                .build();
    }

    @Override
    public ActorResponseDto update(short id, ActorRequestDto request) {
        Actor actor = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Actor not found: " + id));
        ActorMapper.updateEntity(actor, request);
        Actor saved = repository.save(actor);
        return ActorMapper.toDto(saved);
    }

    @Override
    public ActorResponseDto patch(short id, ActorRequestDto request) {
        return update(id, request);
    }

    @Override
    public void delete(short id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Actor not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean exists(short id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
