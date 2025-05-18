package com.example.safevisit.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.safevisit.R;
import com.example.safevisit.data.AppDatabase;
import com.example.safevisit.data.entities.Reservation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class CreateReservationFragment extends Fragment {

    private TextInputEditText dateEditText, timeEditText, peopleEditText;
    private int userId;
    private int restaurantId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_reservation, container, false);

        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
            restaurantId = getArguments().getInt("restaurantId", -1);
        }

        dateEditText = view.findViewById(R.id.dateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        peopleEditText = view.findViewById(R.id.peopleEditText);
        MaterialButton submitButton = view.findViewById(R.id.submitReservationBtn);

        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());

        // Date picker
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Time picker
        timeEditText.setOnClickListener(v -> showTimePicker());

        // Number picker
        peopleEditText.setOnClickListener(v -> showPeoplePicker());

        // Submit reservation
        submitButton.setOnClickListener(v -> {
            String date = Objects.requireNonNull(dateEditText.getText()).toString().trim();
            String time = Objects.requireNonNull(timeEditText.getText()).toString().trim();
            String peopleStr = Objects.requireNonNull(peopleEditText.getText()).toString().trim();

            if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(peopleStr)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int people = Integer.parseInt(peopleStr);
            String qrCode = "QR-" + System.currentTimeMillis();

            Reservation reservation = new Reservation();
            reservation.userId = userId;
            reservation.restaurantId = restaurantId;
            reservation.date = date;
            reservation.time = time;
            reservation.peopleCount = people;
            reservation.qrCode = qrCode;

            new Thread(() -> {
                db.reservationDao().insert(reservation);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Reservation saved!", Toast.LENGTH_SHORT).show();

                    // Show QR code screen
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, ShowQrFragment.newInstance(qrCode))
                            .addToBackStack(null)
                            .commit();
                });
            }).start();
        });

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    @SuppressLint("DefaultLocale")
                    String dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    dateEditText.setText(dateStr);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    @SuppressLint("DefaultLocale")
                    String timeStr = String.format("%02d:%02d", hourOfDay, minute);
                    timeEditText.setText(timeStr);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void showPeoplePicker() {
        NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(20);
        numberPicker.setWrapSelectorWheel(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select number of people");
        builder.setView(numberPicker);
        builder.setPositiveButton("OK", (dialog, which) -> {
            int selected = numberPicker.getValue();
            peopleEditText.setText(String.valueOf(selected));
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}

