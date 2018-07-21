package com.example.sj_sc.scheduling;

import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

 class Assessment {
    private int assessmentId;
    private int assessment_courseId;
    private String title;
    private String code;
    private String status;
    private String description;
    private String goalDate;
    private String notification;

    public final static CopyOnWriteArrayList<Assessment> ASSESSMENTS_LIST = new CopyOnWriteArrayList<>();

    public Assessment(String title, String code, int goalMonth, int goalDay, int goalYear, String status, String description){

        this.title = title;
        this.code = code;
        Calendar cal = Calendar.getInstance();
        cal.set(goalYear, goalMonth,goalDay);
        this.goalDate = String.format(MainActivity.LOCALE, "%1$tA %1$tb %1$td %1$tY", cal);
        this.status = status;
        this.description = description;
    }

    public int getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public int getAssessment_courseId() {
        return assessment_courseId;
    }
    public void setAssessment_courseId(int assessment_courseId) {
        this.assessment_courseId = assessment_courseId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotification() {
        return notification;
    }
    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getGoalDate() {
        return goalDate;
    }
    public void setGoalDate(String goalDate) {
        this.goalDate = goalDate;
    }
    public void setGoalDate(int month, int day, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.goalDate = String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal);
    }
}
