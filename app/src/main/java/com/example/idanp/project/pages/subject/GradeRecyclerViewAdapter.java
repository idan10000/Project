package com.example.idanp.project.pages.subject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.idanp.project.R;
import com.example.idanp.project.pages.grade.GradeTemplate;
import com.example.idanp.project.supportClasses.Grade;

import java.util.ArrayList;

public class GradeRecyclerViewAdapter extends RecyclerView.Adapter<GradeRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "GradeRecyclerViewAdapter";

    private ArrayList<Grade> grades;
    private String subjectName;
    private Context context;


    public GradeRecyclerViewAdapter(Context context, ArrayList<Grade> grades, String subjectName) {
        this.grades = grades;
        this.context = context;
        this.subjectName = subjectName;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_grade_recyclerview , viewGroup, false);
        GradeRecyclerViewAdapter.ViewHolder holder = new GradeRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG,"onBindViewHolder: called.");

        viewHolder.grade.setText(grades.get(i).getGrade() + "");
        viewHolder.testName.setText(grades.get(i).getName() + "");
        viewHolder.distribution.setText(grades.get(i).getDistribution() + "%");

        viewHolder.editBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GradeTemplate.class);
                String id = ((Grade) grades.get(i)).getId();
                intent.putExtra("gradeID", id);
                intent.putExtra("subjectName", subjectName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(grades != null)
            return grades.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView testName, grade, distribution;
        ImageButton editBT;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            testName = itemView.findViewById(R.id.tvSubjectGRVName);
            grade = itemView.findViewById(R.id.tvSubjectGRVGrade);
            distribution = itemView.findViewById(R.id.tvSubjectGRVDistribution);
            editBT = itemView.findViewById(R.id.ibtSubjectGRVEdit);
        }
    }

}
