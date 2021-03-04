package com.thf.journal;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JournalEntryTagAdapter extends ArrayAdapter<JournalEntryTag> {

    private List<JournalEntryTag> tags;
    private LayoutInflater inflater;

    public JournalEntryTagAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_spinner_item);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = (TextView)inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        ((TextView)convertView).setText(tags.get(position).getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = (TextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        ((TextView)convertView).setText(tags.get(position).getName());

        return convertView;
    }

    @Override
    public int getCount() {
        return tags == null ? 0 : tags.size();
    }

    @Nullable
    @Override
    public JournalEntryTag getItem(int position) {
        return tags.get(position);
    }

    public void setTags(List<JournalEntryTag> tags){
        this.tags = tags;
    }
}