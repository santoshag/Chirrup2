package com.codepath.apps.chirrup.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.fragments.DirectMessagesFragment;
import com.codepath.apps.chirrup.fragments.HomeTimelineFragment;
import com.codepath.apps.chirrup.fragments.MentionsTimelineFragment;
import com.codepath.apps.chirrup.fragments.NewTweetFragment;
import com.codepath.apps.chirrup.fragments.TweetsListFragment;
import com.codepath.apps.chirrup.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity {

    // Automatically finds each field by the specified ID.
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;
    public static String loggedUserScreenName;


    private TwitterClient client;
    private TweetsListFragment tweetsListFragment;
    private String tabText[] = {"Home", "Mentions", "Messages"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupTabLayout();
        setpViews();

    }

    private void setpViews() {
        setupProfileImage();

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTweetFragment myDialog = new NewTweetFragment();
                FragmentManager fm = getSupportFragmentManager();
                myDialog.show(fm, "new tweet");

            }
        });
        //setupSwipeToRefreshView();
    }

    private void setupTabLayout() {
        //get the viewpager
        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        //set teh adapter
        vp.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        //find the sliding tab
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //attach the tab to viewpager
        tabStrip.setViewPager(vp);
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbarTitle.setText(tabText[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupProfileImage() {
        client.getLoggedUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                final User user = User.fromJSON(response);
                String profileImageUrl = user.getProfileImageUrl();
                Log.i("profile_PHOTO", profileImageUrl);
                loggedUserScreenName = user.getScreenName();
                Picasso.with(getApplicationContext()).load(profileImageUrl).transform(new CropCircleTransformation()).into(ivProfilePhoto);
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
                Log.d("DEBUG", responseString);
            }


        });

    }

    // pass context to Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        // Customize searchview text and hint colors
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.GRAY);
        et.setHint("Search tweets");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchView.clearFocus();
                Log.i("query", query);
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("q", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    public class TweetsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        private int tabIcons[] = {R.drawable.home, R.drawable.mention, R.drawable.dm};


        //adapter gets the manager to insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //gets the order of fragments withing the pager/tab layout
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else if (position == 2) {
                return new DirectMessagesFragment();

            } else {
                return null;
            }
        }


        //number of tabs
        @Override
        public int getCount() {
            return tabIcons.length;
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }
    }

}
