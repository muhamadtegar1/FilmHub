package com.example.filmhub.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.filmhub.database.entities.FavoriteMovie;
import java.util.List;

@Dao
public interface FavoriteMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteMovie favoriteMovie);

    @Delete
    void delete(FavoriteMovie favoriteMovie);

    @Query("SELECT * FROM favorite_movies ORDER BY title ASC")
    LiveData<List<FavoriteMovie>> getAllFavoriteMovies();

    @Query("SELECT * FROM favorite_movies WHERE movie_id = :movieId")
    LiveData<FavoriteMovie> getFavoriteMovieById(int movieId);
}
