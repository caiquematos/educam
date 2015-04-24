package com.example.caique.educam.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.caique.educam.Components.Log;
import com.example.caique.educam.Components.Post;
import com.example.caique.educam.Components.User;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caique on 11/03/15.
 */
public class EducamDbHandler {
    private SQLiteDatabase mDB;
    private ContentValues mValues;

    public EducamDbHandler(Context context) {
        EducamDbHelper dbHelper = new EducamDbHelper(context);
        mDB = dbHelper.getWritableDatabase();
    }

    public void upgradeDB(int version){
        android.util.Log.e(getClass().getName(), "on Upgrade 1");
        mDB.needUpgrade(version);
    }

    private void insert(String table, ContentValues values) {
        mDB.insert(table,null,values);
    }

    private void update(String table, ContentValues values, int id) {
        mDB.update(table,values,"id = ?", new String[]{""+id});
    }

    public void deleteAll(){
        mDB.execSQL(EducamContract.SQL_DELETE_USERS);
        mDB.execSQL(EducamContract.SQL_DELETE_POSTS);
        mDB.execSQL(EducamContract.SQL_DELETE_LOGS);
    }

    //--Starts user section
    public ContentValues setValues(User user) {
        mValues = new ContentValues();
        mValues.put(EducamContract.Users.COLUMN_NAME_EMAIL, user.getEmail());
        mValues.put(EducamContract.Users.COLUMN_NAME_PASSWORD, user.getPassword());
        mValues.put(EducamContract.Users.COLUMN_NAME_NAME, user.getName());
        mValues.put(EducamContract.Users.COLUMN_NAME_BIRTHDAY, String.valueOf(user.getBirthday()));
        mValues.put(EducamContract.Users.COLUMN_NAME_CREATED_AT, String.valueOf(user.getCreated_at()));
        return mValues;
    }

    public void insert(User user) {
        ContentValues values = setValues(user);
        insert(EducamContract.Users.TABLE_NAME, values);
    }

    public void update(User user) {
        ContentValues values = setValues(user);
        update(EducamContract.Users.TABLE_NAME, values, user.getId());
    }

    //--Starts post section
    public ContentValues setValues(Post post) {
        mValues = new ContentValues();
        mValues.put(EducamContract.Posts.COLUMN_NAME_USER, post.getUser());
        mValues.put(EducamContract.Posts.COLUMN_NAME_USER_NAME, post.getUserName());
        mValues.put(EducamContract.Posts.COLUMN_NAME_TITLE, post.getTitle());
        mValues.put(EducamContract.Posts.COLUMN_NAME_PHOTO, post.getPhoto());
        mValues.put(EducamContract.Posts.COLUMN_NAME_LOCATION, String.valueOf(post.getLocation()));
        mValues.put(EducamContract.Posts.COLUMN_NAME_CREATED_AT, String.valueOf(post.getCreated_at()));
        mValues.put(EducamContract.Posts.COLUMN_NAME_LIKES, post.getLikes());
        return mValues;
    }

    public void insert(Post post) {
        ContentValues values = setValues(post);
        insert(EducamContract.Posts.TABLE_NAME, values);
    }

    public void update(Post post) {
        ContentValues values = setValues(post);
        update(EducamContract.Posts.TABLE_NAME, values, post.getId());
    }

    //--Starts log section
    public ContentValues setValues(Log log) {
        mValues = new ContentValues();
        mValues.put(EducamContract.Logs.COLUMN_NAME_USER, log.getUser());
        mValues.put(EducamContract.Logs.COLUMN_NAME_INFO, log.getInformation());
        mValues.put(EducamContract.Logs.COLUMN_NAME_DATE, String.valueOf(log.getDate()));
        mValues.put(EducamContract.Logs.COLUMN_NAME_TIME, String.valueOf(log.getTime()));
        return mValues;
    }

    public void insert(Log log) {
        ContentValues values = setValues(log);
        insert(EducamContract.Logs.TABLE_NAME, values);
    }

    public void update(Log log) {
        ContentValues values = setValues(log);
        update(EducamContract.Logs.TABLE_NAME, values, log.getId());
    }

    public User findUser(String email){

        String query = "SELECT * FROM " + EducamContract.Users.TABLE_NAME
                + " WHERE " + EducamContract.Users.COLUMN_NAME_EMAIL
                + "='" + email + "'";
        Cursor cursor = this.mDB.rawQuery(query, null);

        android.util.Log.e(getClass().getName(), query);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setEmail(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setName(cursor.getString(3));
            android.util.Log.e(getClass().getName(), user.toString());
            return user;
        }

        return null;
    }

    public User findUserById(int id){

        String query = "SELECT * FROM " + EducamContract.Users.TABLE_NAME
                + " WHERE " + "id"
                + "='" + id + "'";
        Cursor cursor = this.mDB.rawQuery(query, null);

        android.util.Log.e(getClass().getName(), query);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setEmail(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setName(cursor.getString(3));
            android.util.Log.e(getClass().getName(), user.toString());
            return user;
        }

        return null;
    }

    public List<Log> listLogs(){
        List<Log> list = new ArrayList<Log>();
        String[] columns = {EducamContract.Logs.COLUMN_NAME_ID,
                            EducamContract.Logs.COLUMN_NAME_USER,
                            EducamContract.Logs.COLUMN_NAME_INFO,
                            EducamContract.Logs.COLUMN_NAME_DATE,
                            EducamContract.Logs.COLUMN_NAME_TIME};

        Cursor cursor = mDB.query(EducamContract.Logs.TABLE_NAME, columns, null, null, null, null, "name ASC");

        if(cursor.getCount() > 0){
            cursor.moveToFirst();

            do{
                Log log = new Log();
                log.setId((int) cursor.getLong(0));
                log.setUser(cursor.getInt(1));
                log.setInformation(cursor.getString(2));
                log.setDate(java.sql.Date.valueOf(cursor.getString(3)));
                log.setTime(Time.valueOf(cursor.getString(4)));
                list.add(log);
            }while (cursor.moveToNext());
        }

        return list;
    }

    public List<Post> listPosts() {
        List<Post> list = new ArrayList<Post>();
        String[] columns = {
                EducamContract.Posts.COLUMN_NAME_ID,
                EducamContract.Posts.COLUMN_NAME_USER,
                EducamContract.Posts.COLUMN_NAME_USER_NAME,
                EducamContract.Posts.COLUMN_NAME_PHOTO,
                EducamContract.Posts.COLUMN_NAME_LIKES,
                EducamContract.Posts.COLUMN_NAME_TITLE,
                EducamContract.Posts.COLUMN_NAME_LOCATION,
                EducamContract.Posts.COLUMN_NAME_CREATED_AT};

        Cursor cursor = mDB.query(EducamContract.Posts.TABLE_NAME, columns, null, null, null, null, "created_at DESC");

        if(cursor.getCount() > 0){
            cursor.moveToFirst();

            do{
                Post post = new Post();
                post.setId((int) cursor.getLong(0));
                post.setUser(cursor.getInt(1));
                post.setUserName(cursor.getString(2));
                post.setPhoto(cursor.getString(3));
                post.setLikes(cursor.getInt(4));
                post.setTitle(cursor.getString(5));
                post.setLocation(cursor.getString(6));
                post.setCreated_at(Timestamp.valueOf(cursor.getString(7)));
                list.add(post);
            }while (cursor.moveToNext());
        }

        return list;
    }
}