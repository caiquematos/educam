package com.example.caique.educam.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by caique on 15/03/15.
 */
public final class Tools {

    public static void setPic(String photoPath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        if(targetW == 0 || targetH == 0)
        {
            targetW = 1080;
            targetH = 661;
        }

        Log.e("on toools", "width" + targetW + "Height" + targetH);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public static void setPicUrl(final String urlString, final ImageView imageView){
        final Bitmap[] bmp = new Bitmap[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bmp[0]);
                    }
                });

                }
            }).start();
    }
}
