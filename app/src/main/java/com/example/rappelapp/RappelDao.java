package com.example.rappelapp;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RappelDao {
    @Insert
    long insert(Rappel rappel);

    @Update
    void update(Rappel rappel);

    @Delete
    void delete(Rappel rappel);

    @Query("SELECT * FROM rappels")
    List<Rappel> getAllRappels();

    @Query("DELETE FROM rappels")
    void deleteAll();

    @Insert
    void insertAll(List<Rappel> restoredRappels);
}