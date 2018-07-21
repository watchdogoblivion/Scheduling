package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Courses_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CREATE_COURSE_REQUEST_CODE = 1004;
    private static final int COURSE_DETAILS_REQUEST_CODE = 5000;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.courses);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayCourses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_create_course:
                createCourse();
                break;
            case R.id.action_delete_all_courses:
                deleteAllCourses();
                break;
        }
        return true;
    }

    private void displayCourses() {
        String [] from = {DBOpenHelper.COURSE_TITLE};
        int [] to = {R.id.coursesTitleText3};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.courselist_item,
                null, from, to, 0);
        ListView lV = findViewById(R.id.coursesListView3);
        lV.setAdapter(cursorAdapter);
        lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent courseDetailsScreen = new Intent(Courses_Activity.this,Course_Details_Activity.class);
                Uri uri = Uri.parse(Provider.COURSES_URI + "/" + id);
                courseDetailsScreen.putExtra(Provider.COURSES_ITEM_TYPE, uri);
                startActivityForResult(courseDetailsScreen, COURSE_DETAILS_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(1, null, this);
    }

    private void loaderRestart() {
        getLoaderManager().restartLoader(1, null, this);
    }

    private void createCourse() {
        Intent intent = new Intent(Courses_Activity.this, Create_Course_Activity.class);
        startActivityForResult(intent, CREATE_COURSE_REQUEST_CODE);
    }

    private void deleteAllCourses() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(Provider.COURSES_URI, null, null);
                            CourseNotes.COURSE_NOTES_LIST.clear();
                            Course.COURSES_MAP.clear();
                            loaderRestart();
                            Toast.makeText(Courses_Activity.this,
                                    getString(R.string.all_courses_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure3))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_COURSE_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }else if(requestCode == COURSE_DETAILS_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Provider.COURSES_URI, null, null,
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
