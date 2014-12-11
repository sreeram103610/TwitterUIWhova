package com.maadlabs.twitterui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.maadlabs.twitterui.service.ConnectionManager;
import com.maadlabs.twitterui.service.MyServer;
import com.maadlabs.twitterui.service.TweetService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.RestAdapter;

/**
 * Created by brainfreak on 11/27/14.
 */
public class CustomListAdapter extends ArrayAdapter<Status> {

    Context mContext;
    int mResource;
    ArrayList<Status> mStatusArrayList;
    View.OnClickListener mOnClickListener;

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

        initListeners(holder, status);

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

    private void initListeners(final StatusHolder holder, final Status status) {

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int viewId = v.getId();
                Intent intent = new Intent(getContext(), TweetService.class);
                Bundle bundle = new Bundle();

                if(viewId == R.id.replyButton) {


                } else if(viewId == R.id.retweetButton) {

                    String oldCountString = holder.mRetweetButton.getText().toString();

                        if(ConnectionManager.isOnline(getContext())) {

                            bundle.putString("type", "retweet");
                            bundle.putInt("user_id", FeedsFragment.USER_ID);
                            bundle.putString("event_name", FeedsFragment.EVENT_NAME);
                            bundle.putString("tweet_id", status.getTweetId());
                            intent.putExtras(bundle);
                            getContext().startService(intent);
                            if(oldCountString.length() > 0) {
                                holder.mRetweetButton.setText(Integer.parseInt(oldCountString) + 1);
                            } else {
                                holder.mRetweetButton.setText(1+"");
                            }
                    }
                } else if(viewId == R.id.favouriteButton) {

                    String oldCountString = holder.mFavouriteButton.getText().toString();
                        if(ConnectionManager.isOnline(getContext())) {

                            bundle.putString("type", "favourite_tweet");
                            bundle.putInt("user_id", FeedsFragment.USER_ID);
                            bundle.putString("event_name", FeedsFragment.EVENT_NAME);
                            bundle.putString("tweet_id", status.getTweetId());
                            intent.putExtras(bundle);
                            getContext().startService(intent);
                            if(oldCountString.length() > 0) {
                                holder.mFavouriteButton.setText(Integer.parseInt(oldCountString) + 1);
                            } else {
                                holder.mFavouriteButton.setText(1+"");
                            }
                        }
                }
            }
        };

        holder.mFavouriteButton.setOnClickListener(mOnClickListener);
        holder.mReplyButton.setOnClickListener(mOnClickListener);
        holder.mRetweetButton.setOnClickListener(mOnClickListener);
    }


    static class StatusHolder
    {
        ImageView mDisplayPictureImageView;
        TextView mStatusTextView, mUserNameTextView;
        Button mFavouriteButton, mRetweetButton, mReplyButton;
        ImageView mMediaImageView;
    }

}
