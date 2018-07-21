package com.example.sj_sc.scheduling;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Calendar;

 class AlertHandler {

    private SharedPreferences sharedS;
    private SharedPreferences sharedE;
    private SharedPreferences sharedG;
    public final static ArrayList<SharedPreferences> PREFERENCES_LIST = new ArrayList<>();

    private AlertHandler(Activity activity, Course course){
        sharedS =  activity.getApplicationContext().getSharedPreferences("Start: " + course.getCourseId(),Context.MODE_PRIVATE);
        sharedE =  activity.getApplicationContext().getSharedPreferences("End: " + course.getCourseId(),Context.MODE_PRIVATE);
    }

    private AlertHandler(Activity activity, Assessment assessment){
        sharedG =  activity.getApplicationContext().getSharedPreferences("Goal: " + assessment.getAssessmentId(),Context.MODE_PRIVATE);
    }

    private void setAlerts(String type, String alarmText, Calendar cal){
        if(type.equalsIgnoreCase("Start")){
            SharedPreferences.Editor editor = sharedS.edit();
            editor.putString("Title1", alarmText);
            editor.putString("Date1", String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal));
            editor.apply();
            PREFERENCES_LIST.add(sharedS);
        } else if (type.equalsIgnoreCase("End")){
            SharedPreferences.Editor editor = sharedE.edit();
            editor.putString("Title2", alarmText);
            editor.putString("Date2", String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal));
            editor.apply();
            PREFERENCES_LIST.add(sharedE);
        } else{
            SharedPreferences.Editor editor = sharedG.edit();
            editor.putString("Title3", alarmText);
            editor.putString("Date3", String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal));
            editor.apply();
            PREFERENCES_LIST.add(sharedG);
        }

    }

    public static void alert(Activity activity){

        for(SharedPreferences sP: PREFERENCES_LIST){
            if(sP.getString("Title1", null) != null){

                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(sP.getString("Title1", null)
                        + sP.getString("Date1", null))
                        .setNeutralButton(activity.getResources().getString(android.R.string.ok), dialogClickListener)
                        .show();

            }else if(sP.getString("Title2", null) != null){

                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(sP.getString("Title2", null)
                        + sP.getString("Date2", null))
                        .setNeutralButton(activity.getResources().getString(android.R.string.ok), dialogClickListener)
                        .show();
            } else if(sP.getString("Title3", null) != null){

                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(sP.getString("Title3", null)
                        + sP.getString("Date3", null))
                        .setNeutralButton(activity.getResources().getString(android.R.string.ok), dialogClickListener)
                        .show();
            }

        }
    }
    public static void enableNotifications(Assessment assessment, Calendar calGoal, Activity activity) {
        AlertHandler aH = new AlertHandler(activity, assessment);
        Calendar rightNow = Calendar.getInstance();

        if( equals(calGoal, rightNow)){
            aH.setAlerts("Goal", assessment.getTitle() + " " + assessment.getCode() +
                    " is today! ", rightNow);
        }
        rightNow.add(Calendar.DAY_OF_MONTH, 1);
        if( equals(calGoal, rightNow)){
            aH.setAlerts("Goal",   assessment.getTitle() + " " +  assessment.getCode() +
                    " is tomorrow! ", rightNow);
        }
        rightNow = Calendar.getInstance(MainActivity.LOCALE);
        rightNow.add(Calendar.DAY_OF_MONTH, 7);
        if( equals(calGoal, rightNow)){
            aH.setAlerts("Goal",  assessment.getTitle() + " " +  assessment.getCode() +
                    " is in one week! ", rightNow);
        }

    }
    public static void enableNotifications(Course course, Calendar calStart, Calendar calEnd, Activity activity) {
        AlertHandler aH = new AlertHandler(activity, course);
        Calendar rightNow = Calendar.getInstance();

        if( equals(calStart, rightNow)){
            aH.setAlerts("Start", "Course " + course.getTitle() +
                    " starts today! ", rightNow);
        }
        if(  equals(calEnd, rightNow)){
            aH.setAlerts("End", "Course " + course.getTitle() +
                    " ends today! ", rightNow);
        }


        rightNow.add(Calendar.DAY_OF_MONTH, 1);
        if( equals(calStart, rightNow)){
                aH.setAlerts("Start",  "Course " + course.getTitle() +
                        " starts tomorrow! ", rightNow);
        }
        if( equals(calEnd, rightNow)){
                aH.setAlerts("End",  "Course " + course.getTitle() +
                        " ends tomorrow! ", rightNow);
        }


        rightNow = Calendar.getInstance(MainActivity.LOCALE);
        rightNow.add(Calendar.DAY_OF_MONTH, 7);
        if( equals(calStart, rightNow)){
                aH.setAlerts("Start",  "Course " + course.getTitle() +
                        " starts in one week! ", rightNow);
        }
        if( equals(calEnd, rightNow)){
                aH.setAlerts("End",  "Course " + course.getTitle() +
                        " ends in one week! ", rightNow);
        }

    }

    private static boolean equals(Calendar cal, Calendar rightNow){
        return cal.get(Calendar.MONTH) == rightNow.get(Calendar.MONTH)
                & cal.get(Calendar.DAY_OF_MONTH) == rightNow.get(Calendar.DAY_OF_MONTH)
                & cal.get(Calendar.YEAR) == rightNow.get(Calendar.YEAR);
    }
}