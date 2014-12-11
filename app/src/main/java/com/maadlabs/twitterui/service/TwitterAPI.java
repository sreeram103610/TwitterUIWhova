package com.maadlabs.twitterui.service;

import com.maadlabs.twitterui.model.TweetResponse;
import com.maadlabs.twitterui.model.TweetLoad;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by brainfreak on 11/27/14.
 */
public interface TwitterAPI {

    @GET("/event/tw/load/")
    public void loadTweets(@Query("user_id") Integer userId, @Query("event") String eventId, Callback<TweetLoad> tweetLoadCallback);

    @GET("/event/tw/load/")
    public void loadTweets(@Query("user_id") Integer userId, @Query("event") String eventId, @Query("max_id") Long max_id, Callback<TweetLoad> tweetLoadCallback);

    @FormUrlEncoded
    @POST("/event/tw/new/")
    public TweetResponse sendNewTweet(@Field("user_id") Integer userId, @Field("event") String eventId, @Field("content") String content);

    @FormUrlEncoded
    @POST("/event/tw/new/")
    public TweetResponse sendNewTweet(@Field("user_id") Integer userId, @Field("event") String eventId, @Field("content") String content, @Field("reply_to_id") Long replyToId);

    @FormUrlEncoded
    @POST("/event/tw/retw/")
    public TweetResponse sendRetweet(@Field("user_id") Integer userId, @Field("event") String eventId, @Field("tweet_id") String tweetId);

    @FormUrlEncoded
    @POST("/event/tw/fav/")
    public TweetResponse sendFavouriteTweet(@Field("user_id") Integer userId, @Field("event") String eventId, @Field("tweet_id") String tweetId);

}
