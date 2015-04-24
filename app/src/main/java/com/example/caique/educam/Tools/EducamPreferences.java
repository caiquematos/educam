package com.example.caique.educam.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by caique on 15/03/15.
 */
public final class EducamPreferences {
    static SharedPreferences account;
    static SharedPreferences user;
    static SharedPreferences photo;
    static SharedPreferences.Editor editor;

    //save account status, true for logged in, in a file
    public static void saveAccountStatus(Context context, boolean isUserLogged){
        account = PreferenceManager.getDefaultSharedPreferences(context);
        editor = account.edit();
        editor.putBoolean("status", isUserLogged).commit();
        Log.e(EducamPreferences.class.getName(), "status changed: " + getAccountStatus(context));
    }

    public static boolean getAccountStatus(Context context){
        try{
            account = PreferenceManager.getDefaultSharedPreferences(context);
            boolean status = account.getBoolean("status",false);
            Log.e(EducamPreferences.class.getName(), "status requested: " + status );
            return status;
        }catch (NullPointerException e){
            e.printStackTrace();
            saveAccountStatus(context, false);
            return getAccountStatus(context);
        }
    }

    public static void saveUserId(Context context, int id){
        user = PreferenceManager.getDefaultSharedPreferences(context);
        editor = user.edit();
        editor.putInt("id", id).commit();
        Log.e(EducamPreferences.class.getName(), "id changed: " + getUserId(context));
    }

    public static int getUserId(Context context) {
        try{
            user = PreferenceManager.getDefaultSharedPreferences(context);
            int id = user.getInt("id", -1);
            Log.e(EducamPreferences.class.getName(), "id requested: " + id );
            return id;
        }catch (NullPointerException e){
            e.printStackTrace();
            saveAccountStatus(context, false);
            return getUserId(context);
        }
    }

    public static void savePhoto(Context context, String photoPath){
        photo = PreferenceManager.getDefaultSharedPreferences(context);
        editor = photo.edit();
        editor.putString("photo", photoPath).commit();
        Log.e(EducamPreferences.class.getName(), "photo changed: " + getPhoto(context));
    }

    public static String getPhoto(Context context) {
        try{
            photo = PreferenceManager.getDefaultSharedPreferences(context);
            String photoPath = photo.getString("photo", "");
            Log.e(EducamPreferences.class.getName(), "photo requested: " + photoPath );
            return photoPath;
        }catch (NullPointerException e){
            e.printStackTrace();
            saveAccountStatus(context, false);
            return getPhoto(context);
        }
    }
}
