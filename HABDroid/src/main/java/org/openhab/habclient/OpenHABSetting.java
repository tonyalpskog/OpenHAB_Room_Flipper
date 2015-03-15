package org.openhab.habclient;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import org.openhab.habclient.dagger.ApplicationContext;
import org.openhab.habdroid.R;
import org.openhab.habdroid.util.MyAsyncHttpClient;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2014.
 */
@Singleton
public class OpenHABSetting implements IOpenHABSetting {
    private String mBaseUrl;
    private String mSitemapRootUrl;
    private String mUsername = "";
    private String mPassword = "";
    private final Context mContext;

    @Inject
    public OpenHABSetting(@ApplicationContext Context context) {
        mContext = context;
        mBaseUrl = mContext.getString(R.string.openhab_demo_url);
        mSitemapRootUrl = mBaseUrl + context.getString(R.string.openhab_demo_sitemap_postfix);
    }

    @Override
    public String getUsername() {
        return mUsername;
    }

    @Override
    public void setUsername(String username) {
        mUsername = username;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public String getBaseUrl() {
        return mBaseUrl;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    @Override
    public void setSitemapRootUrl(String sitemapRootUrl) {
        mSitemapRootUrl = sitemapRootUrl;
    }

    @Override
    public String getSitemapRootUrl() {return mSitemapRootUrl;}

    @Override
    public AsyncHttpClient createAsyncHttpClient() {
        AsyncHttpClient asyncHttpClient = new MyAsyncHttpClient(mContext);
        asyncHttpClient.setBasicAuth(mUsername, mPassword);
        asyncHttpClient.addHeader("Accept", "application/xml");
        asyncHttpClient.setTimeout(30000);
        return asyncHttpClient;
    }

    @Override
    public boolean runningInDemoMode() {
        return getBaseUrl().equalsIgnoreCase(mContext.getString(R.string.openhab_demo_url));
    }
}
