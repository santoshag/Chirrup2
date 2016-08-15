package com.codepath.apps.chirrup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.activities.DetailActivity;
import com.codepath.apps.chirrup.adapters.TweetsAdapter;
import com.codepath.apps.chirrup.decorators.DividerItemDecoration;
import com.codepath.apps.chirrup.decorators.ItemClickSupport;
import com.codepath.apps.chirrup.models.Tweet;
import com.codepath.apps.chirrup.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.chirrup.utils.Utils;
import com.eyalbira.loadingdots.LoadingDots;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by santoshag on 8/9/16.
 */
public abstract class TweetsListFragment extends Fragment {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Nullable
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    private TweetsAdapter tweetsAdapter;
    private ArrayList<Tweet> tweetList;
    @Nullable
    @BindView(R.id.ivAirplaneMode)
    ImageView ivAirplaneMode;
    @BindView(R.id.ldProgress)
    LoadingDots ldProgress;
    Boolean airplaneMode = false;

    //inflation
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TweetsListFragment", "onCreateView");

        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, v);
        setupTweetsAdapter();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!Utils.checkForInternet()) {
            Toast.makeText(getActivity(), "Not connected to network.", Toast.LENGTH_SHORT).show();
            swipeContainer.setEnabled(false);
            List<Tweet> queryResults = new Select().from(Tweet.class)
                    .orderBy("remote_id DESC").execute();
            addAll(queryResults, true);
        } else {
            airplaneMode = false;
            populateTimeline("since_id", (long) 1);
            //setup swipe to refresh
            setupSwipeToRefreshView();
        }
    }

    //creation life cycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //non referencing view jobs go here
        tweetList = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(getActivity(), tweetList, getActivity().getSupportFragmentManager());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    private void setupTweetsAdapter() {

        rvTweets.setAdapter(tweetsAdapter);
        // Setup layout manager for items with orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Tweet tweet = tweetList.get(position);
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        intent.putExtra("user", Parcels.wrap(tweet.getUser()));
                        intent.putExtra("entity", Parcels.wrap(tweet.getEntity()));
                        startActivity(intent);
                    }
                }
        );

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);
        rvTweets.setLayoutManager(layoutManager);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                //limit totalItemsCount to void "Rate limit exceeded" error
                //load more only when internet is available
                if (!airplaneMode && totalItemsCount < 60) {
                    populateTimeline("max_id", Tweet.lastTweetId);
                }else{
                    Toast.makeText(getActivity(), "Dont want to exceed rate: " + totalItemsCount, Toast.LENGTH_LONG ).show();
                }
            }
        });

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

    protected void addAll(List<Tweet> list, Boolean clearTweetListBeforeAdd){
        if(clearTweetListBeforeAdd){
            tweetList.clear();
        }
        tweetList.addAll(list);
        tweetsAdapter.notifyDataSetChanged();
    }

    protected void onFinishLoadMore(){
        ldProgress.setVisibility(View.GONE);
        rvTweets.setVisibility(View.VISIBLE);
        swipeContainer.setRefreshing(false);
    }

    protected abstract void populateTimeline(String sinceOrMaxId, long count);

}
