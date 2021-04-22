package com.thf.journal;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.preference.PreferenceManager;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends AndroidViewModel {

    private SharedPreferences sharedPreferences;

    private LiveData<List<JournalEntryWithTag>> entries;
    private LiveData<List<JournalEntryTag>> tags;

    private MutableLiveData<Filter> filter;

    private AppDatabase appDatabase;
    private TaskRunner taskRunner;

    private boolean isListLayout;

    public MainViewModel(@NonNull Application application) {
        super(application);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);

        appDatabase = DatabaseClient.getInstance(application).getDatabase();
        taskRunner = new TaskRunner();

        filter = new MutableLiveData<>(new Filter());

        entries = Transformations.switchMap(filter, (value)->{
            List<Object> args = new ArrayList<>();

            StringBuilder query = new StringBuilder("SELECT * FROM journalentry WHERE archived = ?");
            args.add(value.archived);

            if(!value.query.isEmpty()){
                query.append(" AND text LIKE ?");
                args.add("%" + value.query + "%");
            }
            if(!value.tags.isEmpty()) {
                query.append(" AND tag_id IN (");
                for(int i = 0; i < value.tags.size() - 1; i++){
                    query.append("?, ");
                    args.add(value.tags.get(i));
                }
                query.append("?)");
                args.add(value.tags.get(value.tags.size()-1));
            }
            if(value.dateAddedFrom != null){
                query.append(" AND dateAdded >= ?");
                args.add(value.dateAddedFrom);
            }
            if(value.dateAddedTo != null){
                query.append(" AND dateAdded <= ?");
                args.add(value.dateAddedTo);
            }

            return appDatabase.journalEntryDAO().filter(new SimpleSQLiteQuery(query.toString(), args.toArray()));
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
        filter.getValue().setQuery(query);
        updateFilter();
    }

    public void addTagFilter(int id){
        filter.getValue().addTag(id);
        updateFilter();
    }

    public void removeTagFilter(int id){
        filter.getValue().removeTag(id);
        updateFilter();
    }

    public boolean hasTagFilter(int id){
        return filter.getValue().hasTag(id);
    }

    public void setArchivedFilter(boolean archived){
        filter.getValue().setArchived(archived);
        updateFilter();
    }

    public boolean getArchivedFilter(){
        return filter.getValue().archived;
    }

    public void setDateFilter(Long addedFrom, Long addedto){
        filter.getValue().setDate(addedFrom, addedto);
        updateFilter();
    }

    public Bundle getDateFilter(){
        Bundle dateFilter = new Bundle();
        if(filter.getValue().dateAddedFrom != null) dateFilter.putLong("addedFrom", filter.getValue().dateAddedFrom);
        if(filter.getValue().dateAddedTo != null) dateFilter.putLong("addedTo" ,filter.getValue().dateAddedTo);
        return dateFilter;
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

    private void updateFilter(){
        filter.setValue(filter.getValue());
    }

    public class Filter{
        private String query;
        private List<Integer> tags;
        private Boolean archived;
        private Long dateAddedFrom;
        private Long dateAddedTo;

        public Filter(){
            this.query = "";
            this.tags = new ArrayList<>();
            this.archived = false;
            this.dateAddedFrom = null;
            this.dateAddedTo = null;
        }

        public void setQuery(String query){
            this.query = query;
        }

        public void addTag(int tag){
            tags.add(tag);
        }

        public void removeTag(int tag){
            tags.remove(tag);
        }

        public boolean hasTag(int tag){
            return tags.contains(tag);
        }

        public void setArchived(boolean archived){
            this.archived = archived;
        }

        public void setDate(Long dateAddedFrom, Long dateAddedTo){
            this.dateAddedFrom = dateAddedFrom;
            this.dateAddedTo = dateAddedTo;
        }
    }
}
