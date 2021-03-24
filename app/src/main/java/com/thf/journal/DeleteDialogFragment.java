package com.thf.journal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DeleteDialogFragment extends DialogFragment {

    public interface DeleteDialogListener {
        void onDeletePositiveClick(int id);
        void onDeleteNegativeClick(int position);
    }

    DeleteDialogListener listener;

    public static DeleteDialogFragment newInstance(int position, int id, String message){
        DeleteDialogFragment dialogFragment = new DeleteDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("id", id);
        bundle.putString("message", message);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DeleteDialogListener)context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int position = getArguments().getInt("position");
        int id = getArguments().getInt("id");
        String message = getArguments().getString("message");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> listener.onDeletePositiveClick(id))
                .setNegativeButton("No", (dialog, which) -> listener.onDeleteNegativeClick(position));
        return builder.create();
    }
}
