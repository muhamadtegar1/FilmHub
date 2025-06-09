package com.example.filmhub;

import android.app.Application;
import com.example.filmhub.utils.ThemeManager; // Import helper kita

/**
 * Kelas Application kustom.
 * Dijalankan sekali saat aplikasi pertama kali dimulai.
 * Tempat yang tepat untuk inisialisasi awal seperti menerapkan tema.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Saat aplikasi pertama kali dibuka, baca preferensi tema yang tersimpan
        String currentTheme = ThemeManager.getTheme(this);

        // Terapkan tema tersebut agar konsisten dengan sesi sebelumnya
        ThemeManager.applyTheme(currentTheme);
    }
}
