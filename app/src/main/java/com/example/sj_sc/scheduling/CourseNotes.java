package com.example.sj_sc.scheduling;

import java.util.concurrent.CopyOnWriteArrayList;

 class CourseNotes {
    private int notesId;
    private int notes_courseId;
    private String imageUri;
    private String noteText;
    private String title;

    public final static CopyOnWriteArrayList<CourseNotes> COURSE_NOTES_LIST = new CopyOnWriteArrayList<>();

    public CourseNotes(String title, String noteText, String imageUri){

        this.title = title;
        this.noteText = noteText;
        this.imageUri = imageUri;
    }

    public int getNotesId() {
        return notesId;
    }
    public void setNotesId(int notesId) {
        this.notesId = notesId;
    }

    public int getNotes_courseId() {
        return notes_courseId;
    }
    public void setNotes_courseId(int notes_courseId) {
        this.notes_courseId = notes_courseId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
