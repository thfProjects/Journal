package com.thf.journal;

import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class JournalEntryDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(JournalEntry journalEntry);

    @Delete
    abstract void delete(JournalEntry journalEntry);

    @Update
    abstract void update(JournalEntry journalEntry);

    @Transaction
    @Query("SELECT * FROM journalentry")
    abstract LiveData<List<JournalEntryWithTag>> getAll();

    @Transaction
    @Query("SELECT * FROM journalentry WHERE text LIKE :query AND tag_id IN (:tags) AND archived = :archived")
    abstract LiveData<List<JournalEntryWithTag>> filter(String query, List<Integer> tags, boolean archived);

    @Transaction
    @Query("SELECT * FROM journalentry WHERE text LIKE :query AND archived = :archived")
    abstract LiveData<List<JournalEntryWithTag>> filter(String query, boolean archived);

    @Transaction
    void upsert(JournalEntry journalEntry){
        long id = insert(journalEntry);
        if(id == -1){
            update(journalEntry);
        }
    }
}
