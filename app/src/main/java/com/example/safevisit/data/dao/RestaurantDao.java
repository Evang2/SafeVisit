package com.example.safevisit.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.safevisit.data.entities.Restaurant;

import java.util.List;

@Dao
public interface RestaurantDao {

    @Insert
    void insert(Restaurant restaurant);

    @Query("SELECT * FROM restaurants")
    List<Restaurant> getAll();

    @Query("SELECT * FROM restaurants WHERE name LIKE '%' || :query || '%'")
    List<Restaurant> search(String query);
}