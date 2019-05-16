package com.example.idanp.project.pages.subject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChartView;
import com.example.idanp.project.R;
import com.example.idanp.project.pages.grade.GradeTemplate;
import com.example.idanp.project.supportClasses.Subject;
import com.example.idanp.project.supportClasses.baseClasses.BaseGrade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SubjectTemplate extends AppCompatActivity {

    private static final String TAG = "SubjectTemplate";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private String strName;

    private SharedPreferences sharedPref;

    private RecyclerView gradeList;
    private TextView name, average;
    private Button addGrade;
    private AnyChartView chartView;

    private Subject subject;
    private ArrayList<BaseGrade> grades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        sharedPref = getSharedPreferences("storage", MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        gradeList = findViewById(R.id.rvSubject);
        name = findViewById(R.id.tvSubjectName);
        average = findViewById(R.id.tvSubjectAverage);
        addGrade = findViewById(R.id.btSubjectAddGrade);
        chartView = findViewById(R.id.acvSubjectGraph);

        Intent intent = getIntent();
        strName = intent.getStringExtra("name");
        name.setText(strName);

        //Init subject from database
        db.collection("users").document(userID).collection("subjects").document(strName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    subject = task.getResult().toObject(Subject.class);
                    average.setText(subject.getAverage() + "");
                    grades = subject.getGradesObject();
                    initRecyclerView();
                    subject.createChart(chartView);
                }
            }
        });

        /**
         * onClickListener, when pressed will move to {@link GradeTemplate} with the subject name
         * as extra data in the intent.
         */
        addGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent l_intent = new Intent(view.getContext(), GradeTemplate.class);
                l_intent.putExtra("subjectName", strName);
                startActivity(l_intent);
            }
        });



    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: called");
        GradeRecyclerViewAdapter adapter = new GradeRecyclerViewAdapter(this, grades, strName);
        gradeList.setAdapter(adapter);
        gradeList.setLayoutManager(new LinearLayoutManager(this));
    }
}
