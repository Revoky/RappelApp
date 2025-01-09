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
    void insert(Rappel rappel);

    @Update
    void update(Rappel rappel);

    @Delete
    void delete(Rappel rappel);

    @Query("SELECT * FROM rappels")
    List<Rappel> getAllRappels();

    @Query("SELECT * FROM rappels WHERE id = :id LIMIT 1")
    Rappel getRappelById(long id);

    @Query("DELETE FROM rappels")
    void deleteAll();
}
