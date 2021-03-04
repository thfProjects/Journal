package com.thf.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    private MainViewModel mainViewModel;

    private RecyclerView recyclerView;
    private JournalEntryRecyclerAdapter entryAdapter;

    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private Button newEntryButton;

    private SearchView searchView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        drawerLayout = findViewById(R.id.drawerlayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);

        searchView = findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(onQueryTextListener);

        navigationView = findViewById(R.id.navview);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        recyclerView = findViewById(R.id.entries);
        newEntryButton = findViewById(R.id.newentry);

        entryAdapter = new JournalEntryRecyclerAdapter(this);
        entryAdapter.setOnItemClickListener(onItemClickListener);

        recyclerView.setAdapter(entryAdapter);

        linearLayoutManager = new LinearLayoutManager(this);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        if(mainViewModel.getIsListLayout())
            recyclerView.setLayoutManager(linearLayoutManager);
        else
            recyclerView.setLayoutManager(staggeredGridLayoutManager);

        itemTouchHelper.attachToRecyclerView(recyclerView);

        newEntryButton.setOnClickListener(onClickListener);

        mainViewModel.getAllEntries().observe(this, new Observer<List<JournalEntryWithTag>>() {
            @Override
            public void onChanged(List<JournalEntryWithTag> entries) {
                entryAdapter.setEntries(entries);
                entryAdapter.notifyDataSetChanged();
            }
        });

        mainViewModel.getAllTags().observe(this, new Observer<List<JournalEntryTag>>() {
            @Override
            public void onChanged(List<JournalEntryTag> journalEntryTags) {
                Menu menu = navigationView.getMenu().findItem(R.id.tagstitle).getSubMenu();
                menu.clear();

                for(JournalEntryTag tag : journalEntryTags){
                    menu.add(Menu.NONE, tag.getId(), Menu.NONE, tag.getName()).setIcon(R.drawable.ic_baseline_label_24).setCheckable(true);
                }
            }
        });

        mainViewModel.setQueryFilter(""); //get all on start
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem layout = menu.findItem(R.id.layout);
        layout.setIcon(mainViewModel.getIsListLayout() ?  R.drawable.ic_round_dashboard_24 : R.drawable.ic_baseline_view_agenda_24);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.layout){
            if(mainViewModel.getIsListLayout()){
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                mainViewModel.setIsListLayout(false);
                item.setIcon(R.drawable.ic_baseline_view_agenda_24);
            }else {
                recyclerView.setLayoutManager(linearLayoutManager);
                mainViewModel.setIsListLayout(true);
                item.setIcon(R.drawable.ic_round_dashboard_24);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mainViewModel.saveIsListLayout();
        super.onDestroy();
    }

    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            mainViewModel.setQueryFilter(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mainViewModel.setQueryFilter(newText);
            return true;
        }
    };

    NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId() == R.id.archive){
                item.setChecked(!item.isChecked());
                mainViewModel.setArchivedFilter(item.isChecked());
                return false;
            } else {
                item.setChecked(!item.isChecked());
                if (item.isChecked()) {
                    mainViewModel.addTagFilter(item.getItemId());
                } else {
                    mainViewModel.removetagFilter(item.getItemId());
                }
                return false;
            }
        }
    };

    JournalEntryRecyclerAdapter.OnItemClickListener onItemClickListener = new JournalEntryRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onClick(View view, int position) {
            editEntry(entryAdapter.getEntry(position).journalEntry);
        }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if(direction == ItemTouchHelper.RIGHT){
                mainViewModel.archiveJournalEntry(entryAdapter.getEntry(viewHolder.getAdapterPosition()).journalEntry);
            }else{
                entryAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        }
    });

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JournalEntry journalEntry = new JournalEntry("", 1, "", false, new Date(), new Date());
            editEntry(journalEntry);
        }
    };

    private void editEntry(JournalEntry journalEntry){
        Intent intent = new Intent(MainActivity.this, EditJournalEntryActivity.class);
        intent.putExtra("entry", journalEntry);
        startActivity(intent);
    }
}