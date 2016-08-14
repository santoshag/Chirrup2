package com.codepath.apps.chirrup.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.decorators.LinkifiedTextView;
import com.codepath.apps.chirrup.fragments.ReplyTweetFragment;
import com.codepath.apps.chirrup.models.Entity;
import com.codepath.apps.chirrup.models.Tweet;
import com.codepath.apps.chirrup.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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
    ImageView ivRetweetsCount;
    @BindView(R.id.ivLike)
    ImageView ivLike;


    private Tweet tweet;
    private User user;
    private Entity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

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

    private void loadViewItems(Tweet tweet, User user, Entity entity) {
        tvUserName.setText(user.getName());
        tvScreenName.setText(user.getScreenName());
        tvBody.setText(tweet.getBody());
        tvTweetTime.setText(tweet.getRelativeDate());
        Glide.with(this).load(user.getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(this, 15, 0)).into(ivProfileImg);
        if (entity != null) {
            Glide.with(this).load(tweet.getEntity().getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(this, 15, 0)).into(ivPhoto);
        }
        tvRetweetCount.setText("");
        tvLikeCount.setText("");

        if (tweet.getRetweetCount() > 0) {
            ivRetweetsCount.setImageResource(0);
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            ivRetweetsCount.setImageDrawable(this.getResources().getDrawable(R.drawable.retweet_on));
        }
        if (tweet.getFavouritesCount() > 0) {
            ivLike.setImageResource(0);
            tvLikeCount.setText(String.valueOf(tweet.getFavouritesCount()));
            ivLike.setImageDrawable(this.getResources().getDrawable(R.drawable.like_on));

        }
    }

    public void replyToTweet(View view) {

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
