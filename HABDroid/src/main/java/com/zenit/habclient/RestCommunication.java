package com.zenit.habclient;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.openhab.habdroid.core.DocumentHttpResponseHandler;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RestCommunication {

    public void requestOpenHABSitemap(Context context, OpenHABWidget widget) {
        if(widget.hasItem() || widget.hasLinkedPage())
            requestOpenHABSitemap(context, widget.hasItem()? widget.getItem().getLink() : widget.getLinkedPage().getLink(), widget);
        else
            Log.w(HABApplication.getLogTag(2), "Sitemap cannot be requested due to missing sitemap data.");
    }

    public void requestOpenHABSitemap(Context context, String sitemapUrl) {
        requestOpenHABSitemap(context, sitemapUrl, null);
    }

    public void requestOpenHABSitemap(Context context, String sitemapUrl, OpenHABWidget widget) {
        if(sitemapUrl == null || sitemapUrl.isEmpty()) {
            Log.w(HABApplication.getLogTag(), String.format("\n%s\n[AsyncHttpClient] Requested sitemap URL is %s", HABApplication.getLogTag(2), (sitemapUrl == null? "NULL": "empty")));
            return;
        }

        final OpenHABWidget finalWidget = widget;

        Header[] headers = {};

//        if (!longPolling)
//            startProgressIndicator();
//        if (longPolling) {
//            headers = new Header[] {new BasicHeader("X-Atmosphere-Transport", "long-polling")};
//        }
//        //TA - Calling REST Get method, requesting data from server.
//        mAsyncHttpClient.get(mActivity, pageUrl, headers, null, new DocumentHttpResponseHandler()

        AsyncHttpClient asyncHttpClient = HABApplication.getOpenHABSetting().getAsyncHttpClient(context);
        final String RESTaddress = sitemapUrl;
        Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] Requesting REST data from: " + RESTaddress);
        asyncHttpClient.get(context, RESTaddress, headers, null, new DocumentHttpResponseHandler() {
            @Override
            public void onSuccess(Document document) {
                if(document != null) {
                    Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] DocumentHttpResponseHandler.onSuccess() -> 'get_items' = '" + document.toString() + "'");
                    Node rootNode = document.getFirstChild();

                    HABApplication.getOpenHABWidgetProvider().setOpenHABWidgets(finalWidget == null? new OpenHABWidgetDataSource(rootNode): new OpenHABWidgetDataSource(rootNode, finalWidget));
                } else {
                    Log.e(HABApplication.getLogTag(), "[AsyncHttpClient] " + RESTaddress + "\nshowAddUnitDialog() -> Got a null response from openHAB");
                }
            }
            @Override
            public void onFailure(Throwable e, String errorResponse) {
                Log.e(HABApplication.getLogTag(), "[AsyncHttpClient] " + RESTaddress + "\r\nget_items() - asyncHttpClient.onFailure  - " + e.toString());
            }
        });
    }
}
