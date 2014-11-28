package com.maadlabs.twitterui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.maadlabs.twitterui.R;
import com.maadlabs.twitterui.model.TweetLoad;
import com.maadlabs.twitterui.service.ConnectionManager;
import com.maadlabs.twitterui.service.MyServer;
import com.maadlabs.twitterui.service.TweetService;
import com.maadlabs.twitterui.service.TwitterAPI;
import com.squareup.picasso.Picasso;

public class ComposeTweetFragment extends Fragment implements View.OnClickListener {

    View mView;
    ImageView mBackImageView;
    ButtonFlat mTweetButtonFlat;
    TextView mUserNameTextView;
    ImageView mUserImageView;
    Bundle mExtras;
    EditText mComposeMessageEditText;
    Context mContext;

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

    private void initUserViews() {
        if(mExtras.getString("type").equals("newTweet")) {
            mUserNameTextView.setText(mExtras.getString("user_name"));
            Picasso.with(getActivity().getBaseContext()).load(Integer.parseInt(mExtras.getString("user_image"))).fit()
                .into(mUserImageView);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        initReferences();
        initProperties();
        initListeners();
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
        bundle.putInt("user_id", 3);
        bundle.putString("user_name", FeedsFragment.USER_ID);
        bundle.putString("content", s);
        intent.putExtras(bundle);
        Log.i("tweet", "fn");
        mContext.startService(intent);
    }


}
