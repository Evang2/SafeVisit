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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationListFragment extends Fragment {

    private RecyclerView upcomingRecyclerView, pastRecyclerView;
    private ReservationAdapter upcomingAdapter, pastAdapter;
    private final int userId = 1; // Replace with actual logged-in user

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

        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            List<Reservation> upcoming = db.reservationDao().getUpcomingReservations(userId, today);
            List<Reservation> past = db.reservationDao().getPastReservations(userId, today);

            requireActivity().runOnUiThread(() -> {
                upcomingAdapter = new ReservationAdapter(upcoming);
                pastAdapter = new ReservationAdapter(past);

                upcomingRecyclerView.setAdapter(upcomingAdapter);
                pastRecyclerView.setAdapter(pastAdapter);
            });
        }).start();

        return view;
    }
}
