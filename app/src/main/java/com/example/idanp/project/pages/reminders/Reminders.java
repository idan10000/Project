package com.example.idanp.project.pages.reminders;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.idanp.project.R;
import com.example.idanp.project.pages.HomePage;
import com.example.idanp.project.supportClasses.BaseActivity;
import com.example.idanp.project.supportClasses.Date;
import com.example.idanp.project.supportClasses.Subject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Reminders extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Reminders";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPref;

    private final int AUTOMATIC_PROTOCOL = 0,
            CUSTOM_PROTOCOL = 1;


    private Spinner spSubjects, spStudyHours, spNumOfDays;
    private EditText etDate;
    private Button btConfirm;
    private ArrayAdapter<String> arrAdapterSubjects;
    private ArrayAdapter<CharSequence> arrAdapterStudyHours, arrayAdapterNumOfDays;

    private int subjectCurrentPosition, studyHoursCurrentPosition, numOfDaysCurrentPosition;
    private String[] subjectNames;
    private ArrayList<Subject> subjects;
    private String userID;
    private Date storageDate;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        super.onCreateDrawer();
        changeTitle("Study Reminders");

        context = this;

        spStudyHours = findViewById(R.id.spRemindersStudyHours);
        spSubjects = findViewById(R.id.spRemindersSubjects);
        spNumOfDays = findViewById(R.id.spRemindersNumOfDays);
        etDate = findViewById(R.id.etRemindersDate);
        btConfirm = findViewById(R.id.btRemindersConfirm);

        //SharedPref
        sharedPref = getSharedPreferences("storage", MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");
        subjectNames = sharedPref.getString("subjectNames", "").split(",");

        //Firestore
        subjects = new ArrayList<>();
        db.collection("users").document(userID).collection("subjects").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                subjects = (ArrayList<Subject>) queryDocumentSnapshots.toObjects(Subject.class);
            }
        });

        //Spinners
        arrAdapterSubjects = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectNames);
        arrAdapterSubjects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubjects.setAdapter(arrAdapterSubjects);
        spSubjects.setOnItemSelectedListener(this);

        arrAdapterStudyHours = ArrayAdapter.createFromResource(this, R.array.reminders_StudyHours, android.R.layout.simple_spinner_item);
        arrAdapterStudyHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudyHours.setAdapter(arrAdapterStudyHours);
        spStudyHours.setOnItemSelectedListener(this);

        arrayAdapterNumOfDays = ArrayAdapter.createFromResource(this, R.array.reminders_NumOfDays, android.R.layout.simple_spinner_item);
        arrayAdapterNumOfDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNumOfDays.setAdapter(arrayAdapterNumOfDays);
        spNumOfDays.setOnItemSelectedListener(this);

        //Date picker
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener l_date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int theYear, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, theYear);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                storageDate = new Date(theYear, monthOfYear, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                etDate.setText(sdf.format(myCalendar.getTime()));
            }

        };
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, l_date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etDate.getText().length() > 0) {
                    int numOfDays, numOfHours;
                    Subject chosenSubject = null;
                    String chosenSubjectName = arrAdapterSubjects.getItem(subjectCurrentPosition);
                    if (chooseReminderProtocol() == AUTOMATIC_PROTOCOL) {
                        for (Subject sbj : subjects) {
                            if (sbj.getName().equals(chosenSubjectName)) {
                                chosenSubject = sbj;
                                break;
                            }
                        }
                        double subjectAvg = chosenSubject.getAverage();
                        if (subjectAvg > 95) {
                            numOfDays = 1;
                            numOfHours = 3;
                        } else if (subjectAvg > 90) {
                            numOfDays = 2;
                            numOfHours = 2;
                        } else if (subjectAvg > 80) {
                            numOfDays = 3;
                            numOfHours = 2;
                        } else if (subjectAvg > 70) {
                            numOfDays = 4;
                            numOfHours = 3;
                        } else if (subjectAvg > 60) {
                            numOfDays = 5;
                            numOfHours = 2;
                        } else {
                            numOfDays = 7;
                            numOfHours = 1;
                        }
                    } else {
                        numOfDays = Integer.parseInt(arrayAdapterNumOfDays.getItem(numOfDaysCurrentPosition).toString());
                        numOfHours = Integer.parseInt(arrAdapterStudyHours.getItem(studyHoursCurrentPosition).toString());
                    }
                    //TODO undo display code: uncomment the calendar code below, and delete the new instance.

                    //Calendar calendar = myCalendar;

                    Calendar calendar = Calendar.getInstance();

                    Intent intent = new Intent(context, NotificationReceiver.class);
                    intent.setAction("com.example.idanp.project.pages.reminders.MY_NOTIFICATION_MESSAGE");
                    intent.putExtra("numOfDays", numOfDays);
                    intent.putExtra("numOfHours", numOfHours);
                    intent.putExtra("subjectName", chosenSubjectName);
                    intent.putExtra("dayOfTest", calendar.get(Calendar.DAY_OF_MONTH));
                    intent.putExtra("monthOfTest", calendar.get(Calendar.MONTH));

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    //TODO undo display code: uncomment the code below and delete the seconds add

                    calendar.setLenient(true);
                    //calendar.add(Calendar.DAY_OF_MONTH, -numOfDays);
                    //calendar.set(Calendar.HOUR_OF_DAY, 8);
                    //calendar.set(Calendar.MINUTE,0);
                    //calendar.set(Calendar.SECOND,0);
                    calendar.add(Calendar.SECOND, 10);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    //TODO undo display code: change interval to 2 hours
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5000, pendingIntent);
                    Intent intent2 = new Intent(context, HomePage.class);
                    startActivity(intent2);
                }
                else Toast.makeText(context, "Please enter a date...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.equals(spSubjects))
            subjectCurrentPosition = position;
        else if (adapterView.equals(spStudyHours))
            studyHoursCurrentPosition = position;
        else if (adapterView.equals(spNumOfDays))
            numOfDaysCurrentPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private int chooseReminderProtocol() {
        if (numOfDaysCurrentPosition == 0 && studyHoursCurrentPosition == 0) {
            return AUTOMATIC_PROTOCOL;
        } else return CUSTOM_PROTOCOL;
    }

}
