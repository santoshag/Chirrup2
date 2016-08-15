package com.codepath.apps.chirrup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.activeandroid.query.Delete;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.models.Tweet;
import com.codepath.apps.chirrup.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by santoshag on 8/9/16.
 */
public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    //send API request to get tweets and add it to listview
    public void populateTimeline(String sinceOrMaxId, long count) {

        final String finalSinceOrMaxId = sinceOrMaxId;
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG getMentions", response.toString());
                Boolean clearTweetListBeforeAdd = false;
                if (finalSinceOrMaxId.equals("since_id")) {
                    clearTweetListBeforeAdd = true;
                    new Delete().from(Tweet.class).execute(); // all records
                    new Delete().from(User.class).execute(); // all records
                }
                addAll(Tweet.fromJsonArray(response), clearTweetListBeforeAdd);
                onFinishLoadMore();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "onFailure" + responseString);
                onFinishLoadMore();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "onFailure" + errorResponse.toString());
                onFinishLoadMore();
            }
        }, sinceOrMaxId, count);
    }
}
