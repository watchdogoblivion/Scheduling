package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Edit_Term_Activity extends AppCompatActivity {

    private Uri uriTerm;

    private EditText editTitleEdit;
    private EditText startMonthEdit;
    private EditText startDayEdit;
    private EditText startYearEdit;
    private EditText endMonthEdit;
    private EditText endDayEdit;
    private EditText endYearEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__term_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeFields();
        setTexts();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setTexts() {
        Intent intent = getIntent();
        uriTerm = intent.getParcelableExtra(Provider.TERMS_ITEM_TYPE);
        String whereClauseTerm = DBOpenHelper.TERM_ID + "=" + uriTerm.getLastPathSegment();
        Cursor cursor = getContentResolver().query(uriTerm, DBOpenHelper.TERMS_COLUMNS, whereClauseTerm,
                null, null);
        if(cursor != null){
            cursor.moveToFirst();

            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy", MainActivity.LOCALE);
            String startDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END_DATE));

            try {
                cal.setTime(simpleDateFormat.parse(startDate));
                cal2.setTime(simpleDateFormat.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            editTitleEdit.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE)));
            startMonthEdit.setText(cal.get(Calendar.MONTH) + 1 + "");
            startDayEdit.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
            startYearEdit.setText(cal.get(Calendar.YEAR) + "");
            endMonthEdit.setText(cal2.get(Calendar.MONTH) + 1 + "");
            endDayEdit.setText(cal2.get(Calendar.DAY_OF_MONTH) + "");
            endYearEdit.setText(cal2.get(Calendar.YEAR) + "");

            cursor.close();
        }

    }

    private void initializeFields() {
        editTitleEdit = findViewById(R.id.editTitleEdit);
        startMonthEdit = findViewById(R.id.startMonthEdit);
        startDayEdit = findViewById(R.id.startDayEdit);
        startYearEdit = findViewById(R.id.startYearEdit);
        endMonthEdit = findViewById(R.id.endMonthEdit);
        endDayEdit = findViewById(R.id.endDayEdit);
        endYearEdit = findViewById(R.id.endYearEdit);
    }


    public void save(View view) {

        String nE = editTitleEdit.getText().toString().trim();
        String sME = startMonthEdit.getText().toString().trim();
        String sDE = startDayEdit.getText().toString().trim();
        String sYE = startYearEdit.getText().toString().trim();
        String eME = endMonthEdit.getText().toString().trim();
        String eDE = endDayEdit.getText().toString().trim();
        String eYE = endYearEdit.getText().toString().trim();

        try{
            Term editedTerm = Term.TERMS_MAP.get(Integer.parseInt(uriTerm.getLastPathSegment()));
            editedTerm.setTitle(nE);
            editedTerm.setStartDate(Integer.parseInt(sME) -1, Integer.parseInt(sDE), Integer.parseInt(sYE));
            editedTerm.setEndDate(Integer.parseInt(eME) -1, Integer.parseInt(eDE), Integer.parseInt(eYE));
            updateTerm(editedTerm);
            finish();

        }catch(Exception e){
            e.printStackTrace();
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.invalid_input))
                    .setNeutralButton(getString(android.R.string.ok), dialogClickListener)
                    .show();
        }


    }

    private void updateTerm(Term editedTerm) {

        String whereClause = DBOpenHelper.TERM_ID + "=" + editedTerm.getTERM_ID();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, editedTerm.getTitle());
        values.put(DBOpenHelper.TERM_START_DATE, editedTerm.getStartDate());
        values.put(DBOpenHelper.TERM_END_DATE, editedTerm.getEndDate());
        getContentResolver().update(Provider.TERMS_URI, values, whereClause, null);
        setResult(RESULT_OK);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
