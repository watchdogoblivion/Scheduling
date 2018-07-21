package com.example.sj_sc.scheduling;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Edit_CourseNote_Activity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri resultPic;
    private String photoPath;
    private ImageView imageView;
    private EditText editText1;
    private EditText editText2;
    private Uri uriCourseNotes;
    private String whereClauseCourseNotes;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private final static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_note_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        initializeFields();
        setFields();

        FloatingActionButton fab = findViewById(R.id.fab111);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                        Log.i("HELLO", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
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
        editText1 =  findViewById(R.id.editText0011);
        editText2 =  findViewById(R.id.editText0012);
        imageView = findViewById(R.id.imageView0011);
    }

    private void setFields() {
        Intent intent = getIntent();
        uriCourseNotes = intent.getParcelableExtra(Provider.COURSE_NOTE_ITEM_TYPE);
        whereClauseCourseNotes = DBOpenHelper.COURSE_NOTE_ID + "=" + uriCourseNotes.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriCourseNotes, DBOpenHelper.COURSE_NOTES_COLUMNS, whereClauseCourseNotes,
                null, null);

        if (cursor != null){

            cursor.moveToFirst();
            editText1.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TITLE)));
            editText2.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TEXT)));
            String imageUri = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_IMAGE_URI));
            imageView.setImageURI(Uri.parse(imageUri));
            resultPic =  Uri.parse(imageUri);
            cursor.close();
        }



    }
    private File createImageFile() throws IOException {
        // Create an image file name
        checkPermissions(this);
        String timeStamp = new SimpleDateFormat("yyyy MM dd HH mm ss", MainActivity.LOCALE).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private static void checkPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap notesBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoPath));
                resultPic = getImageUri(notesBitmap);
                imageView.setImageURI(resultPic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Uri getImageUri(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void save(View view) {

        String title = editText1.getText().toString().trim();
        String text = editText2.getText().toString().trim();
        String imageUri = resultPic.toString();

        for(CourseNotes cN: CourseNotes.COURSE_NOTES_LIST){
            if(cN.getNotesId() == Integer.parseInt(uriCourseNotes.getLastPathSegment())){
                cN.setTitle(title);
                cN.setNoteText(text);
                cN.setImageUri(imageUri);
                updateNote(cN);
            }
        }
        finish();
    }

    private void updateNote(CourseNotes cN) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NOTE_TITLE, cN.getTitle());
        values.put(DBOpenHelper.COURSE_NOTE_TEXT, cN.getNoteText());
        values.put(DBOpenHelper.COURSE_NOTE_IMAGE_URI, cN.getImageUri());

        getContentResolver().update(Provider.COURSE_NOTES_URI, values, whereClauseCourseNotes, null);
        setResult(RESULT_OK);
    }
}
