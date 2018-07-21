package com.example.sj_sc.scheduling;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Term_Details_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ADD_COURSES_REQUEST_CODE = 1002;
    private static final int EDIT_TERM_REQUEST_CODE = 2001;
    private static final int COURSE_DETAILS_REQUEST_CODE = 3000;

    private Uri uriTerm;

    private CursorAdapter cursorAdapter;
    private TextView titleText2;
    private TextView startText2;
    private TextView endText2;
    private String whereClauseCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term__details_);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.term);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeFields();
        displayTerm();
        displayCourses();

        getLoaderManager().initLoader(1, null, this);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initializeFields() {
        titleText2 = findViewById(R.id.titleText2);
        startText2 = findViewById(R.id.startDateText2);
        endText2 = findViewById(R.id.endDateText2);
    }

    private void displayCourses() {
        //display courses on current list
        whereClauseCourse = DBOpenHelper.COURSE_TERM_ID + "=" + uriTerm.getLastPathSegment();
        String [] from = {DBOpenHelper.COURSE_TITLE};
        int [] to = {R.id.coursesTitleText};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.term_courselist_item,
                null, from, to, 0);

        TextView textView = new TextView(this);
        textView.setText(R.string.course_list);
        textView.setGravity(Gravity.START);
        textView.setTextSize(22);

        ListView lV = findViewById(R.id.coursesListView);
        lV.addHeaderView(textView);
        lV.setAdapter(cursorAdapter);

        lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent courseDetailsScreen = new Intent(Term_Details_Activity.this,Course_Details_Activity.class);
                Uri uri = Uri.parse(Provider.COURSES_URI + "/" + id);
                courseDetailsScreen.putExtra(Provider.COURSES_ITEM_TYPE, uri);
                startActivityForResult(courseDetailsScreen, COURSE_DETAILS_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_term_details, menu);
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
            case R.id.action_edit_term:
                editTerm();
                break;
            case R.id.action_delete_term:
                deleteTerm();
                break;
            case R.id.action_add_course:
                addCourse();
                break;
            case R.id.action_delete_term_courses:
                removeCourses();
                break;
        }
        return true;
    }

    private void displayTerm() {
        //get term from previous activity and display
        Intent intent = getIntent();
        uriTerm = intent.getParcelableExtra(Provider.TERMS_ITEM_TYPE);
        String whereClauseTerm = DBOpenHelper.TERM_ID + "=" + uriTerm.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriTerm, DBOpenHelper.TERMS_COLUMNS, whereClauseTerm,
                null, null);
        if(cursor != null ){
            cursor.moveToFirst();
            titleText2.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE)));
            startText2.setText(getString(R.string.start_date) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START_DATE)));
            endText2.setText(getString(R.string.end_date) + cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END_DATE)));

            cursor.close();
        }

    }

    private void loaderRestart() {
        getLoaderManager().restartLoader(1, null, this);
    }


    private void deleteTerm() {
        final Term term = Term.TERMS_MAP.get(Integer.parseInt(uriTerm.getLastPathSegment()));
        if(!term.getCOURSES_MAP().isEmpty()){
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.cannot_delete))
                    .setNeutralButton(getString(android.R.string.ok), dialogClickListener)
                    .show();
        }else{


            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {

                                String whereClause = DBOpenHelper.TERM_ID + "=" + term.getTERM_ID();
                                getContentResolver().delete(Provider.TERMS_URI, whereClause, null);
                                Term.TERMS_MAP.remove(term.getTERM_ID());
                                Toast.makeText(Term_Details_Activity.this,
                                        getString(R.string.term_deleted),
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();

                            }
                        }
                    };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.are_you_sure4))
                    .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                    .show();
        }
    }


    private void editTerm() {
        Intent editTermScreen = new Intent(this, Edit_Term_Activity.class);
        Uri uri = Uri.parse(Provider.TERMS_URI + "/" + Integer.parseInt(uriTerm.getLastPathSegment()));
        editTermScreen.putExtra(Provider.TERMS_ITEM_TYPE, uri);
        startActivityForResult(editTermScreen, EDIT_TERM_REQUEST_CODE);
    }

    private void addCourse() {
        Intent addCourseScreen = new Intent(Term_Details_Activity.this, Add_Courses_Activity.class);
        Uri uri = Uri.parse(Provider.TERMS_URI + "/" + Integer.parseInt(uriTerm.getLastPathSegment()));
        addCourseScreen.putExtra(Provider.TERMS_ITEM_TYPE, uri);
        startActivityForResult(addCourseScreen, ADD_COURSES_REQUEST_CODE);
    }

    private void removeCourses() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {
                                for(Course c: Course.COURSES_MAP.values()){
                                    if(c.getCourseTermId() == Integer.parseInt(uriTerm.getLastPathSegment())){
                                        String whereClause = DBOpenHelper.COURSE_TERM_ID + "=" + uriTerm.getLastPathSegment();
                                        ContentValues values = new ContentValues();
                                        values.put(DBOpenHelper.COURSE_TERM_ID, -1);
                                        getContentResolver().update(Provider.COURSES_URI, values, whereClause, null);
                                        c.setCourseTermId(-1);
                                        Term.TERMS_MAP.get(Integer.parseInt(uriTerm.getLastPathSegment())).getCOURSES_MAP().clear();
                                    }
                                }
                                loaderRestart();
                                Toast.makeText(Term_Details_Activity.this,
                                        getString(R.string.all_courses_removed),
                                        Toast.LENGTH_SHORT).show();
                            }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure2))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Provider.COURSES_URI, null, whereClauseCourse,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_COURSES_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }else if(requestCode == EDIT_TERM_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
            displayTerm();
        }else if(requestCode == COURSE_DETAILS_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }
    }
}
