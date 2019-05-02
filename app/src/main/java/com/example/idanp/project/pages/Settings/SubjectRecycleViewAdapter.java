package com.example.idanp.project.pages.Settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.idanp.project.R;

import java.util.ArrayList;

public class SubjectRecycleViewAdapter extends RecyclerView.Adapter<SubjectRecycleViewAdapter.ViewHolder> {

    private static final String TAG = "SubjectRecycleViewAdapter";

    public SubjectRecycleViewAdapter(Context context, ArrayList<String> subjectNames) {
        this.subjectNames = subjectNames;
        this.context = context;
    }

    private ArrayList<String> subjectNames = new ArrayList<>();
    private Context context;



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
