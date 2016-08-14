package com.codepath.apps.chirrup.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.activities.ProfileActivity;
import com.codepath.apps.chirrup.decorators.LinkifiedTextView;
import com.codepath.apps.chirrup.fragments.ReplyTweetFragment;
import com.codepath.apps.chirrup.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by santoshag on 8/5/16.
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    // Store a member variable for the tweets
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;
    FragmentManager mFragmentManager;
    TwitterClient client;

    // Pass in the tweet array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets, FragmentManager supportFragmentManager) {
        mTweets = tweets;
        mContext = context;
        mFragmentManager = supportFragmentManager;
        client = TwitterApplication.getRestClient();
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        //inflate the custom view
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        //return the viewholder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);


        return viewHolder;

    }

    //bind data to the viewholder
    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder viewHolder, int position) {
        final Tweet tweet = mTweets.get(position);

        TextView tvUserName = viewHolder.tvUserName;
        TextView tvScreenName = viewHolder.tvScreenName;
        LinkifiedTextView tvBody = viewHolder.tvBody;
        TextView tvRelativeTime = viewHolder.tvRelativeTime;
        ImageView ivProfileImg = viewHolder.ivProfileImg;
        ImageView ivPhoto = viewHolder.ivPhoto;
        final ImageView ivRetweetsCount = viewHolder.ivRetweetsCount;
        final ImageView ivLike = viewHolder.ivLike;
        ImageView ivReply = viewHolder.ivReply;

        final TextView tvRetweetCount = viewHolder.tvRetweetCount;
        final TextView tvLikeCount = viewHolder.tvLikeCount;

        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvRetweetCount.setText("");
        tvLikeCount.setText("");

        if (tweet.getRetweetCount() > 0) {
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
//            ivRetweetsCount.setImageDrawable(getContext().getResources().getDrawable(R.drawable.retweet_on));
        }


        if (tweet.getFavoritesCount() > 0) {
            tvLikeCount.setText(String.valueOf(tweet.getFavoritesCount()));

        }
        if(tweet.getfavorited()){
            ivLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.like_on));
        }
        if(tweet.getRetweeted()){
            ivRetweetsCount.setImageDrawable(getContext().getResources().getDrawable(R.drawable.retweet_on));
        }
//        TextViewUtils.stripUnderlines(tvBody);
        tvRelativeTime.setText(tweet.getRelativeDate());
        ivProfileImg.setImageResource(android.R.color.transparent);
        ivPhoto.setImageResource(android.R.color.transparent);

        ivProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("user", Parcels.wrap(tweet.getUser()));
                getContext().startActivity(intent);
            }
        });

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyTweetFragment myDialog = ReplyTweetFragment.newInstance(true, tweet, tweet.getUser());
                myDialog.show(mFragmentManager, "reply tweet");
            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorTweet(tweet, tvLikeCount, ivLike);

            }
        });

        ivRetweetsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reTweet(tweet, tvRetweetCount, ivRetweetsCount);

            }
        });

        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(mContext, 15, 0)).into(ivProfileImg);
        Glide.with(getContext()).load(tweet.getEntity().getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(mContext, 15, 0)).into(ivPhoto);
    }

    private void favorTweet(final Tweet tweet, final TextView tvFavorCount, final ImageView ivFavor) {

        client.favorTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "favorited" + response.toString());
                try {
                    if(response.getBoolean("favorited")){
                        tweet.setFavorite_count(Integer.parseInt(response.getString("favorite_count")));
                        ivFavor.setImageDrawable(getContext().getResources().getDrawable(R.drawable.like_on));
                    }else{
                        ivFavor.setImageDrawable(getContext().getResources().getDrawable(R.drawable.like_off));
                    }
                    tweet.setFavorited(response.getBoolean("favorited"));
                    if(response.getLong("favorite_count") > 0) {
                        tvFavorCount.setText(String.valueOf(response.getLong("favorite_count")));
                    }else{
                        tvFavorCount.setText("");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "failure" + errorResponse.toString() + "  " + tweet.getRemoteId());

            }
        }, tweet.getfavorited(), tweet.getRemoteId());

    }


    private void reTweet(final Tweet tweet, final TextView tvRetweetCount, final ImageView ivRetweetCount) {

        client.reTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "retweeted" + response.toString());
                try {
                    if(response.getBoolean("retweeted")){
                        tweet.setFavorite_count(Integer.parseInt(response.getString("retweet_count")));
                        ivRetweetCount.setImageDrawable(getContext().getResources().getDrawable(R.drawable.retweet_on));
                    }else{
                        ivRetweetCount.setImageDrawable(getContext().getResources().getDrawable(R.drawable.retweet_off));
                    }
                    tweet.setRetweeted(response.getBoolean("retweeted"));
                    if(response.getLong("retweet_count") > 0) {
                        tvRetweetCount.setText(String.valueOf(response.getLong("retweet_count")));
                    }else{
                        tvRetweetCount.setText("");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "failure" + errorResponse.toString() + "  " + tweet.getRemoteId());

            }
        }, tweet.getRetweeted(), tweet.getRemoteId());

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvUserName, tvRelativeTime, tvScreenName, tvLikeCount, tvRetweetCount;
        public LinkifiedTextView tvBody;
        public ImageView ivProfileImg, ivPhoto, ivLike, ivRetweetsCount, ivReply;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvBody = (LinkifiedTextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
            ivProfileImg = (ImageView) itemView.findViewById(R.id.ivProfileImg);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            ivRetweetsCount = (ImageView) itemView.findViewById(R.id.ivRetweetsCount);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
        }
    }


}

