package com.example.safevisit.data.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "restaurants")
public class Restaurant {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public double latitude;
    public double longitude;
@Ignore
    // ✅ Add this constructor
    public Restaurant(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Optional: empty constructor (Room may use it)
    public Restaurant() {
    }
}
