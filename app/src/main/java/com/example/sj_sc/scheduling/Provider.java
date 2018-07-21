package com.example.sj_sc.scheduling;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Provider extends ContentProvider {

    private static final String AUTHORITY = "com.example.sj_sc.scheduling";

    private static final String TERMS_PATH = "term";
    private static final String COURSES_PATH = "course";
    private static final String COURSE_NOTES_PATH = "courseNotes";
    private static final String ASSESSMENTS_PATH = "assessments";

    public static final Uri TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TERMS_PATH);
    public static final Uri COURSES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSES_PATH);
    public static final Uri COURSE_NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_NOTES_PATH);
    public static final Uri ASSESSMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENTS_PATH);

    // Constant to identify the requested operation
    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSES = 3;
    private static final int COURSES_ID = 4;
    private static final int COURSE_NOTES = 5;
    private static final int COURSE_NOTES_ID = 6;
    private static final int ASSESSMENTS = 7;
    private static final int ASSESSMENTS_ID = 8;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, TERMS_PATH, TERMS);
        uriMatcher.addURI(AUTHORITY, TERMS_PATH +  "/#", TERMS_ID);
        uriMatcher.addURI(AUTHORITY, COURSES_PATH, COURSES);
        uriMatcher.addURI(AUTHORITY, COURSES_PATH +  "/#", COURSES_ID);
        uriMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH, COURSE_NOTES);
        uriMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH + "/#", COURSE_NOTES_ID);
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH, ASSESSMENTS);
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH + "/#", ASSESSMENTS_ID);
        //if uri ends with numeric value it is giving us the specific row with that id
    }

    public static final String TERMS_ITEM_TYPE = "terms";
    public static final String COURSES_ITEM_TYPE = "courses";
    public static final String COURSE_NOTE_ITEM_TYPE = "courseNote";
    public static final String ASSESSMENT_ITEM_TYPE = "assessment";

    private static SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    public static void populate(Activity activity){

        Term.TERMS_MAP.clear();
        String query1 = "SELECT * FROM " + DBOpenHelper.TABLE_TERMS;
        Cursor tCursor = database.rawQuery(query1, null);

        if (tCursor.moveToFirst()) {
            while ( !tCursor.isAfterLast() ) {

                Term term = new Term("", 1,2,2018,
                        1,28,2018);
                int termId = tCursor.getInt(tCursor.getColumnIndex(DBOpenHelper.TERM_ID));
                String title = tCursor.getString(tCursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
                String startDate = tCursor.getString(tCursor.getColumnIndex(DBOpenHelper.TERM_START_DATE));
                String endDate = tCursor.getString(tCursor.getColumnIndex(DBOpenHelper.TERM_END_DATE));
                term.setTERM_ID(termId);
                term.setTitle(title);
                term.setStartDate(startDate);
                term.setEndDate(endDate);
                Term.TERMS_MAP.put(term.getTERM_ID(), term);
                tCursor.moveToNext();
            }
        }
        tCursor.close();

        Course.COURSES_MAP.clear();
        AlertHandler.PREFERENCES_LIST.clear();
        String query2 = "SELECT * FROM " + DBOpenHelper.TABLE_COURSES;
        Cursor cCursor = database.rawQuery(query2, null);

        if (cCursor.moveToFirst()) {
            while ( !cCursor.isAfterLast() ) {

                Course course = new Course("", 1,2,2018,
                        1,28,2018, "", "",
                        "", "", "");
                int courseId = cCursor.getInt(cCursor.getColumnIndex(DBOpenHelper.COURSE_ID));
                int courseTermId = cCursor.getInt(cCursor.getColumnIndex(DBOpenHelper.COURSE_TERM_ID));
                String title = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));
                String startDate = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.COURSE_START_DATE));
                String endDate = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.COURSE_END_DATE));
                String description = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION));
                String status = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.STATUS));
                String mName = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
                String mEmail = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));
                String mPhone = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE_NUMBER));
                String notification = cCursor.getString(cCursor.getColumnIndex(DBOpenHelper.COURSE_NOTIFICATION));

                course.setCourseId(courseId);
                course.setCourseTermId(courseTermId);
                course.setTitle(title);
                course.setStartDate(startDate);
                course.setEndDate(endDate);
                course.setCourseDescription(description);
                course.setStatus(status);
                course.setMentorName(mName);
                course.setMentorEmail(mEmail);
                course.setMentorPhoneNumber(mPhone);
                course.setNotification(notification);

                Calendar calStart = Calendar.getInstance();
                Calendar calEnd = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy", MainActivity.LOCALE);

                try {
                    calStart.setTime(simpleDateFormat.parse(startDate));
                    calEnd.setTime(simpleDateFormat.parse(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(notification.equalsIgnoreCase("Enabled")){
                    AlertHandler.enableNotifications(course, calStart, calEnd, activity);
                }

                Course.COURSES_MAP.put(course.getCourseId(), course);

                for (Term t: Term.TERMS_MAP.values()){
                    if(t.getTERM_ID() == courseTermId){
                        t.addCourse(course);
                    }
                }

                cCursor.moveToNext();
            }
        }
        cCursor.close();

        CourseNotes.COURSE_NOTES_LIST.clear();
        String query3 = "SELECT * FROM " + DBOpenHelper.TABLE_COURSE_NOTES;
        Cursor cNCursor = database.rawQuery(query3, null);

        if (cNCursor.moveToFirst()) {
            while ( !cNCursor.isAfterLast() ) {

                CourseNotes courseNote =  new CourseNotes("","", "");
                int courseNotesId = cNCursor.getInt(cNCursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_ID));
                int notesCourseId = cNCursor.getInt(cNCursor.getColumnIndex(DBOpenHelper.NOTE_COURSE_ID));
                String title = cNCursor.getString(cNCursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TITLE));
                String text = cNCursor.getString(cNCursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TEXT));
                String imageUri = cNCursor.getString(cNCursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_IMAGE_URI));

                courseNote.setNotesId(courseNotesId);
                courseNote.setNotes_courseId(notesCourseId);
                courseNote.setTitle(title);
                courseNote.setNoteText(text);
                courseNote.setImageUri(imageUri);

                CourseNotes.COURSE_NOTES_LIST.add(courseNote);

                for (Course c: Course.COURSES_MAP.values()){
                    if(c.getCourseId() == notesCourseId){
                        c.addCourseNote(courseNote);
                    }
                }

                cNCursor.moveToNext();
            }
        }
        cNCursor.close();

        Assessment.ASSESSMENTS_LIST.clear();
        String query4 = "SELECT * FROM " + DBOpenHelper.TABLE_ASSESSMENTS;
        Cursor aCursor = database.rawQuery(query4, null);

        if (aCursor.moveToFirst()) {
            while ( !aCursor.isAfterLast() ) {

                Assessment assessment =  new Assessment("","",1,2,
                        2018,"","");
                int assessmentId = aCursor.getInt(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ID));
                int assessment_courseId = aCursor.getInt(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE_ID));
                String title = aCursor.getString(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
                String code = aCursor.getString(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_CODE));
                String goal = aCursor.getString(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_GOAL_DATE));
                String notification = aCursor.getString(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTIFICATION));
                String status = aCursor.getString(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_STATUS));
                String description = aCursor.getString(aCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DESCRIPTION));


                assessment.setAssessmentId(assessmentId);
                assessment.setAssessment_courseId(assessment_courseId);
                assessment.setTitle(title);
                assessment.setCode(code);
                assessment.setGoalDate(goal);
                assessment.setNotification(notification);
                assessment.setStatus(status);
                assessment.setDescription(description);

                Calendar calGoal = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy", MainActivity.LOCALE);

                try {
                    calGoal.setTime(simpleDateFormat.parse(goal));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(notification.equalsIgnoreCase("Enabled")){
                    AlertHandler.enableNotifications(assessment, calGoal, activity);
                }

                Assessment.ASSESSMENTS_LIST.add(assessment);

                for (Course c: Course.COURSES_MAP.values()){
                    if(c.getCourseId() == assessment_courseId){
                        c.addAssessment(assessment);
                    }
                }

                aCursor.moveToNext();
            }
        }
        aCursor.close();

    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case TERMS:
                return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERMS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.TERM_ID + " ASC");

            case TERMS_ID:
                selection = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
                return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERMS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.TERM_ID + " ASC");

            case COURSES:
                return database.query(DBOpenHelper.TABLE_COURSES, DBOpenHelper.COURSES_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.COURSE_ID + " ASC");

            case COURSES_ID:
                selection = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
                return database.query(DBOpenHelper.TABLE_COURSES, DBOpenHelper.COURSES_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.COURSE_ID + " ASC");

            case COURSE_NOTES:
                return database.query(DBOpenHelper.TABLE_COURSE_NOTES, DBOpenHelper.COURSE_NOTES_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.COURSE_NOTE_ID + " ASC");

            case COURSE_NOTES_ID:
                selection = DBOpenHelper.COURSE_NOTE_ID + "=" + uri.getLastPathSegment();
                return database.query(DBOpenHelper.TABLE_COURSE_NOTES, DBOpenHelper.COURSE_NOTES_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.COURSE_NOTE_ID + " ASC");

            case ASSESSMENTS:
                return database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ASSESSMENTS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.ASSESSMENT_ID + " ASC");

            case ASSESSMENTS_ID:
                selection = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
                return database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ASSESSMENTS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.ASSESSMENT_ID + " ASC");
            default:
                throw new IllegalArgumentException();
        }

    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        long id;

        switch (uriMatcher.match(uri)) {
            case TERMS:
                id = database.insert(DBOpenHelper.TABLE_TERMS,
                        null, values);
                return Uri.parse(TERMS_PATH + "/" + id);
            case COURSES:
                id = database.insert(DBOpenHelper.TABLE_COURSES,
                        null, values);
                return Uri.parse(COURSES_PATH + "/" + id);
            case COURSE_NOTES:
                id = database.insert(DBOpenHelper.TABLE_COURSE_NOTES,
                        null, values);
                return Uri.parse(COURSE_NOTES_PATH + "/" + id);
            case ASSESSMENTS:
                id = database.insert(DBOpenHelper.TABLE_ASSESSMENTS,
                        null, values);
                return Uri.parse(ASSESSMENTS_PATH + "/" + id);
            default:
                throw new IllegalArgumentException();
        }

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case TERMS:
                return database.delete(DBOpenHelper.TABLE_TERMS, selection, selectionArgs);
            case COURSES:
                return database.delete(DBOpenHelper.TABLE_COURSES, selection, selectionArgs);
            case COURSE_NOTES:
                return database.delete(DBOpenHelper.TABLE_COURSE_NOTES, selection, selectionArgs);
            case ASSESSMENTS:
                return database.delete(DBOpenHelper.TABLE_ASSESSMENTS, selection, selectionArgs);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case TERMS:
                return database.update(DBOpenHelper.TABLE_TERMS,
                        values, selection, selectionArgs);
            case COURSES:
                return database.update(DBOpenHelper.TABLE_COURSES,
                        values, selection, selectionArgs);
            case COURSE_NOTES:
                return database.update(DBOpenHelper.TABLE_COURSE_NOTES,
                        values, selection, selectionArgs);
            case ASSESSMENTS:
                return database.update(DBOpenHelper.TABLE_ASSESSMENTS,
                        values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException();
        }

    }
}

