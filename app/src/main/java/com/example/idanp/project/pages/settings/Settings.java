package com.example.idanp.project.pages.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.idanp.project.R;
import com.example.idanp.project.pages.Login;
import com.example.idanp.project.supportClasses.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class Settings extends BaseActivity implements AdapterView.OnItemSelectedListener, NewSubjectDialog.SettingsSubjectDialogListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Settings";

    private Toolbar toolbar;
    private Button switchAccount, addSubject, applyChanges;
    private Spinner grade, classNum;
    private EditText schoolName;
    private RecyclerView subjectsList;
    private ArrayAdapter<CharSequence> arrAdapterClass, arrAdapterGrade;

    private int classCurrentPosition, gradeCurrentPosition;
    private ArrayList<String> subjectNames = new ArrayList<>();

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();

        //Initialisation
        sharedPref = getSharedPreferences("storage", MODE_PRIVATE);
        editor = sharedPref.edit();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        schoolName = findViewById(R.id.etSettingsSchoolName);
        grade = findViewById(R.id.spSettingsGrades);
        classNum = findViewById(R.id.spSettingsClasses);
        switchAccount = findViewById(R.id.btSettingsChangeUser);
        addSubject = findViewById(R.id.btSettingsAddSubject);
        applyChanges = findViewById(R.id.btSettingsApplyChanges);
        subjectsList = findViewById(R.id.rvSettings);
        changeTitle("Settings");

        userID = sharedPref.getString("userID", "");
        DocumentReference docRef = db.collection("users").document(userID);
        /**
         * If data already exists in database, it will be displayed in the corresponding containers.
         */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.contains("school"))
                            schoolName.setText(document.get("school").toString());
                        if(document.contains("grade"))
                            grade.setSelection(Integer.parseInt(document.get("grade").toString()));
                        if(document.contains("class"))
                            classNum.setSelection(Integer.parseInt(document.get("class").toString()));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //Spinner initialisation
        arrAdapterClass = ArrayAdapter.createFromResource(this, R.array.ClassNums, android.R.layout.simple_spinner_item);
        arrAdapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classNum.setAdapter(arrAdapterClass);

        arrAdapterGrade = ArrayAdapter.createFromResource(this, R.array.GradeNums, android.R.layout.simple_spinner_item);
        arrAdapterGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(arrAdapterGrade);

        //Listeners
        switchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Login.class));
            }
        });
        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "applying changes");
                Map<String, Object> l_data = new HashMap<>();

                l_data.put("class", arrAdapterClass.getItem(classCurrentPosition));
                l_data.put("grade", arrAdapterGrade.getItem(gradeCurrentPosition));
                db.collection("users").document(userID).update(l_data);
            }
        });

        classNum.setOnItemSelectedListener(this);
        grade.setOnItemSelectedListener(this);

        db.collection("users").document(userID).collection("subjects").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(TAG, "onEvent: Listen failed", e);
                    return;
                }

                Log.d(TAG, "onEvent: subject database change detected");
                initSubjectNames();
                Settings.super.menuReset();
            }
        });

    }

    /**
     * Inserts all of the subject names from the database into the {@code subjectNames} and into the recyclerView.
     */
    private void initSubjectNames(){
        subjectNames.clear();
        db.collection("users").document(userID).collection("subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    String sbjNames = "";
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Log.d(TAG, "onComplete: added into subjectNames");
                        subjectNames.add(doc.get("name").toString());
                        sbjNames += doc.get("name").toString() + ",";
                    }
                    editor.putString("subjectNames", sbjNames);
                    editor.commit();
                    Settings.super.menuReset();
                    initRecyclerView();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "initSubjectNames: failure to pull documents");
                Log.e(TAG, e.getMessage());

            }
        });
    }

    /**
     * Sets the adapter for the recycler view
     */
    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        SubjectRecyclerViewAdapter adapter = new SubjectRecyclerViewAdapter(this, subjectNames);
        subjectsList.setAdapter(adapter);
        subjectsList.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Updates the place holders of {@code classCurrentPosition} and {@code gradeCurrentPosition} to the currently selected items.
     *
     * @param adapterView The spinner
     * @param view        The view within the AdapterView that was clicked
     * @param position    The position of the view in the adapter
     * @param id          The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (adapterView.equals(classNum))
            classCurrentPosition = position;
        else if (adapterView.equals(grade))
            gradeCurrentPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG,"nothing selected");
        if (adapterView.equals(classNum))
            classCurrentPosition = -1;
        else if (adapterView.equals(grade))
            gradeCurrentPosition = -1;
    }


    @Override
    public void applyChange(String l_name) {
        Map<String, Object> l_data = new HashMap<>();
        l_data.put("name", l_name);
        String l_AllNames = sharedPref.getString("subjectNames","");
        if(!l_AllNames.contains(l_name)) {
            l_AllNames += l_name + ",";
            editor.putString("subjectNames", l_AllNames);
            editor.commit();
            onCreateDrawer();
            db.collection("users").document(userID).collection("subjects").document(l_name).set(l_data);
        }
        else Toast.makeText(this, "Subject already exists", Toast.LENGTH_SHORT).show();
    }

    public void openDialog() {
        NewSubjectDialog newSubjectDialog = new NewSubjectDialog();
        newSubjectDialog.show(getSupportFragmentManager(), "Create a new subject");
    }
}

