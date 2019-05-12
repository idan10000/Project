package com.example.idanp.project.pages.grade;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idanp.project.R;
import com.example.idanp.project.pages.subject.SubjectTemplate;
import com.example.idanp.project.supportClasses.Grade;
import com.example.idanp.project.supportClasses.baseClasses.BaseGrade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GradeTemplate extends AppCompatActivity {

    private static final String TAG = "GradeTemplate";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storage = FirebaseStorage.getInstance().getReference();

    private SharedPreferences sharedPref;


    private static final int CAMERA_REQUEST_CODE = 1;
    private final Context context = this;

    EditText name, grade, distribution, date;
    FloatingActionButton addPicture;
    Button confirmChanges;
    ProgressBar progressBar;
    RecyclerView pictures;


    private DocumentReference docReference;
    private String userID, subjectName, gradeName;
    private Grade gradeObject;
    private int day, month, year;
    private  Uri[] picturesURLs;

    private final Intent data = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        name = findViewById(R.id.etGradeTestName);
        grade = findViewById(R.id.etGradeGrade);
        distribution = findViewById(R.id.etGradeDistribution);
        date = findViewById(R.id.etGradeDate);
        addPicture = findViewById(R.id.abtGradeAddPicture);
        confirmChanges = findViewById(R.id.btGradeConfirm);
        progressBar = findViewById(R.id.pbGrade);
        pictures = findViewById(R.id.rvGradePictures);

        userID = sharedPref.getString("userID", "");

        if (data.getStringExtra("subjectName") != null)
            subjectName = data.getStringExtra("subjectName");
        else
            subjectName = "default";

        //Retrieving doc path from database, stopping all other functions until complete
        if (data.getStringExtra("gradeID") != null) {
            docReference = db.collection("users").document(userID).collection("subjects").document(subjectName)
                    .collection("grades").document(data.getStringExtra("gradeID"));
        } else {
            docReference = db.collection("users").document(userID).collection("subjects").document(subjectName)
                    .collection("grades").document();
            docReference.update("id", docReference.getId());
        }

        //Initialization of fields
        initFields();

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener l_date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int theYear, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, theYear);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                day = dayOfMonth;
                month = monthOfYear;
                year = theYear;

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        this.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, l_date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        /**
         * Creates a new grade object and checks that all the field were entered correctly.
         * Inserts the new grade object into the database.
         */
        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int l_grade;
                int l_distribution;

                if(grade.getText().toString() != "") {
                    l_grade = Integer.parseInt(grade.getText().toString());

                    if (l_grade < 0 || l_grade > 100) {
                        Toast.makeText(context, "The grade has to be between 0-100...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else {
                    Toast.makeText(context, "Please enter a grade...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (distribution.getText().toString() != "") {
                    l_distribution = Integer.parseInt(distribution.getText().toString());
                }
                else l_distribution = -1;

                if(day == 0 || year == 0 || month == 0){
                    Toast.makeText(context, "Please enter a date...", Toast.LENGTH_SHORT).show();
                    return;
                }

                gradeObject = new Grade(name.getText().toString(), l_grade, docReference.getId(), year, month, day, picturesURLs);

                try {
                    gradeObject.setDistribution(l_distribution);
                } catch (BaseGrade.OutOfDistributionRangeException e) {
                    Log.w(TAG, "onClick: Distribution out of range", e);
                    Toast.makeText(context, "Distribution is out of the allowed range", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                docReference.set(gradeObject);
                Intent intent = new Intent(context, SubjectTemplate.class);
                intent.putExtra("subjectName", subjectName);
            }
        });

        docReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "onEvent: Listen failed", e);
                    return;
                }
                Log.d(TAG, "onEvent: change detected in the db at the " + gradeObject.getName() + " document");
                initPictures(documentSnapshot);
            }

        });


    }


    /**
     * Initializes the fields from the data inside the database if it exists. Includes pictures
     */
    private void initFields() {
        docReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().contains("name")) {
                        gradeObject = task.getResult().toObject(Grade.class);
                        name.setText(task.getResult().getString("name"));
                        double l_distribution = task.getResult().getDouble("distribution");
                        if (l_distribution != -1)
                            distribution.setText(l_distribution + "");
                        date.setText(task.getResult().get("date").toString());
                    }
                }
            }
        });
    }

    /**
     * Inserts all the pictures of the test that are saved in the database into the {@link HorizontalScrollView} {@code pictures}.
     */
    private void initPictures(final DocumentSnapshot snapshot) {

        progressBar.setVisibility(View.VISIBLE);
        picturesURLs = (Uri[]) snapshot.get("pictures");
        StorageReference filepath = storage.child(userID).child(subjectName);
        PictureRecyclerViewAdapter adapter = new PictureRecyclerViewAdapter(context, picturesURLs, docReference, filepath);
        pictures.setAdapter(adapter);
        pictures.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        Log.d(TAG, "onComplete: Failed to receive images");
        Toast.makeText(context, "Failed to load images", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);

    }


    /**
     * Checks if an image was captured successfully and then uploads it to fireStorage.
     * Also inserts the url into the fireStore database at the grade location under the parameter "pictures" with the url as the value
     * Inserts the image into the {@link HorizontalScrollView} {@code pictures}
     *
     * @param requestCode The request code of the intent
     * @param resultCode  The result of the intent
     * @param data        the data from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            progressBar.setVisibility(View.VISIBLE);
            Uri uri = data.getData();

            StorageReference filepath = storage.child(userID).child(subjectName).child(uri.getLastPathSegment() + ".jpg");
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: Picture successfully uploaded");
                    Toast.makeText(GradeTemplate.this, "Picture successfully uploaded", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    String downloadURL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    docReference.update("pictures", FieldValue.arrayUnion(downloadURL));
                }
            });
        }
    }
}
