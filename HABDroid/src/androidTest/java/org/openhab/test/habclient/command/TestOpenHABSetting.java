package org.openhab.test.habclient.command;

import com.loopj.android.http.AsyncHttpClient;

import org.openhab.habclient.IOpenHABSetting;

public class TestOpenHABSetting implements IOpenHABSetting {
    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public String getBaseUrl() {
        return null;
    }

    @Override
    public void setBaseUrl(String baseUrl) {

    }

    @Override
    public void setSitemapRootUrl(String sitemapRootUrl) {

    }

    @Override
    public String getSitemapRootUrl() {
        return null;
    }

    @Override
    public AsyncHttpClient createAsyncHttpClient() {
        return null;
    }

    @Override
    public boolean runningInDemoMode() {
        return false;
    }
}
