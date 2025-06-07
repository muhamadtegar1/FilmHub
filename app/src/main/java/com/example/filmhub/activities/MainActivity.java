package com.example.filmhub.activities;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.filmhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Dapatkan NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment); // Pastikan ID ini sama dengan ID FragmentContainerView

        // 2. Dapatkan NavController dari NavHostFragment
        NavController navController = navHostFragment.getNavController();

        // 3. Dapatkan instance BottomNavigationView dari layout
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation); // Pastikan ID ini sama dengan ID BottomNavigationView

        // 4. Setup BottomNavigationView dengan NavController
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}