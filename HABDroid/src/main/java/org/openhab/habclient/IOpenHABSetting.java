package org.openhab.habclient;

import com.loopj.android.http.AsyncHttpClient;

public interface IOpenHABSetting {
    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getBaseUrl();

    void setBaseUrl(String baseUrl);

    void setSitemapRootUrl(String sitemapRootUrl);

    String getSitemapRootUrl();

    AsyncHttpClient createAsyncHttpClient();

    boolean runningInDemoMode();
}
