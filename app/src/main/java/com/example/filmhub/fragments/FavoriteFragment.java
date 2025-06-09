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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmhub.R;
import com.example.filmhub.adapters.MovieListAdapter;
import com.example.filmhub.database.entities.FavoriteMovie;
import com.example.filmhub.data.model.Movie;
import com.example.filmhub.viewmodel.FavoriteViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment untuk menampilkan daftar film favorit dari database lokal (Room).
 * Berfungsi penuh secara offline.
 */
public class FavoriteFragment extends Fragment implements MovieListAdapter.OnMovieItemClickListener {

    private FavoriteViewModel favoriteViewModel;
    private RecyclerView recyclerViewFavorites;
    private MovieListAdapter movieListAdapter;
    private TextView tvEmptyMessage;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
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
        recyclerViewFavorites = view.findViewById(R.id.recycler_view_favorites);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_favorites);
    }

    private void initViewModel() {
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
    }

    private void setupRecyclerView() {
        // Kita menggunakan ulang MovieListAdapter yang sudah ada
        movieListAdapter = new MovieListAdapter(this);
        recyclerViewFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewFavorites.setAdapter(movieListAdapter);
    }

    private void observeViewModel() {
        favoriteViewModel.getAllFavoriteMovies().observe(getViewLifecycleOwner(), favoriteMovies -> {
            if (favoriteMovies != null && !favoriteMovies.isEmpty()) {
                // Jika daftar favorit tidak kosong, tampilkan RecyclerView
                recyclerViewFavorites.setVisibility(View.VISIBLE);
                tvEmptyMessage.setVisibility(View.GONE);

                // Konversi List<FavoriteMovie> menjadi List<Movie> agar bisa dipakai oleh adapter
                List<Movie> movies = convertFavoriteToMovie(favoriteMovies);
                movieListAdapter.setMovieList(movies);
            } else {
                // Jika daftar favorit kosong, tampilkan pesan
                recyclerViewFavorites.setVisibility(View.GONE);
                tvEmptyMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Metode helper untuk mengubah list objek FavoriteMovie menjadi list objek Movie.
     * Ini diperlukan agar kita bisa menggunakan ulang MovieListAdapter.
     */
    private List<Movie> convertFavoriteToMovie(List<FavoriteMovie> favoriteList) {
        List<Movie> movieList = new ArrayList<>();
        for (FavoriteMovie favMovie : favoriteList) {
            Movie movie = new Movie(); // Buat objek Movie baru
            movie.setId(favMovie.movieId);
            movie.setTitle(favMovie.title);
            movie.setPosterPath(favMovie.posterPath);
            // Set properti lain jika ada dan dibutuhkan oleh adapter
            movieList.add(movie);
        }
        return movieList;
    }

    // Callback saat item film diklik (dari implementasi interface)
    @Override
    public void onMovieClick(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", movieId);

        // Arahkan ke DetailActivity. Pastikan action ini ada di nav_graph.xml
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_favoriteFragment_to_detailActivity, bundle);
        }
    }
}
