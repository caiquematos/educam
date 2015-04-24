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

package com.example.caique.educam.Timeline;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caique.educam.Components.Post;
import com.example.caique.educam.R;
import com.example.caique.educam.Tools.Tools;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private Post[] mDataSet;
    private Context mContext;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mUserText;
        private final TextView mSchoolText;
        private final TextView mDateText;
        private final TextView mLocationText;
        private final TextView mLikeText;
        private final ImageView mPhotoImage;
        private final ImageView mUserButton;
        private final ImageView mLikeButton;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });

            mDateText = (TextView) v.findViewById(R.id.date_text);
            mSchoolText = (TextView) v.findViewById(R.id.school_text);
            mUserText = (TextView) v.findViewById(R.id.user_text);
            mLocationText = (TextView) v.findViewById(R.id.location_text);
            mLikeText = (TextView) v.findViewById(R.id.like_text);
            mPhotoImage = (ImageView) v.findViewById(R.id.photo_image);
            mUserButton = (ImageView) v.findViewById(R.id.user_button);
            mLikeButton = (ImageView) v.findViewById(R.id.like_button);
        }

        public TextView getDateText() { return mDateText; }
        public TextView getSchoolText() { return mSchoolText; }
        public TextView getUserText() { return mUserText; }
        public TextView getLocationText() { return mLocationText; }
        public TextView getLikeText() { return mLikeText; }
        public ImageView getPhotoImage() { return mPhotoImage; }
        public ImageView getUserButton() { return mUserButton; }
        public ImageView getLikeButton() { return mLikeButton; }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(Context context, Post[] dataSet) {
        mDataSet = dataSet;
        mContext = context;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        Post local_post = mDataSet[position];

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getSchoolText().setText(local_post.getTitle());
        viewHolder.getDateText().setText(local_post.getCreated_at().toString());
        viewHolder.getUserText().setText(local_post.getUserName());
        viewHolder.getLocationText().setText(local_post.getLocation());
        viewHolder.getLikeText().setText(local_post.getLikes() + " likes");
        Tools.setPicUrl(local_post.getPhoto(), viewHolder.getPhotoImage());
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
