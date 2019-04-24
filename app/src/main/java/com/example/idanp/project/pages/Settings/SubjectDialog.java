package com.example.idanp.project.pages.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.idanp.project.R;

public class SubjectDialog extends AppCompatDialogFragment {

    private EditText editTextSubjectName;
    private SettingsSubjectDialogListener listener;
    private Button btChooseIcon;
    private ImageView imageViewIcon;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_subject_dialog,null);
        builder.setView(view)
                .setTitle("Change Account")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String l_name = editTextSubjectName.getText().toString();
                        //listener.applyChange(l_name, );
                    }
                });

        //Initialization
        editTextSubjectName = view.findViewById(R.id.etsettingsSDName);
        imageViewIcon = view.findViewById(R.id.ivSettingsSDIcon);
        btChooseIcon = view.findViewById(R.id.btSettingsSDIcon);

        btChooseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


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
        void applyChange(String name, Uri icon);
    }
}
