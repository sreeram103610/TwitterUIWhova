package com.maadlabs.twitterui.service;

import com.maadlabs.twitterui.model.TweetLoad;

import retrofit.Callback;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by brainfreak on 11/27/14.
 */
public interface TwitterAPI {

    @GET("/event/tw/load/")
    public void loadTweets(@Query("user_id") Integer userId, @Query("event") String eventId, Callback<TweetLoad> tweetLoadCallback);

    @GET("/event/tw/load/")
    public void loadTweets(@Query("user_id") Integer userId, @Query("event") String eventId, @Query("max_id") Long max_id, Callback<TweetLoad> tweetLoadCallback);
}
