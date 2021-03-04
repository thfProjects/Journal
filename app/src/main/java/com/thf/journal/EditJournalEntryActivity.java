package com.thf.journal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditJournalEntryActivity extends AppCompatActivity {

    private JournalEntry journalEntry;

    private EditText editText;
    private EditText editTitle;

    private TextView textDateAdded;
    private TextView textDateEditted;

    private Spinner spinner;
    private JournalEntryTagAdapter spinnerAdapter;

    private EditJournalEntryViewModel editJournalEntryViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_journal_entry_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(navUpListener);

        editJournalEntryViewModel = new ViewModelProvider(this).get(EditJournalEntryViewModel.class);

        journalEntry = getIntent().getParcelableExtra("entry");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        editText = findViewById(R.id.edit_text);
        editTitle = findViewById(R.id.edit_title);

        textDateAdded = findViewById(R.id.text_date_added);
        textDateEditted = findViewById(R.id.text_date_editted);

        editText.setText(journalEntry.getText());
        editTitle.setText(journalEntry.getTitle());

        textDateAdded.setText("Added " + dateFormat.format(journalEntry.getDateAdded()));
        textDateEditted.setText("Last editted " + dateFormat.format(journalEntry.getDateEditted()));

        spinnerAdapter = new JournalEntryTagAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_journal_entry_menu, menu);
        spinner = (Spinner)menu.findItem(R.id.tag).getActionView();
        spinner.setAdapter(spinnerAdapter);

        editJournalEntryViewModel.getAllTags().observe(this, new Observer<List<JournalEntryTag>>() {
            @Override
            public void onChanged(List<JournalEntryTag> journalEntryTags) {
                spinnerAdapter.setTags(journalEntryTags);
                spinnerAdapter.notifyDataSetChanged();

                int i = 0;
                boolean isfound = false;
                while (i < journalEntryTags.size() && !isfound){
                    if(journalEntry.getTag_id() == journalEntryTags.get(i).getId()){
                        spinner.setSelection(i);
                        isfound = true;
                    }
                    i++;
                }
            }
        });

        return true;
    }

    View.OnClickListener navUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        journalEntry.setText(editText.getText().toString());
        journalEntry.setTitle(editTitle.getText().toString());
        journalEntry.setTag_id(((JournalEntryTag) spinner.getSelectedItem()).getId());
        journalEntry.setDateEditted(new Date());

        editJournalEntryViewModel.upsert(journalEntry);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
