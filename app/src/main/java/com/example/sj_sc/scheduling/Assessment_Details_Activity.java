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

public class Assessment_Details_Activity extends AppCompatActivity {


    private static final int EDIT_ASSESSMENTS_REQUEST_CODE = 8000;
    private Uri uriAssessment;
    private TextView titleView;
    private TextView code;
    private TextView goalDate;
    private TextView status;
    private TextView description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment__details_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeFields();
        displayAssessment();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initializeFields() {
        titleView = findViewById(R.id.titleTextView);
        code = findViewById(R.id.assessmentCodeTextView);
        goalDate = findViewById(R.id.goalDateTextView);
        status = findViewById(R.id.statusTextView);
        description = findViewById(R.id.descriptionTextView);


    }

    private void displayAssessment() {
        //get course from previous activity and display
        Intent intent = getIntent();
        uriAssessment = intent.getParcelableExtra(Provider.ASSESSMENT_ITEM_TYPE);
        String whereClauseAssessments = DBOpenHelper.ASSESSMENT_ID + "=" + uriAssessment.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriAssessment, DBOpenHelper.ASSESSMENTS_COLUMNS, whereClauseAssessments,
                null, null);
        if (cursor != null){
            cursor.moveToFirst();
            titleView.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE)));
            code.setText(getString(R.string.assess_code) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_CODE)));
            goalDate.setText(getString(R.string.goal_date) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_GOAL_DATE)));
            status.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_STATUS)));
            description.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DESCRIPTION)));
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_assessment_details, menu);
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
            case R.id.action_edit_assessment:
                editAssessment();
                break;
            case R.id.action_delete_assessment:
                deleteAssessment();
                break;
        }
        return true;
    }

    private void deleteAssessment() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            String whereClause = DBOpenHelper.ASSESSMENT_ID + "=" + Integer.parseInt(uriAssessment.getLastPathSegment());
                            getContentResolver().delete(Provider.ASSESSMENTS_URI, whereClause, null);
                            for(Assessment a: Assessment.ASSESSMENTS_LIST){
                                if(a.getAssessmentId() == Integer.parseInt(uriAssessment.getLastPathSegment())){
                                    Assessment.ASSESSMENTS_LIST.remove(a);
                                }
                            }
                            Toast.makeText(Assessment_Details_Activity.this,
                                    getString(R.string.assessment_deleted),
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();

                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure9))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void editAssessment() {
        Intent editAssessmentScreen = new Intent(this, Edit_Assessment_Activity.class);
        Uri uri = Uri.parse(Provider.ASSESSMENTS_URI + "/" + Integer.parseInt(uriAssessment.getLastPathSegment()));
        editAssessmentScreen.putExtra(Provider.ASSESSMENT_ITEM_TYPE, uri);
        startActivityForResult(editAssessmentScreen, EDIT_ASSESSMENTS_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT_ASSESSMENTS_REQUEST_CODE && resultCode == RESULT_OK){
            displayAssessment();
        }
    }

}
