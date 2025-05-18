package com.example.safevisit.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safevisit.R;
import com.example.safevisit.api.RetrofitClient;
import com.example.safevisit.api.WeatherService;
import com.example.safevisit.api.response.WeatherResponse;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationDetailFragment extends Fragment implements OnMapReadyCallback {

    private final double latitude = 37.9838;
    private final double longitude = 23.7275;

    private TextView weatherDataTextView;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_detail, container, false);

        // Map setup
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        // Init views
        weatherDataTextView = view.findViewById(R.id.weatherDataTextView);
        TextView reservationDate = view.findViewById(R.id.reservationDate);
        TextView reservationTime = view.findViewById(R.id.reservationTime);
        TextView reservationPeople = view.findViewById(R.id.reservationPeople);

        // Set reservation info
        String date = "2025-06-01";
        reservationDate.setText("Date: " + date);
        String time = "19:00";
        reservationTime.setText("Time: " + time);
        int people = 2;
        reservationPeople.setText("People: " + people);

        // Fetch weather
        WeatherService weatherService = RetrofitClient.getInstance().create(WeatherService.class);
        weatherService.getCurrentWeather(latitude, longitude, "581144cd723de4b1455b0a304fa4be2d", "metric")
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherResponse data = response.body();
                            String weather = "Temp: " + data.main.temp + "°C\n" +
                                    "Humidity: " + data.main.humidity + "%\n" +
                                    data.weather.get(0).description;
                            weatherDataTextView.setText(weather);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                        weatherDataTextView.setText("Weather info unavailable");
                    }
                });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("Restaurant Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }
}
