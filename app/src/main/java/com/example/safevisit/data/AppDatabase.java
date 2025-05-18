package com.example.safevisit.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.safevisit.data.dao.UserDao;
import com.example.safevisit.data.dao.RestaurantDao;
import com.example.safevisit.data.dao.ReservationDao;
import com.example.safevisit.data.entities.User;
import com.example.safevisit.data.entities.Restaurant;
import com.example.safevisit.data.entities.Reservation;
@SuppressWarnings("deprecation")
@Database(entities = {User.class, Restaurant.class, Reservation.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract RestaurantDao restaurantDao();
    public abstract ReservationDao reservationDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "safevisit_db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
