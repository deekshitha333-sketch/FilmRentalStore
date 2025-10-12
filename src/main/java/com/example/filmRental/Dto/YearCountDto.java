package com.example.filmRental.Dto;
public class YearCountDto {
    private Integer year;
    private Long count;
    public YearCountDto(){}
    public YearCountDto(Integer year, Long count){ this.year=year; this.count=count; }
    public Integer getYear(){ return year; }
    public void setYear(Integer v){ this.year=v; }
    public Long getCount(){ return count; }
    public void setCount(Long v){ this.count=v; }
}
