package com.codepath.apps.chirrup.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.decorators.LinkifiedTextView;
import com.codepath.apps.chirrup.fragments.ReplyTweetFragment;
import com.codepath.apps.chirrup.models.Entity;
import com.codepath.apps.chirrup.models.Tweet;
import com.codepath.apps.chirrup.models.User;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivProfileImg)
    ImageView ivProfileImg;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.tvBody)
    LinkifiedTextView tvBody;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.tvTweetTime)
    TextView tvTweetTime;
    @BindView(R.id.tvLikeCount)
    TextView tvLikeCount;
    @BindView(R.id.tvRetweetCount)
    TextView tvRetweetCount;
    @BindView(R.id.ivRetweetsCount)
    LikeButton ivRetweetsCount;
    @BindView(R.id.ivLike)
    LikeButton ivLike;
    @BindView(R.id.ivReply)
    LikeButton ivReply;

    TwitterClient client;

    private Tweet tweet;
    private User user;
    private Entity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        entity = (Entity) Parcels.unwrap(getIntent().getParcelableExtra("entity"));

        if (tweet != null) {
            Log.i("Tweetdetail", user.getName());
            loadViewItems(tweet, user, entity);
        } else {
            Log.i("Tweetdetail", " tweet is null!");

        }
    }

    private void loadViewItems(final Tweet tweet, User user, Entity entity) {
        tvUserName.setText(user.getName());
        tvScreenName.setText(user.getScreenName());
        tvBody.setText(tweet.getBody());
        tvTweetTime.setText(tweet.getRelativeDate());
        Picasso.with(this).load(user.getProfileImageUrl()).transform(new RoundedCornersTransformation(15, 0)).into(ivProfileImg);
        if (entity != null) {
            Picasso.with(this).load(tweet.getEntity().getMediaUrl()).transform(new RoundedCornersTransformation(15, 0)).into(ivPhoto);
        }
        tvRetweetCount.setText("");
        tvLikeCount.setText("");

        if (tweet.getRetweetCount() > 0) {
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        }
        if(tweet.getRetweeted()){
            ivRetweetsCount.setLiked(true);

        }
        if (tweet.getFavoritesCount() > 0) {
            tvLikeCount.setText(String.valueOf(tweet.getFavoritesCount()));
            ivLike.setLiked(true);

        }
        if(tweet.getfavorited()){
            ivLike.setLiked(true);

        }

        ivLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                favorTweet(tweet, tvLikeCount, ivLike);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                favorTweet(tweet, tvLikeCount, ivLike);
            }
        });

        ivRetweetsCount.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                reTweet(tweet, tvRetweetCount, ivRetweetsCount);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                reTweet(tweet, tvRetweetCount, ivRetweetsCount);
            }
        });

        ivReply.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                replyToTweet();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                replyToTweet();
            }
        });
    }

    private void favorTweet(final Tweet tweet, final TextView tvFavorCount, final LikeButton ivFavor) {

        client.favorTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "favorited" + response.toString());
                try {
                    if(response.getBoolean("favorited")){
                        tweet.setFavorite_count(Integer.parseInt(response.getString("favorite_count")));
                        ivLike.setLiked(true);
                    }else{
                        ivLike.setLiked(false);

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
                Log.d("DEBUG", responseString.toString() );
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        }, tweet.getfavorited(), tweet.getRemoteId());

    }

    private void reTweet(final Tweet tweet, final TextView tvRetweetCount, final LikeButton ivRetweetCount) {

        client.reTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "retweeted" + response.toString());
                try {
                    if(response.getBoolean("retweeted")){
                        tweet.setFavorite_count(Integer.parseInt(response.getString("retweet_count")));
                        ivRetweetCount.setLiked(true);

                    }else {
                        ivRetweetCount.setLiked(false);
                    }
                    tweet.setRetweeted(response.getBoolean("retweeted"));
                    if(response.getLong("retweet_count") > 0) {
                        tvRetweetCount.setText(String.valueOf(response.getLong("retweet_count")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", responseString.toString() );

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString() );

            }
        }, tweet.getRetweeted(), tweet.getRemoteId());

    }

    public void replyToTweet() {

        ReplyTweetFragment myDialog = ReplyTweetFragment.newInstance(true, tweet, user);
        FragmentManager fm = getSupportFragmentManager();
        myDialog.show(fm, "reply tweet");
    }

    // pass context to Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }



}
