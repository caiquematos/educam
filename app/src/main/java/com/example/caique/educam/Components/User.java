package com.example.caique.educam.Components;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by caique on 09/03/15.
 */
public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private Date birthday;
    private Timestamp created_at;

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String toString() {
        return  "Id:" + getId()
                + " Name: " + getName()
                + " Email: " + getEmail()
                + " Password: " + getPassword()
                + " Birthday: " + getBirthday()
                + " Created_at: " +getCreated_at();
    }
}
