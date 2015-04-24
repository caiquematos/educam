package com.example.caique.educam.Database;

import android.provider.BaseColumns;

/**
 * Created by caique on 11/03/15.
 *
 * Specifies database structure
 */
public final class EducamContract {

    public static final String DB_NAME = "educam.db";
    public static final int DB_VERSION = 1;

    public static final String SQL_CREATE_USERS =
            "CREATE TABLE "
                    + Users.TABLE_NAME + "( "
                    + Users.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Users.COLUMN_NAME_EMAIL + " TEXT NOT NULL, "
                    + Users.COLUMN_NAME_PASSWORD + " VARCHAR(13) NOT NULL,"
                    + Users.COLUMN_NAME_NAME + " VARCHAR(255),"
                    + Users.COLUMN_NAME_BIRTHDAY + " DATE,"
                    + Users.COLUMN_NAME_CREATED_AT + " TIMESTAMP);";

    public static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + Users.TABLE_NAME;

    public static final String SQL_CREATE_POSTS =
            "CREATE TABLE "
                    + Posts.TABLE_NAME + "( "
                    + Posts.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Posts.COLUMN_NAME_USER + " INTEGER NOT NULL, "
                    + Posts.COLUMN_NAME_USER_NAME + " INTEGER, "
                    + Posts.COLUMN_NAME_PHOTO + " VARCHAR(255) NOT NULL,"
                    + Posts.COLUMN_NAME_TITLE + " TEXT,"
                    + Posts.COLUMN_NAME_LOCATION + " VARCHAR(45),"
                    + Posts.COLUMN_NAME_LIKES + " INTEGER,"
                    + Posts.COLUMN_NAME_CREATED_AT + " TIMESTAMP);";

    public static final String SQL_DELETE_POSTS =
            "DROP TABLE IF EXISTS " + Posts.TABLE_NAME;


    public static final String SQL_CREATE_LOGS =
            "CREATE TABLE "
                    + Logs.TABLE_NAME + "( "
                    + Logs.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Logs.COLUMN_NAME_USER + " INTEGER NOT NULL, "
                    + Logs.COLUMN_NAME_INFO + " TEXT,"
                    + Logs.COLUMN_NAME_DATE +" DATE NOT NULL,"
                    + Logs.COLUMN_NAME_TIME + " TIME NOT NULL);";

    public static final String SQL_DELETE_LOGS =
            "DROP TABLE IF EXISTS " + Logs.TABLE_NAME;

    public EducamContract(){}

    public static abstract class Users implements BaseColumns{
        public static final String TABLE_NAME = "Users";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }

    public static abstract class Posts implements BaseColumns{
        public static final String TABLE_NAME = "Posts";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_LIKES = "likes";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }


    public static abstract class Logs implements BaseColumns{
        public static final String TABLE_NAME = "Logs";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER = "user";
        public static final String COLUMN_NAME_INFO = "info";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
    }
}