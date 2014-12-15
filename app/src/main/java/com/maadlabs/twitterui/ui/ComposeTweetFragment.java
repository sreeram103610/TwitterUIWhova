package com.maadlabs.twitterui.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.maadlabs.twitterui.R;
import com.maadlabs.twitterui.service.ConnectionManager;
import com.maadlabs.twitterui.service.TweetService;
import com.maadlabs.twitterui.util.CustomFonts;
import com.squareup.picasso.Picasso;

public class ComposeTweetFragment extends Fragment implements View.OnClickListener, TweetService.ICallback {

    public static final String COMPOSE_MESSAGE_TEXT = "compose_message_text";
    View mView;
    ImageView mBackImageView;
    ButtonFlat mTweetButtonFlat;
    TextView mUserNameTextView;
    ImageView mUserImageView;
    Bundle mExtras;
    EditText mComposeMessageEditText;
    Context mContext;

    private TweetService mTweetService;
    private boolean mBound;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            TweetService.LocalBinder binder = (TweetService.LocalBinder) service;
            mTweetService = binder.getService();
            mTweetService.setCallback(ComposeTweetFragment.this);
            mBound = true;
            Log.i("service", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Log.i("service", "disconnected");

        }
    };

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initListeners() {
        mTweetButtonFlat.setOnClickListener(this);
        mBackImageView.setOnClickListener(this);
    }

    private void initProperties() {
        mExtras = getArguments();
        initUserViews();
        mContext = getActivity().getBaseContext();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(COMPOSE_MESSAGE_TEXT, mComposeMessageEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void initUserViews() {

        if(mExtras.getString("type").contains("reply_tweet")) {
            mComposeMessageEditText.setText("@" + mExtras.getString("user_screen_name"));
            mComposeMessageEditText.setText(mComposeMessageEditText.getText().toString() + " " + mExtras.getString("hash_tag"));
        } else {
            mComposeMessageEditText.setText(mExtras.getString("hash_tag"));
        }

        mUserNameTextView.setText(mExtras.getString("user_name"));
        Picasso.with(getActivity().getBaseContext()).load(Integer.parseInt(mExtras.getString("user_image"))).fit()
                .into(mUserImageView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if(savedInstanceState != null && savedInstanceState.containsKey(COMPOSE_MESSAGE_TEXT)) {
            mComposeMessageEditText.setText(savedInstanceState.getString(COMPOSE_MESSAGE_TEXT));
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        initReferences();
        initProperties();
        initListeners();

        CustomFonts.init(mContext, mView);
        return mView;
    }



    private void initReferences() {
        mBackImageView = (ImageView) mView.findViewById(R.id.backButtonImageView);
        mTweetButtonFlat = (ButtonFlat) mView.findViewById(R.id.tweetButtonFlat);
        mUserNameTextView = (TextView) mView.findViewById(R.id.userNameTextView);
        mUserImageView = (ImageView) mView.findViewById(R.id.userDisplayPictureImageView);
        mComposeMessageEditText = (EditText) mView.findViewById(R.id.composeTweetEditText);
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        if(viewId == R.id.backButtonImageView) {
            getActivity().getFragmentManager().popBackStack();
        } else if(viewId == R.id.tweetButtonFlat) {
            String content = mComposeMessageEditText.getText().toString();

            if(content.length() > 0 && content.length() < 140) {
                if(ConnectionManager.isOnline(getActivity().getBaseContext())) {
                    sendTweet(mComposeMessageEditText.getText().toString());
                    getActivity().getFragmentManager().popBackStack();
                }
            }
        }
    }

    private void sendTweet(String s) {

        Intent intent = new Intent(mContext, TweetService.class);
        Bundle bundle = new Bundle();

        bundle.putString("type", mExtras.getString("type"));
        bundle.putInt("user_id", 3);
        bundle.putString("event_name", FeedsFragment.EVENT_NAME);
        bundle.putString("content", s);
        bundle.putString("reply_to_id", mExtras.getString("reply_to_id", ""));
        intent.putExtras(bundle);
        Log.i("tweet", "fn");

        if(!mBound)
            mContext.bindService(intent, mServiceConnection, mContext.BIND_AUTO_CREATE);
        else
            mTweetService.startNetworkOperations(intent);
    }


    @Override
    public void onResult(Bundle resultBundle) {

    }

    @Override
    public void onDestroy() {
        if(mBound)
            mContext.unbindService(mServiceConnection);
        super.onDestroy();
    }
}
