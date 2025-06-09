package com.example.filmhub.viewmodel; // Sesuaikan dengan package Anda

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // <-- REVISI: Gunakan MutableLiveData
import com.example.filmhub.data.model.GenreResponse;
import com.example.filmhub.data.model.MovieResponse;
import com.example.filmhub.data.repository.MovieRepository;

public class HomeViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;

    // REVISI: Gunakan MutableLiveData dan langsung inisialisasi di sini.
    // MutableLiveData bisa diubah nilainya, sedangkan LiveData hanya bisa dibaca.
    private final MutableLiveData<MovieResponse> movieListLiveData = new MutableLiveData<>();
    private final MutableLiveData<GenreResponse> genresLiveData = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        movieRepository = MovieRepository.getInstance(application);
    }

    // ===================================================================================
    // METODE UNTUK MENDAPATKAN LIVE DATA (UNTUK DIOBSERVASI OLEH FRAGMENT)
    // Getter akan tetap mengembalikan LiveData agar Fragment tidak bisa mengubah datanya.
    // ===================================================================================

    public LiveData<MovieResponse> getMovieListLiveData() {
        return movieListLiveData;
    }

    public LiveData<GenreResponse> getGenresLiveData() {
        return genresLiveData;
    }

    // ===================================================================================
    // METODE UNTUK MEMICU PENGAMBILAN DATA DARI REPOSITORY (DIPANGGIL DARI FRAGMENT)
    // REVISI: Metode ini sekarang meng-update nilai MutableLiveData yang sudah ada.
    // ===================================================================================

    public void fetchGenres() {
        // Repository mengembalikan LiveData, kita observasi di sini
        // dan meneruskan nilainya ke LiveData milik ViewModel.
        movieRepository.getGenres().observeForever(genreResponse -> {
            genresLiveData.setValue(genreResponse);
        });
    }

    public void loadDefaultMovies() {
        movieRepository.getDiscoverMovies("popularity.desc", "", 1)
                .observeForever(movieResponse -> {
                    movieListLiveData.setValue(movieResponse);
                });
    }

    public void searchMovies(String query, int page) {
        movieRepository.searchMovies(query, page)
                .observeForever(movieResponse -> {
                    movieListLiveData.setValue(movieResponse);
                });
    }

    public void loadMoviesWithFilters(String sortBy, String genreIds) {
        movieRepository.getDiscoverMovies(sortBy, genreIds, 1)
                .observeForever(movieResponse -> {
                    movieListLiveData.setValue(movieResponse);
                });
    }
}