package com.maadlabs.twitterui.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brainfreak on 11/27/14.
 */

public class TweetLoad {

    @SerializedName("type")
    private String mType;
    @SerializedName("result")
    private TweetResult mResult;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public TweetResult getResult() {
        return mResult;
    }

    public void setResult(TweetResult result) {
        mResult = result;
    }
}
