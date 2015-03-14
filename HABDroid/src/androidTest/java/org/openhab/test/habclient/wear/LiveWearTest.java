package org.openhab.test.habclient.wear;

import android.test.AndroidTestCase;

import org.openhab.habclient.wear.IWearCommandHost;
import org.openhab.habclient.dagger.AndroidModule;
import org.openhab.habclient.dagger.ClientModule;

import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;

/**
 * Created by Tony Alpskog in 2014.
 */
public class LiveWearTest extends AndroidTestCase {
    @Inject IWearCommandHost mIWearCommandHost;

    public void setUp() throws Exception {
        super.setUp();

        ObjectGraph graph = ObjectGraph.create(new AndroidModule(getContext()),
                new TestModule());
        graph.inject(this);

        //mIWearCommandHost.registerReceiver();
    }

    @Module(injects = LiveWearTest.class, includes = ClientModule.class, overrides = true)
    public class TestModule {
        
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
