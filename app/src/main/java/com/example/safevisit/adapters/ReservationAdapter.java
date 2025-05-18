package com.example.safevisit.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safevisit.R;
import com.example.safevisit.data.entities.Reservation;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;
import java.util.Map;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private final Context context;
    private final List<Reservation> reservationList;
    private final Map<Integer, String> restaurantNameMap;

    public ReservationAdapter(Context context, List<Reservation> reservationList, Map<Integer, String> restaurantNameMap) {
        this.context = context;
        this.reservationList = reservationList;
        this.restaurantNameMap = restaurantNameMap;
    }

    @NonNull
    @Override
    public ReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        String restaurantName = restaurantNameMap.get(reservation.restaurantId);
        holder.restaurant.setText(restaurantName != null ? restaurantName : "Restaurant");
        holder.date.setText("Date: " + reservation.date);
        holder.time.setText("Time: " + reservation.time);
        holder.people.setText("People: " + reservation.peopleCount);

        holder.itemView.setOnClickListener(v -> showQRCodeDialog(reservation.qrCode));
    }

    private void showQRCodeDialog(String qrCodeText) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(qrCodeText, BarcodeFormat.QR_CODE, 500, 500);

            ImageView qrView = new ImageView(context);
            qrView.setImageBitmap(bitmap);

            new AlertDialog.Builder(context)
                    .setTitle("Reservation QR Code")
                    .setView(qrView)
                    .setPositiveButton("Close", null)
                    .show();

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurant, date, time, people;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurant = itemView.findViewById(R.id.reservationRestaurant);
            date = itemView.findViewById(R.id.reservationDate);
            time = itemView.findViewById(R.id.reservationTime);
            people = itemView.findViewById(R.id.reservationPeople);
        }
    }
}
