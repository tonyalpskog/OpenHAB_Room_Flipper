package org.openhab.test.habclient.command;

import com.loopj.android.http.AsyncHttpClient;
import org.openhab.habclient.HABApplication;

import org.apache.http.Header;
import org.openhab.habdroid.core.DocumentHttpResponseHandler;
import org.w3c.dom.Document;

import java.net.SocketTimeoutException;

/**
 * Created by Tony Alpskog in 2014.
 */
public class AsyncHttpClientAsAsyncTask implements IAsyncHttpClientAsAsyncTask {

    @Override
    public void doAsync(HABApplication habApplication, final Listener l) {
        Header[] headers = {};
//        headers = new Header[] {new BasicHeader("X-Atmosphere-Transport", "long-polling")};
        AsyncHttpClient asyncHttpClient = habApplication.getOpenHABSetting().createAsyncHttpClient();
//        MyAsyncHttpClient asyncHttpClient = new MyAsyncHttpClient(habApplication.getApplicationContext());
        asyncHttpClient.get(habApplication.getApplicationContext(), "https://demo.openhab.org:8443/rest/sitemaps/demo/demo", headers, null, new DocumentHttpResponseHandler() {
            @Override
            public void onSuccess(Document document) {
                l.onValueChanged("onSuccess", document);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (error instanceof SocketTimeoutException) {
                    l.onValueChanged("onFailure - [AsyncHttpClient] Connection timeout, reconnecting", null);
                    return;
                }
                l.onValueChanged("onFailure - [AsyncHttpClient] Connection error = " + error.getClass().toString(), null);
            }
        });
    }
}
