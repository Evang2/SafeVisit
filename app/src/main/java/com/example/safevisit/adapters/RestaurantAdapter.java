package com.example.safevisit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safevisit.R;
import com.example.safevisit.data.entities.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final List<Restaurant> fullList;
    private final List<Restaurant> filteredList;
    private final LayoutInflater inflater;
    private final OnRestaurantClickListener listener;

    // Interface for click callback
    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    // Constructor with listener
    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, OnRestaurantClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.fullList = new ArrayList<>(restaurantList);
        this.filteredList = new ArrayList<>(restaurantList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = filteredList.get(position);
        holder.nameText.setText(restaurant.name);
        holder.descriptionText.setText(restaurant.description);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestaurantClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (Restaurant r : fullList) {
                if (r.name.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(r);
                }
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Restaurant> newList) {
        fullList.clear();
        fullList.addAll(newList);
        filteredList.clear();
        filteredList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.restaurantName);
            descriptionText = itemView.findViewById(R.id.restaurantDescription);
        }
    }
}
