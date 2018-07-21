package com.example.sj_sc.scheduling;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

 class Term {

    private String title;
    private String startDate;
    private String endDate;
    private int TERM_ID = 0;
    private final Map<Integer, Course> COURSES_MAP = new TreeMap<>();

    public final static Map<Integer, Term> TERMS_MAP = new ConcurrentSkipListMap<>();

     Term(String title, int startMonth, int startDay, int startYear, int endMonth, int endDay, int endYear) {
        this.title = title;
        Calendar cal = Calendar.getInstance();
        cal.set(startYear, startMonth,startDay);
        this.startDate = String.format(MainActivity.LOCALE, "%1$tA %1$tb %1$td %1$tY", cal);
        cal.set(endYear, endMonth,endDay);
        this.endDate = String.format(MainActivity.LOCALE, "%1$tA %1$tb %1$td %1$tY", cal);
    }

    @Override
    public String toString() {
        return "\nTerm ID: " + TERM_ID + "\nTitle: " +
                title +  "\nStart Date: " + startDate + "\nEnd Date: " + endDate;
    }


    public int getTERM_ID(){ return TERM_ID; }
    public void setTERM_ID(int term_id){ TERM_ID = term_id; }

    public String getTitle (){
        return title;
    }
    public void setTitle (String title){
        this.title = title;
    }

    public String getStartDate (){ return startDate; }
    public void setStartDate (String startDate){
        this.startDate = startDate;
    }
    public void setStartDate(int month, int day, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.startDate = String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal);
    }

    public String getEndDate (){ return endDate; }
    public void setEndDate (String endDate){
        this.endDate = endDate;
    }
    public void setEndDate(int month, int day, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.endDate = String.format(MainActivity.LOCALE,"%1$tA %1$tb %1$td %1$tY", cal);
    }

    public Map<Integer, Course> getCOURSES_MAP() {
        return COURSES_MAP;
    }

    public void addCourse(Course course){
        COURSES_MAP.put(course.getCourseId(), course);
    }
}
