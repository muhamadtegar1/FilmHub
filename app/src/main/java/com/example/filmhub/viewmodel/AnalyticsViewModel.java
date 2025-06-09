package com.example.filmhub.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.filmhub.database.entities.WatchedMovie;
import com.example.filmhub.data.repository.MovieRepository;
import java.util.List;
import java.util.Locale;

public class AnalyticsViewModel extends AndroidViewModel {

    private final MovieRepository movieRepository;
    private final LiveData<List<WatchedMovie>> allWatchedMovies;

    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        movieRepository = MovieRepository.getInstance(application);
        allWatchedMovies = movieRepository.getAllWatchedMovies();
    }

    /**
     * Mengekspos LiveData yang berisi semua film yang sudah ditonton.
     */
    public LiveData<List<WatchedMovie>> getAllWatchedMovies() {
        return allWatchedMovies;
    }

    /**
     * Menghitung total durasi dari daftar film dan memformatnya menjadi "X jam Y menit".
     * @param movies Daftar film yang sudah ditonton.
     * @return String durasi yang sudah diformat.
     */
    public String getTotalDurationFormatted(List<WatchedMovie> movies) {
        if (movies == null || movies.isEmpty()) {
            return "0 menit";
        }
        int totalMinutes = 0;
        for (WatchedMovie movie : movies) {
            totalMinutes += movie.runtime;
        }
        if (totalMinutes < 60) {
            return totalMinutes + " menit";
        } else {
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            return String.format(Locale.getDefault(), "%d jam %d menit", hours, minutes);
        }
    }

    /**
     * Menghitung rata-rata rating personal dari daftar film.
     * @param movies Daftar film yang sudah ditonton.
     * @return float rata-rata rating.
     */
    public float getAverageRating(List<WatchedMovie> movies) {
        if (movies == null || movies.isEmpty()) {
            return 0.0f;
        }
        float totalRating = 0;
        for (WatchedMovie movie : movies) {
            totalRating += movie.userRating;
        }
        return totalRating / movies.size();
    }
}
