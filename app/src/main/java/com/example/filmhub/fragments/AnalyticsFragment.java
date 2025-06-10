package com.example.filmhub.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.filmhub.R;
import com.example.filmhub.adapters.WatchedListAdapter;
import com.example.filmhub.viewmodel.AnalyticsViewModel;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Implementasi interface dari adapter sudah benar
public class AnalyticsFragment extends Fragment implements WatchedListAdapter.OnWatchedItemInteractionListener {

    private AnalyticsViewModel analyticsViewModel;
    private TextView tvTotalMovies, tvTotalDuration, tvAverageRating, tvEmptyMessage;
    private RecyclerView recyclerViewWatched;
    private WatchedListAdapter watchedListAdapter;
    // REVISI: Deklarasi TextView untuk Top 3 Genre
    private TextView tvTopGenre1, tvTopGenre2, tvTopGenre3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initViewModel();
        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        tvTotalMovies = view.findViewById(R.id.tv_total_movies_watched);
        tvTotalDuration = view.findViewById(R.id.tv_total_duration);
        tvAverageRating = view.findViewById(R.id.tv_average_rating);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_watched);
        recyclerViewWatched = view.findViewById(R.id.recycler_view_watched);

        // REVISI: Inisialisasi TextView untuk Top 3 Genre
        tvTopGenre1 = view.findViewById(R.id.tv_top_genre_1);
        tvTopGenre2 = view.findViewById(R.id.tv_top_genre_2);
        tvTopGenre3 = view.findViewById(R.id.tv_top_genre_3);
    }

    private void initViewModel() {
        analyticsViewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);
    }

    private void setupRecyclerView() {
        watchedListAdapter = new WatchedListAdapter(this);
        recyclerViewWatched.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewWatched.setAdapter(watchedListAdapter);
    }

    private void observeViewModel() {
        analyticsViewModel.getAllWatchedMovies().observe(getViewLifecycleOwner(), watchedMovies -> {
            if (watchedMovies != null && !watchedMovies.isEmpty()) {
                // Tampilkan data dan RecyclerView
                tvEmptyMessage.setVisibility(View.GONE);
                recyclerViewWatched.setVisibility(View.VISIBLE);

                // Update statistik
                tvTotalMovies.setText(String.valueOf(watchedMovies.size()));
                tvTotalDuration.setText(analyticsViewModel.getTotalDurationFormatted(watchedMovies));
                String avgRatingText = String.format(Locale.getDefault(), "%.1f", analyticsViewModel.getAverageRating(watchedMovies));
                tvAverageRating.setText(avgRatingText);

                // REVISI: Panggil metode untuk menghitung dan menampilkan Top 3 Genre
                List<String> topGenres = analyticsViewModel.getTopGenres(watchedMovies);
                updateTopGenresUi(topGenres);

                // Update adapter
                watchedListAdapter.setWatchedMovieList(watchedMovies);

            } else {
                // Tampilkan pesan kosong
                tvEmptyMessage.setVisibility(View.VISIBLE);
                recyclerViewWatched.setVisibility(View.GONE);

                // Reset statistik
                tvTotalMovies.setText("0");
                tvTotalDuration.setText("0 menit");
                tvAverageRating.setText("-");
                // REVISI: Reset juga tampilan genre
                updateTopGenresUi(new ArrayList<>()); // Kirim list kosong untuk mereset
            }
        });
    }

    /**
     * REVISI: Metode helper baru untuk mengupdate UI Top 3 Genre
     */
    private void updateTopGenresUi(List<String> topGenres) {
        // Mengatur teks untuk TextView genre, dengan pengecekan jika jumlahnya kurang dari 3
        tvTopGenre1.setText(topGenres.size() > 0 ? "1. " + topGenres.get(0) : "1. -");
        tvTopGenre2.setText(topGenres.size() > 1 ? "2. " + topGenres.get(1) : "2. -");
        tvTopGenre3.setText(topGenres.size() > 2 ? "3. " + topGenres.get(2) : "3. -");
    }

    // Metode callback dari interface untuk menghapus item
    @Override
    public void onDeleteClicked(int movieId) {
        analyticsViewModel.deleteWatchedMovie(movieId);
        Toast.makeText(getContext(), "Catatan tontonan dihapus.", Toast.LENGTH_SHORT).show();
    }
}