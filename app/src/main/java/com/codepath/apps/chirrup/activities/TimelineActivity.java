package com.codepath.apps.chirrup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.fragments.HomeTimelineFragment;
import com.codepath.apps.chirrup.fragments.MentionsTimelineFragment;
import com.codepath.apps.chirrup.fragments.NewTweetFragment;
import com.codepath.apps.chirrup.fragments.TweetsListFragment;
import com.codepath.apps.chirrup.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity {

    // Automatically finds each field by the specified ID.
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private TwitterClient client;
    private TweetsListFragment tweetsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get the viewpager
        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        //set teh adapter
        vp.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        //find the sliding tab
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //attach the tab to viewpager
        tabStrip.setViewPager(vp);

        setupProfileImage();

        //setupSwipeToRefreshView();
    }

    private void setupProfileImage() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    final User user = User.fromJSON(response);
                    String profileImageUrl = user.getProfileImageUrl();
                    Log.i("profile_PHOTO", profileImageUrl);
                    Glide.with(getApplicationContext()).load(profileImageUrl).bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 60, 0)).into(ivProfilePhoto);
                    ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("user", Parcels.wrap(user));
                            startActivity(intent);
                        }
                    });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }


        });

    }

    // pass context to Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    public void composeNewTweet(View view) {
        NewTweetFragment myDialog = new NewTweetFragment();

        FragmentManager fm = getSupportFragmentManager();
        myDialog.show(fm, "new tweet");

    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter{
        private String tabTitles[] = { "Home", "Mentions"};

        //adapter gets the manager to insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        //gets the order of fragments withing the pager/tab layout
        @Override
        public Fragment getItem(int position) {
            if(position ==0){
                return new HomeTimelineFragment();
            }else if(position ==1){
                return new MentionsTimelineFragment();
            }else{
                return null;
            }
        }

        //return tabl title displayed at top
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //number of tabs
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

}
