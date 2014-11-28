package com.maadlabs.twitterui.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.maadlabs.twitterui.model.SentTweet;
import com.maadlabs.twitterui.model.TweetLoad;

/**
 * Created by brainfreak on 11/28/14.
 */
public class TweetService extends Service {

    private Handler mHandler;
    private Bundle mResultBundle;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        mResultBundle = new Bundle();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        new Thread() {
            @Override
            public void run() {
                TwitterAPI twitterAPI = MyServer.getRESTAdapter().create(TwitterAPI.class);
                Bundle bundle = intent.getExtras();

                SentTweet tweetLoad = twitterAPI.sendNewTweet(bundle.getInt("user_id"), bundle.getString("user_name"), bundle.getString("content"));
                Message message = new Message();
                if(tweetLoad.getResult().contains("success")) {
                    mResultBundle.putString("message", "Tweet Sent!");
                } else {
                    mResultBundle.putString("message", "Error sending tweet");
                }
                message.setData(mResultBundle);
                mHandler.dispatchMessage(message);
            }
        }.start();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mResultBundle.getString("message"), Toast.LENGTH_LONG).show();
            }
        });
        return Service.START_NOT_STICKY;
    }
}
