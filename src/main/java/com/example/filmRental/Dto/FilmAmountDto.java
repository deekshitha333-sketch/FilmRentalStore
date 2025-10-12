package com.example.filmRental.Dto;
import java.math.BigDecimal;
public class FilmAmountDto {
    private Integer filmId;
    private String title;
    private BigDecimal amount;
    public FilmAmountDto(){}
    public FilmAmountDto(Integer filmId, String title, BigDecimal amount){ this.filmId=filmId; this.title=title; this.amount=amount; }
    public Integer getFilmId(){ return filmId; }
    public void setFilmId(Integer v){ this.filmId=v; }
    public String getTitle(){ return title; }
    public void setTitle(String v){ this.title=v; }
    public BigDecimal getAmount(){ return amount; }
    public void setAmount(BigDecimal v){ this.amount=v; }
}
