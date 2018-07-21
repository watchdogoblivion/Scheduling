package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Course_Details_Activity extends AppCompatActivity {

    private static final int EDIT_COURSE_REQUEST_CODE = 6000;
    private Uri uriCourse;
    private TextView courseView;
    private TextView startDateView;
    private TextView endDateView;
    private TextView status;
    private TextView mentorName;
    private TextView mentorEmail;
    private TextView mentorPhoneNumber;
    private TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course__details_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.course2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeFields();
        displayCourse();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_course_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                onBackPressed();
                break;
            case R.id.action_edit_course:
                editCourse();
                break;
            case R.id.action_delete_course:
                deleteCourse();
                break;
            case R.id.action_open_notes:
                launchNotes();
                break;
            case R.id.action_open_assessments:
                launchAssessments();
                break;
        }
        return true;
    }

    private void launchAssessments() {
        Intent assessmentsScreen = new Intent(this, Assessment_Activity.class);
        Uri uri = Uri.parse(Provider.ASSESSMENTS_URI + "/" + Integer.parseInt(uriCourse.getLastPathSegment()));
        assessmentsScreen.putExtra(Provider.ASSESSMENT_ITEM_TYPE, uri);
        startActivity(assessmentsScreen);
    }

    private void launchNotes() {
        Intent notesScreen = new Intent(this, Course_Notes_Activity.class);
        Uri uri = Uri.parse(Provider.COURSE_NOTES_URI + "/" + Integer.parseInt(uriCourse.getLastPathSegment()));
        notesScreen.putExtra(Provider.COURSE_NOTE_ITEM_TYPE, uri);
        startActivity(notesScreen);
    }

    private void deleteCourse() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            Course course = Course.COURSES_MAP.get(Integer.parseInt(uriCourse.getLastPathSegment()));
                            String whereClause = DBOpenHelper.COURSE_ID + "=" + course.getCourseId();
                            getContentResolver().delete(Provider.COURSES_URI, whereClause, null);
                            for(CourseNotes cN: CourseNotes.COURSE_NOTES_LIST){
                                if(cN.getNotes_courseId() == course.getCourseId()){
                                    CourseNotes.COURSE_NOTES_LIST.remove(cN);
                                }
                            }
                            Course.COURSES_MAP.remove(course.getCourseId());
                            Toast.makeText(Course_Details_Activity.this,
                                    getString(R.string.course_deleted),
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();

                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure5))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void editCourse() {
        Intent editCourseScreen = new Intent(this, Edit_Course_Activity.class);
        Uri uri = Uri.parse(Provider.COURSES_URI + "/" + Integer.parseInt(uriCourse.getLastPathSegment()));
        editCourseScreen.putExtra(Provider.COURSES_ITEM_TYPE, uri);
        startActivityForResult(editCourseScreen, EDIT_COURSE_REQUEST_CODE);
    }

    private void initializeFields() {
        courseView = findViewById(R.id.courseViewC2);
        startDateView = findViewById(R.id.startDateViewC2);
        endDateView = findViewById(R.id.endDateC2);
        status = findViewById(R.id.status2);
        mentorName = findViewById(R.id.mentorName2);
        mentorEmail = findViewById(R.id.mentorEmail2);
        mentorPhoneNumber = findViewById(R.id.mentorPhoneNumber2);
        descriptionView = findViewById(R.id.descriptionView2);
    }

    private void displayCourse() {
        //get course from previous activity and display
        Intent intent = getIntent();
        uriCourse = intent.getParcelableExtra(Provider.COURSES_ITEM_TYPE);
        String whereClauseCourse = DBOpenHelper.COURSE_ID + "=" + uriCourse.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriCourse, DBOpenHelper.COURSES_COLUMNS, whereClauseCourse,
                null, null);
        if (cursor != null){
            cursor.moveToFirst();
            courseView.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE)));
            startDateView.setText(getString(R.string.start_date) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START_DATE)));
            endDateView.setText(getString(R.string.end_date) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END_DATE)));
            status.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.STATUS)));
            mentorName.setText(getString(R.string.mentor_name) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME)));
            mentorEmail.setText(getString(R.string.mentor_email) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL)));
            mentorPhoneNumber.setText(getString(R.string.mentor_phoneNumber) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE_NUMBER)));
            descriptionView.setText(getString(R.string.course_description) + "\n" + cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION)));
            cursor.close();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT_COURSE_REQUEST_CODE && resultCode == RESULT_OK){
            displayCourse();
        }
    }

}
