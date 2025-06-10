package com.example.filmhub.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.filmhub.R;
import com.example.filmhub.data.model.MovieDetailResponse;
import com.example.filmhub.database.entities.WatchedMovie;
import com.example.filmhub.viewmodel.DetailViewModel;

import java.util.Date;
import com.example.filmhub.data.model.Genre;
import java.util.stream.Collectors;

/**
 * DialogFragment untuk menampilkan pop-up input rating dan review.
 * Menggunakan Shared ViewModel (DetailViewModel) untuk berkomunikasi dengan DetailActivity.
 */
public class ReviewInputDialogFragment extends DialogFragment {

    private DetailViewModel detailViewModel;
    private RatingBar ratingBar;
    private EditText etReview;
    private Button btnCancel, btnSave;

    /**
     * Factory method untuk membuat instance fragment.
     * Cara yang direkomendasikan untuk membuat fragment baru.
     */
    public static ReviewInputDialogFragment newInstance() {
        return new ReviewInputDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set style agar dialog tidak fullscreen di beberapa perangkat
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FilmHub_Dialog);
        return inflater.inflate(R.layout.dialog_review_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi ViewModel. Menggunakan requireActivity() untuk mendapatkan ViewModel yang sama dengan Activity.
        detailViewModel = new ViewModelProvider(requireActivity()).get(DetailViewModel.class);

        // Inisialisasi Views dari layout dialog_review_input.xml
        ratingBar = view.findViewById(R.id.rating_bar_personal);
        etReview = view.findViewById(R.id.et_review);
        btnCancel = view.findViewById(R.id.btn_cancel_review);
        btnSave = view.findViewById(R.id.btn_save_review);

        // Observasi data film yang sudah ditonton untuk mengisi form jika sedang mode edit
        observeExistingWatchedData();
        setupListeners();
    }

    /**
     * Mengobservasi data yang ada. Jika pengguna sudah pernah mereview film ini,
     * form akan terisi otomatis dengan data sebelumnya (mode edit).
     */
    private void observeExistingWatchedData() {
        detailViewModel.getWatchedStatusLiveData().observe(getViewLifecycleOwner(), watchedMovie -> {
            if (watchedMovie != null) {
                // Jika data sudah ada (mode edit), isi form
                ratingBar.setRating(watchedMovie.userRating);
                etReview.setText(watchedMovie.userReview);
            }
        });
    }

    /**
     * Menyiapkan listener untuk tombol Simpan dan Batal.
     */
    private void setupListeners() {
        btnCancel.setOnClickListener(v -> dismiss()); // Menutup dialog

        btnSave.setOnClickListener(v -> saveReview());
    }

    /**
     * Logika utama untuk menyimpan review.
     */
    private void saveReview() {
        // Ambil data detail film saat ini dari ViewModel
        MovieDetailResponse movieDetails = detailViewModel.getMovieDetailsLiveData().getValue();
        if (movieDetails == null) {
            Toast.makeText(getContext(), "Detail film belum termuat, coba lagi.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil data dari inputan pengguna
        float rating = ratingBar.getRating();
        String review = etReview.getText().toString().trim();
        long watchDate = new Date().getTime(); // Simpan tanggal saat ini sebagai timestamp

        // Menggabungkan list genre menjadi satu String (misal: "Action, Adventure, Sci-Fi")
        String genresString = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N && movieDetails.getGenres() != null) {
            genresString = movieDetails.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
        }

        // Buat objek WatchedMovie baru untuk disimpan ke database
        WatchedMovie watchedMovie = new WatchedMovie();
        watchedMovie.movieId = movieDetails.getId();
        watchedMovie.title = movieDetails.getTitle();
        watchedMovie.posterPath = movieDetails.getPosterPath();
        watchedMovie.watchedDate = watchDate;
        watchedMovie.userRating = rating;
        watchedMovie.userReview = review;
        watchedMovie.runtime = movieDetails.getRuntime();
        watchedMovie.genres = genresString; // Menyimpan genre

        // Panggil metode di ViewModel untuk menyimpan data ke database
        detailViewModel.saveWatchedMovie(watchedMovie);

        Toast.makeText(getContext(), "Catatan tontonan disimpan!", Toast.LENGTH_SHORT).show();

        // Tutup dialog setelah menyimpan
        dismiss();
    }
}
