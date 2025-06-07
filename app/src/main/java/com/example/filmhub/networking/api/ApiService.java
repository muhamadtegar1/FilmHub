package com.example.filmhub.networking.api;

import com.example.filmhub.data.model.GenreResponse;
import com.example.filmhub.data.model.MovieDetailResponse;
import com.example.filmhub.data.model.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint untuk discover, bisa untuk populer, filter genre, dan sortir
    @GET("discover/movie")
    Call<MovieResponse> getDiscoverMovies(
            @Query("api_key") String apiKey,
            @Query("sort_by") String sortBy,
            @Query("with_genres") String genreIds, // Kirim ID genre, bisa dipisah koma
            @Query("page") int page
    );

    // Endpoint untuk pencarian film
    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page
    );

    // Endpoint untuk mendapatkan daftar semua genre
    @GET("genre/movie/list")
    Call<GenreResponse> getGenres(
            @Query("api_key") String apiKey
    );

    // Endpoint untuk mendapatkan detail lengkap satu film
    @GET("movie/{movie_id}")
    Call<MovieDetailResponse> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );
}
