/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.caique.educam.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caique.educam.Components.Post;
import com.example.caique.educam.Database.EducamDbHandler;
import com.example.caique.educam.R;
import com.example.caique.educam.Timeline.CustomAdapter;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of {@link android.support.v7.widget.RecyclerView} with a {@link android.support.v7.widget.LinearLayoutManager} and a
 * {@link android.support.v7.widget.GridLayoutManager}.
 */
public class TimelineActivity extends Activity implements View.OnClickListener{
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 4;
    private Button mAddButton;
    private AsyncTask<String, Void, Request> mAsyncTask;
    private ProgressBar mProgressBar;



    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected Post[] mDataset;
    private EducamDbHandler mDB;
    private TextView mErrorText;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.OrangeRed);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAddButton = (Button) findViewById(R.id.add_button);
        mAddButton.setOnClickListener(this);
        mErrorText = (TextView) findViewById(R.id.error_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mErrorText = (TextView) findViewById(R.id.error_text);
        //mDB = new EducamDbHandler(getApplicationContext());

        retrievePosts(String.valueOf(EducamPreferences.getUserId(getApplication())),"ALL");

        //uncomment for local db usage
       /* List<Post> posts = mDB.listPosts();
        Log.e(getLocalClassName(), "num posts: " + posts.size());
        if(posts.size() >= 0){
            initDataset(posts);
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorText.setVisibility(View.INVISIBLE);
            mAdapter = new CustomAdapter(getApplicationContext(), mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            mRecyclerView.setAdapter(mAdapter);
            // END_INCLUDE(initializeRecyclerView)
        }*/
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        win.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
       /* win.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       */ final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_button:
                goToPostActivity();
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            goToLoginActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToPostActivity() {
        try
        {
            Intent activity = new Intent(TimelineActivity.this, PostActivity.class);
            startActivity(activity);
        }catch(Exception e){
            Log.e(this.getLocalClassName(),"" + e);
        }
    }

    private void goToLoginActivity() {
        try
        {
            Intent activity = new Intent(TimelineActivity.this, LoginActivity.class);
            EducamPreferences.saveAccountStatus(getApplicationContext(),false);
            startActivity(activity);
            finish();
        }catch(Exception e){
            Log.e(this.getLocalClassName(),"" + e);
        }
    }

    private void retrievePosts(String user, String type){
        mAsyncTask = new AsyncTask<String, Void, Request>(){
            private JSONObject rawJson = new JSONObject();
            private Request checkReq = new Request();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setEnabled(true);
            }

            @Override
            protected Request doInBackground(String... strings) {
                try {
                    if(!strings[0].isEmpty()) { rawJson.put("user",strings[0]); }
                    if(!strings[1].isEmpty()) { rawJson.put("type",strings[1]);
                                                checkReq.setType(strings[1]);   }

                    checkReq.setResponse(POST(rawJson));
                    return checkReq;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Falha na conexão",Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Request request) {
                try {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mProgressBar.setEnabled(false);
                    handleConnection(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mAsyncTask.execute(user,type);
    }

    //for server usage
    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        JSONArray posts;
        response = request.getResponse();

        switch (request.getType()) {
            case "ALL":
                if(response.getString("status").equals("success")) {
                    posts = response.getJSONArray("posts");
                    List<Post> postList = listPosts(posts);
                    Log.e(getLocalClassName(), "num posts: " + postList.size());
                    if(postList.size() >= 0){
                        initDataset(postList);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mErrorText.setVisibility(View.INVISIBLE);
                        mAdapter = new CustomAdapter(getApplicationContext(), mDataset);
                        // Set CustomAdapter as the adapter for RecyclerView.
                        mRecyclerView.setAdapter(mAdapter);
                        // END_INCLUDE(initializeRecyclerView)
                    }
                } else {
                    Toast.makeText(getApplication(),"POR FAVOR TENTE MAIS TARDE",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //for server usage
    private List<Post> listPosts(JSONArray posts) throws JSONException {
        List<Post> list = new ArrayList<Post>();
        Log.e(getLocalClassName(), "on listPost" + posts.length());

        for (int i = 0; i < posts.length(); i++){
            JSONObject JSONpost = posts.getJSONObject(i);
            Log.e(getLocalClassName(), "on listPost: " + JSONpost);
            Post post = new Post();
            post.setId(JSONpost.getInt("id"));
            post.setUser(JSONpost.getInt("user"));
            post.setUserName(JSONpost.getString("email"));
            post.setPhoto(Constants.MAIN_URL+"uploads/"+post.getId()+".jpg");
            post.setLikes(JSONpost.getInt("likes"));
            post.setTitle(JSONpost.getString("title"));
            post.setLocation(JSONpost.getString("location"));
            post.setCreated_at(Timestamp.valueOf(JSONpost.getString("created_at")));
            list.add(post);
        }

        return list;
    }

    private JSONObject POST(JSONObject rawJson) throws IOException, ParseException, JSONException {
        JSONObject json = rawJson;

        Log.e(getLocalClassName(), "JSON IN: " + json);
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = null;
        switch (rawJson.getString("type")) {
            case "ALL":
                post = new HttpPost(Constants.MAIN_URL+"post/all");
                break;
        }

        try {
            StringEntity se = new StringEntity("json="+json.toString());
            post.addHeader("content-type", "application/x-www-form-urlencoded");
            post.setEntity(se);

            HttpResponse response;
            response = client.execute(post);
            String resFromServer = org.apache.http.util.EntityUtils.toString(response.getEntity());
            Log.e("Response: ", resFromServer);

            jsonResponse=new JSONObject(resFromServer);
            Log.e("Response from server: ", jsonResponse.getString("status")
                    + jsonResponse.getJSONArray("posts"));
            return jsonResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return jsonResponse;
        }
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getApplicationContext(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     * @param posts
     */
    private void initDataset(List<Post> posts) {
        mDataset = new Post[posts.size()];
        for (int i = 0; i < posts.size(); i++) {
            mDataset[i] = posts.get(i);
            Log.e(getLocalClassName(), "post " +i+ ":" + mDataset[i].toString());
        }
    }
}
