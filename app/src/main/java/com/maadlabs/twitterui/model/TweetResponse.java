package com.maadlabs.twitterui.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brainfreak on 11/28/14.
 */
public class TweetResponse {

    @SerializedName("type")
    private String mType;
    @SerializedName("result")
    private String mResult;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }
}
