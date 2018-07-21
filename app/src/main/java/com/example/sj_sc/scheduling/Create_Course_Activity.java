package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

public class Create_Course_Activity extends AppCompatActivity {

    private EditText nameEditor;
    private EditText sMonthEditor;
    private EditText sDayEditor;
    private EditText sYearEditor;
    private EditText eMonthEditor;
    private EditText eDayEditor;
    private EditText eYearEditor;
    private EditText mNameEditor;
    private EditText mEmailEditor;
    private EditText mPhoneEditor;
    private EditText descriptionEditor;

    private Spinner spinnerStatus;
    private Spinner spinnerAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__course_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeFields();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initializeFields() {
        spinnerStatus = findViewById(R.id.spinner1);
        String[] status = new String[]{"In progress", "In-Complete", "Complete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status);
        spinnerStatus.setAdapter(adapter);

        spinnerAlert = findViewById(R.id.spinnerAlert);
        String[] status2 = new String[]{"Disabled", "Enabled"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status2);
        spinnerAlert.setAdapter(adapter2);

        nameEditor =  findViewById(R.id.editTitleC);
        sMonthEditor =  findViewById(R.id.startMonthC);
        sDayEditor =  findViewById(R.id.startDayC);
        sYearEditor =  findViewById(R.id.startYearC);
        eMonthEditor =  findViewById(R.id.endMonthC);
        eDayEditor =  findViewById(R.id.endDayC);
        eYearEditor =  findViewById(R.id.endYearC);
        mNameEditor =  findViewById(R.id.editMentor);
        mEmailEditor =  findViewById(R.id.mentorEmailEditor);
        mPhoneEditor =  findViewById(R.id.mentorPhoneEditor);
        descriptionEditor =  findViewById(R.id.descriptionEditor);
    }

    public void save(View view) {

        String nE = nameEditor.getText().toString().trim();
        String sME = sMonthEditor.getText().toString().trim();
        String sDE = sDayEditor.getText().toString().trim();
        String sYE = sYearEditor.getText().toString().trim();
        String eME = eMonthEditor.getText().toString().trim();
        String eDE = eDayEditor.getText().toString().trim();
        String eYE = eYearEditor.getText().toString().trim();
        String spin = spinnerStatus.getSelectedItem().toString();
        String alert = spinnerAlert.getSelectedItem().toString();
        String mNE = mNameEditor.getText().toString().trim();
        String mEE = mEmailEditor.getText().toString().trim();
        String mPE = mPhoneEditor.getText().toString().trim();
        String dE = descriptionEditor.getText().toString().trim();

        try{

            Calendar calStart = Calendar.getInstance();
            int startMonth = Integer.parseInt(sME) -1;
            int startDay = Integer.parseInt(sDE);
            int startYear = Integer.parseInt(sYE);
            calStart.set(startYear, startMonth,startDay);

            Calendar calEnd = Calendar.getInstance();
            int endMonth = Integer.parseInt(eME) -1;
            int endDay = Integer.parseInt(eDE);
            int endYear = Integer.parseInt(eYE);
            calEnd.set(endMonth, endDay,endYear);

            Course newCourse = new Course(nE, startMonth, startDay,
                    startYear, endMonth, endDay, endYear, spin, mNE, mEE, mPE, dE
            );
             newCourse.setNotification(alert);
             if(alert.equalsIgnoreCase("Enabled")){
                 AlertHandler.enableNotifications(newCourse, calStart, calEnd, this);
             }
            insertCourse(newCourse);
            finish();

        }catch(Exception e){
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.invalid_input))
                    .setNeutralButton(getString(android.R.string.ok), dialogClickListener)
                    .show();
        }
    }

    private void insertCourse(Course course){

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, course.getCourseTermId());
        values.put(DBOpenHelper.COURSE_TITLE, course.getTitle());
        values.put(DBOpenHelper.COURSE_START_DATE, course.getStartDate());
        values.put(DBOpenHelper.COURSE_END_DATE, course.getEndDate());
        values.put(DBOpenHelper.COURSE_DESCRIPTION, course.getCourseDescription());
        values.put(DBOpenHelper.STATUS, course.getStatus());
        values.put(DBOpenHelper.MENTOR_NAME, course.getMentorName());
        values.put(DBOpenHelper.MENTOR_PHONE_NUMBER, course.getMentorPhoneNumber());
        values.put(DBOpenHelper.MENTOR_EMAIL, course.getMentorEmail());
        values.put(DBOpenHelper.COURSE_NOTIFICATION, course.getNotification());

        Uri coursesUri = getContentResolver().insert(Provider.COURSES_URI, values);

        if (coursesUri != null) {
            course.setCourseId(Integer.parseInt(coursesUri.getLastPathSegment()));
        }
        Course.COURSES_MAP.put(course.getCourseId(), course);
        setResult(RESULT_OK);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




}
