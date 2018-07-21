package com.example.sj_sc.scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

public static final Locale LOCALE = Locale.ENGLISH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Provider.populate(this);
        AlertHandler.alert(this);

    }

    public void launchTerms(View view) {
        Intent termScreen = new Intent(this, Terms_Activity.class);
        startActivity(termScreen);
    }

    public void launchCourses(View view) {
        Intent courseScreen = new Intent(this, Courses_Activity.class);
        startActivity(courseScreen);
    }
}
