package org.openhab.test.habclient.wear;

import android.test.AndroidTestCase;

import org.openhab.habclient.wear.IWearNotificationActions;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class LiveWearTest extends AndroidTestCase {
    @Inject
    IWearNotificationActions mIWearNotificationActions;

    public void setUp() throws Exception {
        super.setUp();

        //mIWearNotificationActions.registerReceiver();

    }

    public void tearDown() {
        //mIWearNotificationActions.unregisterReceiver();
    }

    public void testShowLeakageMessageOnWear() {
//        mIWearNotificationActions = new WearNotificationActions(mHABApplication);
//        mIWearNotificationActions.registerReceiver();
        //mIWearNotificationActions.startSession("Leakage alarm", "Kitchen Dishwasher leakage sensor [closed]");
    }
}
