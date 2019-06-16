package com.example.idanp.project.pages.subject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import com.example.idanp.project.supportClasses.BaseActivity;
import com.example.idanp.project.supportClasses.Grade;
import com.example.idanp.project.supportClasses.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SubjectTemplate extends BaseActivity {

    private static final String TAG = "SubjectTemplate";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private String strName;

    private SharedPreferences sharedPref;

    private RecyclerView gradeList;
    private TextView average;
    private Button addGrade;
    private AnyChartView chartView;

    private Subject subject;
    private ArrayList<Grade> grades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        super.onCreateDrawer();


        sharedPref = getSharedPreferences("storage", MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        gradeList = findViewById(R.id.rvSubject);
        average = findViewById(R.id.tvSubjectAverage);
        addGrade = findViewById(R.id.btSubjectAddGrade);
        chartView = findViewById(R.id.acvSubjectGraph);

        Intent intent = getIntent();
        strName = intent.getStringExtra("subjectName");
        super.changeTitle(strName);
        grades = new ArrayList<>();
        //Init subject from database
        db.collection("users").document(userID).collection("subjects").document(strName).collection("grades").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    for(QueryDocumentSnapshot document : task.getResult()){
//                        HashMap dateMap = (HashMap) document.get("date");
//                        Date date = new Date(Integer.parseInt(dateMap.get("year").toString()) , Integer.parseInt(dateMap.get("month").toString()), Integer.parseInt(dateMap.get("day").toString()));
//                        grades.add(new Grade(document.get("name").toString(),Integer.parseInt(document.get("grade").toString()), document.get("id").toString(), date.getYear(),
//                                date.getMonth(), date.getDay(), Integer.parseInt(document.get("distribution").toString()), (Uri[]) document.get("testPictures")));
//                    }
                    grades = (ArrayList<Grade>) task.getResult().toObjects(Grade.class);
                    grades = sortGradesByDate(grades);
                    subject = new Subject(strName, grades);
                    double avg = subject.calculateAverage();
                    average.setText(avg + "");
                    db.collection("users").document(userID).collection("subjects").document(strName).update("average", avg);
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

    private ArrayList<Grade> sortGradesByDate(ArrayList<Grade> grades) {
        ArrayList<Grade> sorted = new ArrayList<>();
        if (grades != null) {
            Grade earliest;
            int earliestPosition;
            for (int i = 0; i < grades.size(); i++) {
                earliest = grades.get(i);
                earliestPosition = i;
                //if (earliest != null) {
                for (int y = 0; y < grades.size(); y++)
                    if (grades.get(y) != null && earliest != null) {
                        if (!earliest.getDate().isEarlier(grades.get(y).getDate())) {
                            earliest = grades.get(y);
                            earliestPosition = y;
                        }
                    }else if (grades.get(y) != null && earliest == null) {
                            earliest = grades.get(y);
                            earliestPosition = y;
                        }

                // }
                sorted.add(earliest);
                grades.set(earliestPosition, null);
                earliest = null;
                earliestPosition = -1;
            }
            for (Grade grade : grades) {
                if (grade != null)
                    sorted.add(grade);
            }
        }
        return sorted;
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: called");
        GradeRecyclerViewAdapter adapter = new GradeRecyclerViewAdapter(this, subject);
        gradeList.setAdapter(adapter);
        gradeList.setLayoutManager(new LinearLayoutManager(this));
    }
}
