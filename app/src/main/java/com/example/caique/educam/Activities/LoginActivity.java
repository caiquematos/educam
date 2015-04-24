package com.example.caique.educam.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caique.educam.Components.User;
import com.example.caique.educam.Database.EducamDbHandler;
import com.example.caique.educam.R;
import com.example.caique.educam.Tools.Constants;
import com.example.caique.educam.Tools.EducamPreferences;
import com.example.caique.educam.Tools.Request;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText mEmailET;
    private EditText mPasswordET;
    private Button mLoginBT;
    private Button mRegisterBT;
    private EducamDbHandler mDB;
    private User mUser;
    private AsyncTask<String, Void, Request> mAsyncTask;
    private float mDpHeight;
    private float mDpWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailET = (EditText) findViewById(R.id.email_editext);
        mPasswordET = (EditText) findViewById(R.id.password_edittext);
        mLoginBT = (Button) findViewById(R.id.login_button);
        mRegisterBT = (Button) findViewById(R.id.register_button);
        mLoginBT.setOnClickListener(this);
        mRegisterBT.setOnClickListener(this);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();

        mDpHeight = (float) ((displayMetrics.heightPixels / displayMetrics.density) *2.54);
        mDpWidth = (float) ((displayMetrics.widthPixels / displayMetrics.density) *2.54);

        //mDB = new EducamDbHandler(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.OrangeRed);
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
        //getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*noinspection SimplifiableIfStatement
        if (id == R.id.action_upgrade) {
            mDB.deleteAll();
            return true;
        }*/
        if (id == R.id.action_register) {
            register();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                //login();
                loginRequest();
            break;
            case R.id.register_button:
                registerRequest();
                //register();
            break;
        }
    }

    private void register() {
        String email = mEmailET.getText().toString();
        String pswd = mPasswordET.getText().toString();

        if(isFormOK(email, pswd)){
            mUser = new User();
            mUser.setEmail(email);
            mUser.setPassword(pswd);

            if(isUserRegistered(mUser.getEmail())){
                Toast.makeText(getApplicationContext(),"USUARIO JÁ CADASTRADO!!!", Toast.LENGTH_LONG).show();
            }else {
                mDB.insert(mUser);
                Toast.makeText(getApplicationContext(),"USUARIO CADASTRADO COM SUCESSO", Toast.LENGTH_LONG).show();
                User userFeedback = userFeedback(mUser.getEmail());
                if(userFeedback != null) {
                    //updating user with an id from database
                    mUser = userFeedback;
                    EducamPreferences.saveUserId(getApplicationContext(), mUser.getId());
                    EducamPreferences.saveAccountStatus(getApplicationContext(), true);
                    goToTimelineActivity();
                }else {
                    Toast.makeText(getApplicationContext(),"ERRO AO CADASTRAR! TENTE MAIS TARDE", Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"INFORMACOES INCOMPLETAS!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * this a local DB based login
    * */
    private void login() {
        String email = mEmailET.getText().toString();
        String pswd = mPasswordET.getText().toString();

        if(isFormOK(email, pswd)){
            mUser = new User();
            mUser.setEmail(email);
            mUser.setPassword(pswd);
            User userFeedback = userFeedback(mUser.getEmail());

            if(isUserRegistered(mUser.getEmail()) && mUser.getPassword().equals(userFeedback.getPassword())){
                if(userFeedback != null) {
                    //updating user with an id from database
                    mUser = userFeedback;
                    EducamPreferences.saveUserId(getApplicationContext(), mUser.getId());
                    EducamPreferences.saveAccountStatus(getApplicationContext(), true);
                    Toast.makeText(getApplicationContext(),"USUARIO LOGADO", Toast.LENGTH_SHORT).show();
                    goToTimelineActivity();
                }else {
                    Toast.makeText(getApplicationContext(),"ERRO AO ENTRAR!", Toast.LENGTH_SHORT).show();
                    goToLoginActivity();
                }
            }else {
                Toast.makeText(getApplicationContext(),"EMAIL OU SENHA INCORRETO!!!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"INFORMAÇÕES INCOMPLETAS!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * this is a server DB based login
    * */
    private void loginRequest() {
        String email = mEmailET.getText().toString();
        String pswd = mPasswordET.getText().toString();

        if(isFormOK(email, pswd)){
            mUser = new User();
            mUser.setEmail(email);
            mUser.setPassword(pswd);
            connectionRequest(mUser.getEmail(), mUser.getPassword(), ""+mDpHeight, ""+mDpWidth, "LOGIN");
        }
        else {
            Toast.makeText(getApplicationContext(),"INFORMAÇÕES INCOMPLETAS!", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerRequest(){
        String email = mEmailET.getText().toString();
        String pswd = mPasswordET.getText().toString();

        if(isFormOK(email, pswd)){
            mUser = new User();
            mUser.setEmail(email);
            mUser.setPassword(pswd);
            connectionRequest(mUser.getEmail(), mUser.getPassword(), ""+mDpHeight, ""+mDpWidth, "REGISTER");
        }
        else {
            Toast.makeText(getApplicationContext(),"INFORMAÇÕES INCOMPLETAS!", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToLoginActivity() {
        try
        {
            Intent activity = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(activity);
            finish();
        }catch(Exception e){
            Log.e(this.getLocalClassName(), "" + e);
        }
    }

    private User userFeedback(String email) {
        return mDB.findUser(email);
    }

    /*
    * This is a local DB check
    * */
    private boolean isUserRegistered(String email)
    {
        User userFeedback = userFeedback(email);
        if(userFeedback == null) {
            Log.e(this.getLocalClassName(),"userFeedback is null!!! email:" + email);
            return false;
        } else {
            Log.e(this.getLocalClassName(),"" + userFeedback.toString());
            return true;
        }
    }

    /*
    * this is a server DB check
    * */
    private void connectionRequest(final String email, final String pswd,
                                   final String height, final String width,
                                   final String type) {

        mAsyncTask = new AsyncTask<String, Void, Request>(){
            private JSONObject rawJson = new JSONObject();
            private Request checkReq = new Request();

            @Override
            protected Request doInBackground(String... strings) {
                try {
                    if(!strings[0].isEmpty()) { rawJson.put("email",strings[0]); }
                    if(!strings[1].isEmpty()) { rawJson.put("password",strings[1]); }
                    if(!strings[2].isEmpty()) { rawJson.put("height",strings[2]); }
                    if(!strings[3].isEmpty()) { rawJson.put("width",strings[3]); }
                    if(!strings[4].isEmpty()) { rawJson.put("type",strings[4]);
                                                checkReq.setType(strings[4]);
                                                }

                    checkReq.setResponse(POST(rawJson));
                    return checkReq;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Request request) {
                try {
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mAsyncTask.execute(email, pswd, height, width, type);
    }

    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();

        switch (request.getType()) {
            case "LOGIN":
                if(response.getString("status").equals("success")) {
                    if(response.getBoolean("wasRegistered")) {
                        EducamPreferences.saveAccountStatus(getApplication(),true);
                        EducamPreferences.saveUserId(getApplication(), response.getJSONObject("user").getInt("id"));
                        goToTimelineActivity();
                    } else {
                        EducamPreferences.saveAccountStatus(getApplication(),false);
                        Toast.makeText(getApplication(),"Email ou senha incorreta",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplication(),"POR FAVOR TENTE MAIS TARDE",Toast.LENGTH_SHORT).show();
                }
                break;
            case "REGISTER":
                if(response.getString("status").equals("success")) {
                    if(response.getBoolean("wasRegistered")) {
                        Toast.makeText(getApplication(),"USUÁRIO JÁ CADASTRADO",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplication(),"USUÁRIO CADASTRADO COM SUCESSO",Toast.LENGTH_SHORT).show();
                    }
                    goToLoginActivity();
                } else {
                    Toast.makeText(getApplication(),"POR FAVOR TENTE MAIS TARDE",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void goToTimelineActivity() {
        try
        {
            Intent activity = new Intent(LoginActivity.this, TimelineActivity.class);
            startActivity(activity);
            finish();
        }catch(Exception e){
            Log.e(this.getLocalClassName(), "" + e);
        }
    }

    //check if login form was filled properly
    private boolean isFormOK( String email, String pswd){
        if (email.trim().length() > 0
                && pswd.trim().length() > 3 ) {
            return true;
        } else {
            return false;
        }
    }

    private JSONObject POST(JSONObject rawJson) throws IOException, ParseException, JSONException {
        JSONObject json = rawJson;

        Log.e(getLocalClassName(), "JSON IN: " + json);
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = null;
        switch (rawJson.getString("type")) {
            case "LOGIN":
                post = new HttpPost(Constants.MAIN_URL+"user/login");
                break;
            case "REGISTER":
                post = new HttpPost(Constants.MAIN_URL+"user/register");
                Log.e(getLocalClassName(), post.getURI().toString());
                break;
        }


        try {
            StringEntity se = new StringEntity("json="+json.toString());
            post.addHeader("content-type", "application/x-www-form-urlencoded");
            post.setEntity(se);
            Log.e(getLocalClassName(), se.toString());

            HttpResponse response;
            response = client.execute(post);
            String resFromServer = org.apache.http.util.EntityUtils.toString(response.getEntity());

            jsonResponse=new JSONObject(resFromServer);
            Log.e("Response from server: ", "status: " + jsonResponse.getString("status")
                                            + "was registered: " + jsonResponse.getString("wasRegistered"));
            return jsonResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return jsonResponse;
        }
    }
}
