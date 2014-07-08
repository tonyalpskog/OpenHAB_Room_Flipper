package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRestCommunication {
    void requestOpenHABSitemap(OpenHABWidget widget);

    void requestOpenHABSitemap(String sitemapUrl);

    void requestOpenHABSitemap(String sitemapUrl, OpenHABWidget widget);
}
