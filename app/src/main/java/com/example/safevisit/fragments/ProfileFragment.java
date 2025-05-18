package com.example.safevisit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safevisit.R;
import com.example.safevisit.activities.LoginActivity;
import com.example.safevisit.activities.QRScannerActivity;
import com.example.safevisit.data.AppDatabase;
import com.example.safevisit.data.entities.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextInputEditText usernameEdit, emailEdit, passwordEdit;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameEdit = view.findViewById(R.id.usernameEdit);
        emailEdit = view.findViewById(R.id.emailEdit);
        passwordEdit = view.findViewById(R.id.passwordEdit);
        MaterialButton updateProfileBtn = view.findViewById(R.id.updateProfileBtn);
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        MaterialButton qrScannerButton = view.findViewById(R.id.qrScannerButton);

        if (getActivity() != null && getActivity().getIntent() != null) {
            userId = getActivity().getIntent().getIntExtra("userId", -1);
        }

        if (userId == -1) {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());

        new Thread(() -> {
            User user = db.userDao().getUserById(userId);
            if (user != null) {
                requireActivity().runOnUiThread(() -> {
                    usernameEdit.setText(user.username);
                    emailEdit.setText(user.email);
                    passwordEdit.setText(user.password);
                });
            }
        }).start();

        updateProfileBtn.setOnClickListener(v -> {
            String username = Objects.requireNonNull(usernameEdit.getText()).toString().trim();
            String email = Objects.requireNonNull(emailEdit.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordEdit.getText()).toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                User updatedUser = new User();
                updatedUser.id = userId;
                updatedUser.username = username;
                updatedUser.email = email;
                updatedUser.password = password;
                db.userDao().update(updatedUser);

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                );
            }).start();
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        qrScannerButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), QRScannerActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
