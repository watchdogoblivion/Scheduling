package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Edit_Assessment_Activity extends AppCompatActivity {


    private Uri uriAssessment;
    private String whereClauseAssessment;

    private Spinner spinnerTitle;
    private EditText editCode;
    private EditText goalMonth;
    private EditText goalDay;
    private EditText goalYear;
    private Spinner spinnerAlert;
    private Spinner spinnerStatus;
    private EditText editDescription;

    private Calendar calGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__assessment_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeFields();
        setTexts();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initializeFields() {
        spinnerTitle = findViewById(R.id.spinner22);
        String[] status = new String[]{"Objective Assessment", "Performance Assessment"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status);
        spinnerTitle.setAdapter(adapter);

        spinnerAlert= findViewById(R.id.spinnerAlert4);
        String[] status2 = new String[]{"Disabled", "Enabled"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status2);
        spinnerAlert.setAdapter(adapter2);

        spinnerStatus = findViewById(R.id.spinner223);
        String[] status3 = new String[]{"Not Attempted", "Pass", "Fail"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status3);
        spinnerStatus.setAdapter(adapter3);

        editCode = findViewById(R.id.assessmentCodeEditText2);
        goalMonth = findViewById(R.id.goalMonth2);
        goalDay = findViewById(R.id.goalDay2);
        goalYear = findViewById(R.id.goalYear2);
        editDescription = findViewById(R.id.descriptionEditor4);

    }

    private void setTexts() {
        Intent intent = getIntent();
        uriAssessment = intent.getParcelableExtra(Provider.ASSESSMENT_ITEM_TYPE);
        whereClauseAssessment = DBOpenHelper.ASSESSMENT_ID + "=" + uriAssessment.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriAssessment, DBOpenHelper.ASSESSMENTS_COLUMNS, whereClauseAssessment,
                null, null);

        if(cursor != null){
            cursor.moveToFirst();

            calGoal = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy", MainActivity.LOCALE);
            String goalDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_GOAL_DATE));


            try {
                calGoal.setTime(simpleDateFormat.parse(goalDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            setSpinner(spinnerTitle, cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE)));
            goalMonth.setText(Integer.toString(calGoal.get(Calendar.MONTH) + 1));
            goalDay.setText(Integer.toString(calGoal.get(Calendar.DAY_OF_MONTH)));
            goalYear.setText(Integer.toString(calGoal.get(Calendar.YEAR)));
            setSpinner(spinnerAlert, cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTIFICATION)));
            setSpinner(spinnerStatus, cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_STATUS)));
            editCode.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_CODE)));
            editDescription.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DESCRIPTION)));

            cursor.close();
        }

    }

    private void setSpinner(Spinner spinner, String string)
    {
        for(int i= 0; i < spinner.getAdapter().getCount(); i++)
        {
            if(spinner.getAdapter().getItem(i).toString().contains(string))
            {
                spinner.setSelection(i);
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void save(View view) {

        String spinTitle = spinnerTitle.getSelectedItem().toString().trim();
        String code = editCode.getText().toString().trim();
        String gME = goalMonth.getText().toString().trim();
        String gDE = goalDay.getText().toString().trim();
        String gYE = goalYear.getText().toString().trim();
        String spinAlert = spinnerAlert.getSelectedItem().toString();
        String spinStatus = spinnerStatus.getSelectedItem().toString().trim();
        String description = editDescription.getText().toString().trim();

        try{

            for(Assessment a: Assessment.ASSESSMENTS_LIST){
                if(a.getAssessmentId() == Integer.parseInt(uriAssessment.getLastPathSegment())){
                    a.setTitle(spinTitle);
                    a.setCode(code);
                    a.setGoalDate(Integer.parseInt(gME) -1, Integer.parseInt(gDE), Integer.parseInt(gYE));
                    a.setNotification(spinAlert);
                    a.setStatus(spinStatus);
                    a.setDescription(description);
                    if(spinAlert.equalsIgnoreCase("Enabled")){
                        AlertHandler.enableNotifications(a, calGoal,this);
                    }
                    updateAssessment(a);
                    finish();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
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

    private void updateAssessment(Assessment a) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_TITLE, a.getTitle());
        values.put(DBOpenHelper.ASSESSMENT_CODE, a.getCode());
        values.put(DBOpenHelper.ASSESSMENT_STATUS, a.getStatus());
        values.put(DBOpenHelper.ASSESSMENT_GOAL_DATE, a.getGoalDate());
        values.put(DBOpenHelper.ASSESSMENT_NOTIFICATION, a.getNotification());
        values.put(DBOpenHelper.ASSESSMENT_DESCRIPTION, a.getDescription());

        getContentResolver().update(Provider.ASSESSMENTS_URI, values, whereClauseAssessment, null);
        setResult(RESULT_OK);
    }
}
