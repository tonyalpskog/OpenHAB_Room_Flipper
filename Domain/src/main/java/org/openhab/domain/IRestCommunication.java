package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRestCommunication {
    void requestOpenHABSitemap(OpenHABWidget widget, boolean longPolling, Object ownerTag);

    void requestOpenHABSitemap(String sitemapUrl, boolean longPolling, Object ownerTag);

    void requestOpenHABSitemap(String sitemapUrl, OpenHABWidget widget, boolean longPolling, Object ownerTag);

    void cancelRequests(Object ownerTag);
}
