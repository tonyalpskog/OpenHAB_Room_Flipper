package com.zenit.habclient;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import org.openhab.habdroid.util.MyAsyncHttpClient;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSetting {
    private String mBaseUrl = "https://demo.openhab.org:8443/";
    private String mSitemapRootUrl = "";
    private String mUsername = "";
    private String mPassword = "";


    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public void setSitemapRootUrl(String sitemapRootUrl) {
        mSitemapRootUrl = sitemapRootUrl;
    }

    public String getSitemapRootUrl() {return mSitemapRootUrl;}

    public AsyncHttpClient getAsyncHttpClient(Context context) {
        AsyncHttpClient asyncHttpClient = new MyAsyncHttpClient(context);
        asyncHttpClient.setBasicAuth(mUsername, mPassword);
        return asyncHttpClient;
    }
}
