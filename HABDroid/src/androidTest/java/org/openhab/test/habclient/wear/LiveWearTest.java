package org.openhab.test.habclient.wear;

import android.test.AndroidTestCase;

import org.openhab.habclient.wear.IWearCommandHost;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class LiveWearTest extends AndroidTestCase {
    @Inject IWearCommandHost mIWearCommandHost;

    public void setUp() throws Exception {
        super.setUp();

        //mIWearCommandHost.registerReceiver();

    }

    public void tearDown() {
        //mIWearCommandHost.unregisterReceiver();
    }

    public void testShowLeakageMessageOnWear() {
//        mWearCommandHost = new WearCommandHost(mHABApplication);
//        mWearCommandHost.registerReceiver();
        //mIWearCommandHost.startSession("Leakage alarm", "Kitchen Dishwasher leakage sensor [closed]");
    }
}
