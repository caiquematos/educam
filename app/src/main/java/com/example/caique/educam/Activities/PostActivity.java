package com.example.caique.educam.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.caique.educam.Components.Post;
import com.example.caique.educam.Components.User;
import com.example.caique.educam.Database.EducamDbHandler;
import com.example.caique.educam.R;
import com.example.caique.educam.Tools.Constants;
import com.example.caique.educam.Tools.EducamPreferences;
import com.example.caique.educam.Tools.Request;
import com.example.caique.educam.Tools.Tools;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PostActivity extends Activity implements View.OnClickListener {
    //camera vars
    private ImageView mImageView;
    private ImageView mButton;
    private EditText mPostET;
    private EditText mLocateText;
    private Button mPostBT;
    private ImageView mLocateButton;
    private String mCurrentPhotoPath = "";
    static final int REQUEST_TAKE_PHOTO = 1;
    private AsyncTask<Post, Void, Request> mAsyncTask;

    private Post mPost;
    private EducamDbHandler mDB;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.OrangeRed);

        mImageView = (ImageView) findViewById(R.id.picture);
        mButton = (ImageView) findViewById(R.id.pictureButton);
        mPostET = (EditText) findViewById(R.id.post_edit);
        mLocateText = (EditText) findViewById(R.id.location_new_text);
        mPostBT = (Button) findViewById(R.id.post_button);
        mLocateButton = (ImageView) findViewById(R.id.location_button);
        mPostBT.setOnClickListener(this);
        mLocateButton.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mDB = new EducamDbHandler(getApplicationContext());
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
    protected void onResume() {
        super.onResume();
        if(EducamPreferences.getPhoto(getApplicationContext()).isEmpty()){
            Tools.setPic(EducamPreferences.getPhoto(getApplicationContext()), mImageView);
        }else {
            Log.e(getLocalClassName(), "Photo Preferences empty");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_camera) {
            dispatchTakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToMapsActivity() {
        try
        {
            Intent activity = new Intent(PostActivity.this, MapsActivity.class);
            startActivity(activity);
        }catch(Exception e){
            Log.e(this.getLocalClassName(),"" + e);
        }
    }

    //return the picture taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String photo = EducamPreferences.getPhoto(getApplication());
            if(!photo.isEmpty()) {
                Log.e(this.getLocalClassName(), "on ActivityResult with photo!");
                Tools.setPic(photo, mImageView);
            } else {
                Log.e(this.getLocalClassName(), "on ActivityResult with no photo!");
            }
        }
    }

    //for db local usage
    private void createPost() {
        mPost = new Post();
        mPost.setPhoto(mCurrentPhotoPath);
        mPost.setTitle(mPostET.getText().toString());
        mPost.setUser(EducamPreferences.getUserId(getApplicationContext()));
        mPost.setUserName(findUser(mPost.getUser()));
        mPost.setLikes(0);
        mPost.setLocation(mLocateText.getText().toString());
        mPost.setCreated_at(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        Log.e(this.getLocalClassName(), mPost.toString());
        mDB.insert(mPost);
    }

    private String findUser(int id) {
        User userFeedback = userFeedback(id);
        if(userFeedback != null) {
            //updating user with an id from database
            mUser = userFeedback;
            return mUser.getEmail();
        }else {
            Toast.makeText(getApplicationContext(), "ERRO AO BUSCAR USUÁRIO!!!", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private User userFeedback(int id) {
        return mDB.findUserById(id);
    }

    //evoke android camera activity to take a picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(this.getLocalClassName(), "" + ex);// Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //create an image and save it to external storage returning its path
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        EducamPreferences.savePhoto(getApplicationContext(), mCurrentPhotoPath);
        return image;
    }

    public Post getPost(){
        mPost = new Post();
        mPost.setPhoto(EducamPreferences.getPhoto(getApplicationContext()));
        mPost.setTitle(mPostET.getText().toString());
        mPost.setUser(EducamPreferences.getUserId(getApplicationContext()));
        mPost.setUserName("Missing Name");
        mPost.setLikes(0);
        mPost.setLocation(mLocateText.getText().toString());
        mPost.setCreated_at(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        Log.e(this.getLocalClassName(), mPost.toString());
        return mPost;
    }

    private JSONObject createJSONpost(Post post) throws JSONException {
        JSONObject JSONpost = new JSONObject();
        JSONpost.put("photo", prepareImage(post.getPhoto()));
        JSONpost.put("title", post.getTitle());
        JSONpost.put("user", post.getUser());
        JSONpost.put("likes", post.getLikes());
        JSONpost.put("location", post.getLocation());
        Log.e(this.getLocalClassName(), JSONpost.toString());
        return JSONpost;
    }

    private String prepareImage(String path){
        /* String encoded = Base64x.encodeFromFile(path);*/
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.v(getLocalClassName(), "ENCODED AQUI:" + encoded);
        return encoded;
        /*  Log.e(getLocalClassName(), "No prepare:" + path + " e " + path+ " e " + mCurrentPhotoPath);

        if (!path.isEmpty()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }*/
    }

    private void post(Post post){
        mAsyncTask = new AsyncTask<Post, Void, Request>(){
            private JSONObject rawJson = new JSONObject();
            private Request checkReq = new Request();

            @Override
            protected Request doInBackground(Post... posts) {
                try {
                    if( posts[0] != null ) {
                        rawJson = createJSONpost(posts[0]);
                        rawJson.put("type", "POST");
                        checkReq.setType("POST");
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

        mAsyncTask.execute(post);
    }

    public void handleConnection(Request request) throws JSONException {
        JSONObject response;
        response = request.getResponse();

        switch (request.getType()) {
            case "POST":
                if(response.getString("status").equals("success")) {
                    Toast.makeText(getApplication(),"POSTAGEM FINALIZADA",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(),"POR FAVOR TENTE MAIS TARDE",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private JSONObject POST(JSONObject rawJson) throws IOException, ParseException, JSONException {
        JSONObject json = rawJson;

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = null;
        switch (rawJson.getString("type")) {
            case "POST":
                post = new HttpPost(Constants.MAIN_URL+"post/post");
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
            Log.e("Response from server: ", jsonResponse.getString("status"));
            return jsonResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return jsonResponse;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.post_button:
                //createPost(); uncomment this for db local usage
                post(getPost());
                EducamPreferences.savePhoto(getApplicationContext(), "");
                NavUtils.navigateUpFromSameTask(this);
            break;
            case R.id.location_button:
                goToMapsActivity();
            break;
            case R.id.pictureButton:
                dispatchTakePictureIntent();
            break;
        }
    }
}