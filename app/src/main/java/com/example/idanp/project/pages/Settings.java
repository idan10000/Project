package com.example.idanp.project.pages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import com.example.idanp.project.R;

public class Settings extends AppCompatActivity {

    TextView personalCode;
    Button switchAccount, addSubject, applyChanges;
    Spinner grade, classNum;
    SpinnerAdapter spinnerAdapter;
    ScrollView subjectsList;
    ArrayAdapter<CharSequence> arrAdapterClass, arrAdapterGrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Initialisation
        personalCode = findViewById(R.id.tvSettingsPersonalCode);
        grade = findViewById(R.id.spSettingsGrades);
        classNum = findViewById(R.id.spSettingsClasses);
        switchAccount = findViewById(R.id.btSettingsChangeUser);
        addSubject = findViewById(R.id.btSettingsAddSubject);
        applyChanges = findViewById(R.id.btSettingsApplyChanges);
        subjectsList = findViewById(R.id.svSettingsSubjects);

        arrAdapterClass = ArrayAdapter.createFromResource(this, R.array.ClassNums,android.R.layout.simple_spinner_item);
        arrAdapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classNum.setAdapter(arrAdapterClass);

        arrAdapterGrade = ArrayAdapter.createFromResource(this, R.array.GradeNums,android.R.layout.simple_spinner_item);
        arrAdapterGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(arrAdapterGrade);

    }
}
