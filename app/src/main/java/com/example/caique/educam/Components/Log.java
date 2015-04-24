package com.example.caique.educam.Components;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by caique on 09/03/15.
 */
public class Log {
    private int id;
    private int user;
    private String information;
    private Date date;
    private Time time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String toString() {
        return  "Id:" + getId()
                + " User: " + getUser()
                + " Info: " + getInformation()
                + " Date: " + getDate()
                + " Time: " + getTime();
    }
}
