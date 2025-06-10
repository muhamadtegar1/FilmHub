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
import androidx.lifecycle.MediatorLiveData;


public class HomeViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;
    private final LiveData<GenreResponse> genresLiveData;

    // MediatorLiveData adalah LiveData "super" yang bisa mengobservasi LiveData lain.
    private final MediatorLiveData<List<Movie>> movieList = new MediatorLiveData<>();

    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;
    private String currentQuery = "";
    private String currentSortBy = "popularity.desc";
    private String currentGenreIds = "";

    public HomeViewModel(@NonNull Application application) {
        super(application);
        movieRepository = MovieRepository.getInstance(application);
        genresLiveData = movieRepository.getGenres();
        loadMovies(); // Muat data awal
    }

    public LiveData<List<Movie>> getMovieList() {
        return movieList;
    }

    public LiveData<GenreResponse> getGenresLiveData() {
        return genresLiveData;
    }

    public void loadMoreMovies() {
        if (isLoading || currentPage >= totalPages) return;
        currentPage++;
        loadMovies();
    }

    public void refreshData() {
        currentPage = 1;
        totalPages = 1;
        loadMovies();
    }

    public void applySearch(String query) {
        currentQuery = query;
        refreshData();
    }

    public void applyFilters(String sortBy, String genreIds) {
        currentQuery = ""; // Hapus query pencarian saat filter diterapkan
        currentSortBy = sortBy;
        currentGenreIds = genreIds;
        refreshData();
    }

    private void loadMovies() {
        isLoading = true;
        LiveData<MovieResponse> source;
        if (!currentQuery.isEmpty()) {
            source = movieRepository.searchMovies(currentQuery, currentPage);
        } else {
            source = movieRepository.getDiscoverMovies(currentSortBy, currentGenreIds, currentPage);
        }

        movieList.addSource(source, movieResponse -> {
            if (movieResponse != null && movieResponse.getResults() != null) {
                totalPages = movieResponse.getTotalPages();
                List<Movie> currentList = (currentPage == 1) ? new ArrayList<>() : movieList.getValue();
                if (currentList == null) {
                    currentList = new ArrayList<>();
                }
                currentList.addAll(movieResponse.getResults());
                movieList.setValue(currentList);
            } else {
                if (currentPage == 1) {
                    movieList.setValue(null); // Kirim null untuk menandakan error
                }
            }
            isLoading = false;
            movieList.removeSource(source);
        });
    }
}