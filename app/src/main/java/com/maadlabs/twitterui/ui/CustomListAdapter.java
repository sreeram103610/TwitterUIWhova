package com.maadlabs.twitterui.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maadlabs.twitterui.MainActivity;
import com.maadlabs.twitterui.R;
import com.maadlabs.twitterui.model.Status;
import com.maadlabs.twitterui.model.TweetType;
import com.maadlabs.twitterui.service.ConnectionManager;
import com.maadlabs.twitterui.service.TweetService;
import com.maadlabs.twitterui.util.CustomFonts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by brainfreak on 11/27/14.
 */
public class CustomListAdapter extends ArrayAdapter<Status> implements TweetService.ICallback {

    Context mContext;
    Activity mActivity;
    int mResource;
    ArrayList<Status> mStatusArrayList;
    View.OnClickListener mOnClickListener;
    TweetService mTweetService;
    Status mStatus;
    StatusHolder mHolder;
    boolean mServiceActive;
    private TweetType mServiceType;
    private boolean mBound;

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            TweetService.LocalBinder binder = (TweetService.LocalBinder) service;
            mTweetService = binder.getService();
            mTweetService.setCallback(CustomListAdapter.this);
            mTweetService.startNetworkOperations();
            mBound = true;
            Log.i("service", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Log.i("service", "disconnected");

        }
    };

    public CustomListAdapter(Activity activity, Context context, int resource, ArrayList<Status> mStatusArrayList) {
        super(context, resource, mStatusArrayList);
        this.mContext = context;
        this.mResource = resource;
        this.mStatusArrayList = mStatusArrayList;
        mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StatusHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);  // like a template to add values

            holder = new StatusHolder();
            holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.userNameTextView);
            holder.mStatusTextView = (TextView) convertView.findViewById(R.id.userTweetTextView);
            holder.mRetweetButton = (Button) convertView.findViewById(R.id.retweetButton);
            holder.mFavouriteButton = (Button) convertView.findViewById(R.id.favouriteButton);
            holder.mDisplayPictureImageView = (ImageView) convertView.findViewById(R.id.userDisplayPictureImageView);
            holder.mReplyButton = (Button) convertView.findViewById(R.id.replyButton);
            holder.mTweetImageView = (ImageView) convertView.findViewById(R.id.userTweetImageView);
            holder.mUserScreenNameTextView = (TextView) convertView.findViewById(R.id.userScreenNameTextView);
            convertView.setTag(holder);
        } else {
            holder = (StatusHolder) convertView.getTag();
        }

        Status status = mStatusArrayList.get(position);

        holder.mStatusTextView.setText(status.getText());
        holder.mUserNameTextView.setText(status.getUserName());
        holder.mUserScreenNameTextView.setText("@" + status.getUserScreenName());

        if((status.getStatusPicture() != null) && (status.getStatusPicture().length() > 0)) {
            holder.mTweetImageView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(status.getStatusPicture()).resize(dpToPx(120), dpToPx(120)).centerInside().into(
                    holder.mTweetImageView);
        }
        initListeners(holder, status);

        if (status.getUserPicture() != null && status.getUserPicture().length() > 0) {
            Picasso.with(getContext()).load(status.getUserPicture()).fit().into(holder.mDisplayPictureImageView);
        } else {
            Picasso.with(getContext()).load(R.drawable.user_place_holder).fit().into(holder.mDisplayPictureImageView);
        }


        if(status.isFavourite()) {
            holder.mFavouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_icon_checked, 0, 0, 0);
            holder.mFavouriteButton.setText(status.getFavouriteCount() + "");
        } else {
            holder.mFavouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_icon_unchecked, 0, 0, 0);
            if (status.getFavouriteCount() != null && status.getFavouriteCount() > 0)
                holder.mFavouriteButton.setText(status.getFavouriteCount() + "");
            else
                holder.mFavouriteButton.setText("");
        }




            if (status.isRetweet()) {
                holder.mRetweetButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_icon, 0, 0, 0);
                holder.mRetweetButton.setText(Long.toString(status.getRetweetCount()) + "");
            } else {
                holder.mRetweetButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_unchecked, 0, 0, 0);
                if (status.getRetweetCount() != null && status.getRetweetCount() > 0)
                    holder.mRetweetButton.setText(status.getRetweetCount() + "");
                else
                    holder.mRetweetButton.setText("");
            }

        CustomFonts.init(getContext(), convertView);
        return convertView;
    }



    private void initListeners(final StatusHolder holder, final Status status) {

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int viewId = v.getId();
                Intent intent = new Intent(getContext(), TweetService.class);
                Bundle bundle = new Bundle();

                if (ConnectionManager.isOnline(getContext())) {
                    if (viewId == R.id.replyButton) {

                        ComposeTweetFragment composeTweetFragment = new ComposeTweetFragment();
                        Bundle extras = new Bundle();
                        extras.putString("type", "reply_tweet");
                        extras.putString("user_name", "UserName");
                        extras.putString("user_image", Integer.toString(R.drawable.user_place_holder));
                        extras.putString("user_screen_name", status.getUserScreenName());
                        extras.putString("reply_to_id", status.getTweetId());
                        extras.putString("hash_tag", MainActivity.HASHTAG);
                        composeTweetFragment.setArguments(extras);
                        FragmentManager fragmentManager = mActivity.getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main, composeTweetFragment).addToBackStack(null).commit();

                    } else if (viewId == R.id.retweetButton) {

                        setCurrentItem(holder, status, TweetType.RETWEET);
                        String oldCountString = holder.mRetweetButton.getText().toString();

                        bundle.putString("type", "retweet");
                        bundle.putInt("user_id", FeedsFragment.USER_ID);
                        bundle.putString("event_name", FeedsFragment.EVENT_NAME);
                        bundle.putString("tweet_id", status.getTweetId());
                        intent.putExtras(bundle);

                        if(!status.isRetweet()) {
                            status.setRetweet(true);
                            holder.mRetweetButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_icon, 0, 0, 0);

                            if (oldCountString.length() > 0) {
                                holder.mRetweetButton.setText(Integer.parseInt(oldCountString) + 1 + "");
                            } else {
                                holder.mRetweetButton.setText(1 + "");
                            }

                        } else {
                            status.setRetweet(false);
                            holder.mRetweetButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_unchecked, 0, 0, 0);

                            if (oldCountString.length() > 0) {
                                holder.mRetweetButton.setText(Integer.parseInt(oldCountString) - 1 + "");
                            } else {
                                holder.mRetweetButton.setText("");
                            }
                        }
                        mContext.bindService(intent, mServiceConnection, mContext.BIND_AUTO_CREATE);

                    } else if (viewId == R.id.favouriteButton) {

                        setCurrentItem(holder, status, TweetType.FAVOURITE);
                        String oldCountString = holder.mFavouriteButton.getText().toString();

                        bundle.putString("type", "favourite_tweet");
                        bundle.putInt("user_id", FeedsFragment.USER_ID);
                        bundle.putString("event_name", FeedsFragment.EVENT_NAME);
                        bundle.putString("tweet_id", status.getTweetId());
                        intent.putExtras(bundle);

                        if(!status.isFavourite()) {
                            status.setFavourite(true);
                            holder.mFavouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_icon_checked, 0, 0, 0);

                            if (oldCountString.length() > 0 && Integer.parseInt(oldCountString) > 0) {
                                holder.mFavouriteButton.setText(Integer.parseInt(oldCountString) + 1 + "");
                            } else {
                                holder.mFavouriteButton.setText(1 + "");
                            }

                        } else {
                            status.setFavourite(false);
                            holder.mFavouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_icon_unchecked, 0, 0, 0);

                            if (oldCountString.length() > 0 && Integer.parseInt(oldCountString) > 1) {
                                holder.mFavouriteButton.setText(Integer.parseInt(oldCountString) - 1 + "");
                            } else {
                                holder.mFavouriteButton.setText("");
                            }
                        }
                        mContext.bindService(intent, mServiceConnection, mContext.BIND_AUTO_CREATE);
                    }
                } else {
                    Toast.makeText(getContext(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        };

        holder.mFavouriteButton.setOnClickListener(mOnClickListener);
        holder.mReplyButton.setOnClickListener(mOnClickListener);
        holder.mRetweetButton.setOnClickListener(mOnClickListener);
    }

    private void setCurrentItem(StatusHolder holder, Status status, TweetType type) {

        setServiceActive(true);
        mHolder = holder;
        mStatus = status;
        mServiceType = type;
    }

    private void setServiceActive(boolean b) {

        mServiceActive = b;
    }

    private boolean isServiceActive() {
        return mServiceActive;
    }

    @Override
    public void onResult(Bundle resultBundle) {

        if(mServiceType == TweetType.RETWEET) {
            if(!resultBundle.getString("result").contains("success")) {
                unsetOption(mHolder);

            }
        } else if(mServiceType == TweetType.FAVOURITE) {
            if(!resultBundle.getString("result").contains("success")) {
                unsetOption(mHolder);
            }
        }
        mStatus = null;
        mHolder = null;
        mServiceActive = false;
    }

    private void unsetOption(StatusHolder holder) {

        String count;

        if(mServiceType == TweetType.FAVOURITE) {
            holder.mFavouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favourite_icon_unchecked, 0, 0, 0);
            count = holder.mFavouriteButton.getText().toString();

            if(count.length() > 0 && Integer.parseInt(count) > 1) {
                holder.mFavouriteButton.setText(Integer.parseInt(count) - 1 + "");
            } else {
                holder.mFavouriteButton.setText("");
            }

            mStatus.setFavourite(false);
        } else if(mServiceType == TweetType.RETWEET) {
            holder.mRetweetButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_unchecked, 0, 0, 0);
            count = holder.mRetweetButton.getText().toString();

            if(count.length() > 0 && Integer.parseInt(count) > 1) {
                holder.mRetweetButton.setText(Integer.parseInt(count) - 1 + "");
            } else {
                holder.mRetweetButton.setText("");
            }
            mStatus.setRetweet(false);
        }
    }


    private int dpToPx(int dp)
    {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    static class StatusHolder
    {
        ImageView mDisplayPictureImageView;
        TextView mStatusTextView, mUserNameTextView, mUserScreenNameTextView;
        Button mFavouriteButton, mRetweetButton, mReplyButton;
        ImageView mTweetImageView;
    }



}
