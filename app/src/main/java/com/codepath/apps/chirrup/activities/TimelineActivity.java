package com.codepath.apps.chirrup.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.fragments.NewTweetFragment;
import com.codepath.apps.chirrup.fragments.TweetsListFragment;
import com.codepath.apps.chirrup.models.Tweet;
import com.codepath.apps.chirrup.models.User;
import com.codepath.apps.chirrup.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity {

    // Automatically finds each field by the specified ID.
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.ivAirplaneMode)
    ImageView ivAirplaneMode;
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private TwitterClient client;
    private TweetsListFragment tweetsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        //get the fragment only for the first time, otherwise the fragment is already available most likely
        if(savedInstanceState == null) {
            //access the fragment
            tweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }

        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get singleton rest client
        client = TwitterApplication.getRestClient();
        setupProfileImage();
        //setupSwipeToRefreshView();

    }



    private void setupProfileImage() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String profileImageUrl = response.getString("profile_image_url");
                    Log.i("profile_PHOTO", profileImageUrl);
                    Glide.with(getApplicationContext()).load(profileImageUrl).bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 60, 0)).into(ivProfilePhoto);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.checkForInternet()) {
            Toast.makeText(this, "Not connected to network. Using offline tweets.", Toast.LENGTH_SHORT).show();
            fabCompose.setVisibility(View.INVISIBLE);
            swipeContainer.setEnabled(false);
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .orderBy("remote_id DESC").execute();
            // Load the result into the adapter using `addAll`
            Log.i("sql", "loading data from offline: " + queryResults.size() + " " + queryResults.get(1).getUser().getProfileImageUrl());
            ivAirplaneMode.setVisibility(View.VISIBLE);
            tweetsListFragment.addAll(queryResults, true);
        } else {
            fabCompose.setVisibility(View.VISIBLE);
            ivAirplaneMode.setVisibility(View.GONE);
            //get timeline here
            populateTimeline("since_id", (long) 1);
            //setup swipe to refresh
            setupSwipeToRefreshView();
        }
    }

    // pass context to Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    private void populateMoreTimeline() {
        if (Tweet.lastTweetId != null) {
            populateTimeline("max_id", Tweet.lastTweetId);
        }
    }

    //send API request to get tweets and add it to listview
    private void populateTimeline(final String sinceOrMaxId, long count) {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                Boolean clearTweetListBeforeAdd = false;
                if (sinceOrMaxId.equals("since_id")) {
                    clearTweetListBeforeAdd = true;
                    new Delete().from(Tweet.class).execute(); // all records
                    new Delete().from(User.class).execute(); // all records
                }
                tweetsListFragment.addAll(Tweet.fromJsonArray(response), clearTweetListBeforeAdd);
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "onFailure" + errorResponse.toString());
                swipeContainer.setRefreshing(false);
            }
        }, sinceOrMaxId, count);
    }

    private void setupSwipeToRefreshView() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline("since_id", (long) 1);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void composeNewTweet(View view) {
        NewTweetFragment myDialog = new NewTweetFragment();

        FragmentManager fm = getSupportFragmentManager();
        myDialog.show(fm, "new tweet");

    }

}
