package com.example.filmhub.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie {
    @PrimaryKey@ColumnInfo(name = "movie_id")
    public int movieId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "poster_path")
    public String posterPath;

    // Buat constructor dan getter jika diperlukan
    public FavoriteMovie(int movieId, String title, String posterPath) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
