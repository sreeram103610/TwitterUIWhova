package com.maadlabs.twitterui.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brainfreak on 11/27/14.
 */
public class Status {

    @SerializedName("is_retweeted")
    private boolean mRetweet;

    @SerializedName("ret_count")
    private Long mRetweetCount;

    @SerializedName("text")
    private String mText;

    @SerializedName("user_screen_name")
    private String mUserScreenName;

    @SerializedName("time")
    private String mTimeStamp;

    @SerializedName("tweet_id")
    private String mTweetId;

    @SerializedName("user_pic")
    private String mUserPicture;

    @SerializedName("media_pic_url")
    private String mStatusPicture;

    @SerializedName("hashtag")
    private String mHashTag;

    @SerializedName("is_favorite")
    private boolean mFavourite;

    @SerializedName("user_name")
    private String mUserName;

    @SerializedName("fav_count")
    private Long mFavouriteCount;

    public boolean isRetweet() {
        return mRetweet;
    }

    public void setRetweet(boolean retweet) {
        mRetweet = retweet;
    }

    public Long getRetweetCount() {
        return mRetweetCount;
    }

    public void setRetweetCount(Long retweetCount) {
        mRetweetCount = retweetCount;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getUserScreenName() {
        return mUserScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        mUserScreenName = userScreenName;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getTweetId() {
        return mTweetId;
    }

    public void setTweetId(String tweetId) {
        mTweetId = tweetId;
    }

    public String getUserPicture() {
        return mUserPicture;
    }

    public void setUserPicture(String userPicture) {
        mUserPicture = userPicture;
    }

    public String getStatusPicture() {
        return mStatusPicture;
    }

    public void setStatusPicture(String statusPicture) {
        mStatusPicture = statusPicture;
    }

    public String getHashTag() {
        return mHashTag;
    }

    public void setHashTag(String hashTag) {
        mHashTag = hashTag;
    }

    public boolean isFavourite() {
        return mFavourite;
    }

    public void setFavourite(boolean favourite) {
        mFavourite = favourite;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public Long getFavouriteCount() {
        return mFavouriteCount;
    }

    public void setFavouriteCount(Long favouriteCount) {
        mFavouriteCount = favouriteCount;
    }
}
