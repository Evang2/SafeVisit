package com.example.safevisit.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class CreateReservationFragment extends Fragment {

    private TextInputEditText dateEditText, timeEditText, peopleEditText;
    private int userId;
    private int restaurantId;
    private int selectedPeopleCount = 1;

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

        // 🗓️ Material Date Picker
        dateEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select reservation date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                dateEditText.setText(formattedDate);
            });

            datePicker.show(getParentFragmentManager(), "date_picker");
        });

        // 🕒 Material Time Picker
        timeEditText.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTitleText("Select reservation time")
                    .setHour(12)
                    .setMinute(0)
                    .build();

            picker.addOnPositiveButtonClickListener(dialog -> {
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", picker.getHour(), picker.getMinute());
                timeEditText.setText(formattedTime);
            });

            picker.show(getParentFragmentManager(), "time_picker");
        });

        // 👥 People Picker using Material AlertDialog
        peopleEditText.setOnClickListener(v -> {
            View pickerView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_people_picker, null);
            NumberPicker numberPicker = pickerView.findViewById(R.id.numberPicker);
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(20);
            numberPicker.setValue(selectedPeopleCount);

            new AlertDialog.Builder(requireContext(), R.style.NumberPickerStyle)
                    .setTitle("Select number of people")
                    .setView(pickerView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        selectedPeopleCount = numberPicker.getValue();
                        peopleEditText.setText(String.valueOf(selectedPeopleCount));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        // 📅 Submit reservation
        submitButton.setOnClickListener(v -> {
            String date = Objects.requireNonNull(dateEditText.getText()).toString().trim();
            String time = Objects.requireNonNull(timeEditText.getText()).toString().trim();
            String peopleStr = Objects.requireNonNull(peopleEditText.getText()).toString().trim();

            if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(peopleStr)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int people = Integer.parseInt(peopleStr);
            String qrCodeText = "QR-" + System.currentTimeMillis();

            Reservation reservation = new Reservation();
            reservation.userId = userId;
            reservation.restaurantId = restaurantId;
            reservation.date = date;
            reservation.time = time;
            reservation.peopleCount = people;
            reservation.qrCode = qrCodeText;

            new Thread(() -> {
                db.reservationDao().insert(reservation);

                requireActivity().runOnUiThread(() -> {
                    showQRCodeDialog(qrCodeText);
                    Toast.makeText(getContext(), "Reservation saved!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }).start();
        });

        return view;
    }

    private void showQRCodeDialog(String qrCodeText) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(qrCodeText, BarcodeFormat.QR_CODE, 600, 600);

            ImageView qrImage = new ImageView(requireContext());
            qrImage.setImageBitmap(bitmap);
            int padding = (int) getResources().getDimension(R.dimen.padding_large);
            qrImage.setPadding(padding, padding, padding, padding);

            new MaterialAlertDialogBuilder(requireContext(), R.style.RoundedQRDialog)
                    .setTitle("Your Reservation QR Code")
                    .setView(qrImage)
                    .setPositiveButton("Close", null)
                    .show();

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
