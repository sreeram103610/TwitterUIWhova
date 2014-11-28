package com.maadlabs.twitterui.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maadlabs.twitterui.R;
import com.maadlabs.twitterui.model.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by brainfreak on 11/27/14.
 */
public class CustomListAdapter extends ArrayAdapter<Status> {

    Context mContext;
    int mResource;
    ArrayList<Status> mStatusArrayList;

    public CustomListAdapter(Context context, int resource, ArrayList<Status> mStatusArrayList) {
        super(context, resource, mStatusArrayList);
        this.mContext = context;
        this.mResource = resource;
        this.mStatusArrayList = mStatusArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        StatusHolder holder = null;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(mResource, parent, false);  // like a template to add values

            holder = new StatusHolder();
            holder.mUserNameTextView = (TextView) row.findViewById(R.id.userNameTextView);
            holder.mStatusTextView = (TextView) row.findViewById(R.id.userTweetTextView);
            holder.mRetweetButton = (Button) row.findViewById(R.id.retweetButton);
            holder.mFavouriteButton = (Button) row.findViewById(R.id.favouriteButton);
            holder.mDisplayPictureImageView = (ImageView) row.findViewById(R.id.userDisplayPictureImageView);
            holder.mReplyButton = (Button) row.findViewById(R.id.replyButton);
            row.setTag(holder);
        }

        else
        {
            row = convertView;
            holder = (StatusHolder) row.getTag();
        }

        Status status = mStatusArrayList.get(position);

        holder.mStatusTextView.setText(status.getText());
        holder.mUserNameTextView.setText(status.getUserName());
        if(status.getStatusPicture() != null && status.getStatusPicture().length() > 0) {
            Log.i("dp", status.getStatusPicture());
            Picasso.with(getContext()).load(status.getStatusPicture()).fit().into(holder.mDisplayPictureImageView);
        } else {
            Picasso.with(getContext()).load(R.drawable.user_place_holder).fit().into(holder.mDisplayPictureImageView);
        }
        if(status.getFavouriteCount() != null && status.getFavouriteCount() > 0)
            holder.mFavouriteButton.setText(status.getFavouriteCount() + "");
        if(status.getRetweetCount() != null && status.getRetweetCount() > 0)
            holder.mRetweetButton.setText(status.getRetweetCount() + "");

        return row;
    }



    static class StatusHolder
    {
        ImageView mDisplayPictureImageView;
        TextView mStatusTextView, mUserNameTextView;
        Button mFavouriteButton, mRetweetButton, mReplyButton;
        ImageView mMediaImageView;
    }

}
