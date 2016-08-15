package com.codepath.apps.chirrup.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.codepath.apps.chirrup.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by santoshag on 8/5/16.
 */

@Parcel(analyze={Tweet.class})   // add Parceler annotation here
@Table(name = "Tweets")
public class Tweet extends Model{

    @Column(name = "remote_id")
    private long remoteId;
    @Column(name = "body")
    private String body;
    @Column(name = "user")//, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "entities")//, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Entity entity;
    @Column(name = "created_at")
    private String createdAt;
    @Column(name = "relative_date")
    private String relativeDate;

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    @Column(name = "retweet_count")
    private int retweetCount = 0;
    @Column(name = "favorite_count")
    private int favorite_count = 0;

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    @Column(name = "favorited")
    private Boolean favorited;

    public Boolean getfavorited() {
        return favorited;
    }

    public static Long getLastTweetId() {
        return lastTweetId;
    }

    @Column(name = "retweeted")
    private Boolean retweeted;

    public Boolean getRetweeted() {
        return retweeted;
    }

    public int getFavoritesCount() {
        return favorite_count;
    }



    public int getRetweetCount() {
        return retweetCount;
    }

    // Make sure to have a default constructor for every ActiveAndroid model
    public Tweet(){
        super();
    }


    public String getRelativeDate() {
        return relativeDate;
    }
    public User getUser() {
        return user;
    }

    //for getting tweets in batches for endless scrolling
    public static Long lastTweetId;

    public long getRemoteId() {
        return remoteId;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static Tweet fromJson(JSONObject jsonObject){

        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.remoteId = jsonObject.getLong("id");
            if(jsonObject.has("retweet_count")) {
                tweet.retweetCount = Integer.parseInt(jsonObject.getString("retweet_count"));
            }


            if(jsonObject.has("favorite_count")) {
                tweet.favorite_count = Integer.parseInt(jsonObject.getString("favorite_count"));
            }
            User user = User.findOrCreateFromJSON(jsonObject.getJSONObject("user"));
            tweet.user = user;
            Entity entity = Entity.fromJSON(jsonObject.getJSONObject("entities"));
            tweet.entity = entity;
            tweet.relativeDate = getRelativeTimeAgo(jsonObject.getString("created_at"));

            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.favorited = jsonObject.getBoolean("favorited");
            if(tweet.getBody().startsWith("RT")) {
                tweet.favorited = false;
                tweet.favorite_count = 0;
            }
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJsonArray(JSONArray jsonArray){

        ArrayList<Tweet> result = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++){
            try {
                Tweet tweet = fromJson(jsonArray.getJSONObject(i));
                result.add(tweet);
                lastTweetId = tweet.getRemoteId() - 1;
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }

        return result;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = Utils.getTimeString(dateMillis);
        } catch (ParseException e) {
//            e.printStackTrace();
        }

        return relativeDate;
    }

    public Entity getEntity() {
        return entity;
    }
}
