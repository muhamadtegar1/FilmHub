package com.example.filmhub.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenreResponse {
    @SerializedName("genres")
    private List<Genre> genres;

    // Getter

    public List<Genre> getGenres() {
        return genres;
    }
}