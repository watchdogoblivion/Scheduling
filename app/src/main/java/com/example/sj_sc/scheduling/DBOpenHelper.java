package com.example.sj_sc.scheduling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

 class DBOpenHelper extends SQLiteOpenHelper {
    //Constants for db name and version
    private static final String DATABASE_NAME = "scheduler.db";
    private static final int DATABASE_VERSION = 1;


    //Constants for identifying table and columns

    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_TITLE = "title";
    public static final String TERM_START_DATE = "startDate";
    public static final String TERM_END_DATE = "endDate";
    public static final String[] TERMS_COLUMNS =
            {TERM_ID, TERM_TITLE, TERM_START_DATE, TERM_END_DATE};

    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_TERM_ID = "courseTermId";
    public static final String COURSE_TITLE = "title";
    public static final String COURSE_START_DATE = "startDate";
    public static final String COURSE_END_DATE = "endDate";
    public static final String COURSE_DESCRIPTION = "courseDescription";
    public static final String STATUS = "status";
    public static final String MENTOR_NAME = "mentorName";
    public static final String MENTOR_EMAIL = "mentorEmail";
    public static final String MENTOR_PHONE_NUMBER = "mentorPhoneNumber";
    public static final String COURSE_NOTIFICATION = "courseNotification";
    public static final String[] COURSES_COLUMNS =
            {COURSE_ID, COURSE_TERM_ID, COURSE_TITLE, COURSE_START_DATE, COURSE_END_DATE,
                    COURSE_DESCRIPTION, STATUS, MENTOR_NAME, MENTOR_EMAIL, MENTOR_PHONE_NUMBER,
                    COURSE_NOTIFICATION};

    public static final String TABLE_COURSE_NOTES = "courseNotes";
    public static final String COURSE_NOTE_ID = "_id";
    public static final String NOTE_COURSE_ID = "notesCourseId";
    public static final String COURSE_NOTE_TITLE = "courseNoteTitle";
    public static final String COURSE_NOTE_TEXT = "courseNoteText";
    public static final String COURSE_NOTE_IMAGE_URI = "courseNoteUri";
    public static final String[] COURSE_NOTES_COLUMNS = {COURSE_NOTE_ID, NOTE_COURSE_ID, COURSE_NOTE_TITLE,
            COURSE_NOTE_TEXT, COURSE_NOTE_IMAGE_URI};

    // Assessment table

    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_COURSE_ID = "assessmentCourseId";
    public static final String ASSESSMENT_TITLE = "assessmentTitle";
    public static final String ASSESSMENT_CODE = "assessmentCode";
    public static final String ASSESSMENT_GOAL_DATE = "assessmentGoalDate";
    public static final String ASSESSMENT_NOTIFICATION = "assessmentNotification";
    public static final String ASSESSMENT_STATUS = "assessmentStatus";
    public static final String ASSESSMENT_DESCRIPTION = "assessmentDescription";
    public static final String[] ASSESSMENTS_COLUMNS = {ASSESSMENT_ID, ASSESSMENT_COURSE_ID,
            ASSESSMENT_TITLE, ASSESSMENT_CODE, ASSESSMENT_GOAL_DATE,
            ASSESSMENT_NOTIFICATION, ASSESSMENT_STATUS, ASSESSMENT_DESCRIPTION};

    //SQL to create table
    private static final String TABLE_CREATE_TERMS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TERMS + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    TERM_START_DATE + " TEXT, " +
                    TERM_END_DATE + " TEXT " +
                    ")";

    private static final String TABLE_CREATE_COURSES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + " (" +
                    COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_TERM_ID + " INTEGER, " +
                    COURSE_TITLE + " TEXT, " +
                    COURSE_START_DATE + " TEXT, " +
                    COURSE_END_DATE + " TEXT, " +
                    COURSE_DESCRIPTION + " TEXT, " +
                    STATUS + " TEXT, " +
                    MENTOR_NAME + " TEXT, " +
                    MENTOR_EMAIL + " TEXT, " +
                    MENTOR_PHONE_NUMBER + " TEXT, " +
                    COURSE_NOTIFICATION + " TEXT, " +
                    "FOREIGN KEY(" + COURSE_TERM_ID + ") REFERENCES " + TABLE_TERMS + "(" + TERM_ID + ")" +
                    ")";

    private static final String TABLE_CREATE_COURSE_NOTES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COURSE_NOTES + " (" +
                    COURSE_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_COURSE_ID + " INTEGER, " +
                    COURSE_NOTE_TITLE + " TEXT, " +
                    COURSE_NOTE_TEXT + " TEXT, " +
                    COURSE_NOTE_IMAGE_URI + " TEXT, " +
                    "FOREIGN KEY(" + NOTE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COURSE_ID + ") " +
                    "ON DELETE CASCADE" +
                    ")";

    // Assessment table
    private static final String ASSESSMENTS_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ASSESSMENTS + " (" +
                    ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_COURSE_ID + " INTEGER, " +
                    ASSESSMENT_TITLE + " TEXT, " +
                    ASSESSMENT_CODE + " TEXT, " +
                    ASSESSMENT_GOAL_DATE + " TEXT, " +
                    ASSESSMENT_NOTIFICATION + " TEXT, " +
                    ASSESSMENT_STATUS + " TEXT, " +
                    ASSESSMENT_DESCRIPTION + " TEXT, " +
                    "FOREIGN KEY(" + ASSESSMENT_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COURSE_ID + ") " +
                    "ON DELETE CASCADE" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_TERMS);
        db.execSQL(TABLE_CREATE_COURSES);
        db.execSQL(TABLE_CREATE_COURSE_NOTES);
        db.execSQL(ASSESSMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        onCreate(db);
    }
}
