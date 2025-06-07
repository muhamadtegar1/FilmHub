package com.example.filmhub.data.repository;

import android.app.Application; // <-- Import Application untuk inisialisasi database

import androidx.annotation.NonNull; // <-- Import untuk anotasi @NonNull
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.filmhub.BuildConfig;
import com.example.filmhub.data.model.GenreResponse;
import com.example.filmhub.data.model.MovieDetailResponse;
import com.example.filmhub.data.model.MovieResponse;
import com.example.filmhub.database.dao.FavoriteMovieDao;
import com.example.filmhub.database.dao.WatchedMovieDao;
import com.example.filmhub.database.db.AppDatabase;
import com.example.filmhub.networking.api.ApiService;
import com.example.filmhub.networking.clients.RetrofitClient;

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
    private final FavoriteMovieDao favoriteMovieDao; // <-- Tambahkan field untuk DAO
    private final WatchedMovieDao watchedMovieDao; // <-- Tambahkan field untuk DAO
    private static MovieRepository instance;

    // Constructor diubah untuk menerima Application context agar bisa inisialisasi database
    private MovieRepository(Application application) {
        // Inisialisasi Retrofit ApiService
        this.apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Inisialisasi Database dan DAO
        AppDatabase database = AppDatabase.getInstance(application);
        this.favoriteMovieDao = database.favoriteMovieDao();
        this.watchedMovieDao = database.watchedMovieDao();
    }

    // Singleton pattern diubah untuk menerima Application context
    public static synchronized MovieRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MovieRepository(application);
        }
        return instance;
    }

    /**
     * Mengambil daftar film dari API berdasarkan sortir, genre, dan halaman.
     * Digunakan untuk Halaman Utama.
     */
    public LiveData<MovieResponse> getDiscoverMovies(String sortBy, String genreIds, int page) {
        MutableLiveData<MovieResponse> data = new MutableLiveData<>();
        apiService.getDiscoverMovies(BuildConfig.API_KEY, sortBy, genreIds, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            // Bisa di-handle jika respons tidak sukses tapi bukan failure (misal: error 404)
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        // Handle jika terjadi kegagalan jaringan
                        data.setValue(null);
                    }
                });
        return data;
    }

    /**
     * Mengambil daftar film dari API berdasarkan query pencarian.
     * Digunakan untuk fitur pencarian.
     */
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

    /**
     * Mengambil daftar semua genre yang tersedia dari API.
     * Digunakan untuk fitur filter genre.
     */
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

    /**
     * Mengambil detail lengkap satu film dari API berdasarkan ID film.
     * Digunakan untuk Halaman Detail.
     */
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

    // Nanti di fase berikutnya, kita akan menambahkan metode untuk berinteraksi dengan DAO
    // Contoh:
    // public LiveData<List<FavoriteMovie>> getAllFavoriteMovies() {
    //     return favoriteMovieDao.getAllFavoriteMovies();
    // }
    //
    // public void insertFavoriteMovie(FavoriteMovie movie) {
    //     // Jalankan di background thread
    // }
}
