package com.example.filmhub.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class AnalyticsFragment extends Fragment {

    private AnalyticsViewModel analyticsViewModel;
    private TextView tvTotalMovies, tvTotalDuration, tvAverageRating, tvEmptyMessage;
    private RecyclerView recyclerViewWatched;
    private WatchedListAdapter watchedListAdapter;

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
    }

    private void initViewModel() {
        analyticsViewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);
    }

    private void setupRecyclerView() {
        watchedListAdapter = new WatchedListAdapter();
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
                tvTotalMovies.setText("Total Film Ditonton: " + watchedMovies.size());
                tvTotalDuration.setText("Total Durasi Tonton: " + analyticsViewModel.getTotalDurationFormatted(watchedMovies));
                String avgRatingText = String.format(Locale.getDefault(), "Rata-rata Rating Personal: %.1f/5.0", analyticsViewModel.getAverageRating(watchedMovies));
                tvAverageRating.setText(avgRatingText);

                // Update adapter
                watchedListAdapter.setWatchedMovieList(watchedMovies);

            } else {
                // Tampilkan pesan kosong
                tvEmptyMessage.setVisibility(View.VISIBLE);
                recyclerViewWatched.setVisibility(View.GONE);

                // Reset statistik
                tvTotalMovies.setText("Total Film Ditonton: 0");
                tvTotalDuration.setText("Total Durasi Tonton: 0 menit");
                tvAverageRating.setText("Rata-rata Rating Personal: -");
            }
        });
    }
}