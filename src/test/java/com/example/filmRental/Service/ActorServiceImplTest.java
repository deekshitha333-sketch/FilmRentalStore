package com.example.filmRental.Service;

import com.example.filmRental.Dto.ActorRequestDto;
import com.example.filmRental.Entity.Actor;
import com.example.filmRental.Exception.NotFoundException;
import com.example.filmRental.Repository.ActorRepository;
import com.example.filmRental.Service.impl.ActorServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class ActorServiceImplTest {

    @Test
    void create_and_get_update_delete_flow() {
        ActorRepository repo = Mockito.mock(ActorRepository.class);
        ActorService service = new ActorServiceImpl(repo);

        Actor saved = new Actor();
        saved.setActor_id((short)1);
        saved.setFirst_name("A");
        saved.setLast_name("B");
        saved.setLast_update(Timestamp.from(Instant.now()));

        Mockito.when(repo.save(any())).thenReturn(saved);
        Mockito.when(repo.findById((short)1)).thenReturn(Optional.of(saved));
        Mockito.when(repo.existsById((short)1)).thenReturn(true);

        var res = service.create(new ActorRequestDto() {{ setFirstName("A"); setLastName("B"); }});
        assertEquals((short)1, res.getId());

        var got = service.getById((short)1);
        assertEquals("A", got.getFirstName());

        var upd = service.update((short)1, new ActorRequestDto() {{ setFirstName("X"); setLastName("Y"); }});
        assertEquals((short)1, upd.getId());

        assertTrue(service.exists((short)1));
        assertTrue(service.count() >= 0);

        service.delete((short)1);
        Mockito.verify(repo).deleteById((short)1);
    }

    @Test
    void list_search() {
        ActorRepository repo = Mockito.mock(ActorRepository.class);
        ActorService service = new ActorServiceImpl(repo);
        Page<Actor> page = new PageImpl<>(List.of());
        Mockito.when(repo.search(any(), any(), any())).thenReturn(page);
        var resp = service.list(null, null, PageRequest.of(0, 10));
        assertEquals(0, resp.getContent().size());
    }

    @Test
    void get_not_found() {
        ActorRepository repo = Mockito.mock(ActorRepository.class);
        ActorService service = new ActorServiceImpl(repo);
        Mockito.when(repo.findById((short)9)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById((short)9));
    }
}