package com.example.safevisit.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safevisit.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ShowQrFragment extends Fragment {

    private static final String ARG_QR_DATA = "qrData";

    public static ShowQrFragment newInstance(String qrData) {
        ShowQrFragment fragment = new ShowQrFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QR_DATA, qrData);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_qr, container, false);

        ImageView qrImageView = view.findViewById(R.id.qrImageView);
        TextView qrTextView = view.findViewById(R.id.qrTextTextView);

        String qrData = getArguments() != null ? getArguments().getString(ARG_QR_DATA) : "No Data";

        qrTextView.setText(qrData);

        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);
            qrImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
