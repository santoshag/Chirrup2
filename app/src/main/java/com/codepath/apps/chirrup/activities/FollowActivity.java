package com.codepath.apps.chirrup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.adapters.FollowAdapter;
import com.codepath.apps.chirrup.decorators.DividerItemDecoration;
import com.codepath.apps.chirrup.decorators.ItemClickSupport;
import com.codepath.apps.chirrup.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FollowActivity extends AppCompatActivity {

    // Automatically finds each field by the specified ID.
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.rvFollows)
    RecyclerView rvFollows;
    private FollowAdapter followAdapter;
    private ArrayList<User> followList;
    private User user;
    private TwitterClient client;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        //non referencing view jobs go here
        followList = new ArrayList<>();
        followAdapter = new FollowAdapter(this, followList);
        setupfollowAdapter();

        ProfileActivity.Follow follow = (ProfileActivity.Follow) getIntent().getSerializableExtra("Follow");
        switch (follow) {
            case Follower:
                toolbarTitle.setText("Followers");
                populateFollowers();
                break;
            case Following:
                toolbarTitle.setText("Following");
                populateFriends();
                break;
        }


    }

    private void populateFollowers(){

        client.getFollowers(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG getHomeTimeline", response.toString());

                try {
                    followList.addAll(User.fromJSONArray(response.getJSONArray("users")));
                    followAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", responseString.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        }, user.getScreenName());

    }


    private void populateFriends(){

        client.getFriends(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG getHomeTimeline", response.toString());

                try {
                    followList.addAll(User.fromJSONArray(response.getJSONArray("users")));
                    followAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", responseString.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        }, user.getScreenName());

    }

    private void setupfollowAdapter() {

        rvFollows.setAdapter(followAdapter);
        // Setup layout manager for items with orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);

        ItemClickSupport.addTo(rvFollows).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        User user = followList.get(position);
                        Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                        startActivity(intent);
                    }
                }
        );

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvFollows.addItemDecoration(itemDecoration);
        rvFollows.setLayoutManager(layoutManager);
    }

    // pass context to Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

}
