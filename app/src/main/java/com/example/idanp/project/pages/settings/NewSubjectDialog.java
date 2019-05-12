package com.example.idanp.project.pages.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.idanp.project.R;

public class NewSubjectDialog extends AppCompatDialogFragment {

    private EditText editTextSubjectName;
    private SettingsSubjectDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_subject_dialog,null);
        builder.setView(view)
                .setTitle("Create a new subject")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String l_name = editTextSubjectName.getText().toString();
                        listener.applyChange(l_name);
                    }
                });

        //Initialization
        editTextSubjectName = view.findViewById(R.id.etsettingsSDName);


        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SettingsSubjectDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement SettingsSubjectDialogListener");
        }
    }

    public interface SettingsSubjectDialogListener {
        void applyChange(String name);
    }
}
