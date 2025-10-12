package com.example.filmRental.Dto;
public class ActorFilmCountDto {
    private Short actorId;
    private String fullName;
    private Long films;
    public ActorFilmCountDto(){}
    public ActorFilmCountDto(Short actorId, String fullName, Long films){ this.actorId=actorId; this.fullName=fullName; this.films=films; }
    public Short getActorId(){ return actorId; }
    public void setActorId(Short v){ this.actorId=v; }
    public String getFullName(){ return fullName; }
    public void setFullName(String v){ this.fullName=v; }
    public Long getFilms(){ return films; }
    public void setFilms(Long v){ this.films=v; }
}
