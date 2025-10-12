package com.example.filmRental.Dto;

public class TopFilmDto {
    private Integer filmId;
    private String title;
    private Long rentCount;

    public TopFilmDto() { }
    public TopFilmDto(Integer filmId, String title, Long rentCount) {
        this.filmId = filmId;
        this.title = title;
        this.rentCount = rentCount;
    }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getRentCount() { return rentCount; }
    public void setRentCount(Long rentCount) { this.rentCount = rentCount; }
}
