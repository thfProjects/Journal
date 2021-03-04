package com.thf.journal;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class JournalEntryRecyclerAdapter extends RecyclerView.Adapter<JournalEntryRecyclerAdapter.ViewHolder>{

    private List<JournalEntryWithTag> entries;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;


    public JournalEntryRecyclerAdapter(Context context, List<JournalEntryWithTag> entries){
        this.entries = entries;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public JournalEntryRecyclerAdapter(Context context){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.journal_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(entries.get(position).journalEntry.getText());
        holder.title.setVisibility(entries.get(position).journalEntry.getTitle().isEmpty()? View.GONE: View.VISIBLE);
        holder.title.setText(entries.get(position).journalEntry.getTitle());
        //((GradientDrawable)holder.textView.getBackground().mutate()).setStroke(entries.get(position).tag.getId() == 1? 2 : 4, entries.get(position).tag.getColor());
    }

    @Override
    public int getItemCount() {
        return entries == null ? 0 : entries.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setEntries(List<JournalEntryWithTag> entries){
        this.entries = entries;
        Collections.reverse(this.entries); //temporary
    }

    public JournalEntryWithTag getEntry(int position){
        return entries.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            title = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(v,getAdapterPosition());
        }
    }

    public static interface OnItemClickListener{
        public void onClick(View view, int position);
    }
}
