package com.codepath.apps.chirrup.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by santoshag on 8/5/16.
 */
@Parcel(analyze={User.class})   // add Parceler annotation here
@Table(name = "Users")
public class User extends Model{

    @Column(name = "remote_id", unique = true)
    public Long remoteId;
    @Column(name = "screen_name")
    public String screenName;
    @Column(name = "name")
    public String name;

    public String getDescription() {
        return description;
    }

    @Column(name = "description")
    public String description;
    @Column(name = "profile_image_url")
    public String profileImageUrl;
    @Column(name = "following_count")
    public int followingCount;
    @Column(name = "followers_count")
    public int followersCount;


    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    // Make sure to have a default constructor for every ActiveAndroid model
    public User(){
        super();
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public static User findOrCreateFromJSON(JSONObject json) {
        long rId = 0; // get just the remote id
        try {
            rId = json.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        User existingUser =
                new Select().from(User.class).where("remote_id = ?", rId).executeSingle();
        if (existingUser != null) {
            // found and return existing
            return existingUser;
        } else {
            // create and return new user
            User user = fromJSON(json);
            user.save();
            return user;
        }
    }

    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        try {
            user.remoteId = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.description = jsonObject.getString("description");
            user.screenName = "@" + jsonObject.getString("screen_name");
            user.profileImageUrl = getOriginalImage(jsonObject.getString("profile_image_url"));
            user.followingCount = jsonObject.getInt("friends_count");
            user.followersCount = jsonObject.getInt("followers_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static String getOriginalImage(String imageUrl){
        return imageUrl.replace("_normal", "");
    }


    public static ArrayList<User> fromJSONArray(JSONArray jsonArray){

        ArrayList<User> result = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++){

            try {
                Log.i("FOLLOW", jsonArray.getJSONObject(i).toString());
                User user = fromJSON(jsonArray.getJSONObject(i));
                result.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
