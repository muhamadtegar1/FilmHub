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

    // Deklarasi komponen data & logika
    private HomeViewModel homeViewModel;
    private MovieListAdapter movieAdapter;

    // Variabel untuk menyimpan state filter & sortir saat ini
    private String currentSortBy = "popularity.desc"; // Nilai default
    private String currentGenreIds = "";              // Nilai default
    private LinearLayout errorLayout;   // <-- REVISI: Tambahan
    private Button btnRefresh;          // <-- REVISI: Tambahan
    private TextView tvEmptyMessage;    // <-- REVISI: Tambahan (jika belum ada)

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initViewModel();
        setupRecyclerView();
        setupSortSpinner();
        setupListeners();
        observeViewModel();

        // Panggilan data awal sekarang dikelola oleh ViewModel constructor,
        // jadi kita bisa hapus blok 'if (savedInstanceState == null)' dari sini.
    }

    // Inisialisasi semua view dari layout XML
    private void initViews(View view) {
        recyclerViewMovies = view.findViewById(R.id.recycler_view_movies); // Ganti dengan ID Anda
        searchView = view.findViewById(R.id.search_view); // Ganti dengan ID Anda
        chipGroupGenres = view.findViewById(R.id.chip_group_genres); // Ganti dengan ID Anda
        spinnerSort = view.findViewById(R.id.spinner_sort); // Ganti dengan ID Anda
        progressBar = view.findViewById(R.id.progress_bar); // Ganti dengan ID Anda
        errorLayout = view.findViewById(R.id.error_layout);   // <-- REVISI: Inisialisasi layout error
        btnRefresh = view.findViewById(R.id.btn_refresh);     // <-- REVISI: Inisialisasi tombol refresh
    }

    // Inisialisasi ViewModel
    private void initViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    // Setup awal untuk RecyclerView
    private void setupRecyclerView() {
        movieAdapter = new MovieListAdapter(this);
        recyclerViewMovies.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Tampilan grid 2 kolom
        recyclerViewMovies.setAdapter(movieAdapter);
    }

    // Mengisi data ke dalam Spinner Sortir
    private void setupSortSpinner() {
        // Buat array string di res/values/strings.xml untuk opsi sortir
        // <string-array name="sort_options">
        //     <item>Paling Populer</item>
        //     <item>Rating Tertinggi</item>
        //     <item>Rilis Terbaru</item>
        // </string-array>
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);
    }

    // Setup semua listener untuk interaksi pengguna
    private void setupListeners() {
        // Listener untuk SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    homeViewModel.searchMovies(query); // <-- Panggil metode baru
                    searchView.clearFocus();
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });

        searchView.setOnCloseListener(() -> {
            progressBar.setVisibility(View.VISIBLE);
            homeViewModel.loadDefaultMovies();
            return false;
        });

        // REVISI: Listener untuk ChipGroup
        chipGroupGenres.setOnCheckedStateChangeListener((group, checkedIds) -> {
            StringBuilder selectedGenreIds = new StringBuilder();
            for (Integer id : checkedIds) {
                Chip chip = group.findViewById(id);
                if (chip != null) {
                    if (selectedGenreIds.length() > 0) {
                        selectedGenreIds.append(",");
                    }
                    selectedGenreIds.append(chip.getTag().toString());
                }
            }
            progressBar.setVisibility(View.VISIBLE);
            homeViewModel.setGenreIds(selectedGenreIds.toString()); // <-- Panggil metode baru
        });

        // REVISI: Listener untuk Spinner
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortByValue = getSortParameterFromPosition(position);
                progressBar.setVisibility(View.VISIBLE);
                homeViewModel.setSortBy(sortByValue); // <-- Panggil metode baru
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener untuk tombol refresh tetap sama
        btnRefresh.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            homeViewModel.loadDefaultMovies();
        });
    }

    // Mengobservasi perubahan data dari ViewModel
    private void observeViewModel() {
        // <-- REVISI: Logika di dalam observer ini diubah total -->
        homeViewModel.getMovieListLiveData().observe(getViewLifecycleOwner(), movieResponse -> {
            progressBar.setVisibility(View.GONE);
            if (movieResponse != null && movieResponse.getResults() != null) {
                // KONDISI SUKSES
                errorLayout.setVisibility(View.GONE);
                recyclerViewMovies.setVisibility(View.VISIBLE);
                movieAdapter.setMovieList(movieResponse.getResults());
            } else {
                // KONDISI GAGAL (TIDAK ADA KONEKSI)
                errorLayout.setVisibility(View.VISIBLE);
                recyclerViewMovies.setVisibility(View.GONE);
            }
        });

        // Observasi genre tetap sama
        homeViewModel.getGenresLiveData().observe(getViewLifecycleOwner(), genreResponse -> {
            if (genreResponse != null && genreResponse.getGenres() != null) {
                displayGenresAsChips(genreResponse.getGenres());
            } else {
                Toast.makeText(getContext(), "Gagal memuat genre", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode untuk menampilkan genre sebagai Chip secara dinamis
    private void displayGenresAsChips(List<Genre> genres) {
        chipGroupGenres.removeAllViews();
        for (Genre genre : genres) {
            Chip chip = new Chip(getContext());
            chip.setText(genre.getName());
            chip.setCheckable(true);
            chip.setTag(genre.getId());
            // Beri ID unik agar bisa digunakan di onCheckedStateChangeListener
            chip.setId(View.generateViewId());
            chipGroupGenres.addView(chip);
        }
    }

    // Metode helper untuk mengubah posisi spinner menjadi parameter API
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

    // Callback saat item film di RecyclerView diklik
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
