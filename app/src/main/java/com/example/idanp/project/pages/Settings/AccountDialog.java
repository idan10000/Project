package com.example.idanp.project.pages.Settings;

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

public class AccountDialog extends AppCompatDialogFragment {

    private EditText editTextPersonalCode;
    private SettingsAccountDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_account_dialog,null);
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
                        String personalCode = editTextPersonalCode.getText().toString();
                        listener.applyChange(personalCode);
                    }
                });
        editTextPersonalCode = view.findViewById(R.id.settingsAccDgPC);

        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SettingsAccountDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement SettingsAccountDialogListener");
        }
    }

    public interface SettingsAccountDialogListener{
        void applyChange(String id);
    }
}
