package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Assessment_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CREATE_ASSESSMENT_REQUEST_CODE = 9000;
    private static final int ASSESSMENT_DETAILS_REQUEST_CODE = 5555;
    private Uri uriCourse;
    private CursorAdapter cursorAdapter;
    private String whereClauseAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayAssessments();

        getLoaderManager().initLoader(4, null, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void displayAssessments() {
        //display courses notes on list
        Intent intent = getIntent();
        uriCourse = intent.getParcelableExtra(Provider.ASSESSMENT_ITEM_TYPE);
        whereClauseAssessment = DBOpenHelper.ASSESSMENT_COURSE_ID + "=" + uriCourse.getLastPathSegment();
        String [] from = {DBOpenHelper.ASSESSMENT_TITLE, DBOpenHelper.ASSESSMENT_CODE};
        int [] to = {R.id.assessmentsTextView, R.id.assessmentCodeTextView2};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.assessments_list_item,
                null, from, to, 0);

        TextView textView = new TextView(this);
        textView.setText(R.string.assessments_list);
        textView.setGravity(Gravity.START);
        textView.setTextSize(22);

        ListView lV = findViewById(R.id.assessmentsListView);
        lV.addHeaderView(textView);
        lV.setAdapter(cursorAdapter);
        lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent assessmentDetailsScreen = new Intent(Assessment_Activity.this,Assessment_Details_Activity.class);
                Uri uri = Uri.parse(Provider.ASSESSMENTS_URI + "/" + id);
                assessmentDetailsScreen.putExtra(Provider.ASSESSMENT_ITEM_TYPE, uri);
                startActivityForResult(assessmentDetailsScreen, ASSESSMENT_DETAILS_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_assessments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_create_assessment:
                createAssessment();
                break;
            case R.id.action_delete_all_assessments:
                deleteAssessments();
                break;
        }
        return true;
    }

    private void deleteAssessments() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(Provider.ASSESSMENTS_URI, whereClauseAssessment, null);
                            for(Assessment a: Assessment.ASSESSMENTS_LIST){
                                if(a.getAssessment_courseId() == Integer.parseInt(uriCourse.getLastPathSegment())){
                                    Assessment.ASSESSMENTS_LIST.remove(a);
                                }
                            }
                            for(Course c: Course.COURSES_MAP.values()){
                                if(c.getCourseId() == Integer.parseInt(uriCourse.getLastPathSegment())){
                                    c.getASSESSMENTS_MAP().clear();
                                }
                            }
                            loaderRestart();
                            Toast.makeText(Assessment_Activity.this,
                                    getString(R.string.all_assessments_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure8))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void createAssessment() {
        Intent createAssessmentsScreen = new Intent(this, Create_Assessments_Activity.class);
        Uri uri = Uri.parse(Provider.ASSESSMENTS_URI + "/" + Integer.parseInt(uriCourse.getLastPathSegment()));
        createAssessmentsScreen.putExtra(Provider.ASSESSMENT_ITEM_TYPE, uri);
        startActivityForResult(createAssessmentsScreen, CREATE_ASSESSMENT_REQUEST_CODE);
    }

    private void loaderRestart() {
        getLoaderManager().restartLoader(1, null, this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_ASSESSMENT_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }else if(requestCode == ASSESSMENT_DETAILS_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Provider.ASSESSMENTS_URI, null, whereClauseAssessment,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
