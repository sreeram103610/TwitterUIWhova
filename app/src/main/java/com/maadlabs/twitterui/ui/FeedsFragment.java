package com.maadlabs.twitterui.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndetermininate;
import com.maadlabs.twitterui.MainActivity;
import com.maadlabs.twitterui.R;
import com.maadlabs.twitterui.model.Status;
import com.maadlabs.twitterui.model.TweetLoad;
import com.maadlabs.twitterui.service.ConnectionManager;
import com.maadlabs.twitterui.service.MyServer;
import com.maadlabs.twitterui.service.TwitterAPI;

import java.util.ArrayList;
import java.util.Objects;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FeedsFragment extends Fragment implements Callback<TweetLoad>, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String EVENT_NAME = "whova_201411";
    public static final int USER_ID = 3;
    View mView;
    ListView mTweetsListView;
    String mMaxTweetId;
    SwipeRefreshLayout mRefreshLayout;
    ButtonRectangle mRetryButton;
    Button mReplyButton, mRetweetButton, mFavouriteButton;
    LinearLayout mNoConnectionLinearLayout;
    ProgressBarCircularIndetermininate mProgressBar;
    TwitterAPI mTwitterAPI;
    ArrayList<Status> mStatusArrayList;
    CustomListAdapter mTweetsAdapter;
    SharedPreferences mSharedPreferences;
    Context mContext;
    TextView mNoTweetsTextView;
    EditText mTweetEditText;
    TweetLoad mTweetLoad;
    FrameLayout mFeedsFrameLayout;
    private boolean mIsLoading;

    public static FeedsFragment newInstance(String param1, String param2) {
        FeedsFragment fragment = new FeedsFragment();
        return fragment;
    }

    @Override
    public void success(TweetLoad tweetLoad, Response response) {

        if(tweetLoad.getResult().getMaxId() != -1) {
            mStatusArrayList.clear();
            mStatusArrayList.addAll(0, tweetLoad.getResult().getStatusList());
            mTweetsAdapter.notifyDataSetChanged();
            mMaxTweetId = mStatusArrayList.get(mStatusArrayList.size() - 1).getTweetId();
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
            mTwitterAPI.loadTweets(3, EVENT_NAME, this);
        } else if(v.getId() == R.id.newTweetEditText) {
            ComposeTweetFragment composeTweetFragment = new ComposeTweetFragment();
            Bundle extras = new Bundle();
            extras.putString("type", "new_tweet");
            extras.putString("user_name", "UserName");
            extras.putString("user_image", Integer.toString(R.drawable.user_place_holder));
            extras.putString("hash_tag", MainActivity.HASHTAG);
            composeTweetFragment.setArguments(extras);
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, composeTweetFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onRefresh() {
       // mTwitterAPI.loadTweets(3, EVENT_NAME, mSharedPreferences.getLong("max_id", 0), this);
        mTwitterAPI.loadTweets(3, EVENT_NAME, this);
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
        mRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright);
    }

    private void initAdapters() {

        mTweetsAdapter = new CustomListAdapter(getActivity(), getActivity().getBaseContext(), R.layout.tweet_row, mStatusArrayList);
        mTweetsListView.setAdapter(mTweetsAdapter);
    }

    private void initListeners() {
        mRetryButton.setOnClickListener(this);
        mTweetEditText.setOnClickListener(this);
        mTweetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Status status = (Status) parent.getAdapter().getItem(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + status.getUserScreenName() + "/status/" + status.getTweetId()));
                startActivity(browserIntent);
            }
        });

        mTweetsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(mIsLoading == false)
                    {
                        mIsLoading = true;
                        AsyncTask<Object, Object, ArrayList<Status>> loadMoreTweetsTask = new AsyncTask<Object, Object, ArrayList<Status>>() {

                            @Override
                            protected ArrayList<com.maadlabs.twitterui.model.Status> doInBackground(Object[] params) {
                                TweetLoad tweetLoad = mTwitterAPI.loadTweets(3, EVENT_NAME, Long.parseLong(mMaxTweetId));
                                if (tweetLoad.getResult().getMaxId() != -1) {
                                    mStatusArrayList.addAll(tweetLoad.getResult().getStatusList());
                                    mMaxTweetId = mStatusArrayList.get(mStatusArrayList.size() - 1).getTweetId();
                                }
                                return tweetLoad.getResult().getStatusList();
                            }

                            @Override
                            protected void onPostExecute(ArrayList<com.maadlabs.twitterui.model.Status> o) {
                                mTweetsAdapter.notifyDataSetChanged();
                                mIsLoading = false;
                            }
                        };
                        loadMoreTweetsTask.execute(new Object[1]);
                    }
                }
            }
        });

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
                mTwitterAPI.loadTweets(3, EVENT_NAME, this);
            else
                setConnectionViews(NetworkInfo.DISCONNECTED);
    }

    private void setConnectionViews(NetworkInfo status) {

        if(status == NetworkInfo.DISCONNECTED) {
            mProgressBar.setVisibility(View.GONE);
            mFeedsFrameLayout.setVisibility(View.GONE);
            mNoConnectionLinearLayout.setVisibility(View.VISIBLE);
            mNoTweetsTextView.setVisibility(View.GONE);
        } else if(status == NetworkInfo.CONNECTING) {
            mProgressBar.setVisibility(View.VISIBLE);
            mFeedsFrameLayout.setVisibility(View.GONE);
            mNoConnectionLinearLayout.setVisibility(View.GONE);
            mNoTweetsTextView.setVisibility(View.GONE);
        } else if(status == NetworkInfo.CONNECTED) {
            mProgressBar.setVisibility(View.GONE);
            mFeedsFrameLayout.setVisibility(View.VISIBLE);
            mNoConnectionLinearLayout.setVisibility(View.GONE);
            mNoTweetsTextView.setVisibility(View.GONE);
        } else if(status == NetworkInfo.NO_NEW_DATA) {
            mNoTweetsTextView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mFeedsFrameLayout.setVisibility(View.GONE);
            mNoConnectionLinearLayout.setVisibility(View.GONE);

        }
    }


    private void initReferences() {

        mTweetsListView = (ListView) mView.findViewById(R.id.tweetsListView);
        mRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        mRetryButton = (ButtonRectangle) mView.findViewById(R.id.retryConnectionButton);
        mNoConnectionLinearLayout = (LinearLayout) mView.findViewById(R.id.noConnectionLinearLayout);
        mProgressBar = (ProgressBarCircularIndetermininate) mView.findViewById(R.id.circularProgressBar);
        mNoTweetsTextView = (TextView) mView.findViewById(R.id.noTweetsTextView);
        mTweetEditText = (EditText) mView.findViewById(R.id.newTweetEditText);
        mFeedsFrameLayout = (FrameLayout) mView.findViewById(R.id.feedsFrameLayout);
    }



}
