package org.openhab.domain.model;

/**
 * Created by Tony Alpskog in 2014.
 */
public class SitemapUpdateRequest {
    private String mSitemap;

    public SitemapUpdateRequest(String sitemap) {
        mSitemap = sitemap;
    }

    public String getSitemap() {
        return mSitemap;
    }
}
