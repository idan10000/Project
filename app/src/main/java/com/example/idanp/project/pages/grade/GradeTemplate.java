package com.example.idanp.project.pages.grade;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
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
import com.example.idanp.project.supportClasses.BaseActivity;
import com.example.idanp.project.supportClasses.Date;
import com.example.idanp.project.supportClasses.Grade;
import com.example.idanp.project.supportClasses.baseClasses.BaseGrade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GradeTemplate extends BaseActivity implements PictureRecyclerViewAdapter.PictureRecyclerViewListener {

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
    ImageView fullPicture;

    private DocumentReference docReference;
    private String userID, subjectName;
    private Grade gradeObject;
    private int day, month, year;
    private ArrayList<String> picturesURLs;
    private SharedPreferences.Editor editor;
    private String currentPhotoPath;
    private Uri newImageUri;
    private File newImageFile;

    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        super.onCreateDrawer();

        name = findViewById(R.id.etGradeTestName);
        grade = findViewById(R.id.etGradeGrade);
        distribution = findViewById(R.id.etGradeDistribution);
        date = findViewById(R.id.etGradeDate);
        addPicture = findViewById(R.id.abtGradeAddPicture);
        confirmChanges = findViewById(R.id.btGradeConfirm);
        progressBar = findViewById(R.id.pbGrade);
        pictures = findViewById(R.id.rvGradePictures);
        fullPicture = findViewById(R.id.ivGradesFullPicture);
        fullPicture.setVisibility(View.GONE);

        sharedPref = getSharedPreferences("storage", MODE_PRIVATE);
        editor = sharedPref.edit();
        userID = sharedPref.getString("userID", "");

        data = getIntent();
        if (data.getStringExtra("subjectName") != null)
            subjectName = data.getStringExtra("subjectName");
        else
            subjectName = "default";

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
                checkFilePermissions();
                dispatchTakePictureIntent();
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

                if (grade.getText().length() > 0) {
                    l_grade = Integer.parseInt(grade.getText().toString());

                    if (l_grade < 0 || l_grade > 100) {
                        Toast.makeText(context, "The grade has to be between 0-100...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(context, "Please enter a grade...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (distribution.getText().length() != 0) {
                    l_distribution = Integer.parseInt(distribution.getText().toString());
                } else l_distribution = -1;

                if (day == 0 || year == 0 || month == 0) {
                    Toast.makeText(context, "Please enter a date...", Toast.LENGTH_SHORT).show();
                    return;
                }

                gradeObject = new Grade(name.getText().toString(), l_grade, docReference.getId(), year, month, day, l_distribution, picturesURLs);

                try {
                    gradeObject.setDistribution(l_distribution);
                } catch (BaseGrade.OutOfDistributionRangeException e) {
                    Log.w(TAG, "onClick: Distribution out of range", e);
                    Toast.makeText(context, "Distribution is out of the allowed range", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                docReference.set(gradeObject);

                editor.putString("lastGrade", name.getText().toString() + "  Grade: " + l_grade);
                editor.commit();
                Intent intent = new Intent(context, SubjectTemplate.class);
                intent.putExtra("subjectName", subjectName);
                startActivity(intent);
            }
        });

        docReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "onEvent: Listen failed", e);
                    return;
                }
                if (gradeObject != null)
                    Log.d(TAG, "onEvent: change detected in the db at the " + gradeObject.getName() + " document");
                initPictures();
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
                        picturesURLs = gradeObject.getPictures();
                        name.setText(task.getResult().getString("name"));
                        grade.setText(gradeObject.getGrade() + "");
                        double l_distribution = gradeObject.getDistribution();
                        if (l_distribution != -1)
                            distribution.setText(l_distribution + "");
                        date.setText(gradeObject.getDate().toString());
                        initPictures();
                    }
                }
            }
        });
    }

    /**
     * Inserts all the pictures of the test that are saved in the database into the {@link HorizontalScrollView} {@code pictures}.
     */
    private void initPictures() {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference filepath = storage.child(userID).child(subjectName);
        PictureRecyclerViewAdapter adapter = new PictureRecyclerViewAdapter(context, picturesURLs, docReference, filepath);
        pictures.setAdapter(adapter);
        pictures.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        Log.d(TAG, "initPictures: Initialized pictures from db at: " + subjectName);
        Toast.makeText(context, "loaded images", Toast.LENGTH_SHORT).show();
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

            final StorageReference filepath = storage.child(userID).child(subjectName).child(newImageUri.getLastPathSegment());

            filepath.putFile(newImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: Picture successfully uploaded");
                    Toast.makeText(GradeTemplate.this, "Picture successfully uploaded", Toast.LENGTH_SHORT).show();

                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: ", e);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String generatedFilePath = uri.toString();
                            Toast.makeText(context, generatedFilePath, Toast.LENGTH_SHORT).show();
                            docReference.update("pictures", FieldValue.arrayUnion(generatedFilePath)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: initializing pictures");
                                    initPictures();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: Failed to upload picture", e);
                                }
                            });
                            picturesURLs.add(generatedFilePath);
                        }
                    });
                }
            });

        }
    }

    private void checkFilePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = GradeTemplate.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += GradeTemplate.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            permissionCheck += GradeTemplate.this.checkSelfPermission("Manifest.permission.CAMERA");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = Calendar.getInstance().getTime().getTime() + "";
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Creates an intent to take a photo
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            newImageFile = null;
            try {
                newImageFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: Error occurred while creating the File", ex);

            }
            // Continue only if the File was successfully created
            if (newImageFile != null) {
                newImageUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        newImageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newImageUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fullPicture.getVisibility() == View.VISIBLE)
            fullPicture.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    @Override
    public void openImage(String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(fullPicture);
        fullPicture.setVisibility(View.VISIBLE);
    }
}
