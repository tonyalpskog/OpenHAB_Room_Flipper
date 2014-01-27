package com.zenit.habclient;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
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
        requestOpenHABSitemap(context, widget.getItem().getName(), widget);
    }

    public void requestOpenHABSitemap(Context context, String sitemapId) {
        requestOpenHABSitemap(context, sitemapId, null);
    }

    public void requestOpenHABSitemap(Context context, String sitemapId, OpenHABWidget widget) {
        if(sitemapId == null || sitemapId.isEmpty()) {
            Log.w(HABApplication.GetLogTag(), String.format("\n%s\nRequested sitemap name is %s", HABApplication.GetLogTag(2), (sitemapId == null? "NULL": "empty")));
            sitemapId = null;
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
        final String RESTaddress = HABApplication.getOpenHABSetting().getBaseUrl() + "rest/sitemaps/demo" + (sitemapId == null? "": "/" + sitemapId);
        asyncHttpClient.get(context, RESTaddress, headers, null, new DocumentHttpResponseHandler() {
            @Override
            public void onSuccess(Document document) {
                if(document != null) {
                    Log.d(HABApplication.GetLogTag(), "DocumentHttpResponseHandler.onSuccess() -> 'get_items' = '" + document.getTextContent() + "'");
                    Node rootNode = document.getFirstChild();

                    HABApplication.getOpenHABWidgetProvider().setOpenHABWidgets(finalWidget == null? new OpenHABWidgetDataSource(rootNode): new OpenHABWidgetDataSource(rootNode, finalWidget));
                } else {
                    Log.e(HABApplication.GetLogTag(), RESTaddress + "\nshowAddUnitDialog() -> Got a null response from openHAB");
                }
            }
            @Override
            public void onFailure(Throwable e, String errorResponse) {
                Log.e(HABApplication.GetLogTag(), RESTaddress + "\r\nget_items() - asyncHttpClient.onFailure  - " + e.toString());
            }
        });
    }
}
