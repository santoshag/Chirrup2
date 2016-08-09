package com.codepath.apps.chirrup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.activities.DetailActivity;
import com.codepath.apps.chirrup.adapters.TweetsAdapter;
import com.codepath.apps.chirrup.decorators.DividerItemDecoration;
import com.codepath.apps.chirrup.decorators.ItemClickSupport;
import com.codepath.apps.chirrup.models.Tweet;
import com.codepath.apps.chirrup.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by santoshag on 8/9/16.
 */
public class TweetsListFragment extends Fragment {


    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    private TweetsAdapter tweetsAdapter;
    private ArrayList<Tweet> tweetList;

    //inflation
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, v);
        setupTweetsAdapter();
        return v;
    }

    //creation life cycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //non referencing view jobs go here
        tweetList = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(getActivity(), tweetList);

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
//                if (totalItemsCount < 60) {
//                    getActivity().populateMoreTimeline();
//                }
            }
        });

    }



    public void addAll(List<Tweet> list, Boolean clearTweetListBeforeAdd){
        if(clearTweetListBeforeAdd){
            tweetList.clear();
        }
        tweetList.addAll(list);
        tweetsAdapter.notifyDataSetChanged();
    }


}
