package org.openhab.habdroid.ui.widget;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABWebWidget extends OpenHABWidgetBase {

    public OpenHABWebWidget(IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
    }

    @Override
    public View getWidget() {
        WebView webWeb = (WebView)mViewData.widgetView.findViewById(R.id.webweb);
        if (mViewData.openHABWidget.getHeight() > 0) {
            ViewGroup.LayoutParams webLayoutParams = webWeb.getLayoutParams();
            webLayoutParams.height = mViewData.openHABWidget.getHeight() * 80;
            webWeb.setLayoutParams(webLayoutParams);
        }
        webWeb.setWebViewClient(new WebViewClient());
        webWeb.loadUrl(mViewData.openHABWidget.getUrl());

        return mViewData.widgetView;
    }
}
