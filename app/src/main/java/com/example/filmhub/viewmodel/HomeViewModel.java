package com.example.filmhub.viewmodel; // Sesuaikan dengan package Anda

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // <-- REVISI: Gunakan MutableLiveData
import androidx.lifecycle.Observer;

import com.example.filmhub.data.model.GenreResponse;
import com.example.filmhub.data.model.MovieResponse;
import com.example.filmhub.data.repository.MovieRepository;
import com.example.filmhub.data.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;
    private final LiveData<GenreResponse> genresLiveData;

    // REVISI: LiveData untuk daftar film sekarang menampung list akumulatif
    private final MutableLiveData<List<Movie>> cumulativeMovieList = new MutableLiveData<>();

    // REVISI: Variabel untuk mengelola state pagination
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;
    private String currentSortBy = "popularity.desc";
    private String currentGenreIds = "";
    private String currentQuery = "";

    public HomeViewModel(@NonNull Application application) {
        super(application);
        movieRepository = MovieRepository.getInstance(application);
        genresLiveData = movieRepository.getGenres();
        // Muat data awal
        loadMovies();
    }

    // --- Getter untuk diobservasi oleh Fragment ---
    public LiveData<List<Movie>> getCumulativeMovieList() {
        return cumulativeMovieList;
    }

    public LiveData<GenreResponse> getGenresLiveData() {
        return genresLiveData;
    }

    // --- Metode untuk mengubah parameter & memuat data ---

    private void resetPagination() {
        currentPage = 1;
        totalPages = 1;
        cumulativeMovieList.setValue(new ArrayList<>()); // Kosongkan list
    }

    public void setSortBy(String sort) {
        if (!sort.equals(currentSortBy)) {
            currentSortBy = sort;
            resetPagination();
            loadMovies();
        }
    }

    public void setGenreIds(String genres) {
        if (!genres.equals(currentGenreIds)) {
            currentGenreIds = genres;
            resetPagination();
            loadMovies();
        }
    }

    public void searchMovies(String query) {
        currentQuery = query;
        resetPagination();
        loadMovies();
    }

    public void loadDefaultMovies() {
        currentQuery = "";
        setSortBy("popularity.desc"); // Ini akan otomatis memanggil reset dan loadMovies
    }

    public void loadMoreMovies() {
        // Hanya muat lebih banyak jika tidak sedang loading DAN masih ada halaman berikutnya
        if (isLoading || currentPage >= totalPages) {
            return;
        }
        currentPage++;
        loadMovies();
    }

    private void loadMovies() {
        isLoading = true;
        LiveData<MovieResponse> source;
        if (!currentQuery.isEmpty()) {
            source = movieRepository.searchMovies(currentQuery, currentPage);
        } else {
            source = movieRepository.getDiscoverMovies(currentSortBy, currentGenreIds, currentPage);
        }

        source.observeForever(new Observer<MovieResponse>() {
            @Override
            public void onChanged(MovieResponse movieResponse) {
                if (movieResponse != null && movieResponse.getResults() != null) {
                    List<Movie> currentList = cumulativeMovieList.getValue();
                    if (currentList == null) {
                        currentList = new ArrayList<>();
                    }
                    currentList.addAll(movieResponse.getResults());
                    cumulativeMovieList.setValue(currentList);
                    totalPages = movieResponse.getTotalPages();
                }
                isLoading = false;
                source.removeObserver(this); // Hapus observer agar tidak menumpuk
            }
        });
    }
}