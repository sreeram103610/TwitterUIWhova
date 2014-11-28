package com.maadlabs.twitterui.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by brainfreak on 11/27/14.
 */
public class TweetResult {

    @SerializedName("max_id")
    private Long mMaxId;
    @SerializedName("is_anonymous")
    private boolean mAnonymous;
    @SerializedName("statuses")
    private ArrayList<Status> mStatusList;

    public Long getMaxId() {
        return mMaxId;
    }

    public void setMaxId(Long maxId) {
        mMaxId = maxId;
    }

    public boolean isAnonymous() {
        return mAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        mAnonymous = anonymous;
    }

    public ArrayList<Status> getStatusList() {
        return mStatusList;
    }

    public void setStatusList(ArrayList<Status> statusList) {
        mStatusList = statusList;
    }
}
