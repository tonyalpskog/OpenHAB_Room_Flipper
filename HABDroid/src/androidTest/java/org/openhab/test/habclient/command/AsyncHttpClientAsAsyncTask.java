package org.openhab.test.habclient.command;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.openhab.domain.IDocumentFactory;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habdroid.core.DocumentHttpResponseHandler;
import org.w3c.dom.Document;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class AsyncHttpClientAsAsyncTask implements IAsyncHttpClientAsAsyncTask {
    @Inject Context mContext;
    @Inject IOpenHABSetting mOpenHABSetting;
    @Inject IDocumentFactory mDocumentFactory;

    @Override
    public void doAsync(final Listener l) {
        Header[] headers = {};
//        headers = new Header[] {new BasicHeader("X-Atmosphere-Transport", "long-polling")};
        AsyncHttpClient asyncHttpClient = mOpenHABSetting.createAsyncHttpClient();
//        MyAsyncHttpClient asyncHttpClient = new MyAsyncHttpClient(habApplication.getApplicationContext());
        asyncHttpClient.get(mContext, "https://demo.openhab.org:8443/rest/sitemaps/demo/demo", headers, null, new DocumentHttpResponseHandler(mDocumentFactory) {
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
