package com.example.idanp.project.pages.Settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import com.example.idanp.project.R;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AccountDialog.SettingsAccountDialogListener {

    TextView personalCode;
    Button switchAccount, addSubject, applyChanges;
    Spinner grade, classNum;
    SpinnerAdapter spinnerAdapter;
    ScrollView subjectsList;
    ArrayAdapter<CharSequence> arrAdapterClass, arrAdapterGrade;
    int classCurrentPosition, gradeCurrentPosition;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Initialisation
        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        personalCode = findViewById(R.id.tvSettingsPersonalCode);
        grade = findViewById(R.id.spSettingsGrades);
        classNum = findViewById(R.id.spSettingsClasses);
        switchAccount = findViewById(R.id.btSettingsChangeUser);
        addSubject = findViewById(R.id.btSettingsAddSubject);
        applyChanges = findViewById(R.id.btSettingsApplyChanges);
        subjectsList = findViewById(R.id.svSettingsSubjects);

        //Spinner initialisation
        arrAdapterClass = ArrayAdapter.createFromResource(this, R.array.ClassNums,android.R.layout.simple_spinner_item);
        arrAdapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classNum.setAdapter(arrAdapterClass);

        arrAdapterGrade = ArrayAdapter.createFromResource(this, R.array.GradeNums,android.R.layout.simple_spinner_item);
        arrAdapterGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(arrAdapterGrade);

        //Listeners
        switchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccountDialog();
            }
        });
        classNum.setOnItemSelectedListener(this);
        grade.setOnItemSelectedListener(this);

    }

    /**
     * Updates the place holders of {@code classCurrentPosition} and {@code gradeCurrentPosition} to the currently selected items.
     * @param adapterView The spinner
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     *
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if(adapterView.equals(classNum))
            classCurrentPosition = position;
        else
            if(adapterView.equals(grade))
                gradeCurrentPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        if(adapterView.equals(classNum))
            classCurrentPosition = -1;
        else
            if(adapterView.equals(grade))
                gradeCurrentPosition = -1;
    }


    @Override
    public void applyChange(String id) {
        editor.putString("PersonalCode", id);
        editor.commit();
    }

    public void openAccountDialog(){
        AccountDialog accountDialog = new AccountDialog();
        accountDialog.show(getSupportFragmentManager(),"account dialog");
    }
}

