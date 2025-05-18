package com.example.safevisit.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safevisit.R;
import com.example.safevisit.adapters.RestaurantAdapter;
import com.example.safevisit.data.AppDatabase;
import com.example.safevisit.data.entities.Restaurant;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.restaurantRecyclerView);
        TextInputEditText searchEditText = view.findViewById(R.id.searchEditText);

        // Make sure it doesn’t auto focus
        searchEditText.clearFocus();
        searchEditText.setFocusable(false);

        // When user taps it, allow focus and show keyboard
        searchEditText.setOnClickListener(v -> {
            searchEditText.setFocusableInTouchMode(true);
            searchEditText.setFocusable(true);
            searchEditText.requestFocus();

            // Show keyboard manually
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RestaurantAdapter(getContext(), new ArrayList<>(), restaurant -> {
            // Navigate to detail fragment on restaurant click
            RestaurantDetailFragment detailFragment = RestaurantDetailFragment.newInstance(restaurant);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        recyclerView.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());

        // Load or insert sample restaurants
        new Thread(() -> {
            if (db.restaurantDao().getAll().isEmpty()) {
                db.restaurantDao().insert(new Restaurant("Athens Bites", "Modern Greek dining", 37.9838, 23.7275));
                db.restaurantDao().insert(new Restaurant("Sunset Grill", "Great food with a view", 37.9758, 23.7345));
                db.restaurantDao().insert(new Restaurant("Urban Souvlaki", "Street-style souvlaki joint", 37.9845, 23.7289));
                db.restaurantDao().insert(new Restaurant("Cafe Agora", "Relaxed cafe with pastries", 37.9821, 23.7300));
                db.restaurantDao().insert(new Restaurant("Green Garden", "Vegan delights in nature", 37.9870, 23.7261));
            }

            restaurantList = db.restaurantDao().getAll();

            requireActivity().runOnUiThread(() -> adapter.updateList(restaurantList));
        }).start();

        // Search filter
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }
}
