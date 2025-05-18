package com.example.safevisit.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.safevisit.R;
import com.example.safevisit.fragments.HomeFragment;
import com.example.safevisit.fragments.ProfileFragment;
import com.example.safevisit.fragments.ReservationListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // ✅ Get the logged-in userId from LoginActivity
        userId = getIntent().getIntExtra("userId", -1);

        // ✅ Load HomeFragment with userId by default
        HomeFragment homeFragment = new HomeFragment();
        Bundle homeBundle = new Bundle();
        homeBundle.putInt("userId", userId);
        homeFragment.setArguments(homeBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);

            int id = item.getItemId();

            if (id == R.id.homeFragment) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.searchFragment) {
                selectedFragment = new ReservationListFragment();
            } else if (id == R.id.profileFragment) {
                selectedFragment = new ProfileFragment();
            } else {
                return false;
            }

            selectedFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });
    }
}
