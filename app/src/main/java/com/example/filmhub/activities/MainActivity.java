package com.example.filmhub.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // <-- Import Toolbar
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.filmhub.R; // Sesuaikan
import com.example.filmhub.utils.ThemeManager; // Sesuaikan
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Bagian dari Kode Lama (Setup Navigasi) ---
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // --- TAMBAHAN: Setup Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // --- Bagian dari Kode Revisi (Setup Menu & Tema) ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem themeToggle = menu.findItem(R.id.action_theme_toggle);
        if (themeToggle != null) {
            String currentTheme = ThemeManager.getTheme(this);
            if (currentTheme.equals(ThemeManager.THEME_DARK)) {
                themeToggle.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_light_mode));
            } else {
                themeToggle.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_dark_mode));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_theme_toggle) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        String currentTheme = ThemeManager.getTheme(this);
        String nextTheme = currentTheme.equals(ThemeManager.THEME_DARK) ? ThemeManager.THEME_LIGHT : ThemeManager.THEME_DARK;

        ThemeManager.applyTheme(nextTheme);

        ThemeManager.setTheme(this, nextTheme);

        recreate();
    }
}