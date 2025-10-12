package com.example.filmRental.Dto;

public class FilmInventoryCountDto {
    private Integer filmId;
    private String title;
    private Long copies;

    public FilmInventoryCountDto() { }
    public FilmInventoryCountDto(Integer filmId, String title, Long copies) {
        this.filmId = filmId; this.title = title; this.copies = copies;
    }
    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getCopies() { return copies; }
    public void setCopies(Long copies) { this.copies = copies; }
}