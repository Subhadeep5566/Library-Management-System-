package com.library.management.desktop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorCreateRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("biography")
    private String biography;

    @JsonProperty("birthDate")
    private java.time.LocalDate birthDate;

    @JsonProperty("deathDate")
    private java.time.LocalDate deathDate;

    @JsonProperty("nationality")
    private String nationality;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    public java.time.LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(java.time.LocalDate birthDate) { this.birthDate = birthDate; }
    public java.time.LocalDate getDeathDate() { return deathDate; }
    public void setDeathDate(java.time.LocalDate deathDate) { this.deathDate = deathDate; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}