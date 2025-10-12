package com.example.filmRental.Mapper;

import com.example.filmRental.Dto.ActorRequestDto;
import com.example.filmRental.Dto.ActorResponseDto;
import com.example.filmRental.Entity.Actor;

import java.sql.Timestamp;
import java.time.Instant;

public final class ActorMapper {
    private ActorMapper() { }

    public static Actor toEntity(ActorRequestDto dto) {
        Actor a = new Actor();
        a.setFirst_name(dto.getFirstName());
        a.setLast_name(dto.getLastName());
        a.setLast_update(Timestamp.from(Instant.now()));
        return a;
    }

    public static void updateEntity(Actor entity, ActorRequestDto dto) {
        if (dto.getFirstName() != null) {
            entity.setFirst_name(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLast_name(dto.getLastName());
        }
        entity.setLast_update(Timestamp.from(Instant.now()));
    }

    public static ActorResponseDto toDto(Actor actor) {
        ActorResponseDto dto = new ActorResponseDto();
        dto.setId(actor.getActor_id());
        dto.setFirstName(actor.getFirst_name());
        dto.setLastName(actor.getLast_name());
        dto.setLastUpdate(actor.getLast_update());
        return dto;
    }
}
