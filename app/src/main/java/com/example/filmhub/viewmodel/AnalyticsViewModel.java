package com.example.filmhub.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.filmhub.database.entities.WatchedMovie;
import com.example.filmhub.data.repository.MovieRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
     *
     * @param movies Daftar film yang sudah ditonton.
     * @return String durasi yang sudah diformat.
     */
    /**
     * REVISI: Menghitung total durasi dan HANYA memformatnya dalam menit.
     * @param movies Daftar film yang sudah ditonton.
     * @return String durasi yang sudah diformat (misal: "150 menit").
     */
    public String getTotalDurationFormatted(List<WatchedMovie> movies) {
        if (movies == null || movies.isEmpty()) {
            return "0 menit";
        }
        int totalMinutes = 0;
        for (WatchedMovie movie : movies) {
            totalMinutes += movie.runtime;
        }
        // Langsung kembalikan dalam format menit
        return totalMinutes + " menit";
    }

    /**
     * Menghitung rata-rata rating personal dari daftar film.
     *
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

    public void deleteWatchedMovie(int movieId) { // <-- TAMBAHKAN METODE INI
        movieRepository.deleteWatchedMovie(movieId);
    }

    /**
     * Menghitung genre yang paling sering muncul dan mengembalikan Top 3.
     *
     * @param movies Daftar film yang sudah ditonton.
     * @return List of String berisi maksimal 3 nama genre teratas.
     */
    public List<String> getTopGenres(List<WatchedMovie> movies) {
        if (movies == null || movies.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Gunakan HashMap untuk menghitung frekuensi setiap genre
        Map<String, Integer> genreCounts = new HashMap<>();

        // 2. Loop semua film yang ditonton
        for (WatchedMovie movie : movies) {
            if (movie.genres != null && !movie.genres.isEmpty()) {
                // 3. Split string genre (misal: "Action, Sci-Fi")
                String[] genres = movie.genres.split(",");
                // 4. Tambahkan hitungan untuk setiap genre
                for (String genre : genres) {
                    String trimmedGenre = genre.trim();
                    if (!trimmedGenre.isEmpty()) {
                        genreCounts.put(trimmedGenre, genreCounts.getOrDefault(trimmedGenre, 0) + 1);
                    }
                }
            }
        }

        // 5. Ubah Map menjadi List agar bisa diurutkan
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(genreCounts.entrySet());

        // 6. Urutkan list secara descending berdasarkan jumlah (value)
        // (Jika Java 8+ tidak masalah, jika tidak gunakan Collections.sort dengan Comparator)
        sortedList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // 7. Ambil 3 nama genre teratas
        List<String> topGenres = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sortedList.size()); i++) {
            topGenres.add(sortedList.get(i).getKey());
        }

        return topGenres;
    }
}
