package org.openhab.habclient;

import org.openhab.domain.model.OpenHABWidget;

/**
 * Created by Tony Alpskog on 2014-06-30.
 */
public interface IRestCommunication {
    void requestOpenHABSitemap(OpenHABWidget widget);

    void requestOpenHABSitemap(String sitemapUrl);

    void requestOpenHABSitemap(String sitemapUrl, OpenHABWidget widget);
}
