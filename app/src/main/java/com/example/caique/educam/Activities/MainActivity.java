package com.example.caique.educam.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.caique.educam.R;
import com.example.caique.educam.Tools.EducamPreferences;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //is user logged in?
        if(EducamPreferences.getAccountStatus(getApplicationContext())){
           goToTimelineActivity();
        }
        else {
            setContentView(R.layout.activity_main);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
            }

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.OrangeRed);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    goToLoginActivity();
                }
            }, 2000);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        win.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        win.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        try
        {
            Intent activity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(activity);
            finish();
        }catch(Exception e){
            Log.e(this.getLocalClassName(),"" + e);
        }
    }

    private void goToTimelineActivity() {
        try
        {
            Intent activity = new Intent(MainActivity.this, TimelineActivity.class);
            startActivity(activity);
            finish();
        }catch(Exception e){
            Log.e(this.getLocalClassName(),"" + e);
        }
    }

}

