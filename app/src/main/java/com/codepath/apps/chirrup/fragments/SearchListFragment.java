package com.codepath.apps.chirrup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by santoshag on 8/9/16.
 */
public class SearchListFragment extends TweetsListFragment{
    private TwitterClient client;

    public static SearchListFragment newInstance(String query) {
        
        Bundle args = new Bundle();
        
        SearchListFragment fragment = new SearchListFragment();
        args.putString("q", query);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("HomeTimelineFragment", "oncreate");
        client  = TwitterApplication.getRestClient();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    //send API request to get tweets and add it to listview
    //send API request to get tweets and add it to listview
    public void populateTimeline(String sinceOrMaxId, long count) {
        Log.i("hometime", "populateTimeline");
        String query = getArguments().getString("q");
        final String finalSinceOrMaxId = sinceOrMaxId;
        client.searchTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG getHomeTimeline", response.toString());
                Boolean clearTweetListBeforeAdd = false;
                if (finalSinceOrMaxId.equals("since_id")) {
                    clearTweetListBeforeAdd = true;
                }
                try {
                    addAll(Tweet.fromJsonArray(response.getJSONArray("statuses")), clearTweetListBeforeAdd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

            }
        }, sinceOrMaxId, count, query);
    }
}
