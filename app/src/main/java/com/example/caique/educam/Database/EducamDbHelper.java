package com.example.caique.educam.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by caique on 11/03/15.
 */
public class EducamDbHelper extends SQLiteOpenHelper {

    public EducamDbHelper(Context context) {
        super(context, EducamContract.DB_NAME, null, EducamContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EducamContract.SQL_CREATE_USERS);
        db.execSQL(EducamContract.SQL_CREATE_POSTS);
        db.execSQL(EducamContract.SQL_CREATE_LOGS);
        Log.e(getClass().getName(), "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.e(getClass().getName(), "on Upgrade 2");
        db.execSQL(EducamContract.SQL_DELETE_USERS);
        db.execSQL(EducamContract.SQL_DELETE_POSTS);
        db.execSQL(EducamContract.SQL_DELETE_LOGS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
