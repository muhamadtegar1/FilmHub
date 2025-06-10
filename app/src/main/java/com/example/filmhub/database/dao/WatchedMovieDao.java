package com.example.filmhub.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.filmhub.database.entities.WatchedMovie;
import java.util.List;

@Dao
public interface WatchedMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(WatchedMovie watchedMovie);

    @Query("SELECT * FROM watched_movies ORDER BY watched_date DESC")
    LiveData<List<WatchedMovie>> getAllWatchedMovies();

    @Query("SELECT * FROM watched_movies WHERE movie_id = :movieId")
    LiveData<WatchedMovie> getWatchedMovieById(int movieId);

    @Query("DELETE FROM watched_movies WHERE movie_id = :movieId")
    void deleteById(int movieId);
}
