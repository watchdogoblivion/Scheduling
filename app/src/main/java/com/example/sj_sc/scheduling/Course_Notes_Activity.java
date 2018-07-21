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

public class Course_Notes_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CREATE_COURSE_NOTE_REQUEST_CODE = 9000;
    private static final int COURSE_NOTES_DETAILS_REQUEST_CODE = 5555;
    private Uri uriCourse;
    private CursorAdapter cursorAdapter;
    private String whereClauseCourseNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_notes_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Notes");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        displayCourseNotes();

        getLoaderManager().initLoader(4, null, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void displayCourseNotes() {
        //display courses notes on list
        Intent intent = getIntent();
        uriCourse = intent.getParcelableExtra(Provider.COURSE_NOTE_ITEM_TYPE);
        whereClauseCourseNote = DBOpenHelper.NOTE_COURSE_ID + "=" + uriCourse.getLastPathSegment();
        String [] from = {DBOpenHelper.COURSE_NOTE_TITLE};
        int [] to = {R.id.courseNotesTextView};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.coursenotes_list_item,
                null, from, to, 0);

        TextView textView = new TextView(this);
        textView.setText(R.string.notes_list);
        textView.setGravity(Gravity.START);
        textView.setTextSize(22);

        ListView lV = findViewById(R.id.courseNotesListView);
        lV.addHeaderView(textView);
        lV.setAdapter(cursorAdapter);
        lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent courseNotesDetailsScreen = new Intent(Course_Notes_Activity.this,Course_Notes_Details_Activity.class);
                Uri uri = Uri.parse(Provider.COURSE_NOTES_URI + "/" + id);
                courseNotesDetailsScreen.putExtra(Provider.COURSE_NOTE_ITEM_TYPE, uri);
                startActivityForResult(courseNotesDetailsScreen, COURSE_NOTES_DETAILS_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_course_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_create_a_note:
                createNote();
                break;
            case R.id.action_delete_all_notes:
                deleteAllNotes();
                break;
        }
        return true;
    }

    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(Provider.COURSE_NOTES_URI, whereClauseCourseNote, null);
                            for(CourseNotes cN: CourseNotes.COURSE_NOTES_LIST){
                                if(cN.getNotes_courseId() == Integer.parseInt(uriCourse.getLastPathSegment())){
                                    CourseNotes.COURSE_NOTES_LIST.remove(cN);
                                }
                            }
                            for(Course c: Course.COURSES_MAP.values()){
                                if(c.getCourseId() == Integer.parseInt(uriCourse.getLastPathSegment())){
                                    c.getCourseNotesMap().clear();
                                }
                            }
                            loaderRestart();
                            Toast.makeText(Course_Notes_Activity.this,
                                    getString(R.string.all_notes_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure6))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void createNote() {
        Intent createNotesScreen = new Intent(this, Create_CourseNotes_Activity.class);
        Uri uri = Uri.parse(Provider.COURSE_NOTES_URI + "/" + Integer.parseInt(uriCourse.getLastPathSegment()));
        createNotesScreen.putExtra(Provider.COURSE_NOTE_ITEM_TYPE, uri);
        startActivityForResult(createNotesScreen, CREATE_COURSE_NOTE_REQUEST_CODE);
    }


    private void loaderRestart() {
        getLoaderManager().restartLoader(1, null, this);
    }
   


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_COURSE_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }else if(requestCode == COURSE_NOTES_DETAILS_REQUEST_CODE && resultCode == RESULT_OK){
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
        return new CursorLoader(this, Provider.COURSE_NOTES_URI, null, whereClauseCourseNote,
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