package com.thf.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EditJournalEntryViewModel extends AndroidViewModel {

    LiveData<List<JournalEntryTag>> tags;

    private AppDatabase appDatabase;
    private TaskRunner taskRunner;

    public EditJournalEntryViewModel(@NonNull Application application) {
        super(application);
        appDatabase = DatabaseClient.getInstance(application).getDatabase();
        taskRunner = new TaskRunner();
        tags = appDatabase.journalEntryTagDAO().getAll();
    }

    public LiveData<List<JournalEntryTag>> getAllTags(){
        return tags;
    }

    public void upsert(JournalEntry journalEntry){
        taskRunner.executeAsync(()->{appDatabase.journalEntryDAO().upsert(journalEntry); return null;},(result)->{});
    }
}
