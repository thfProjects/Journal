package com.thf.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class EditTagsViewModel extends AndroidViewModel {

    LiveData<List<JournalEntryTag>> tags;

    private AppDatabase appDatabase;
    private TaskRunner taskRunner;

    public EditTagsViewModel(@NonNull Application application) {
        super(application);
        appDatabase = DatabaseClient.getInstance(application).getDatabase();
        taskRunner = new TaskRunner();
        tags = appDatabase.journalEntryTagDAO().getAllWithoutDefault();
    }

    public LiveData<List<JournalEntryTag>> getAllTags(){
        return tags;
    }

    public void add(JournalEntryTag tag){
        taskRunner.executeAsync(()->{appDatabase.journalEntryTagDAO().insert(tag); return null;}, (result)->{});
    }

    public void update(JournalEntryTag tag, String name){
        tag.setName(name);
        taskRunner.executeAsync(()->{appDatabase.journalEntryTagDAO().update(tag); return null;}, (result)->{});
    }

    public void delete(JournalEntryTag tag){
        taskRunner.executeAsync(()->{appDatabase.journalEntryTagDAO().delete(tag); return null;}, (result)->{});
    }

    public void deleteById(int id){
        taskRunner.executeAsync(()->{appDatabase.journalEntryTagDAO().deleteById(id); return null;}, (result)->{});
    }
}
