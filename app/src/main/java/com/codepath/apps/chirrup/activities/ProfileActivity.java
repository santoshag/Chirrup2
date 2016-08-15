package com.codepath.apps.chirrup.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.chirrup.R;
import com.codepath.apps.chirrup.TwitterApplication;
import com.codepath.apps.chirrup.TwitterClient;
import com.codepath.apps.chirrup.fragments.UserTimelineFragment;
import com.codepath.apps.chirrup.models.User;
import com.codepath.apps.chirrup.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    @BindView(R.id.ivProfilePic)
    ImageView ivProfilePic;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    public enum Follow {
        Following, Follower;
    }

    ;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        client = TwitterApplication.getRestClient();

        if (savedInstanceState == null) {
            //get screen name
            if (getIntent().getBooleanExtra("from_user_span", false)) {

                Log.i("name", getIntent().getStringExtra("screen_name"));

                lookupUser(getIntent().getStringExtra("screen_name"));
            } else {
                user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
                setupView();
            }


        }
    }

    private void setupView() {
        String screenName = user.getScreenName();
        UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
        //display user fragment dynamically within this activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, fragmentUserTimeline);
        ft.commit();

        collapsingToolbar.setTitle(user.getName());

        tvName.setText(user.getName());
        tvScreenName.setText(user.getScreenName());
        tvName.setText(user.getName());
        tvDescription.setText(user.getDescription());
        tvFollowers.setText(getCustomTextForFollow("FOLLOWERS", user.getFollowersCount()), TextView.BufferType.EDITABLE);
        tvFollowing.setText(getCustomTextForFollow("FOLLOWING", user.getFollowingCount()), TextView.BufferType.EDITABLE);

        Log.i("user.getProfileImageU", user.getProfileImageUrl());

        Picasso.with(getApplicationContext()).load(user.getProfileImageUrl()).transform(new RoundedCornersTransformation(30, 0)).into(ivProfilePic);
    }

    private void lookupUser(String screenName) {
        client.lookupUser(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("DEBUG", "success" + response.toString());
                user = User.fromJSON(response);
                setupView();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "onFailure" + responseString);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "onFailure" + errorResponse.toString());

            }
        });
    }

    private SpannableStringBuilder getCustomTextForFollow(String str, int count) {

        String countStr = Utils.getRelativeNum(count);
        // Create a span that will make the text red
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
                getResources().getColor(R.color.primaryTextColor));

        // Use a SpannableStringBuilder so that both the text and the spans are mutable
        SpannableStringBuilder ssb = new SpannableStringBuilder(countStr);
//
        // Apply the color span
        ssb.setSpan(
                foregroundColorSpan,            // the span to add
                0,                                 // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder


        Typeface mediumTypeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_medium.ttf");
        CustomTypefaceSpan ctf = new CustomTypefaceSpan(mediumTypeface);

        // SPAN_EXCLUSIVE_EXCLUSIVE means to not extend the span when additional
        // text is added in later

        // Apply the typeface span
        ssb.setSpan(
                ctf,            // the span to add
                0,                                 // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder

        AbsoluteSizeSpan absoluteSizeSpan40 = new AbsoluteSizeSpan(40);
        // Apply the typeface span
        ssb.setSpan(
                absoluteSizeSpan40,            // the span to add
                0,                                 // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder

        // Add a blank space
        ssb.append(" ");

        AbsoluteSizeSpan absoluteSizeSpan35 = new AbsoluteSizeSpan(35);

        // Add the secondWord and apply the size span to only the second word
        ssb.append(str);
        ssb.setSpan(
                absoluteSizeSpan35,                                // the span to add
                ssb.length() - str.length(),        // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder

        Typeface lightTypeFace = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        CustomTypefaceSpan lightFontSpan = new CustomTypefaceSpan(lightTypeFace);
        // Apply the font span to only the second word
        ssb.setSpan(
                lightFontSpan,                                // the span to add
                ssb.length() - str.length(),        // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder

        // Create a span that will make the text red
        ForegroundColorSpan textColorSpan = new ForegroundColorSpan(
                getResources().getColor(R.color.textColorSecondary));


        // Apply the color span
        ssb.setSpan(
                textColorSpan,            // the span to add
                ssb.length() - str.length(),        // the start of the span (inclusive)
                ssb.length(),                      // the end of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // behavior when text is later inserted into the SpannableStringBuilder


        return ssb;
    }

    // pass context to Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    public void onFollowingCountClick(View view) {
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        intent.putExtra("Follow", Follow.Following);
        startActivity(intent);
    }

    public void onFollowersCountClick(View view) {
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        intent.putExtra("Follow", Follow.Follower);
        startActivity(intent);
    }


    public class CustomTypefaceSpan extends MetricAffectingSpan {
        private final Typeface typeface;

        public CustomTypefaceSpan(final Typeface typeface) {
            this.typeface = typeface;
        }

        @Override
        public void updateDrawState(final TextPaint drawState) {
            apply(drawState);
        }

        @Override
        public void updateMeasureState(final TextPaint paint) {
            apply(paint);
        }

        private void apply(final Paint paint) {
            final Typeface oldTypeface = paint.getTypeface();
            final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
            final int fakeStyle = oldStyle & ~typeface.getStyle();

            if ((fakeStyle & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fakeStyle & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(typeface);
        }
    }


}
