package com.example.filmhub.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.filmhub.adapters.MovieListAdapter;
import com.example.filmhub.data.model.Genre;
import com.example.filmhub.viewmodel.HomeViewModel;
import com.example.filmhub.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment utama yang menampilkan daftar film.
 * Mengelola interaksi pengguna seperti pencarian, filter, dan sortir.
 */

public class HomeFragment extends Fragment implements MovieListAdapter.OnMovieItemClickListener {

    // REVISI: Deklarasi variabel disatukan dan disederhanakan
    private RecyclerView recyclerViewMovies;
    private MovieListAdapter movieAdapter;
    private HomeViewModel homeViewModel;
    private GridLayoutManager gridLayoutManager;

    // UI untuk kontrol
    private SearchView searchView;
    private ChipGroup chipGroupGenres;
    private Spinner spinnerSort;

    // UI untuk state (loading, error, empty)
    private ProgressBar progressBar;
    private LinearLayout errorLayoutConnection;
    private Button btnRefresh;
    private TextView tvMessage;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Urutan ini lebih aman
        initViews(view);
        setupRecyclerView();
        setupSortSpinner();
        initViewModel(); // ViewModel diinisialisasi terakhir sebelum observasi
        setupListeners(); // Listener disetup sebelum ViewModel diobservasi

    }

    private void initViews(View view) {
        recyclerViewMovies = view.findViewById(R.id.recycler_view_movies);
        searchView = view.findViewById(R.id.search_view);
        chipGroupGenres = view.findViewById(R.id.chip_group_genres);
        spinnerSort = view.findViewById(R.id.spinner_sort);
        progressBar = view.findViewById(R.id.progress_bar);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        errorLayoutConnection = view.findViewById(R.id.error_layout_connection); // REVISI
        btnRefresh = view.findViewById(R.id.btn_refresh);
        tvMessage = view.findViewById(R.id.tv_message); // REVISI
    }

    private void initViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // Langsung observasi dan muat data awal
        observeViewModel();
        homeViewModel.refreshData(); // Memanggil data awal (populer)
    }

    private void setupRecyclerView() {
        movieAdapter = new MovieListAdapter(this);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewMovies.setLayoutManager(gridLayoutManager);
        recyclerViewMovies.setAdapter(movieAdapter);
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);
    }

    private void setupListeners() {
        // Listener untuk SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    showLayout(progressBar);
                    homeViewModel.applySearch(query);
                    searchView.clearFocus();
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // Jika teks pencarian kosong, reset ke daftar default
                if (newText.isEmpty() && !searchView.isIconified()) {
                    showLayout(progressBar);
                    homeViewModel.refreshData();
                }
                return true;
            }
        });

        // Listener untuk ChipGroup dan Spinner sekarang digabung ke satu metode
        ChipGroup.OnCheckedStateChangeListener chipListener = (group, checkedIds) -> applyFilters();
        chipGroupGenres.setOnCheckedStateChangeListener(chipListener);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRefresh.setOnClickListener(v -> {
            showLayout(progressBar);
            homeViewModel.refreshData();
        });

        // Listener untuk Pagination
        recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    homeViewModel.loadMoreMovies();
                }
            }
        });
    }

    // Metode helper untuk menerapkan filter dan sortir
    private void applyFilters() {
        showLayout(progressBar);
        String sortBy = getSortParameterFromPosition(spinnerSort.getSelectedItemPosition());
        StringBuilder selectedGenreIds = new StringBuilder();
        for (Integer id : chipGroupGenres.getCheckedChipIds()) {
            Chip chip = chipGroupGenres.findViewById(id);
            if (chip != null) {
                if (selectedGenreIds.length() > 0) {
                    selectedGenreIds.append(",");
                }
                selectedGenreIds.append(chip.getTag().toString());
            }
        }
        homeViewModel.applyFilters(sortBy, selectedGenreIds.toString());
    }

    private void observeViewModel() {
        homeViewModel.getMovieList().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                if (movies.isEmpty()) {
                    showLayout(tvMessage);
                    tvMessage.setText("Film tidak ditemukan.");
                } else {
                    showLayout(recyclerViewMovies);
                    movieAdapter.setMovieList(movies);
                }
            } else {
                showLayout(errorLayoutConnection);
            }
        });

        homeViewModel.getGenresLiveData().observe(getViewLifecycleOwner(), genreResponse -> {
            if (genreResponse != null && genreResponse.getGenres() != null) {
                displayGenresAsChips(genreResponse.getGenres());
            } else {
                Toast.makeText(getContext(), "Gagal memuat genre", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLayout(View viewToShow) {
        progressBar.setVisibility(View.GONE);
        recyclerViewMovies.setVisibility(View.GONE);
        errorLayoutConnection.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);

        if (viewToShow != null) {
            viewToShow.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void displayGenresAsChips(List<Genre> genres) {
        chipGroupGenres.removeAllViews();
        for (Genre genre : genres) {
            Chip chip = new Chip(getContext());
            chip.setText(genre.getName());
            chip.setCheckable(true);
            chip.setTag(genre.getId());
            chip.setId(View.generateViewId());
            chipGroupGenres.addView(chip);
        }
    }

    private String getSortParameterFromPosition(int position) {
        switch (position) {
            case 1:
                return "vote_average.desc";
            case 2:
                return "release_date.desc";
            default:
            case 0:
                return "popularity.desc";
        }
    }

    @Override
    public void onMovieClick(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", movieId); // "movieId" harus sama dengan nama argumen di nav_graph

        // Gunakan NavController untuk navigasi ke DetailActivity
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_detailActivity, bundle);
        }
    }
}




