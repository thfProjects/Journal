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
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private SharedPreferences sharedPreferences;

    private LiveData<List<JournalEntryWithTag>> entries;
    private LiveData<List<JournalEntryTag>> tags;

    //private MutableLiveData<String> queryFilter;

    //private MutableLiveData<ArrayList<Integer>> tagFilters;

    //private MutableLiveData<Boolean> archivedFilter;

    //private MutableLiveData<Long> dateAddedFromFilter;
    //private MutableLiveData<Long> dateAddedToFilter;

    private MutableLiveData<Filter> filter;

    private AppDatabase appDatabase;
    private TaskRunner taskRunner;

    private boolean isListLayout;

    public MainViewModel(@NonNull Application application) {
        super(application);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);

        appDatabase = DatabaseClient.getInstance(application).getDatabase();
        taskRunner = new TaskRunner();
        /*
        queryFilter = new MutableLiveData<>();

        tagFilters = new MutableLiveData<>(new ArrayList<>());

        archivedFilter = new MutableLiveData<>(false);

        dateAddedFromFilter = new MutableLiveData<>();

        dateAddedToFilter = new MutableLiveData<>();

        MediatorLiveData<Filter> filter = new MediatorLiveData<>();
        filter.addSource(queryFilter, value -> {filter.setValue(new Filter(value, tagFilters.getValue(), archivedFilter.getValue(), dateAddedFromFilter.getValue(), dateAddedToFilter.getValue()));});
        filter.addSource(tagFilters, value -> {filter.setValue(new Filter(queryFilter.getValue(), value, archivedFilter.getValue(), dateAddedFromFilter.getValue(), dateAddedToFilter.getValue()));});
        filter.addSource(archivedFilter, value -> {filter.setValue(new Filter(queryFilter.getValue(), tagFilters.getValue(), value, dateAddedFromFilter.getValue(), dateAddedToFilter.getValue()));});
        filter.addSource(dateAddedFromFilter, value -> {filter.setValue(new Filter(queryFilter.getValue(), tagFilters.getValue(), archivedFilter.getValue() , value, dateAddedToFilter.getValue()));});
        filter.addSource(dateAddedToFilter, value -> {filter.setValue(new Filter(queryFilter.getValue(), tagFilters.getValue(), archivedFilter.getValue() , dateAddedToFilter.getValue(), value));});
        */
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
            if(value.dateAddedFrom != 0){

            }
            if(value.dateAddedTo != Long.MAX_VALUE){

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
        //queryFilter.setValue(query);
        filter.getValue().setQuery(query);
        filter.setValue(filter.getValue());
    }

    public void addTagFilter(int id){
        //tagFilters.getValue().add(id);
        //tagFilters.setValue(tagFilters.getValue());
        filter.getValue().addTag(id);
        filter.setValue(filter.getValue());
    }

    public void removeTagFilter(int id){
        //tagFilters.getValue().remove((Integer) id);
        //tagFilters.setValue(tagFilters.getValue());
        filter.getValue().removeTag(id);
        filter.setValue(filter.getValue());
    }

    public boolean hasTagFilter(int id){
        //return tagFilters.getValue().contains(id);
        return filter.getValue().hasTag(id);
    }

    public void setArchivedFilter(boolean archived){
        //this.archivedFilter.setValue(archived);
        filter.getValue().setArchived(archived);
        filter.setValue(filter.getValue());
    }

    public boolean getArchivedFilter(){
        //return archivedFilter.getValue();
        return filter.getValue().getArchived();
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
        Long dateAddedFrom;
        Long dateAddedTo;


        public Filter(String query, List<Integer> tags, Boolean archived, Long dateAddedFrom, Long dateAddedTo){
            this.query = query;
            this.tags = tags;
            this.archived = archived;
            this.dateAddedFrom = dateAddedFrom;
            this.dateAddedTo = dateAddedTo;
        }

        public Filter(){
            this.query = "";
            this.tags = new ArrayList<>();
            this.archived = false;
            this.dateAddedFrom = 0L;
            this.dateAddedTo = Long.MAX_VALUE;
        }

        /*public Filter updateQuery(String query){
            return new Filter(query, this.tags, this.archived, this.dateAddedFrom, this.dateAddedTo);
        }

        public Filter addTag(int tag){
            tags.add(tag);
            return new Filter(this.query, tags, this.archived, this.dateAddedFrom, this.dateAddedTo);
        }

        public Filter updateArchived(Boolean archived){
            return new Filter(this.query, this.tags, archived, this.dateAddedFrom, this.dateAddedTo);
        }*/

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

        public boolean getArchived(){
            return archived;
        }
    }
}
