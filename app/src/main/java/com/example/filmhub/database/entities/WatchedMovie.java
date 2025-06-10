package com.example.filmhub.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "watched_movies")
public class WatchedMovie {
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    public int movieId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "poster_path")
    public String posterPath;

    @ColumnInfo(name = "watched_date")
    public long watchedDate; // Simpan sebagai timestamp (long) agar mudah diurutkan

    @ColumnInfo(name = "user_rating")
    public float userRating; // Gunakan float untuk rating seperti 4.5

    @ColumnInfo(name = "user_review")
    public String userReview;

    @ColumnInfo(name = "runtime")
    public int runtime; // Durasi film dalam menit

    // TAMBAHKAN KOLOM INI
    @ColumnInfo(name = "genres")
    public String genres;

    // Buat constructor dan getter/setter yang lengkap
    public WatchedMovie() {
    }

    public WatchedMovie(int movieId, String title, String posterPath, long watchedDate, float userRating, String userReview, int runtime) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.watchedDate = watchedDate;
        this.userRating = userRating;
        this.userReview = userReview;
        this.runtime = runtime;
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

    public long getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(long watchedDate) {
        this.watchedDate = watchedDate;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getUserReview() {
        return userReview;
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
}
