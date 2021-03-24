package com.thf.journal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EditTagsRecyclerAdapter extends RecyclerView.Adapter<EditTagsRecyclerAdapter.ViewHolder>{

    private List<JournalEntryTag> tags;
    private LayoutInflater inflater;
    private InputMethodManager inputMethodManager;

    private TagUpdateListener tagUpdateListener;
    private TagDeleteListener tagDeleteListener;

    public EditTagsRecyclerAdapter(Context context){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.edit_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(tags.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return tags == null ? 0 : tags.size();
    }

    public void setTags(List<JournalEntryTag> tags){
        this.tags = tags;
    }

    public JournalEntryTag getTag(int position){
        return tags.get(position);
    }

    public void setTagUpdateListener(TagUpdateListener tagUpdateListener){
        this.tagUpdateListener = tagUpdateListener;
    }

    public void setTagDeleteListener(TagDeleteListener tagDeleteListener){
        this.tagDeleteListener = tagDeleteListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnClickListener {

        EditText name;
        ImageView leftIcon;
        ImageView rightIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            leftIcon = itemView.findViewById(R.id.icon_left);
            rightIcon = itemView.findViewById(R.id.icon_right);
            name.setOnFocusChangeListener(this);
            rightIcon.setOnClickListener(this);
            leftIcon.setOnClickListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                ImageViewCompat.setImageTintList(rightIcon, ContextCompat.getColorStateList(inflater.getContext(), R.color.primary));
                rightIcon.setImageResource(R.drawable.ic_baseline_check_24);
                leftIcon.setImageResource(R.drawable.ic_baseline_delete_24);
            }else {
                ImageViewCompat.setImageTintList(rightIcon, ColorStateList.valueOf(Color.BLACK));
                rightIcon.setImageResource(R.drawable.ic_baseline_edit_24);
                leftIcon.setImageResource(R.drawable.ic_baseline_label_24);
            }
        }

        @Override
        public void onClick(View v) {
            if(name.hasFocus()){
                if(v == rightIcon){
                    tagUpdateListener.tagUpdate(getAdapterPosition(), name.getText().toString());
                }else if(v == leftIcon){
                    tagDeleteListener.tagDelete(getAdapterPosition());
                }
                name.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(name.getWindowToken(), 0);
            }else {
                name.requestFocus();
            }
        }
    }

    public interface TagUpdateListener{
        void tagUpdate(int position, String name);
    }

    public interface TagDeleteListener{
        void tagDelete(int position);
    }
}
