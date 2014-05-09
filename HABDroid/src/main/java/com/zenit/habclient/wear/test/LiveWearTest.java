package com.zenit.habclient.wear.test;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.wear.WearCommandHost;

/**
 * Created by Tony Alpskog in 2014.
 */
public class LiveWearTest extends android.test.ApplicationTestCase<HABApplication> {
    private HABApplication mHABApplication;
    private WearCommandHost mWearCommandHost;

    public LiveWearTest() {
        super(HABApplication.class);
    }

    public void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
        }
        createApplication();
        mHABApplication = getApplication();

        mWearCommandHost = new WearCommandHost(mHABApplication);
        mWearCommandHost.registerReceiver();
    }

    public void tearDown() {
        mWearCommandHost.unregisterReceiver();
    }

    public void testShowLeakageMessageOnWear() {
//        mWearCommandHost = new WearCommandHost(mHABApplication);
//        mWearCommandHost.registerReceiver();
        mWearCommandHost.startSession("Leakage alarm", "Kitchen Dishwasher leakage sensor [closed]");
    }
}
