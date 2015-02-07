package org.openhab.domain;

import org.openhab.domain.model.OpenHABSitemap;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2015.
 */
public class OpenHABSitemapProvider implements IOpenHABSitemapProvider {
    private List<OpenHABSitemap> mSitemapList;
    private OpenHABSitemap mSelectedSitemap;

    @Inject
    public OpenHABSitemapProvider() {
        mSitemapList = new ArrayList<OpenHABSitemap>();
    }

    @Override
    public void setOpenHABSitemaps(List<OpenHABSitemap> sitemaps) {
        mSitemapList = sitemaps;
    }

    @Override
    public List<OpenHABSitemap> getOpenHABSitemaps() {
        return mSitemapList;
    }

    @Override
    public List<String> getOpenHABSitemapLabels() {
        List<String> labels = new ArrayList<String>();
        for(OpenHABSitemap sitemap : mSitemapList)
            labels.add(sitemap.getLabel());
        return labels;
    }

    @Override
    public OpenHABSitemap getOpenHABSitemapByLabel(String label) throws IllegalArgumentException {
        for(OpenHABSitemap sitemap : mSitemapList)
        {
            if(sitemap.getLabel().equals(label))
                return sitemap;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void setSelectedSitemap(OpenHABSitemap sitemap) {
        mSelectedSitemap = sitemap;
    }

    @Override
    public OpenHABSitemap getSelectedSitemap() {
        return mSelectedSitemap;
    }
}
