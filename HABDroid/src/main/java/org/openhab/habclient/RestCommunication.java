package org.openhab.habclient;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
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

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

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
    private final AsyncHttpClient mAsyncHttpClient;

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
        mAsyncHttpClient = mOpenHABSetting.createAsyncHttpClient();
    }

    @Override
    public void requestOpenHABSitemap(final OpenHABWidget widget, final boolean longPolling, final Object ownerTag) {
        if(widget != null && (widget.hasItem() || widget.hasLinkedPage()))
            requestOpenHABSitemap(/*"https://demo.openhab.org:8443/rest/sitemaps/demo/" + */(widget.hasLinkedPage()? widget.getLinkedPage().getLink() : widget.getParent().getLinkedPage().getLink()), widget, longPolling, ownerTag);
        else
            mLogger.e(HABApplication.getLogTag(2), "[AsyncHttpClient] Sitemap cannot be requested due to missing sitemap data.");
    }

    @Override
    public void requestOpenHABSitemap(final String sitemapUrl, final boolean longPolling, final Object ownerTag) {
        requestOpenHABSitemap(sitemapUrl, null, longPolling, ownerTag);
    }

    @Override
    public void requestOpenHABSitemap(final String sitemapUrl, final OpenHABWidget widget, final boolean longPolling, final Object ownerTag) {
        final String RESTaddress;
        if(StringHandler.isNullOrEmpty(sitemapUrl)) {
            mLogger.w(HABApplication.getLogTag(), String.format("\n\r%s\n\r[AsyncHttpClient] Requested sitemap URL is %s", HABApplication.getLogTag(2), (sitemapUrl == null? "NULL": "empty")));
            RESTaddress = mOpenHABSetting.getBaseUrl() + mContext.getString(R.string.openhab_demo_sitemap_postfix);
        }
        else {
            RESTaddress = sitemapUrl;
        }

        mLogger.d(HABApplication.getLogTag(2), String.format("\n\r[AsyncHttpClient] Requested sitemap URL is '%s'    longpolling = '%s'", sitemapUrl, longPolling));
        final String callingMethod = HABApplication.getLogTag(2);

        Header[] headers = {};
        if (longPolling)
            headers = new Header[] {new BasicHeader("X-Atmosphere-Transport", "long-polling")};

        mLogger.d(HABApplication.getLogTag(), "[AsyncHttpClient] Requesting REST data from: " + RESTaddress);
        mAsyncHttpClient.get(mContext, RESTaddress, headers, null, new DocumentHttpResponseHandler(mDocumentFactory) {
            @Override
            public void onSuccess(Document document) {
                if (document == null) {
                    mLogger.e(HABApplication.getLogTag(), "[AsyncHttpClient] " + RESTaddress + "\nshowAddUnitDialog() -> Got a null response from openHAB");
                    return;
                }

                mLogger.d(HABApplication.getLogTag(), String.format("\n\r%s - %s\n\r[AsyncHttpClient] DocumentHttpResponseHandler.onSuccess() for requested sitemap URL '%s'    longpolling = '%s'", ownerTag, callingMethod, sitemapUrl, longPolling));
                final Node rootNode = document.getFirstChild();

                if (widget == null)
                    mWidgetProvider.setOpenHABWidgets(new OpenHABWidgetDataSource(rootNode, mLogger, mColorParser));
                else
                    mWidgetProvider.setOpenHABWidgets(new OpenHABWidgetDataSource(rootNode, widget, mLogger, mColorParser));

                checkForLongPolling();
            }
            @Override
            public void onFailure(Throwable e, String errorResponse) {
                mLogger.e(HABApplication.getLogTag(), "[AsyncHttpClient] " + RESTaddress + "\r\nget_items() - DocumentHttpResponseHandler.onFailure  - " + e.toString());
                if (e instanceof SocketTimeoutException) {
                    Log.d(HABApplication.getLogTag(), String.format("\n\r%s - %s\n\r[AsyncHttpClient] Connection timeout for requested sitemap URL '%s'    longpolling = '%s'", ownerTag, callingMethod, sitemapUrl, longPolling));
                }
                checkForLongPolling();
            }

            private void checkForLongPolling() {
                if(longPolling) {
                    Log.d(HABApplication.getLogTag(), String.format("\n\r%s - %s\n\r[AsyncHttpClient] LongPolling => Will run the sitemap request again for requested sitemap URL '%s'    longpolling = '%s'", ownerTag, callingMethod, sitemapUrl, longPolling));
                    requestOpenHABSitemap(RESTaddress, longPolling, ownerTag);
                }
            }
        }, ownerTag);
    }

    @Override
    public void cancelRequests(Object ownerTag) {
        mAsyncHttpClient.cancelRequests(mContext, ownerTag, true);
    }
}
