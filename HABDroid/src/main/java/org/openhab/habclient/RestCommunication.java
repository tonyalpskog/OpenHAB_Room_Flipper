package org.openhab.habclient;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetDataSource;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.StringHandler;
import org.openhab.habdroid.R;
import org.openhab.habdroid.core.DocumentHttpResponseHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RestCommunication {
    private final ILogger mLogger;
    private final IColorParser mColorParser;

    public RestCommunication(ILogger logger, IColorParser colorParser) {
        if(logger == null) throw new IllegalArgumentException("logger is null");
        if(colorParser == null) throw new IllegalArgumentException("colorParser is null");

        mLogger = logger;
        mColorParser = colorParser;
    }

    public void requestOpenHABSitemap(Context context, OpenHABWidget widget) {
        if(widget != null && (widget.hasItem() || widget.hasLinkedPage()))
            requestOpenHABSitemap(context, /*"https://demo.openhab.org:8443/rest/sitemaps/demo/" + */(widget.hasLinkedPage()? widget.getLinkedPage().getLink() : widget.getItem().getLink()), widget);
        else
            Log.e(HABApplication.getLogTag(2), "[AsyncHttpClient] Sitemap cannot be requested due to missing sitemap data.");
    }

    public void requestOpenHABSitemap(Context context, String sitemapUrl) {
        requestOpenHABSitemap(context, sitemapUrl, null);
    }

    public void requestOpenHABSitemap(Context context, String sitemapUrl, OpenHABWidget widget) {
        if(StringHandler.isNullOrEmpty(sitemapUrl)) {
            Log.w(HABApplication.getLogTag(), String.format("\n\r%s\n\r[AsyncHttpClient] Requested sitemap URL is %s", HABApplication.getLogTag(2), (sitemapUrl == null? "NULL": "empty")));
            sitemapUrl = HABApplication.getOpenHABSetting(context).getBaseUrl() + context.getString(R.string.openhab_demo_sitemap_postfix);
        }

        Log.d(HABApplication.getLogTag(2), String.format("\n\r[AsyncHttpClient] Requested sitemap URL is '%s'", sitemapUrl));

        final OpenHABWidget finalWidget = widget;

        Header[] headers = {};

//        if (!longPolling)
//            startProgressIndicator();
//        if (longPolling) {
//            headers = new Header[] {new BasicHeader("X-Atmosphere-Transport", "long-polling")};
//        }
//        //TA - Calling REST Get method, requesting data from server.
//        mAsyncHttpClient.get(mActivity, pageUrl, headers, null, new DocumentHttpResponseHandler()

        AsyncHttpClient asyncHttpClient = HABApplication.getOpenHABSetting(context).getAsyncHttpClient();
        final String RESTaddress = sitemapUrl;
        Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] Requesting REST data from: " + RESTaddress);
        asyncHttpClient.get(context, RESTaddress, headers, null, new DocumentHttpResponseHandler() {
            @Override
            public void onSuccess(Document document) {
                if(document != null) {
                    Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] DocumentHttpResponseHandler.onSuccess() -> 'get_items' = '" + document.toString() + "'");
                    Node rootNode = document.getFirstChild();

                    HABApplication.getOpenHABWidgetProvider2().setOpenHABWidgets(finalWidget == null? new OpenHABWidgetDataSource(rootNode, mLogger, mColorParser): new OpenHABWidgetDataSource(rootNode, finalWidget, mLogger, mColorParser));
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
