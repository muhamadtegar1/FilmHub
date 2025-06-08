package com.example.filmhub.database.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.filmhub.database.entities.FavoriteMovie;
import com.example.filmhub.database.entities.WatchedMovie;
import com.example.filmhub.database.dao.FavoriteMovieDao;
import com.example.filmhub.database.dao.WatchedMovieDao;

/**
 * Ini adalah kelas utama database Room untuk aplikasi.
 * Kelas ini harus abstrak dan meng-extend RoomDatabase.
 * Bertindak sebagai "cetak biru" yang mengikat semua komponen database (Entities, DAOs).
 */
// Anotasi @Database untuk menandai kelas ini sebagai database Room.
// 'entities' berisi daftar semua kelas Entity (tabel) yang akan digunakan.
// 'version' adalah versi database, harus dinaikkan setiap kali ada perubahan skema.
@Database(entities = {FavoriteMovie.class, WatchedMovie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Metode abstrak untuk mendapatkan instance dari setiap DAO.
    // Room akan otomatis meng-generate implementasi untuk metode ini.
    public abstract FavoriteMovieDao favoriteMovieDao();
    public abstract WatchedMovieDao watchedMovieDao();

    // 'volatile' memastikan variabel ini selalu up-to-date di semua thread.
    private static volatile AppDatabase INSTANCE;

    /**
     * Metode ini menggunakan Singleton Pattern.
     * Tujuannya adalah untuk memastikan hanya ada SATU instance (satu koneksi)
     * ke database yang terbuka di seluruh aplikasi pada satu waktu. Ini lebih efisien.
     * @param context Context aplikasi.
     * @return Instance tunggal dari AppDatabase.
     */
    public static AppDatabase getInstance(final Context context) {
        // Jika instance belum ada, maka kita buat.
        if (INSTANCE == null) {
            // 'synchronized' memastikan bahwa proses pembuatan instance
            // tidak akan dijalankan oleh dua thread secara bersamaan.
            synchronized (AppDatabase.class) {
                // Pengecekan ulang di dalam blok synchronized untuk keamanan thread.
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "movie_journal_database") // "movie_journal_database" adalah nama file database di perangkat.
                            .build();
                }
            }
        }
        // Jika instance sudah ada, langsung kembalikan.
        return INSTANCE;
    }
}
