package com.example.filmRental.Dto;
public class FilmRentCountDto {
    private Integer filmId;
    private String title;
    private Long rentals;
    public FilmRentCountDto(){}
    public FilmRentCountDto(Integer filmId, String title, Long rentals){ this.filmId=filmId; this.title=title; this.rentals=rentals; }
    public Integer getFilmId(){ return filmId; }
    public void setFilmId(Integer v){ this.filmId=v; }
    public String getTitle(){ return title; }
    public void setTitle(String v){ this.title=v; }
    public Long getRentals(){ return rentals; }
    public void setRentals(Long v){ this.rentals=v; }
}
