package com.example.filmhub.data.repository;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmhub.BuildConfig;
import com.example.filmhub.database.entities.FavoriteMovie;
import com.example.filmhub.data.model.GenreResponse;
import com.example.filmhub.data.model.MovieDetailResponse;
import com.example.filmhub.data.model.MovieResponse;
import com.example.filmhub.database.entities.WatchedMovie;
import com.example.filmhub.database.dao.FavoriteMovieDao;
import com.example.filmhub.database.dao.WatchedMovieDao;
import com.example.filmhub.database.db.AppDatabase;
import com.example.filmhub.networking.api.ApiService;
import com.example.filmhub.networking.clients.RetrofitClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository bertindak sebagai Single Source of Truth.
 * Mengelola pengambilan data dari sumber remote (API) dan lokal (database Room).
 * ViewModel hanya akan berkomunikasi dengan Repository, tidak langsung ke API atau Database.
 */
public class MovieRepository {

    private final ApiService apiService;
    private final FavoriteMovieDao favoriteMovieDao;
    private final WatchedMovieDao watchedMovieDao;
    private final ExecutorService databaseExecutor; // <-- REVISI: Tambahkan Executor
    private static MovieRepository instance;

    // Constructor diubah untuk menerima Application context agar bisa inisialisasi database
    private MovieRepository(Application application) {
        // Inisialisasi Retrofit ApiService
        this.apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Inisialisasi Database dan DAO
        AppDatabase database = AppDatabase.getInstance(application);
        this.favoriteMovieDao = database.favoriteMovieDao();
        this.watchedMovieDao = database.watchedMovieDao();

        // REVISI: Inisialisasi Executor untuk menjalankan operasi database di background
        this.databaseExecutor = Executors.newSingleThreadExecutor();
    }

    // Singleton pattern diubah untuk menerima Application context
    public static synchronized MovieRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MovieRepository(application);
        }
        return instance;
    }

    // ===================================================================================
    // BAGIAN API (TIDAK ADA PERUBAHAN)
    // ===================================================================================

    public LiveData<MovieResponse> getDiscoverMovies(String sortBy, String genreIds, int page) {
        MutableLiveData<MovieResponse> data = new MutableLiveData<>();
        apiService.getDiscoverMovies(BuildConfig.API_KEY, sortBy, genreIds, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public LiveData<MovieResponse> searchMovies(String query, int page) {
        MutableLiveData<MovieResponse> data = new MutableLiveData<>();
        apiService.searchMovies(BuildConfig.API_KEY, query, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public LiveData<GenreResponse> getGenres() {
        MutableLiveData<GenreResponse> data = new MutableLiveData<>();
        apiService.getGenres(BuildConfig.API_KEY)
                .enqueue(new Callback<GenreResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GenreResponse> call, @NonNull Response<GenreResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<GenreResponse> call, @NonNull Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public LiveData<MovieDetailResponse> getMovieDetails(int movieId) {
        MutableLiveData<MovieDetailResponse> data = new MutableLiveData<>();
        apiService.getMovieDetails(movieId, BuildConfig.API_KEY)
                .enqueue(new Callback<MovieDetailResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieDetailResponse> call, @NonNull Response<MovieDetailResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<MovieDetailResponse> call, @NonNull Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    // ===================================================================================
    // REVISI: BAGIAN INTERAKSI DENGAN DATABASE FAVORIT
    // ===================================================================================

    public LiveData<List<FavoriteMovie>> getAllFavoriteMovies() {
        return favoriteMovieDao.getAllFavoriteMovies();
    }

    public LiveData<FavoriteMovie> getFavoriteStatus(int movieId) {
        return favoriteMovieDao.getFavoriteMovieById(movieId);
    }

    public void insertFavorite(FavoriteMovie favoriteMovie) {
        databaseExecutor.execute(() -> {
            favoriteMovieDao.insert(favoriteMovie);
        });
    }

    public void deleteFavorite(FavoriteMovie favoriteMovie) {
        databaseExecutor.execute(() -> {
            favoriteMovieDao.delete(favoriteMovie);
        });
    }

    // ===================================================================================
    // REVISI: BAGIAN INTERAKSI DENGAN DATABASE "SUDAH DITONTON"
    // ===================================================================================

    public LiveData<List<WatchedMovie>> getAllWatchedMovies() {
        return watchedMovieDao.getAllWatchedMovies();
    }

    public LiveData<WatchedMovie> getWatchedStatus(int movieId) {
        return watchedMovieDao.getWatchedMovieById(movieId);
    }

    public void saveWatchedMovie(WatchedMovie watchedMovie) {
        databaseExecutor.execute(() -> {
            watchedMovieDao.insertOrUpdate(watchedMovie);
        });
    }

    public void deleteWatchedMovie(int movieId) { // <-- TAMBAHKAN METODE INI
        databaseExecutor.execute(() -> {
            watchedMovieDao.deleteById(movieId);
        });
    }
}