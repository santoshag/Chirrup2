package com.codepath.apps.chirrup.fragments;

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

import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.adapters.DirectMessageAdapter;
import com.codepath.apps.chirrup.decorators.DividerItemDecoration;
import com.codepath.apps.chirrup.models.DirectMessage;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by santoshag on 8/9/16.
 */
public class DirectMessagesFragment extends Fragment {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Nullable
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;
    @BindView(R.id.rvMessages)
    RecyclerView rvMessages;
    private DirectMessageAdapter messageAdapter;
    private ArrayList<DirectMessage> messageList;

    TwitterClient client;

    //inflation
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TweetsListFragment", "onCreateView");

        View v = inflater.inflate(R.layout.fragment_message_list, container, false);
        ButterKnife.bind(this, v);
        setupmessageAdapter();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
            //get timeline here
            populateTimeline();
            //setup swipe to refresh
            setupSwipeToRefreshView();
    }

    //creation life cycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        //non referencing view jobs go here
        messageList = new ArrayList<>();
        messageAdapter = new DirectMessageAdapter(getContext(), messageList);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    private void setupmessageAdapter() {
        rvMessages.setAdapter(messageAdapter);
        // Setup layout manager for items with orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        rvMessages.addItemDecoration(itemDecoration);
        rvMessages.setLayoutManager(layoutManager);

    }

    private void setupSwipeToRefreshView() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    protected void onFinishLoadMore(){
        swipeContainer.setRefreshing(false);
    }

    protected  void populateTimeline(){
        client.getDirectMessages(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG getHomeTimeline", response.toString());
                messageList.clear();
                messageList.addAll(DirectMessage.fromJsonArray(response));
                messageAdapter.notifyDataSetChanged();
                onFinishLoadMore();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "onFailure" + responseString.toString());
            }
        });

    }

}
