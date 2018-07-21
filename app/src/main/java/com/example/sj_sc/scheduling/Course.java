package com.example.sj_sc.scheduling;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

 class Course {


    private String title;
    private String startDate;
    private String endDate;
    private String status;
    private String mentorName;
    private String mentorEmail;
    private String mentorPhoneNumber;
    private String courseDescription;
    private String notification;
    private int courseId = 0;
    private int courseTermId = -1;

    private final Map<Integer, CourseNotes> COURSE_NOTES_MAP = new TreeMap<>();
    private final Map<Integer, Assessment> ASSESSMENTS_MAP = new TreeMap<>();

    public final static Map<Integer, Course> COURSES_MAP = new ConcurrentSkipListMap<>();

     Course( String title, int startMonth, int startDay, int startYear, int endMonth, int endDay, int endYear,
                  String status, String mentorName, String mentorEmail, String mentorPhoneNumber, String courseDescription ){

        this.title = title;
        Calendar cal = Calendar.getInstance();
        cal.set(startYear, startMonth,startDay);
        this.startDate =String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal);
        cal.set(endYear, endMonth,endDay);
        this.endDate = String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal);
        this.status = status;
        this.mentorName = mentorName;
        this.mentorEmail = mentorEmail;
        this.mentorPhoneNumber = mentorPhoneNumber;
        this.courseDescription = courseDescription;

    }

    @Override
    public String toString() {
        return "\nCourse ID: " + courseId + "\nCourse Term ID: " + courseTermId + "\nTitle: " +
                title +  "\nStart Date: " + startDate + "\nEnd Date: " + endDate + "\nStatus: " +
                status + "\nMentor Name: " +mentorName + "\nMentor Email: " + mentorEmail +
                "\nMentor Phone: " + mentorPhoneNumber;
    }

    public int getCourseId() {
        return courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCourseTermId() {
        return courseTermId;
    }
    public void setCourseTermId(int courseTermId) {
        this.courseTermId = courseTermId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public void setStartDate(int month, int day, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.startDate = String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal);
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public void setEndDate(int month, int day, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.endDate = String.format(MainActivity.LOCALE, "%1$tA %1$tb %1$td %1$tY", cal);
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMentorName() {
        return mentorName;
    }
    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }
    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public String getMentorPhoneNumber() {
        return mentorPhoneNumber;
    }
    public void setMentorPhoneNumber(String mentorPhoneNumber) {
        this.mentorPhoneNumber = mentorPhoneNumber;
    }

    public String getCourseDescription() {
        return courseDescription;
    }
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Map<Integer, CourseNotes> getCourseNotesMap(){
         return COURSE_NOTES_MAP;
    }
    public void addCourseNote(CourseNotes courseNotes){
        COURSE_NOTES_MAP.put(courseNotes.getNotesId(), courseNotes);
    }

    public Map<Integer, Assessment> getASSESSMENTS_MAP(){
        return ASSESSMENTS_MAP;
    }
    public void addAssessment(Assessment assessment){
        ASSESSMENTS_MAP.put(assessment.getAssessmentId(), assessment);
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }


}