package com.thf.journal;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EditTagsActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener{

    private EditTagsViewModel editTagsViewModel;

    private RecyclerView recyclerView;
    private EditTagsRecyclerAdapter tagsAdapter;

    private EditText addTagEdit;
    private ImageView addTagButtonConfirm;

    private boolean addingTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_tags_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(navUpListener);

        editTagsViewModel = new ViewModelProvider(this).get(EditTagsViewModel.class);

        recyclerView = findViewById(R.id.tags);
        tagsAdapter = new EditTagsRecyclerAdapter(this);

        tagsAdapter.setTagUpdateListener(tagUpdateListener);
        tagsAdapter.setTagDeleteListener(tagDeleteListener);

        recyclerView.setAdapter(tagsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addTagEdit = findViewById(R.id.add_tag);
        addTagEdit.addTextChangedListener(textWatcher);

        addTagButtonConfirm = findViewById(R.id.button_right);
        addTagButtonConfirm.setOnClickListener(addTagListener);

        addingTag = false;

        editTagsViewModel.getAllTags().observe(this, new Observer<List<JournalEntryTag>>() {
            @Override
            public void onChanged(List<JournalEntryTag> journalEntryTags) {
                tagsAdapter.setTags(journalEntryTags);
                tagsAdapter.notifyDataSetChanged();
            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(s.toString().equals("")){
                addingTag = true;
                addTagButtonConfirm.setImageResource(R.drawable.ic_baseline_check_24);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.toString().equals("")){
                addingTag = false;
                addTagButtonConfirm.setImageDrawable(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnClickListener addTagListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(addingTag){
                editTagsViewModel.add(new JournalEntryTag(addTagEdit.getText().toString(), Color.BLACK));
                addTagEdit.setText("");
            }
        }
    };

    EditTagsRecyclerAdapter.TagUpdateListener tagUpdateListener = new EditTagsRecyclerAdapter.TagUpdateListener() {
        @Override
        public void tagUpdate(int position, String name) {
            if(name.equals("")) tagsAdapter.notifyItemChanged(position);
            else editTagsViewModel.update(tagsAdapter.getTag(position), name);
        }
    };

    EditTagsRecyclerAdapter.TagDeleteListener tagDeleteListener = new EditTagsRecyclerAdapter.TagDeleteListener() {
        @Override
        public void tagDelete(int position) {
            JournalEntryTag tag = tagsAdapter.getTag(position);
            DeleteDialogFragment dialogFragment = DeleteDialogFragment.newInstance(position, tag.getId(), "Permanently delete tag " + tag.getName() + "?");
            dialogFragment.show(getSupportFragmentManager(), "DeleteJournalEntryTagDialogFragment");
        }
    };

    View.OnClickListener navUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        recyclerView.clearFocus();
        super.onBackPressed();
    }

    @Override
    public void onDeletePositiveClick(int id) {
        editTagsViewModel.deleteById(id);
    }

    @Override
    public void onDeleteNegativeClick(int position) {

    }
}
