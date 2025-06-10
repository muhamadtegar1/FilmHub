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

    // LiveData utama yang akan diobservasi oleh Fragment
    private final MutableLiveData<MovieResponse> movieListLiveData = new MutableLiveData<>();
    private final LiveData<GenreResponse> genresLiveData;

    // MutableLiveData untuk menampung parameter filter dan sortir
    private final MutableLiveData<String> sortBy = new MutableLiveData<>("popularity.desc");
    private final MutableLiveData<String> genreIds = new MutableLiveData<>("");

    public HomeViewModel(@NonNull Application application) {
        super(application);
        movieRepository = MovieRepository.getInstance(application);

        // Ambil daftar genre sekali saja saat ViewModel dibuat
        genresLiveData = movieRepository.getGenres();

        // Muat film pertama kali
        loadMovies();
    }

    // --- Metode untuk mendapatkan LiveData (tidak berubah) ---
    public LiveData<MovieResponse> getMovieListLiveData() {
        return movieListLiveData;
    }

    public LiveData<GenreResponse> getGenresLiveData() {
        return genresLiveData;
    }

    // --- Metode untuk MENGUBAH parameter (dipanggil dari Fragment) ---
    public void setSortBy(String sort) {
        // Jika nilai sortir berubah, update LiveData-nya
        if (!sort.equals(this.sortBy.getValue())) {
            this.sortBy.setValue(sort);
            loadMovies(); // Muat ulang film dengan parameter baru
        }
    }

    public void setGenreIds(String genres) {
        // Jika nilai genre berubah, update LiveData-nya
        if (!genres.equals(this.genreIds.getValue())) {
            this.genreIds.setValue(genres);
            loadMovies(); // Muat ulang film dengan parameter baru
        }
    }

    public void searchMovies(String query) {
        // Pencarian adalah kasus khusus, kita langsung panggil repository
        // dan update LiveData utama kita saat hasilnya kembali
        movieRepository.searchMovies(query, 1).observeForever(movieResponse -> {
            movieListLiveData.setValue(movieResponse);
        });
    }

    public void loadDefaultMovies() {
        // Reset filter dan sortir ke default, lalu muat ulang
        this.sortBy.setValue("popularity.desc");
        this.genreIds.setValue("");
        loadMovies();
    }

    // --- Metode PRIVATE untuk memuat ulang data film ---
    private void loadMovies() {
        String currentSort = sortBy.getValue();
        String currentGenres = genreIds.getValue();
        // Ambil data dari repository dan "salin" hasilnya ke LiveData utama kita
        movieRepository.getDiscoverMovies(currentSort, currentGenres, 1).observeForever(movieResponse -> {
            movieListLiveData.setValue(movieResponse);
        });
    }
}