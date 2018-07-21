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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Terms_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private CursorAdapter cursorAdapter;
    private static final int CREATE_TERMS_REQUEST_CODE = 1001;
    private static final int TERM_DETAILS_REQUEST_CODE = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.terms);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateList();
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void populateList() {
        String [] from = {DBOpenHelper.TERM_TITLE, DBOpenHelper.TERM_START_DATE, DBOpenHelper.TERM_END_DATE};
        int [] to = {R.id.titleText, R.id.startDateText, R.id.endDateText};

        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.termslist_item, null, from, to, 0);
        ListView lv = findViewById(R.id.termsListView);
        lv.setAdapter(cursorAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent termDetailsScreen = new Intent(Terms_Activity.this,Term_Details_Activity.class);
                Uri uri = Uri.parse(Provider.TERMS_URI + "/" + id);
                termDetailsScreen.putExtra(Provider.TERMS_ITEM_TYPE, uri);
                startActivityForResult(termDetailsScreen, TERM_DETAILS_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_terms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_create_term:
                createTerms();
                break;
            case R.id.action_delete_all_terms:
                deleteTerms();
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Provider.TERMS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }

    private void createTerms() {
        Intent addScreen = new Intent(this, Create_Terms_Activity.class);
        startActivityForResult(addScreen, CREATE_TERMS_REQUEST_CODE);

    }

    private void restartLoader(){
        getLoaderManager().restartLoader(0, null, this);
    }

    private void deleteTerms() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            for(Term t: Term.TERMS_MAP.values()){
                                if(t.getCOURSES_MAP().isEmpty()){
                                    String whereClause = DBOpenHelper.TERM_ID + "=" + t.getTERM_ID();
                                    getContentResolver().delete(Provider.TERMS_URI, whereClause, null);
                                    Term.TERMS_MAP.remove(t.getTERM_ID());
                                }
                            }
                            restartLoader();
                            Toast.makeText(Terms_Activity.this,
                                    getString(R.string.all_terms_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_TERMS_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }else if(requestCode == TERM_DETAILS_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

}
