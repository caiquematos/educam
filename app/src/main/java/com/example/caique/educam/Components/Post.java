package com.example.caique.educam.Components;

import java.sql.Timestamp;

/**
 * Created by caique on 09/03/15.
 */
public class Post {
    private int id;
    private int user;
    private String user_name;
    private String photo;
    private String title;
    private String location;
    private int likes = 0;
    private Timestamp created_at;

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

    public String getPhoto() {
        return photo;
    }

    public void setLikes(int likes) { this.likes = likes; };

    public int getLikes() {
        return likes;
    }

    public void liked() {
        this.likes = this.likes + 1;
    }

    public void disliked() {
        this.likes = this.likes - 1;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String toString() {
        return  "Id:" + getId()
                + " User: " + getUserName()
                + " Photo: " + getPhoto()
                + " Likes: " + getLikes()
                + " Title: " + getTitle()
                + " Location: " +getLocation()
                + " Created_at: " +getCreated_at();
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }
}
