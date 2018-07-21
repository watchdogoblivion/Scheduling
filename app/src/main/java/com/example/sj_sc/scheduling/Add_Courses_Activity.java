package com.example.sj_sc.scheduling;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;

public class Add_Courses_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CREATE_COURSE_REQUEST_CODE = 1003;
    private CursorAdapter cursorAdapter;
    private ListView lv;
    private final Map<Integer, Course> UPDATE_MAP = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__courses_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String [] from = {DBOpenHelper.COURSE_TITLE};
        int [] to = {R.id.checkBox};

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.add_courselist_item,
                null, from, to, 0);


        TextView textView = new TextView(this);
        textView.setText(R.string.add_aCourse_list);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(22);
        lv = findViewById(R.id.coursesListView2);
        lv.addHeaderView(textView);
        lv.setAdapter(cursorAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {


                RelativeLayout cL = (RelativeLayout) lv.getChildAt(position);
                CheckBox cB = (CheckBox) cL.getChildAt(0);
                if(cB.isChecked()){
                    cB.setChecked(false);
                }else{
                    cB.setChecked(true);
                }

                int color = Color.TRANSPARENT;
                Drawable background = view.getBackground();
                if (background instanceof ColorDrawable)
                    color = ((ColorDrawable) background).getColor();

                if(color == Color.TRANSPARENT){
                    view.setBackgroundColor(Color.GREEN);
                    Course retrieved = Course.COURSES_MAP.get((int)id);
                    UPDATE_MAP.put(retrieved.getCourseId(), retrieved);
                }else{
                    view.setBackgroundColor(Color.TRANSPARENT);
                    UPDATE_MAP.remove((int)id);
                }
            }
        });


        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void loaderRestart() {
        getLoaderManager().restartLoader(1, null, this);
    }

    public void createCourse(View view) {
        Intent createCourseScreen = new Intent(this, Create_Course_Activity.class);
        startActivityForResult(createCourseScreen, CREATE_COURSE_REQUEST_CODE);
    }

    public void save(View view) {
        Intent intent = getIntent();
        Uri termUri = intent.getParcelableExtra(Provider.TERMS_ITEM_TYPE);
        for(Course course: UPDATE_MAP.values()){
            course.setCourseTermId(Integer.parseInt(termUri.getLastPathSegment()));
            updateCourse(course);
            for(Term term: Term.TERMS_MAP.values()){
                if(course.getCourseTermId() == term.getTERM_ID()){
                    term.addCourse(course);
                }
            }
        }


        setResult(RESULT_OK);
        finish();
    }

    private void updateCourse(Course course) {
        String whereClauseC = DBOpenHelper.COURSE_ID + "=" + course.getCourseId();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, course.getCourseTermId());
        getContentResolver().update(Provider.COURSES_URI, values, whereClauseC, null);
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

    private void finishEditing(){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {

        finishEditing();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_COURSE_REQUEST_CODE && resultCode == RESULT_OK){
            loaderRestart();
        }
    }

}
