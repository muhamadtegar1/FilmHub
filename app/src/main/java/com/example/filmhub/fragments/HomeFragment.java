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

    // Deklarasi komponen UI
    private RecyclerView recyclerViewMovies;
    private SearchView searchView;
    private ChipGroup chipGroupGenres;
    private Spinner spinnerSort;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRefresh;
    private TextView tvEmptyMessage; // Untuk pesan jika daftar favorit/analitik kosong

    // Deklarasi komponen data & logika
    private HomeViewModel homeViewModel;
    private MovieListAdapter movieAdapter;
    private GridLayoutManager gridLayoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupSortSpinner();
        initViewModel(); // Pindahkan initViewModel setelah setup UI
        setupListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerViewMovies = view.findViewById(R.id.recycler_view_movies);
        searchView = view.findViewById(R.id.search_view);
        chipGroupGenres = view.findViewById(R.id.chip_group_genres);
        spinnerSort = view.findViewById(R.id.spinner_sort);
        progressBar = view.findViewById(R.id.progress_bar);
        errorLayout = view.findViewById(R.id.error_layout);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        tvEmptyMessage = view.findViewById(R.id.tv_error_message); // Menggunakan ID yang sama untuk pesan error/kosong
    }

    private void initViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
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
                    showLoading();
                    homeViewModel.applySearch(query);
                    searchView.clearFocus();
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    showLoading();
                    homeViewModel.applySearch(""); // Memanggil dengan query kosong akan me-reset ke daftar populer
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
            showLoading();
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
        showLoading();
        // Dapatkan state saat ini dari UI dan kirim ke ViewModel
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
        // Mengobservasi daftar film
        homeViewModel.getMovieList().observe(getViewLifecycleOwner(), movies -> {
            hideLoading();
            if (movies != null) {
                showSuccessLayout();
                movieAdapter.setMovieList(movies);
                tvEmptyMessage.setVisibility(movies.isEmpty() ? View.VISIBLE : View.GONE);
                tvEmptyMessage.setText("Film tidak ditemukan.");
            } else {
                showErrorLayout();
            }
        });

        // Mengobservasi daftar genre
        homeViewModel.getGenresLiveData().observe(getViewLifecycleOwner(), genreResponse -> {
            if (genreResponse != null && genreResponse.getGenres() != null) {
                displayGenresAsChips(genreResponse.getGenres());
            } else {
                Toast.makeText(getContext(), "Gagal memuat genre", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode UI helper
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        recyclerViewMovies.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        progressBar.setVisibility(View.GONE);
        recyclerViewMovies.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void showSuccessLayout() {
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        recyclerViewMovies.setVisibility(View.VISIBLE);
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




