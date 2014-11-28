package com.maadlabs.twitterui.service;

import com.maadlabs.twitterui.model.SentTweet;
import com.maadlabs.twitterui.model.TweetLoad;

import retrofit.Callback;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
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

    @POST("/event/tw/new/")
    public SentTweet sendNewTweet(@Query("user_id") Integer userId, @Query("event") String eventId, @Query("content") String content);

    @POST("/event/tw/new/")
    public SentTweet sendNewTweet(@Query("user_id") Integer userId, @Query("event") String eventId, @Query("content") String content, @Query("reply_to_id") Long replyToId);

}
