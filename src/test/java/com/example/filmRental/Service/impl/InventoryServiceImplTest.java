package com.example.filmRental.Service.impl;

import com.example.filmRental.Dto.*;
import com.example.filmRental.Exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock NamedParameterJdbcTemplate jdbc;
    @InjectMocks InventoryServiceImpl service;

    @Test
    void add_ok_generates_key() {
        // FK checks: film & store exist
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        // Overload-proof stubbing of INSERT with KeyHolder
        when(jdbc.update(
                anyString(),
                any(SqlParameterSource.class),
                any(KeyHolder.class),
                any(String[].class)
        )).thenAnswer(inv -> {
            for (Object arg : inv.getArguments()) {
                if (arg instanceof KeyHolder kh) {
                    kh.getKeyList().add(Map.of("inventory_id", 42));
                    break;
                }
            }
            return 1;
        });

        InventoryResponseDto dto = service.add(new InventoryAddRequestDto(1, (short)1));
        assertThat(dto.getInventoryId()).isEqualTo(42);
        assertThat(dto.getFilmId()).isEqualTo(1);
        assertThat(dto.getStoreId()).isEqualTo((short)1);
    }

    @Test
    void add_404_when_store_missing() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(0L);

        assertThatThrownBy(() -> service.add(new InventoryAddRequestDto(1,(short)1)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Store not found");
    }

    @Test
    void allFilmsAllStores_maps_rows() {
        when(jdbc.queryForList(startsWith("SELECT f.film_id AS fid"), anyMap()))
                .thenReturn(List.of(Map.of(
                        "fid", 5, "title", "ACADEMY DINOSAUR", "copies", 12L
                )));
        List<FilmInventoryCountDto> list = service.allFilmsAllStores();
        assertThat(list).singleElement().satisfies(d -> {
            assertThat(d.getFilmId()).isEqualTo(5);
            assertThat(d.getTitle()).isEqualTo("ACADEMY DINOSAUR");
            assertThat(d.getCopies()).isEqualTo(12L);
        });
    }

    @Test
    void filmAllStores_checks_film_exists_and_maps() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForList(startsWith("SELECT s.store_id AS sid"), anyMap()))
                .thenReturn(List.of(Map.of(
                        "sid", 1, "saddr", "47 MySakila Drive, Lethbridge", "copies", 4L
                )));

        List<StoreInventoryCountDto> list = service.filmAllStores(5);
        assertThat(list).singleElement().satisfies(d -> {
            assertThat(d.getStoreId()).isEqualTo((short)1);
            assertThat(d.getStoreAddress()).isEqualTo("47 MySakila Drive, Lethbridge");
            assertThat(d.getCopies()).isEqualTo(4L);
        });
    }

    @Test
    void storeAllFilms_checks_store_exists_and_maps() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForList(startsWith("SELECT f.film_id AS fid"), anyMap()))
                .thenReturn(List.of(Map.of(
                        "fid", 10, "title", "BEAR GRILLS", "copies", 3L
                )));

        List<FilmInventoryCountDto> list = service.storeAllFilms((short)1);
        assertThat(list).singleElement().satisfies(d -> {
            assertThat(d.getFilmId()).isEqualTo(10);
            assertThat(d.getTitle()).isEqualTo("BEAR GRILLS");
            assertThat(d.getCopies()).isEqualTo(3L);
        });
    }

    @Test
    void filmInStore_nonzero_and_zero_copies() {
        // film & store exist
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
                .thenReturn(1L);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
                .thenReturn(1L);

        // Case 1: rows present
        when(jdbc.queryForList(startsWith("SELECT s.store_id AS sid"), anyMap()))
                .thenReturn(List.of(Map.of(
                        "sid", 1,
                        "saddr", "47 MySakila Drive, Lethbridge",
                        "fid", 1,
                        "title", "ACADEMY DINOSAUR",
                        "copies", 5L
                )));
        StoreFilmInventoryCountDto d1 = service.filmInStore(1,(short)1);
        assertThat(d1.getCopies()).isEqualTo(5L);

        // Case 2: zero copies -> service fetches title and store address via two scalar queries
        when(jdbc.queryForList(startsWith("SELECT s.store_id AS sid"), anyMap()))
                .thenReturn(List.of());
        when(jdbc.queryForObject(startsWith("SELECT title FROM film"), anyMap(), eq(String.class)))
                .thenReturn("ACADEMY DINOSAUR");
        when(jdbc.queryForObject(startsWith("SELECT CONCAT(a.address"), anyMap(), eq(String.class)))
                .thenReturn("47 MySakila Drive, Lethbridge");

        StoreFilmInventoryCountDto d2 = service.filmInStore(1,(short)1);
        assertThat(d2.getCopies()).isEqualTo(0L);
        assertThat(d2.getTitle()).isEqualTo("ACADEMY DINOSAUR");
        assertThat(d2.getStoreAddress()).isEqualTo("47 MySakila Drive, Lethbridge");
    }
}//package com.example.filmRental.Service.impl;
////src/test/java/com/example/filmRental/Service/InventoryServiceImplTest
//import com.example.filmRental.Dto.*;
//import com.example.filmRental.Exception.NotFoundException;
//import com.example.filmRental.Service.impl.InventoryServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class InventoryServiceImplTest {
//
// @Mock private NamedParameterJdbcTemplate jdbc;
// @InjectMocks private InventoryServiceImpl service;
//
//
//@Test
//void add_ok_generates_key() {
//    // FK checks pass
//    when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
//            .thenReturn(1L);
//    when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
//            .thenReturn(1L);});
//
//
// @Test
// void add_film_or_store_missing_throws() {
//     when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
//             .thenReturn(0L);
//
//     assertThrows(NotFoundException.class, () -> service.add(new InventoryAddRequestDto(10, (short) 1)));
// }
//
// @Test
// void all_and_byFilm_byStore_reports() {
//     // allFilmsAllStores
//     when(jdbc.queryForList(startsWith("SELECT f.film_id AS fid"), anyMap()))
//             .thenReturn(List.of(Map.of("fid", 1, "title", "A", "copies", 3)));
//
//     List<FilmInventoryCountDto> all = service.allFilmsAllStores();
//     assertThat(all).singleElement().satisfies(r -> {
//         assertThat(r.getFilmId()).isEqualTo(1);
//         assertThat(r.getCopies()).isEqualTo(3L);
//     });
//
//     // filmAllStores: exists(film), then grouped rows
//     when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
//             .thenReturn(1L);
//     when(jdbc.queryForList(startsWith("SELECT s.store_id AS sid"), anyMap()))
//             .thenReturn(List.of(Map.of("sid", 1, "saddr", "ADDR", "copies", 2)));
//
//     List<StoreInventoryCountDto> perFilm = service.filmAllStores(1);
//     assertThat(perFilm).singleElement().extracting(StoreInventoryCountDto::getCopies).isEqualTo(2L);
//
//     // storeAllFilms: exists(store)
//     when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
//             .thenReturn(1L);
//     when(jdbc.queryForList(startsWith("SELECT f.film_id AS fid"), anyMap()))
//             .thenReturn(List.of(Map.of("fid", 3, "title", "X", "copies", 5)));
//
//     List<FilmInventoryCountDto> perStore = service.storeAllFilms((short) 1);
//     assertThat(perStore).singleElement().extracting(FilmInventoryCountDto::getCopies).isEqualTo(5L);
// }
//
// @Test
// void filmAllStores_missing_film_throws() {
//     when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
//             .thenReturn(0L);
//     assertThrows(NotFoundException.class, () -> service.filmAllStores(1));
// }
//
// @Test
// void storeAllFilms_missing_store_throws() {
//     when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
//             .thenReturn(0L);
//     assertThrows(NotFoundException.class, () -> service.storeAllFilms((short) 1));
// }
//
// @Test
// void filmInStore_ok_with_rows_and_without_rows() {
//     // exists(film) and exists(store)
//     when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM film"), anyMap(), eq(Long.class)))
//             .thenReturn(1L);
//     when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM store"), anyMap(), eq(Long.class)))
//             .thenReturn(1L);
//
//     // case 1: with rows
//     when(jdbc.queryForList(startsWith("SELECT s.store_id AS sid, CONCAT"), anyMap()))
//             .thenReturn(List.of(Map.of(
//                     "sid", 1, "saddr", "ADDR", "fid", 10, "title", "FILM", "copies", 4L
//             )));
//     StoreFilmInventoryCountDto dto1 = service.filmInStore(10, (short) 1);
//     assertThat(dto1.getCopies()).isEqualTo(4L);
//
//     // case 2: zero rows path -> fetch title and address via queryForObject
//     when(jdbc.queryForList(startsWith("SELECT s.store_id AS sid, CONCAT"), anyMap()))
//             .thenReturn(List.of());
//     when(jdbc.queryForObject(startsWith("SELECT title FROM film"), anyMap(), eq(String.class)))
//             .thenReturn("TITLE");
//     when(jdbc.queryForObject(startsWith("SELECT CONCAT"), anyMap(), eq(String.class)))
//             .thenReturn("ADDR");
//     StoreFilmInventoryCountDto dto2 = service.filmInStore(10, (short) 1);
//     assertThat(dto2.getTitle()).isEqualTo("TITLE");
//     assertThat(dto2.getStoreAddress()).isEqualTo("ADDR");
//     assertThat(dto2.getCopies()).isEqualTo(0L);
// }
//}
