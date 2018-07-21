package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Course_Notes_Details_Activity extends AppCompatActivity {

    private static final int EDIT_COURSE_NOTES_REQUEST_CODE = 6000;
    private Uri uriCourseNotes;
    private TextView titleView;
    private TextView textView;
    private ImageView imageView;
    private final static String EMAIL = "EMAIL@wgu.edu";
    private final static String WEB_URL = "https://www.gmail.com/";
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course__notes__details_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Note");

        initializeFields();
        displayNotes();

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] addresses = {EMAIL};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, titleView.getText().toString().trim());
                emailIntent.putExtra(Intent.EXTRA_TEXT, textView.getText().toString().trim());
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUri));
                if(emailIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(Intent.createChooser(emailIntent , "Send email..."));
                }else{
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_URL));
                    if(webIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(webIntent);
                    }
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initializeFields() {
        titleView = findViewById(R.id.courseNotesTextView2);
        textView = findViewById(R.id.courseNotesTextView3);
        imageView = findViewById(R.id.courseNotesImageView2);
    }

    private void displayNotes() {
        //get course from previous activity and display
        Intent intent = getIntent();
        uriCourseNotes = intent.getParcelableExtra(Provider.COURSE_NOTE_ITEM_TYPE);
        String whereClauseCourseNotes = DBOpenHelper.COURSE_NOTE_ID + "=" + uriCourseNotes.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriCourseNotes, DBOpenHelper.COURSE_NOTES_COLUMNS, whereClauseCourseNotes,
                null, null);
        if (cursor != null){
            cursor.moveToFirst();
            titleView.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TITLE)));
            textView.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TEXT)));
            imageUri = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_IMAGE_URI));
            imageView.setImageURI(Uri.parse(imageUri));
            cursor.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_coursenote_details, menu);
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
            case R.id.action_edit_note:
                editNote();
                break;
            case R.id.action_delete_note:
                deleteNote();
                break;
        }
        return true;
    }

    private void deleteNote() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            String whereClause = DBOpenHelper.COURSE_NOTE_ID + "=" + Integer.parseInt(uriCourseNotes.getLastPathSegment());
                            getContentResolver().delete(Provider.COURSE_NOTES_URI, whereClause, null);
                            for(CourseNotes cN: CourseNotes.COURSE_NOTES_LIST){
                                if(cN.getNotesId() == Integer.parseInt(uriCourseNotes.getLastPathSegment())){
                                    CourseNotes.COURSE_NOTES_LIST.remove(cN);
                                }
                            }
                            Toast.makeText(Course_Notes_Details_Activity.this,
                                    getString(R.string.note_deleted),
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();

                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure7))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

    }

    private void editNote() {
        Intent editNotesScreen = new Intent(this, Edit_CourseNote_Activity.class);
        Uri uri = Uri.parse(Provider.COURSE_NOTES_URI + "/" + Integer.parseInt(uriCourseNotes.getLastPathSegment()));
        editNotesScreen.putExtra(Provider.COURSE_NOTE_ITEM_TYPE, uri);
        startActivityForResult(editNotesScreen, EDIT_COURSE_NOTES_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT_COURSE_NOTES_REQUEST_CODE && resultCode == RESULT_OK){
            displayNotes();
        }
    }
}
