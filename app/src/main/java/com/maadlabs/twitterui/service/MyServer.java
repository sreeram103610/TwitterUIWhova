package com.maadlabs.twitterui.service;

import retrofit.RestAdapter;

/**
 * Created by brainfreak on 11/27/14.
 */
public class MyServer {

    private static final String BASE_URL = "http://www.whova.net:6767";
    private static RestAdapter mRestAdapter;

    public static RestAdapter getRESTAdapter() {
        if(mRestAdapter == null)
            mRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .build();
        return mRestAdapter;
    }


}
