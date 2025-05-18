package com.example.safevisit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safevisit.R;
import com.example.safevisit.adapters.ReservationAdapter;
import com.example.safevisit.data.AppDatabase;
import com.example.safevisit.data.entities.Reservation;
import com.example.safevisit.data.entities.Restaurant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReservationListFragment extends Fragment {

    private RecyclerView upcomingRecyclerView, pastRecyclerView;
    private ReservationAdapter upcomingAdapter, pastAdapter;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservations, container, false);

        upcomingRecyclerView = view.findViewById(R.id.upcomingRecyclerView);
        pastRecyclerView = view.findViewById(R.id.pastRecyclerView);

        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        }

        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());

        // ✅ Get current datetime: yyyy-MM-dd HH:mm
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            // ✅ Use full datetime filtering in DAO
            List<Reservation> upcoming = db.reservationDao().getUpcomingReservations(userId, now);
            List<Reservation> past = db.reservationDao().getPastReservations(userId, now);
            List<Restaurant> allRestaurants = db.restaurantDao().getAll();

            Map<Integer, String> restaurantNames = new HashMap<>();
            for (Restaurant r : allRestaurants) {
                restaurantNames.put(r.id, r.name);
            }

            requireActivity().runOnUiThread(() -> {
                upcomingAdapter = new ReservationAdapter(getContext(), upcoming, restaurantNames);
                pastAdapter = new ReservationAdapter(getContext(), past, restaurantNames);

                upcomingRecyclerView.setAdapter(upcomingAdapter);
                pastRecyclerView.setAdapter(pastAdapter);
            });
        }).start();

        return view;
    }
}
