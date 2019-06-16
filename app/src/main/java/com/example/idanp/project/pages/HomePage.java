package com.example.idanp.project.pages;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.idanp.project.R;
import com.example.idanp.project.supportClasses.BaseActivity;
import com.example.idanp.project.supportClasses.Grade;
import com.example.idanp.project.supportClasses.Subject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomePage extends BaseActivity {

    private static final String TAG = "HomePage";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPref;

    private String userID;
    private ArrayList<Subject> subjects;

    private TextView tvAvg, tvBestSubject, tvHighestSubjectAvg, tvRecentGrade;
    private AnyChartView chartView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        super.onCreateDrawer();
        super.changeTitle("Home Page");

        tvAvg = findViewById(R.id.tvHomeAvg);
        tvBestSubject = findViewById(R.id.tvHomeBestSubject);
        tvHighestSubjectAvg = findViewById(R.id.tvHomeHighestSubjectAvg);
        tvRecentGrade = findViewById(R.id.tvHomeRecentGrade);
        chartView = findViewById(R.id.acvHomeGraph);

        sharedPref = getSharedPreferences("storage", MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        db.collection("users").document(userID).collection("subjects").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                subjects = (ArrayList<Subject>) queryDocumentSnapshots.toObjects(Subject.class);
                tvAvg.setText(CalculateOverallAvg() + "");
                Subject highest = getHighestSubject();
                if (highest != null) {
                    tvBestSubject.setText(highest.getName());
                    tvHighestSubjectAvg.setText(highest.getAverage() + "");
                } else {
                    tvBestSubject.setText("No subjects");
                    tvHighestSubjectAvg.setText(0);
                }
                tvRecentGrade.setText(sharedPref.getString("lastGrade", "No grades"));
                chartView.setChart(createChart());
            }
        });

    }

    private Subject getHighestSubject() {
        if (subjects != null) {
            Subject highest = subjects.get(0);
            for (int i = 1; i < subjects.size(); i++) {
                if (highest.getAverage() < subjects.get(i).getAverage())
                    highest = subjects.get(i);
            }
            return highest;
        } else
            return null;
    }

    private double CalculateOverallAvg() {
        double sum = 0, num = 0;
        if (subjects != null) {
            for (Subject subj : subjects) {
                if (subj.getAverage() != 0) {
                    sum += subj.getAverage();
                    num++;
                }
            }
            if (num != 0)
                return sum / num;
            else return 0;
        } else return 0;
    }

    private Cartesian createChart() {
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        subjects = sortSubjectsByName(subjects);
        for (Subject subject : subjects) {
            data.add(new ValueDataEntry(subject.getName(), subject.getAverage()));
        }
        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Averages of All Subjects");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Subject");
        cartesian.yAxis(0).title("Average");

        return cartesian;
    }

    private ArrayList<Subject> sortSubjectsByName(ArrayList<Subject> subjects) {
        ArrayList<Subject> sorted = new ArrayList<>();
        if (subjects != null) {
            Subject first;
            int earliestPosition;
            for (int i = 0; i < subjects.size(); i++) {
                first = subjects.get(i);
                earliestPosition = i;
                for (int y = 0; y < subjects.size(); y++)
                    if (subjects.get(y) != null && first != null) {
                        if (!isSubjectBefore(first, subjects.get(y))) {
                            first = subjects.get(y);
                            earliestPosition = y;
                        }
                    } else if (subjects.get(y) != null && first == null) {
                        first = subjects.get(y);
                        earliestPosition = y;
                    }

                // }
                sorted.add(first);
                subjects.set(earliestPosition, null);
                first = null;
                earliestPosition = -1;
            }
        }
        return sorted;
    }

    /**
     * Compares 2 subjects are returns if the first subject is alphabetically before the second
     *
     * @param subject1 The subject that that is wanted
     * @param subject2 The subject compared
     * @return true if subject1 comes before subject2, false otherwise.
     */
    private boolean isSubjectBefore(Subject subject1, Subject subject2) {
        if (subject1.getName().compareToIgnoreCase(subject2.getName()) < 0)
            return true;
        else return false;
    }

}
