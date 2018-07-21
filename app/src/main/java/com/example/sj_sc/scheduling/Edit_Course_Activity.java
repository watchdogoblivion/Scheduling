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

public class Edit_Course_Activity extends AppCompatActivity {

    private Uri uriCourse;

    private EditText editTitle;
    private EditText startMonth;
    private EditText startDay;
    private EditText startYear;
    private EditText endMonth;
    private EditText endDay;
    private EditText endYear;
    private Spinner spinnerStatus;
    private Spinner spinnerAlert;
    private EditText mentorName;
    private EditText mentorEmail;
    private EditText mentorPhone;
    private EditText description;

    private Calendar calStart;
    private Calendar calEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__course_);

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
        spinnerStatus = findViewById(R.id.spinner3);
        String[] status = new String[]{"In progress", "In-Complete", "Complete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status);
        spinnerStatus.setAdapter(adapter);

        spinnerAlert = findViewById(R.id.spinnerAlert2);
        String[] status2 = new String[]{"Disabled", "Enabled"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status2);
        spinnerAlert.setAdapter(adapter2);

        editTitle = findViewById(R.id.editTitleC3);
        startMonth = findViewById(R.id.startMonthC3);
        startDay = findViewById(R.id.startDayC3);
        startYear = findViewById(R.id.startYearC3);
        endMonth = findViewById(R.id.endMonthC3);
        endDay = findViewById(R.id.endDayC3);
        endYear = findViewById(R.id.endYearC3);
        mentorName = findViewById(R.id.editMentor3);
        mentorEmail = findViewById(R.id.mentorEmailEditor3);
        mentorPhone = findViewById(R.id.mentorPhoneEditor3);
        description = findViewById(R.id.descriptionEditor3);
    }

    private void setTexts() {
        Intent intent = getIntent();
        uriCourse = intent.getParcelableExtra(Provider.COURSES_ITEM_TYPE);
        String whereClauseCourse = DBOpenHelper.COURSE_ID + "=" + uriCourse.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriCourse, DBOpenHelper.COURSES_COLUMNS, whereClauseCourse,
                null, null);

        if(cursor != null){
            cursor.moveToFirst();

            calStart = Calendar.getInstance();
            calEnd = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy", MainActivity.LOCALE);
            String startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END_DATE));

            try {
                calStart.setTime(simpleDateFormat.parse(startDate));
                calEnd.setTime(simpleDateFormat.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            editTitle.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE)));
            startMonth.setText(calStart.get(Calendar.MONTH) + 1 + "");
            startDay.setText(calStart.get(Calendar.DAY_OF_MONTH) + "");
            startYear.setText(calStart.get(Calendar.YEAR) + "");
            endMonth.setText(calEnd.get(Calendar.MONTH) + 1 + "");
            endDay.setText(calEnd.get(Calendar.DAY_OF_MONTH) + "");
            endYear.setText(calEnd.get(Calendar.YEAR) + "");
            setSpinner(spinnerStatus, cursor.getString(cursor.getColumnIndex(DBOpenHelper.STATUS)));
            setSpinner(spinnerAlert, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTIFICATION)));
            mentorName.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME)));
            mentorEmail.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL)));
            mentorPhone.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE_NUMBER)));
            description.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION)));

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

    public void save(View view) {
        String nE = editTitle.getText().toString().trim();
        String sME = startMonth.getText().toString().trim();
        String sDE = startDay.getText().toString().trim();
        String sYE = startYear.getText().toString().trim();
        String eME = endMonth.getText().toString().trim();
        String eDE = endDay.getText().toString().trim();
        String eYE = endYear.getText().toString().trim();
        String spinStatus = spinnerStatus.getSelectedItem().toString();
        String spinAlert = spinnerAlert.getSelectedItem().toString();
        String mNE = mentorName.getText().toString().trim();
        String mEE = mentorEmail.getText().toString().trim();
        String mPE = mentorPhone.getText().toString().trim();
        String dE = description.getText().toString().trim();

        try{
            Course editedCourse = Course.COURSES_MAP.get(Integer.parseInt(uriCourse.getLastPathSegment()));
            editedCourse.setTitle(nE);
            editedCourse.setStartDate(Integer.parseInt(sME) -1, Integer.parseInt(sDE), Integer.parseInt(sYE));
            editedCourse.setEndDate(Integer.parseInt(eME) -1, Integer.parseInt(eDE), Integer.parseInt(eYE));
            editedCourse.setStatus(spinStatus);
            editedCourse.setMentorName(mNE);
            editedCourse.setMentorEmail(mEE);
            editedCourse.setMentorPhoneNumber(mPE);
            editedCourse.setCourseDescription(dE);
            editedCourse.setNotification(spinAlert);
            if(spinAlert.equalsIgnoreCase("Enabled")){
                AlertHandler.enableNotifications(editedCourse, calStart, calEnd, this);
            }
            updateCourse(editedCourse);
            finish();

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

    private void updateCourse(Course editedCourse) {
        String whereClause = DBOpenHelper.COURSE_ID + "=" + editedCourse.getCourseId();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, editedCourse.getTitle());
        values.put(DBOpenHelper.TERM_START_DATE, editedCourse.getStartDate());
        values.put(DBOpenHelper.TERM_END_DATE, editedCourse.getEndDate());
        values.put(DBOpenHelper.STATUS, editedCourse.getStatus());
        values.put(DBOpenHelper.MENTOR_NAME, editedCourse.getMentorName());
        values.put(DBOpenHelper.MENTOR_EMAIL, editedCourse.getMentorEmail());
        values.put(DBOpenHelper.MENTOR_PHONE_NUMBER, editedCourse.getMentorPhoneNumber());
        values.put(DBOpenHelper.COURSE_DESCRIPTION, editedCourse.getCourseDescription());
        values.put(DBOpenHelper.COURSE_NOTIFICATION, editedCourse.getNotification());

        getContentResolver().update(Provider.COURSES_URI, values, whereClause, null);
        setResult(RESULT_OK);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
