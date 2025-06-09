package com.example.filmhub.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.filmhub.database.entities.FavoriteMovie;
import com.example.filmhub.data.repository.MovieRepository;
import java.util.List;

/**
 * ViewModel untuk FavoriteFragment.
 * Bertanggung jawab untuk menyediakan daftar film favorit dari database lokal.
 */
public class FavoriteViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;
    private final LiveData<List<FavoriteMovie>> allFavoriteMovies;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        // Mendapatkan instance dari MovieRepository
        movieRepository = MovieRepository.getInstance(application);
        // Mengambil LiveData dari daftar film favorit saat ViewModel pertama kali dibuat
        allFavoriteMovies = movieRepository.getAllFavoriteMovies();
    }

    /**
     * Metode untuk diekspos ke Fragment.
     * Fragment akan mengobservasi LiveData ini untuk mendapatkan update
     * setiap kali ada perubahan pada data film favorit di database.
     * @return LiveData yang berisi daftar semua FavoriteMovie.
     */
    public LiveData<List<FavoriteMovie>> getAllFavoriteMovies() {
        return allFavoriteMovies;
    }
}
