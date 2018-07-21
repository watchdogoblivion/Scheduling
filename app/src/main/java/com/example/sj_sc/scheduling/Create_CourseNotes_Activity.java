package com.example.sj_sc.scheduling;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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

public class Create_CourseNotes_Activity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri resultPic;
    private String photoPath;
    private ImageView imageView;
    private EditText editText1;
    private EditText editText2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private final static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__course_notes_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

       initializeFields();

        FloatingActionButton fab = findViewById(R.id.fab11);
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
        Uri defaultPath = Uri.parse("android.resource://com.example.sj_sc.scheduling/" + R.mipmap.ic_launcher);
        editText1 =  findViewById(R.id.editText001);
        editText2 =  findViewById(R.id.editText002);
        imageView = findViewById(R.id.imageView001);
        imageView.setImageURI(defaultPath);
        resultPic =  defaultPath;
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

        CourseNotes cN = new CourseNotes(title, text, imageUri);
        insertNotes(cN);
        finish();
    }

    private void insertNotes(CourseNotes cN) {
        Intent intent = getIntent();
        Uri courseUri = intent.getParcelableExtra(Provider.COURSE_NOTE_ITEM_TYPE);
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_COURSE_ID, Integer.parseInt(courseUri.getLastPathSegment()));
        values.put(DBOpenHelper.COURSE_NOTE_TITLE, cN.getTitle());
        values.put(DBOpenHelper.COURSE_NOTE_TEXT, cN.getNoteText());
        values.put(DBOpenHelper.COURSE_NOTE_IMAGE_URI, cN.getImageUri());

        Uri courseNotesUri = getContentResolver().insert(Provider.COURSE_NOTES_URI, values);

        if (courseNotesUri != null) {
            cN.setNotesId(Integer.parseInt(courseNotesUri.getLastPathSegment()));
        }
        CourseNotes.COURSE_NOTES_LIST.add(cN);
        for(Course c: Course.COURSES_MAP.values()){
            if(c.getCourseId() == Integer.parseInt(courseUri.getLastPathSegment())){
                c.addCourseNote(cN);
            }
        }
        setResult(RESULT_OK);
    }
}
