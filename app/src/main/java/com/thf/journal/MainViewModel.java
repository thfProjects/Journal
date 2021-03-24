package com.thf.journal;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private SharedPreferences sharedPreferences;

    private LiveData<List<JournalEntryWithTag>> entries;
    private LiveData<List<JournalEntryTag>> tags;

    private MutableLiveData<String> queryFilter;

    private MutableLiveData<ArrayList<Integer>> tagFilters;

    private MutableLiveData<Boolean> archivedFilter;

    private AppDatabase appDatabase;
    private TaskRunner taskRunner;

    private boolean isListLayout;

    public MainViewModel(@NonNull Application application) {
        super(application);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);

        appDatabase = DatabaseClient.getInstance(application).getDatabase();
        taskRunner = new TaskRunner();

        queryFilter = new MutableLiveData<>();

        tagFilters = new MutableLiveData<>(new ArrayList<>());

        archivedFilter = new MutableLiveData<>(false);

        MediatorLiveData<Filter> filter = new MediatorLiveData<>();
        filter.addSource(queryFilter, value -> {filter.setValue(new Filter(value, tagFilters.getValue(), archivedFilter.getValue()));});
        filter.addSource(tagFilters, value -> {filter.setValue(new Filter(queryFilter.getValue(), value, archivedFilter.getValue()));});
        filter.addSource(archivedFilter, value -> {filter.setValue(new Filter(queryFilter.getValue(), tagFilters.getValue(), value));});

        entries = Transformations.switchMap(filter, (value)->{
            if(!value.tags.isEmpty())
                return appDatabase.journalEntryDAO().filter("%" + value.query + "%", value.tags, value.archived);
            else
                return appDatabase.journalEntryDAO().filter("%" + value.query + "%", value.archived);
        });

        tags = appDatabase.journalEntryTagDAO().getAll();

        isListLayout = sharedPreferences.getBoolean("islistlayout", true);
    }

    public void delete(JournalEntry journalEntry){
        taskRunner.executeAsync(()->{appDatabase.journalEntryDAO().delete(journalEntry); return null;}, (result)->{});
    }

    public void deleteById(int id){
        taskRunner.executeAsync(()->{appDatabase.journalEntryDAO().deleteById(id); return null;}, (result)->{});
    }

    public LiveData<List<JournalEntryWithTag>> getAllEntries(){
        return entries;
    }

    public LiveData<List<JournalEntryTag>> getAllTags(){
        return tags;
    }

    public boolean getIsListLayout(){
        return isListLayout;
    }

    public void setIsListLayout(boolean isListLayout){
        this.isListLayout = isListLayout;
    }

    public void setQueryFilter(String query){
        this.queryFilter.setValue(query);
    }

    public void addTagFilter(int id){
        tagFilters.getValue().add(id);
        tagFilters.setValue(tagFilters.getValue());
    }

    public void removeTagFilter(int id){
        tagFilters.getValue().remove((Integer) id);
        tagFilters.setValue(tagFilters.getValue());
    }

    public boolean hasTagFilter(int id){
        return tagFilters.getValue().contains(id);
    }

    public void setArchivedFilter(boolean archived){
        this.archivedFilter.setValue(archived);
    }

    public boolean getArchivedFilter(){
        return archivedFilter.getValue();
    }

    public void saveIsListLayout(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("islistlayout", isListLayout);
        editor.commit();
    }

    public void archiveJournalEntry(JournalEntry journalEntry){
        journalEntry.setArchived(true);
        taskRunner.executeAsync(()->{appDatabase.journalEntryDAO().update(journalEntry); return null;}, (result)->{});
    }

    public void unarchiveJournalEntry(JournalEntry journalEntry){
        journalEntry.setArchived(false);
        taskRunner.executeAsync(()->{appDatabase.journalEntryDAO().update(journalEntry); return null;},(result)->{});
    }

    public class Filter{
        String query;
        List<Integer> tags;
        Boolean archived;

        public Filter(String query, List<Integer> tags, Boolean archived){
            this.query = query;
            this.tags = tags;
            this.archived = archived;
        }
    }
}
