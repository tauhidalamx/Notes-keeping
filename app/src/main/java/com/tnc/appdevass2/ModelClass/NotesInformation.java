package com.tnc.appdevass2.ModelClass;

import java.io.Serializable;

public class NotesInformation implements Serializable {
    String Title;
    String Description;
    String Date;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
