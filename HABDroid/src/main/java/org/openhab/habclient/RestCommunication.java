package org.openhab.habclient;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IRestCommunication;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetDataSource;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.StringHandler;
import org.openhab.habdroid.R;
import org.openhab.habdroid.core.DocumentHttpResponseHandler;
import org.openhab.domain.IDocumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RestCommunication implements IRestCommunication {
    private final ILogger mLogger;
    private final IColorParser mColorParser;
    private final IOpenHABSetting mOpenHABSetting;
    private final IOpenHABWidgetProvider mWidgetProvider;
    private final Context mContext;
    private final IDocumentFactory mDocumentFactory;

    @Inject
    public RestCommunication(Context context,
                             ILogger logger,
                             IColorParser colorParser,
                             IOpenHABSetting openHABSetting,
                             IOpenHABWidgetProvider widgetProvider,
                             IDocumentFactory documentFactory) {
        if(context == null) throw new IllegalArgumentException("context is null");
        if(logger == null) throw new IllegalArgumentException("logger is null");
        if(colorParser == null) throw new IllegalArgumentException("colorParser is null");
        if(openHABSetting == null) throw new IllegalArgumentException("openHABSetting is null");
        if(widgetProvider == null) throw new IllegalArgumentException("widgetProvider is null");
        if(documentFactory == null) throw new IllegalArgumentException("documentFactory is null");

        mContext = context;
        mLogger = logger;
        mColorParser = colorParser;
        mOpenHABSetting = openHABSetting;
        mWidgetProvider = widgetProvider;
        mDocumentFactory = documentFactory;
    }

    @Override
    public void requestOpenHABSitemap(OpenHABWidget widget) {
        if(widget != null && (widget.hasItem() || widget.hasLinkedPage()))
            requestOpenHABSitemap(/*"https://demo.openhab.org:8443/rest/sitemaps/demo/" + */(widget.hasLinkedPage()? widget.getLinkedPage().getLink() : widget.getItem().getLink()), widget);
        else
            mLogger.e(HABApplication.getLogTag(2), "[AsyncHttpClient] Sitemap cannot be requested due to missing sitemap data.");
    }

    @Override
    public void requestOpenHABSitemap(String sitemapUrl) {
        requestOpenHABSitemap(sitemapUrl, null);
    }

    @Override
    public void requestOpenHABSitemap(String sitemapUrl, final OpenHABWidget widget) {
        final String RESTaddress;
        if(StringHandler.isNullOrEmpty(sitemapUrl)) {
            mLogger.w(HABApplication.getLogTag(), String.format("\n\r%s\n\r[AsyncHttpClient] Requested sitemap URL is %s", HABApplication.getLogTag(2), (sitemapUrl == null? "NULL": "empty")));
            RESTaddress = mOpenHABSetting.getBaseUrl() + mContext.getString(R.string.openhab_demo_sitemap_postfix);
        }
        else {
            RESTaddress = sitemapUrl;
        }

        mLogger.d(HABApplication.getLogTag(2), String.format("\n\r[AsyncHttpClient] Requested sitemap URL is '%s'", sitemapUrl));

        final Header[] headers = {};
        final AsyncHttpClient asyncHttpClient = mOpenHABSetting.createAsyncHttpClient();

        mLogger.d(HABApplication.getLogTag(), "[AsyncHttpClient] Requesting REST data from: " + RESTaddress);
        asyncHttpClient.get(mContext, RESTaddress, headers, null, new DocumentHttpResponseHandler(mDocumentFactory) {
            @Override
            public void onSuccess(Document document) {
                if (document == null) {
                    mLogger.e(HABApplication.getLogTag(), "[AsyncHttpClient] " + RESTaddress + "\nshowAddUnitDialog() -> Got a null response from openHAB");
                    return;
                }

                mLogger.d(HABApplication.getLogTag(), "[AsyncHttpClient] DocumentHttpResponseHandler.onSuccess() -> 'get_items' = '" + document.toString() + "'");
                final Node rootNode = document.getFirstChild();

                if (widget == null)
                    mWidgetProvider.setOpenHABWidgets(new OpenHABWidgetDataSource(rootNode, mLogger, mColorParser));
                else
                    mWidgetProvider.setOpenHABWidgets(new OpenHABWidgetDataSource(rootNode, widget, mLogger, mColorParser));
            }
            @Override
            public void onFailure(Throwable e, String errorResponse) {
                mLogger.e(HABApplication.getLogTag(), "[AsyncHttpClient] " + RESTaddress + "\r\nget_items() - asyncHttpClient.onFailure  - " + e.toString());
            }
        });
    }
}
