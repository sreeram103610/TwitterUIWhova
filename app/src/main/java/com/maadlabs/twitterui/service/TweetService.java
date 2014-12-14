package com.maadlabs.twitterui.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.maadlabs.twitterui.model.TweetResponse;

/**
 * Created by brainfreak on 11/28/14.
 */
public class TweetService extends Service {

    private Handler mHandler;
    private Bundle mResultBundle;
    private Intent mIntent;
    private ICallback mCallback;

    @Override
    public IBinder onBind(Intent intent) {

        mIntent = intent;
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public TweetService getService() {
            return TweetService.this;
        }
    }

    public Bundle getResultBundle() {
        return mResultBundle;
    }

    public interface ICallback {
        public void onResult(Bundle resultBundle);
    }

    public void setCallback(ICallback callback) {
        mCallback = callback;
    }

    public void startNetworkOperations() {

        mResultBundle = new Bundle();


        AsyncTask asyncTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                TwitterAPI twitterAPI = MyServer.getRESTAdapter().create(TwitterAPI.class);
                Bundle bundle = mIntent.getExtras();
                String requestType = bundle.getString("type");
                TweetResponse tweetResponse = null;

                if(requestType.equals("new_tweet")) {
                    tweetResponse = twitterAPI.sendNewTweet(bundle.getInt("user_id"), bundle.getString("event_name"), bundle.getString("content"));
                } else if(requestType.equals("favourite_tweet")) {
                    tweetResponse = twitterAPI.sendFavouriteTweet(bundle.getInt("user_id"), bundle.getString("event_name"), bundle.getString("tweet_id"));
                } else if(requestType.equals("retweet")) {
                    tweetResponse = twitterAPI.sendRetweet(bundle.getInt("user_id"), bundle.getString("event_name"), bundle.getString("tweet_id"));
                } else if(requestType.equals("reply_tweet")) {
                    tweetResponse = twitterAPI.sendNewTweet(bundle.getInt("user_id"), bundle.getString("event_name"), bundle.getString("content"), Long.parseLong(bundle.getString("reply_to_id")));
                }

                if (tweetResponse.getResult().contains("success") && (requestType.equals("new_tweet") || requestType.equals("reply_tweet"))) {
                    mResultBundle.putString("message", "Tweet Sent!");

                } else if(tweetResponse.getResult().contains("fail")) {
                    mResultBundle.putString("message", "Connection Error");
                }

                mResultBundle.putString("result", tweetResponse.getResult());

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                if(mResultBundle.getString("message") != null) {
                    Toast.makeText(getApplicationContext(), mResultBundle.getString("message"), Toast.LENGTH_LONG).show();
                    mCallback.onResult(mResultBundle);
                }
            }
        };

        asyncTask.execute(new Object[1]);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mIntent = intent;
        startNetworkOperations();
        return Service.START_NOT_STICKY;
    }
}
