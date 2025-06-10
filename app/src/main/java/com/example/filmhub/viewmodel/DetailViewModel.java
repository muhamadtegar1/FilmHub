package com.example.filmhub.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // <-- REVISI: Gunakan MutableLiveData
import com.example.filmhub.database.entities.FavoriteMovie;
import com.example.filmhub.data.model.MovieDetailResponse;
import com.example.filmhub.database.entities.WatchedMovie;
import com.example.filmhub.data.repository.MovieRepository;
import androidx.lifecycle.MediatorLiveData; // <-- REVISI: Gunakan MediatorLiveData

public class DetailViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;

    // REVISI: Ubah MutableLiveData menjadi MediatorLiveData agar konsisten
    private final MediatorLiveData<MovieDetailResponse> movieDetailsLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<FavoriteMovie> favoriteStatusLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<WatchedMovie> watchedStatusLiveData = new MediatorLiveData<>();

    // Variabel untuk menyimpan referensi ke sumber LiveData, agar bisa di-remove nanti
    private LiveData<MovieDetailResponse> detailsSource;
    private LiveData<FavoriteMovie> currentFavoriteSource;
    private LiveData<WatchedMovie> currentWatchedSource;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        this.movieRepository = MovieRepository.getInstance(application);
    }

    // ===================================================================================
    // GETTER UNTUK DIOBSERVASI OLEH UI (Tidak ada perubahan di sini)
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
    // AKSI YANG DIPANGGIL DARI UI
    // ===================================================================================

    public void loadAllData(int movieId) {
        // Ambil LiveData dari Repository
        detailsSource = movieRepository.getMovieDetails(movieId);
        currentFavoriteSource = movieRepository.getFavoriteStatus(movieId);
        currentWatchedSource = movieRepository.getWatchedStatus(movieId);

        // Teruskan nilai dari sumber ke LiveData milik ViewModel
        movieDetailsLiveData.addSource(detailsSource, movieDetails -> {
            movieDetailsLiveData.setValue(movieDetails);
            // Hapus sumber setelah dapat nilai agar tidak memicu update ganda
            movieDetailsLiveData.removeSource(detailsSource);
        });

        favoriteStatusLiveData.addSource(currentFavoriteSource, favoriteMovie -> {
            favoriteStatusLiveData.setValue(favoriteMovie);
        });

        watchedStatusLiveData.addSource(currentWatchedSource, watchedMovie -> {
            watchedStatusLiveData.setValue(watchedMovie);
        });
    }

    // Metode toggleFavorite dan saveWatchedMovie tidak ada perubahan
    public void toggleFavorite() {
        MovieDetailResponse movieDetails = movieDetailsLiveData.getValue();
        FavoriteMovie favoriteMovie = favoriteStatusLiveData.getValue();
        if (movieDetails == null) return;
        if (favoriteMovie == null) {
            FavoriteMovie newFavorite = new FavoriteMovie(
                    movieDetails.getId(), movieDetails.getTitle(), movieDetails.getPosterPath());
            movieRepository.insertFavorite(newFavorite);
        } else {
            movieRepository.deleteFavorite(favoriteMovie);
        }
    }

    public void saveWatchedMovie(WatchedMovie watchedMovie) {
        movieRepository.saveWatchedMovie(watchedMovie);
    }

    // onCleared untuk membersihkan semua source agar tidak ada memory leak
    @Override
    protected void onCleared() {
        super.onCleared();
        if (detailsSource != null) {
            movieDetailsLiveData.removeSource(detailsSource);
        }
        if (currentFavoriteSource != null) {
            favoriteStatusLiveData.removeSource(currentFavoriteSource);
        }
        if (currentWatchedSource != null) {
            watchedStatusLiveData.removeSource(currentWatchedSource);
        }
    }
}
