package com.example.safevisit.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safevisit.R;
import com.example.safevisit.api.RetrofitClient;
import com.example.safevisit.api.WeatherService;
import com.example.safevisit.api.response.WeatherResponse;
import com.example.safevisit.data.entities.Restaurant;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailFragment extends Fragment implements OnMapReadyCallback {

    private double latitude = 0;
    private double longitude = 0;
    private String name = "";
    private String description = "";

    private TextView weatherText;

    public static RestaurantDetailFragment newInstance(Restaurant restaurant) {
        RestaurantDetailFragment fragment = new RestaurantDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", restaurant.name);
        args.putString("description", restaurant.description);
        args.putDouble("latitude", restaurant.latitude);
        args.putDouble("longitude", restaurant.longitude);
        args.putInt("restaurantId", restaurant.id); // Make sure Restaurant has a public 'id' field
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        if (getArguments() != null) {
            name = getArguments().getString("name", "");
            description = getArguments().getString("description", "");
            latitude = getArguments().getDouble("latitude", 0);
            longitude = getArguments().getDouble("longitude", 0);
        }

        TextView nameText = view.findViewById(R.id.restaurantNameText);
        TextView descText = view.findViewById(R.id.restaurantDescriptionText);
        weatherText = view.findViewById(R.id.weatherText);
        Button reserveButton = view.findViewById(R.id.reserveButton);

        nameText.setText(name);
        descText.setText(description);

        reserveButton.setOnClickListener(v -> {
            int userId = requireActivity().getIntent().getIntExtra("userId", -1);
            assert getArguments() != null;
            int restaurantId = getArguments().getInt("restaurantId", -1);

            if (userId == -1 || restaurantId == -1) {
                Toast.makeText(getContext(), "Invalid user or restaurant", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateReservationFragment reservationFragment = new CreateReservationFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            bundle.putInt("restaurantId", restaurantId);
            reservationFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, reservationFragment)
                    .addToBackStack(null)
                    .commit();
        });


        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.mapFrame, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        fetchWeather();

        return view;
    }

    private void fetchWeather() {
        WeatherService service = RetrofitClient.getInstance().create(WeatherService.class);
        service.getCurrentWeather(latitude, longitude, "581144cd723de4b1455b0a304fa4be2d", "metric")
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherResponse data = response.body();
                            String text = "Temp: " + data.main.temp + "°C\n" +
                                    "Humidity: " + data.main.humidity + "%\n" +
                                    data.weather.get(0).description;
                            weatherText.setText(text);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                        weatherText.setText("Weather data unavailable");
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng loc = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(loc).title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));
    }
}
