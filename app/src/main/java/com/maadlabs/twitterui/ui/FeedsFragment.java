package com.maadlabs.twitterui.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.maadlabs.twitterui.R;
import com.maadlabs.twitterui.model.Status;
import com.maadlabs.twitterui.model.TweetLoad;
import com.maadlabs.twitterui.service.ConnectionManager;
import com.maadlabs.twitterui.service.MyServer;
import com.maadlabs.twitterui.service.TwitterAPI;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FeedsFragment extends Fragment implements Callback<TweetLoad>, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String USER_ID = "whova_201411";
    View mView;
    ListView mTweetsListView;
    SwipeRefreshLayout mRefreshLayout;
    ButtonRectangle mRetryButton;
    LinearLayout mNoConnectionLinearLayout;
    CircularProgressBar mProgressBar;
    TwitterAPI mTwitterAPI;
    ArrayList<Status> mStatusArrayList;
    CustomListAdapter mTweetsAdapter;
    SharedPreferences mSharedPreferences;
    Context mContext;
    TextView mNoTweetsTextView;
    EditText mTweetEditText;
    TweetLoad mTweetLoad;

    public static FeedsFragment newInstance(String param1, String param2) {
        FeedsFragment fragment = new FeedsFragment();
        return fragment;
    }

    @Override
    public void success(TweetLoad tweetLoad, Response response) {

        if(tweetLoad.getResult().getMaxId() != -1) {
            mStatusArrayList.addAll(tweetLoad.getResult().getStatusList());
            mTweetsAdapter.notifyDataSetChanged();
            mSharedPreferences.edit().putLong("max_id", tweetLoad.getResult().getMaxId()).commit();
            mTweetLoad = tweetLoad;
            setConnectionViews(NetworkInfo.CONNECTED);
        } else if(mStatusArrayList.size() == 0) {
            setConnectionViews(NetworkInfo.NO_NEW_DATA);
        }
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void failure(RetrofitError error) {

        if(mStatusArrayList.size() == 0)
            setConnectionViews(NetworkInfo.DISCONNECTED);
        else
            if(mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.retryConnectionButton) {
            setConnectionViews(NetworkInfo.CONNECTING);
            mTwitterAPI.loadTweets(3, USER_ID, this);
        } else if(v.getId() == R.id.newTweetEditText) {
            ComposeTweetFragment composeTweetFragment = new ComposeTweetFragment();
            Bundle extras = new Bundle();
            extras.putString("type", "newTweet");
            extras.putString("user_name", "UserName");
            extras.putString("user_image", Integer.toString(R.drawable.user_place_holder));
            composeTweetFragment.setArguments(extras);
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, composeTweetFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onRefresh() {
        mTwitterAPI.loadTweets(3, USER_ID, mSharedPreferences.getLong("max_id", 0), this);

    }


    public enum NetworkInfo {
        CONNECTED, DISCONNECTED, CONNECTING, NO_NEW_DATA;
    }

    public FeedsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_feeds, container, false);
        initReferences();
        initProperties();
        initData();
        initListeners();
        initAdapters();
        return mView;
    }

    private void initProperties() {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_blue_dark, android.R.color.holo_blue_light,
                android.R.color.holo_blue_bright);
    }

    private void initAdapters() {

        mTweetsAdapter = new CustomListAdapter(getActivity().getBaseContext(), R.layout.tweet_row, mStatusArrayList);
        mTweetsListView.setAdapter(mTweetsAdapter);
    }

    private void initListeners() {
        mRetryButton.setOnClickListener(this);
        mTweetEditText.setOnClickListener(this);
    }

    private void initData() {
        mContext = getActivity().getBaseContext();
        mTwitterAPI = MyServer.getRESTAdapter().create(TwitterAPI.class);
        mStatusArrayList = new ArrayList<Status>();
        mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        initTweets();
    }

    private void initTweets() {

        setConnectionViews(NetworkInfo.CONNECTING);
            if(ConnectionManager.isOnline(getActivity().getBaseContext()))
                mTwitterAPI.loadTweets(3, USER_ID, this);
            else
                setConnectionViews(NetworkInfo.DISCONNECTED);
    }

    private void setConnectionViews(NetworkInfo status) {

        if(status == NetworkInfo.DISCONNECTED) {
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.GONE);
            mNoConnectionLinearLayout.setVisibility(View.VISIBLE);
            mNoTweetsTextView.setVisibility(View.GONE);
        } else if(status == NetworkInfo.CONNECTING) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshLayout.setVisibility(View.GONE);
            mNoConnectionLinearLayout.setVisibility(View.GONE);
            mNoTweetsTextView.setVisibility(View.GONE);
        } else if(status == NetworkInfo.CONNECTED) {
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mNoConnectionLinearLayout.setVisibility(View.GONE);
            mNoTweetsTextView.setVisibility(View.GONE);
        } else if(status == NetworkInfo.NO_NEW_DATA) {
            mNoTweetsTextView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.GONE);
            mNoConnectionLinearLayout.setVisibility(View.GONE);

        }
    }


    private void initReferences() {

        mTweetsListView = (ListView) mView.findViewById(R.id.tweetsListView);
        mRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        mRetryButton = (ButtonRectangle) mView.findViewById(R.id.retryConnectionButton);
        mNoConnectionLinearLayout = (LinearLayout) mView.findViewById(R.id.noConnectionLinearLayout);
        mProgressBar = (CircularProgressBar) mView.findViewById(R.id.circularProgressBar);
        mNoTweetsTextView = (TextView) mView.findViewById(R.id.noTweetsTextView);
        mTweetEditText = (EditText) mView.findViewById(R.id.newTweetEditText);
    }



}
