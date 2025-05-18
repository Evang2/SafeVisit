package com.example.safevisit.data.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "reservations",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Restaurant.class, parentColumns = "id", childColumns = "restaurantId", onDelete = CASCADE)
        },
        indices = {@Index("userId"), @Index("restaurantId")}
)
public class Reservation {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int restaurantId;
    public String date;
    public String time;
    public int peopleCount;
    public String qrCode;
}
