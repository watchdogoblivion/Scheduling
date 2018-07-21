package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Create_Assessments_Activity extends AppCompatActivity {


    private Spinner spinnerTitle;
    private EditText codeEditor;
    private EditText monthEditor;
    private EditText dayEditor;
    private EditText yearEditor;
    private Spinner spinnerAlert;
    private Spinner spinnerStatus;
    private EditText descriptionEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__assessments_);
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
        spinnerTitle = findViewById(R.id.spinner12);
        String[] status = new String[]{"Objective Assessment", "Performance Assessment"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status);
        spinnerTitle.setAdapter(adapter);

        spinnerAlert = findViewById(R.id.spinnerAlert33);
        String[] status2 = new String[]{"Disabled", "Enabled"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status2);
        spinnerAlert.setAdapter(adapter2);

        spinnerStatus = findViewById(R.id.spinner123);
        String[] status3 = new String[]{"Not Attempted", "Pass", "Fail"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status3);
        spinnerStatus.setAdapter(adapter3);

        codeEditor = findViewById(R.id.assessmentCodeEditText);
        monthEditor =  findViewById(R.id.goalMonth);
        dayEditor =  findViewById(R.id.goalDay);
        yearEditor =  findViewById(R.id.goalYear);
        descriptionEditor =  findViewById(R.id.assessmentEditText);
    }

    public void save(View view) {

        String spinTitle = spinnerTitle.getSelectedItem().toString().trim();
        String code = codeEditor.getText().toString().trim();
        String gME = monthEditor.getText().toString().trim();
        String gDE = dayEditor.getText().toString().trim();
        String gYE = yearEditor.getText().toString().trim();
        String spinAlert = spinnerAlert.getSelectedItem().toString().trim();
        String spinStatus = spinnerStatus.getSelectedItem().toString().trim();
        String description = descriptionEditor.getText().toString().trim();

        try{

            Calendar calGoal = Calendar.getInstance();
            int startMonth = Integer.parseInt(gME) -1;
            int startDay = Integer.parseInt(gDE);
            int startYear = Integer.parseInt(gYE);
            calGoal.set(startYear, startMonth,startDay);

            Assessment assessment = new Assessment(spinTitle, code, startMonth, startDay, startYear, spinStatus, description);
            assessment.setNotification(spinAlert);
            if(spinAlert.equalsIgnoreCase("Enabled")){
                AlertHandler.enableNotifications(assessment, calGoal, this);
            }
            insertAssessment(assessment);
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

    private void insertAssessment(Assessment assessment) {
        Intent intent = getIntent();
        Uri courseUri = intent.getParcelableExtra(Provider.ASSESSMENT_ITEM_TYPE);
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_COURSE_ID, Integer.parseInt(courseUri.getLastPathSegment()));
        values.put(DBOpenHelper.ASSESSMENT_TITLE, assessment.getTitle());
        values.put(DBOpenHelper.ASSESSMENT_CODE, assessment.getCode());
        values.put(DBOpenHelper.ASSESSMENT_GOAL_DATE, assessment.getGoalDate());
        values.put(DBOpenHelper.ASSESSMENT_NOTIFICATION, assessment.getNotification());
        values.put(DBOpenHelper.ASSESSMENT_STATUS, assessment.getStatus());
        values.put(DBOpenHelper.ASSESSMENT_DESCRIPTION, assessment.getDescription());

        Uri assessmentsUri = getContentResolver().insert(Provider.ASSESSMENTS_URI, values);

        if (assessmentsUri != null) {
            assessment.setAssessmentId(Integer.parseInt(assessmentsUri.getLastPathSegment()));
        }
        Assessment.ASSESSMENTS_LIST.add(assessment);
        for(Course c: Course.COURSES_MAP.values()){
            if(c.getCourseId() == Integer.parseInt(courseUri.getLastPathSegment())){
                c.addAssessment(assessment);
            }
        }
        setResult(RESULT_OK);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
