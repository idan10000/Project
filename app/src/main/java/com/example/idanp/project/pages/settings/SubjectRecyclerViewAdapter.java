package com.example.idanp.project.pages.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.idanp.project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class SubjectRecyclerViewAdapter extends RecyclerView.Adapter<SubjectRecyclerViewAdapter.ViewHolder>  {

    private static final String TAG = "SubjectRecyclerViewAdapter";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPref;




    private ArrayList<String> subjectNames;
    private Context context;

    public SubjectRecyclerViewAdapter(Context context, ArrayList<String> subjectNames) {
        this.subjectNames = subjectNames;
        this.context = context;
        sharedPref = context.getSharedPreferences("storage", context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.settings_subject_recycleview , viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        Log.d(TAG,"onBindViewHolder: called.");

        holder.subjectName.setText(subjectNames.get(i));

        holder.deleteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"Subject delete dialog clicked on");
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                db.collection("users").document(sharedPref.getString("userID","")).collection("subjects").document(subjectNames.get(i)).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,  "deleting from the database");
                                    }
                                });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to delete " + subjectNames.get(i) + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectName;
        TextView deleteBT;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.tvSettingsSRVName);
            deleteBT = itemView.findViewById(R.id.tvSettingsSRVDelete);
            layout = itemView.findViewById(R.id.clSettingsSRV);
        }
    }
}
