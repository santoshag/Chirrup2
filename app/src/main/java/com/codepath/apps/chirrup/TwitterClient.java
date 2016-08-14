package com.codepath.apps.chirrup;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "G39JPM8gqGksbOeF9xeyXdsVf";       // Change this
    public static final String REST_CONSUMER_SECRET = "FUr3JvJxEQo5Ze5hEklbgWoHJ8Zqr9PoBqVc7z0oezhz7MMxA2"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cpchirrup"; // Change this (here and in manifest)

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }


    /************
     * GET METHODS
     ********************/

    public void getHomeTimeline(AsyncHttpResponseHandler repsonseHandler, String sinceOrMaxId, Long id) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        //specify the params
        RequestParams params = new RequestParams();
        if (sinceOrMaxId.equals("since_id")) {

            params.put("since_id", id);
            params.put("count", 25);

        } else {
            params.put(sinceOrMaxId, id);
        }
        getClient().get(apiUrl, params, repsonseHandler);
    }

    public void getLoggedUserProfile(AsyncHttpResponseHandler repsonseHandler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, null, repsonseHandler);

    }

    public void lookupUser(String screenName, AsyncHttpResponseHandler repsonseHandler) {
        String apiUrl = getApiUrl("users/show.json");
        //specify the params
        //specify the params
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, repsonseHandler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler repsonseHandler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        //specify the params
        //specify the params
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, repsonseHandler);
    }

    public void getMentionsTimeline(JsonHttpResponseHandler repsonseHandler, String sinceOrMaxId, long id) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        //specify the params
        RequestParams params = new RequestParams();
        if (sinceOrMaxId.equals("since_id")) {

            params.put("since_id", id);
            params.put("count", 25);

        } else {
            params.put(sinceOrMaxId, id);
        }
        getClient().get(apiUrl, params, repsonseHandler);
    }

    public void getFollowers(JsonHttpResponseHandler repsonseHandler, String screenName) {
        String apiUrl = getApiUrl("followers/list.json");
        //specify the params
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("count", 200);
        getClient().get(apiUrl, params, repsonseHandler);
    }


    public void getFriends(JsonHttpResponseHandler repsonseHandler, String screenName) {
        String apiUrl = getApiUrl("friends/list.json");
        //specify the params
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("count", 200);
        getClient().get(apiUrl, params, repsonseHandler);
    }

    public void searchTweets(JsonHttpResponseHandler repsonseHandler, String sinceOrMaxId, Long id, String query) {
        String apiUrl = getApiUrl("search/tweets.json");
        //specify the params
        RequestParams params = new RequestParams();
        /*if(sinceOrMaxId.equals("since_id")) {

            params.put("since_id", id);
            params.put("count", 25);

        }else {
            params.put(sinceOrMaxId, id);
        }*/
        params.put("q", query);
        getClient().get(apiUrl, params, repsonseHandler);
    }


    public void getDirectMessages(JsonHttpResponseHandler repsonseHandler) {
        String apiUrl = getApiUrl("direct_messages.json");
        //specify the params
        RequestParams params = new RequestParams();
        params.put("count", 20);
        params.put("since_id", 1);

        getClient().get(apiUrl, null, repsonseHandler);
    }

    /************
     * POST METHODS
     ********************/

    public void composeTweet(AsyncHttpResponseHandler repsonseHandler, String tweetBody, Boolean isReply, long id) {
        String apiUrl = getApiUrl("statuses/update.json");
        //specify the params
        RequestParams params = new RequestParams();
        params.put("status", tweetBody);
        if (isReply) {
            params.put("in_reply_to_status_id", id);
        }
        getClient().post(apiUrl, params, repsonseHandler);
    }

    public void favorTweet(AsyncHttpResponseHandler repsonseHandler, Boolean favourite, long id) {

        String apiUrl;

        if(favourite){
            apiUrl = getApiUrl("favorites/destroy.json");
        }else{
            apiUrl = getApiUrl("favorites/create.json");
        }
        //specify the params
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, repsonseHandler);
    }

    public void reTweet(AsyncHttpResponseHandler repsonseHandler, Boolean reTweet, long id) {
        String apiUrl;
        if(reTweet) {
            apiUrl = getApiUrl("statuses/unretweet/:id.json");
        }else{
            apiUrl = getApiUrl("statuses/retweet/:id.json");
        }
        //specify the params
        apiUrl = apiUrl.replace(":id", String.valueOf(id));
        Log.i("TAG", apiUrl);
        getClient().post(apiUrl, null, repsonseHandler);
    }
}