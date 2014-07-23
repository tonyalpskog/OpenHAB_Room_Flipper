package org.openhab.domain.model;

/**
 * Created by Tony Alpskog in 2014.
 */
public class SitemapUpdateEvent {
    private boolean mUpdateFinished;

    public SitemapUpdateEvent(boolean updateFinished) {
        mUpdateFinished = updateFinished;
    }

    public boolean isUpdateFinished() {
        return mUpdateFinished;
    }
}
