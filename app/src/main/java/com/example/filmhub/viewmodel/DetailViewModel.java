package com.example.filmhub.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.filmhub.database.entities.FavoriteMovie;
import com.example.filmhub.data.model.MovieDetailResponse;
import com.example.filmhub.database.entities.WatchedMovie;
import com.example.filmhub.data.repository.MovieRepository;

/**
 * ViewModel untuk DetailActivity.
 * Mengelola semua data yang berkaitan dengan satu film,
 * baik dari API maupun dari database lokal (Favorit dan Sudah Ditonton).
 */
public class DetailViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;

    // LiveData untuk menampung data yang akan diobservasi oleh DetailActivity
    private LiveData<MovieDetailResponse> movieDetailsLiveData;
    private LiveData<FavoriteMovie> favoriteStatusLiveData;
    private LiveData<WatchedMovie> watchedStatusLiveData;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        // Mendapatkan satu-satunya instance dari MovieRepository
        this.movieRepository = MovieRepository.getInstance(application);
    }

    // ===================================================================================
    // METODE UNTUK MENDAPATKAN LIVE DATA (UNTUK DIOBSERVASI OLEH UI)
    // ===================================================================================

    public LiveData<MovieDetailResponse> getMovieDetailsLiveData() {
        return movieDetailsLiveData;
    }

    public LiveData<FavoriteMovie> getFavoriteStatusLiveData() {
        return favoriteStatusLiveData;
    }

    public LiveData<WatchedMovie> getWatchedStatusLiveData() {
        return watchedStatusLiveData;
    }

    // ===================================================================================
    // METODE UNTUK MEMICU AKSI (UNTUK DIPANGGIL DARI UI)
    // ===================================================================================

    /**
     * Metode utama yang dipanggil dari DetailActivity saat pertama kali dibuat.
     * Metode ini akan memicu pengambilan semua data yang diperlukan untuk film dengan ID tertentu.
     * @param movieId ID dari film yang akan ditampilkan.
     */
    public void loadAllData(int movieId) {
        // Meminta data detail film dari API melalui repository
        movieDetailsLiveData = movieRepository.getMovieDetails(movieId);
        // Meminta status favorit dari database melalui repository
        favoriteStatusLiveData = movieRepository.getFavoriteStatus(movieId);
        // Meminta status "sudah ditonton" dari database melalui repository
        watchedStatusLiveData = movieRepository.getWatchedStatus(movieId);
    }

    /**
     * Mengelola logika untuk tombol favorit.
     * Jika film belum favorit, maka akan ditambahkan.
     * Jika sudah favorit, maka akan dihapus.
     */
    public void toggleFavorite() {
        // Dapatkan data film saat ini dari LiveData
        MovieDetailResponse movieDetails = movieDetailsLiveData.getValue();
        FavoriteMovie favoriteMovie = favoriteStatusLiveData.getValue();

        // Pastikan data detail film sudah ada sebelum melakukan aksi
        if (movieDetails == null) {
            return; // Tidak melakukan apa-apa jika detail belum ter-load
        }

        if (favoriteMovie == null) {
            // Belum favorit -> Tambahkan ke database
            FavoriteMovie newFavorite = new FavoriteMovie(
                    movieDetails.getId(),
                    movieDetails.getTitle(),
                    movieDetails.getPosterPath()
            );
            movieRepository.insertFavorite(newFavorite);
        } else {
            // Sudah favorit -> Hapus dari database
            movieRepository.deleteFavorite(favoriteMovie);
        }
    }

    /**
     * Menyimpan data film yang sudah ditonton (beserta review dan rating) ke database.
     * @param watchedMovie Objek WatchedMovie yang berisi semua data dari dialog input.
     */
    public void saveWatchedMovie(WatchedMovie watchedMovie) {
        movieRepository.saveWatchedMovie(watchedMovie);
    }
}
