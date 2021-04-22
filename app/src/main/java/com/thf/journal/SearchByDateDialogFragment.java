package com.thf.journal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class SearchByDateDialogFragment extends DialogFragment {

    public interface SearchByDateDialogListener{
        void onSearchByDatePositiveClick(Long addedFrom, Long addedTo);
        void onSearchByDateNegativeClick();
    }

    private SearchByDateDialogListener listener;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public static SearchByDateDialogFragment newInstance(Bundle dates){
        SearchByDateDialogFragment dialogFragment = new SearchByDateDialogFragment();
        dialogFragment.setArguments(dates);
        return dialogFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (SearchByDateDialogListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View content = View.inflate(getActivity(), R.layout.search_by_date_dialog, null);

        EditText editAddedFrom = content.findViewById(R.id.added_from);
        EditText editAddedTo = content.findViewById(R.id.added_to);

        if(getArguments().containsKey("addedFrom")) editAddedFrom.setText(format.format(getArguments().getLong("addedFrom")));
        if(getArguments().containsKey("addedTo")) editAddedTo.setText(format.format(getArguments().getLong("addedTo")));

        editAddedFrom.addTextChangedListener(new CustomTextWatcher(editAddedFrom));
        editAddedTo.addTextChangedListener(new CustomTextWatcher(editAddedTo));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content)
                .setPositiveButton("Search", (dialog, which) -> listener.onSearchByDatePositiveClick(
                        getDate(editAddedFrom),
                        getDate(editAddedTo)
                ))
                .setNegativeButton("Cancel", (dialog, which) -> listener.onSearchByDateNegativeClick());
        return builder.create();
    }

    private Long getDate(EditText editText) {
        Long date = null;
        try {
            date = format.parse(editText.getText().toString()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private class CustomTextWatcher implements TextWatcher {

        int cursor;
        int lenghtBefore;
        int lenghtAfter;

        boolean ignore = false;

        EditText editText;

        public CustomTextWatcher(EditText editText){
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(ignore) return;
            lenghtBefore = s.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(ignore) return;
            cursor = start + count;
            lenghtAfter = s.length();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(ignore) return;
            ignore = true;

            String text = s.toString().replace("/", "");
            s.replace(0, s.length(), text);
            if(text.length() > 2) s.insert(2, "/");
            if(text.length() > 4) s.insert(5, "/");

            if(cursor == 3 || cursor == 6) cursor += isDeleting()? -1 : 1;
            editText.setSelection(cursor);

            ignore = false;
        }

        private boolean isDeleting(){
            return lenghtAfter < lenghtBefore;
        }
    }
}
