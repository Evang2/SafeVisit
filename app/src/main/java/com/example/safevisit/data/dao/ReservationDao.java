package com.example.safevisit.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.safevisit.data.entities.Reservation;

import java.util.List;

@Dao
public interface ReservationDao {

    @Insert
    void insert(Reservation reservation);

    @Delete
    void delete(Reservation reservation);

    @Query("SELECT * FROM reservations WHERE userId = :userId AND date >= :todayDate ORDER BY date ASC")
    List<Reservation> getUpcomingReservations(int userId, String todayDate);

    @Query("SELECT * FROM reservations WHERE userId = :userId AND date < :todayDate ORDER BY date DESC")
    List<Reservation> getPastReservations(int userId, String todayDate);
}
