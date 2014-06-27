package com.zenit.habclient;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import org.openhab.habdroid.R;
import org.openhab.habdroid.util.MyAsyncHttpClient;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABSetting {
    private String mBaseUrl;
    private String mSitemapRootUrl;
    private String mUsername = "";
    private String mPassword = "";
    private Context mContext;

    public OpenHABSetting(Context context) {
        mContext = context;
        mBaseUrl = mContext.getString(R.string.openhab_demo_url);
        mSitemapRootUrl = mBaseUrl + context.getString(R.string.openhab_demo_sitemap_postfix);
    }


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

    public AsyncHttpClient getAsyncHttpClient() {
        AsyncHttpClient asyncHttpClient = new MyAsyncHttpClient(mContext);
        asyncHttpClient.setBasicAuth(mUsername, mPassword);
        return asyncHttpClient;
    }

    public boolean runningInDemoMode() {
        return getBaseUrl().equalsIgnoreCase(mContext.getString(R.string.openhab_demo_url));
    }
}
