package com.codepath.apps.chirrup;

import android.content.Context;

import com.facebook.stetho.Stetho;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		Stetho.initializeWithDefaults(this);
		TwitterApplication.context = this;
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setFontAttrId(R.attr.fontPath)
				.build()
		);
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}

}