package com.codepath.apps.chirrup.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.codepath.apps.chirrup.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by santoshag on 8/5/16.
 */

@Table(name = "Messages")
public class DirectMessage extends Model{

    public User getRecipientUser() {
        return recipientUser;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public String getText() {
        return text;
    }

    public String getRelativeDate() {
        return relativeDate;
    }

    @Column(name = "rUser")
    User recipientUser;
    @Column(name = "sUser")
    User senderUser;
    @Column(name = "text")
    String text;
    @Column(name = "relative_date")
    String relativeDate;


    // Make sure to have a default constructor for every ActiveAndroid model
    public DirectMessage(){
        super();
    }
    public static DirectMessage fromJson(JSONObject jsonObject){

        DirectMessage message = new DirectMessage();
        try {
            message.text = jsonObject.getString("text");
            message.relativeDate = getRelativeTimeAgo(jsonObject.getString("created_at"));
            message.recipientUser = User.fromJSON(jsonObject.getJSONObject("recipient"));
            message.senderUser = User.fromJSON(jsonObject.getJSONObject("sender"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static ArrayList<DirectMessage> fromJsonArray(JSONArray jsonArray){

        ArrayList<DirectMessage> result = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++){
            try {
                Log.i("Message", jsonArray.getJSONObject(i).toString());
                DirectMessage message = fromJson(jsonArray.getJSONObject(i));

                result.add(message);
            } catch (JSONException e) {
                e.printStackTrace();
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
            e.printStackTrace();
        }

        return relativeDate;
    }


}
