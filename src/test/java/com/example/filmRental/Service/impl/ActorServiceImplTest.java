package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.ActorRequestDto;
import com.example.filmRental.Dto.ActorResponseDto;
import com.example.filmRental.Dto.PagedResponse;
import com.example.filmRental.Entity.Actor;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.ActorRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceImplTest {

    @Mock private ActorRepository repository;
    @InjectMocks private ActorServiceImpl service;

    private Actor actor;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10, Sort.by("actor_id").ascending());
        actor = new Actor();
        actor.setActor_id((short) 1);
        actor.setFirst_name("PENELOPE");
        actor.setLast_name("GUINESS");
        actor.setLast_update(Timestamp.from(Instant.now()));
    }

    @Test
    void create_ok() {
        when(repository.save(any(Actor.class))).thenAnswer(inv -> {
            Actor saved = inv.getArgument(0);
            saved.setActor_id((short) 1);
            return saved;
        });

        ActorRequestDto req = new ActorRequestDto();
        req.setFirstName("John");
        req.setLastName("Doe");

        ActorResponseDto dto = service.create(req);
        assertThat(dto.getId()).isEqualTo((short) 1);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
    }

    @Test
    void getById_ok() {
        when(repository.findById((short) 1)).thenReturn(Optional.of(actor));
        ActorResponseDto dto = service.getById((short) 1);
        assertThat(dto.getFirstName()).isEqualTo("PENELOPE");
        assertThat(dto.getLastName()).isEqualTo("GUINESS");
    }

    @Test
    void getById_notFound() {
        when(repository.findById((short) 9)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById((short) 9));
    }

    @Test
    void list_ok() {
        Page<Actor> page = new PageImpl<>(List.of(actor), pageable, 1);
        when(repository.search(null, null, pageable)).thenReturn(page);

        PagedResponse<ActorResponseDto> resp = service.list(null, null, pageable);
        assertThat(resp.getContent()).hasSize(1);
        assertThat(resp.getContent().get(0).getId()).isEqualTo((short)1);
        assertThat(resp.getTotalElements()).isEqualTo(1);
        assertThat(resp.getTotalPages()).isEqualTo(1);
    }

    @Test
    void update_ok() {
        when(repository.findById((short) 1)).thenReturn(Optional.of(actor));
        when(repository.save(any(Actor.class))).thenAnswer(inv -> inv.getArgument(0));

        ActorRequestDto req = new ActorRequestDto();
        req.setFirstName("NEW");
        req.setLastName("NAME");
        ActorResponseDto dto = service.update((short) 1, req);

        assertThat(dto.getFirstName()).isEqualTo("NEW");
        assertThat(dto.getLastName()).isEqualTo("NAME");
    }

    @Test
    void patch_ok_delegates_to_update() {
        when(repository.findById((short) 1)).thenReturn(Optional.of(actor));
        when(repository.save(any(Actor.class))).thenAnswer(inv -> inv.getArgument(0));

        ActorRequestDto req = new ActorRequestDto();
        req.setFirstName("PATCH");
        ActorResponseDto dto = service.patch((short) 1, req);

        assertThat(dto.getFirstName()).isEqualTo("PATCH");
    }

    @Test
    void delete_ok() {
        when(repository.existsById((short) 1)).thenReturn(true);
        service.delete((short) 1);
        verify(repository).deleteById((short) 1);
    }

    @Test
    void delete_notFound() {
        when(repository.existsById((short) 9)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete((short) 9));
        verify(repository, never()).deleteById(anyShort());
    }

    @Test
    void exists_and_count() {
        when(repository.existsById((short) 1)).thenReturn(true);
        when(repository.count()).thenReturn(123L);

        assertThat(service.exists((short) 1)).isTrue();
        assertThat(service.count()).isEqualTo(123L);
    }
}