package com.example.sj_sc.scheduling;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class Create_Terms_Activity extends AppCompatActivity {

    private EditText sMonthEditor;
    private EditText sDayEditor;
    private EditText sYearEditor;
    private EditText nameEditor;
    private EditText eMonthEditor;
    private EditText eDayEditor;
    private EditText eYearEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__terms_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeFields();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initializeFields() {
        sMonthEditor =  findViewById(R.id.startMonth);
        sDayEditor =  findViewById(R.id.startDay);
        sYearEditor =  findViewById(R.id.startYear);
        nameEditor =  findViewById(R.id.editTitle);
        eMonthEditor =  findViewById(R.id.endMonth);
        eDayEditor =  findViewById(R.id.endDay);
        eYearEditor =  findViewById(R.id.endYear);
    }

    public void save(View view){
        String sME = sMonthEditor.getText().toString().trim();
        String sDE = sDayEditor.getText().toString().trim();
        String sYE = sYearEditor.getText().toString().trim();
        String nE = nameEditor.getText().toString().trim();
        String eME = eMonthEditor.getText().toString().trim();
        String eDE = eDayEditor.getText().toString().trim();
        String eYE = eYearEditor.getText().toString().trim();
        try{
            Term newTerm = new Term(nE, Integer.parseInt(sME) -1 , Integer.parseInt(sDE), Integer.parseInt(sYE),
                    Integer.parseInt(eME) - 1, Integer.parseInt(eDE), Integer.parseInt(eYE));

                    insertTerm(newTerm);
                    finish();

        }catch(Exception e){
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

    private void insertTerm(Term term) {

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, term.getTitle());
        values.put(DBOpenHelper.TERM_START_DATE, term.getStartDate());
        values.put(DBOpenHelper.TERM_END_DATE, term.getEndDate());

        Uri termUri = getContentResolver().insert(Provider.TERMS_URI, values);

        if (termUri != null) {
            term.setTERM_ID(Integer.parseInt(termUri.getLastPathSegment()));
        }
        Term.TERMS_MAP.put(term.getTERM_ID(), term);
        setResult(RESULT_OK);
    }

}
