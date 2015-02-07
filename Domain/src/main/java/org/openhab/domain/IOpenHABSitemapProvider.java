package org.openhab.domain;

import org.openhab.domain.model.OpenHABSitemap;

import java.util.List;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface IOpenHABSitemapProvider {
    void setOpenHABSitemaps(List<OpenHABSitemap> sitemaps);
    List<OpenHABSitemap> getOpenHABSitemaps();
    List<String> getOpenHABSitemapLabels();
    OpenHABSitemap getOpenHABSitemapByLabel(String label);
    void setSelectedSitemap(OpenHABSitemap sitemap);
    OpenHABSitemap getSelectedSitemap();
}
