package com.thf.journal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JournalEntryTagDAO {

    @Insert
    void insert(JournalEntryTag tag);

    @Delete
    void delete(JournalEntryTag tag);

    @Update
    void update(JournalEntryTag tag);

    @Query("SELECT * FROM journalentrytag")
    LiveData<List<JournalEntryTag>> getAll();

    @Query("SELECT name FROM journalentrytag")
    List<String> getAllNames();
}
