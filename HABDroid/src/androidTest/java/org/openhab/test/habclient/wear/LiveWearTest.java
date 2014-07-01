package org.openhab.test.habclient.wear;

import org.openhab.domain.wear.IWearCommandHost;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.command.ICommandAnalyzer;
import org.openhab.habclient.dagger.AndroidModule;
import org.openhab.habclient.dagger.ClientModule;
import org.openhab.habclient.wear.WearCommandHost;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

/**
 * Created by Tony Alpskog in 2014.
 */
public class LiveWearTest extends android.test.ApplicationTestCase<HABApplication> {
    private HABApplication mHABApplication;
    @Inject IWearCommandHost mIWearCommandHost;

    public LiveWearTest() {
        super(HABApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        mHABApplication = getApplication();

        ObjectGraph graph = mHABApplication.getObjectGraph()
                .plus(new AndroidModule(mHABApplication), new TestModule(mHABApplication));
        graph.inject(this);

        mIWearCommandHost.registerReceiver();
    }

    @Module(injects = LiveWearTest.class, includes = ClientModule.class, overrides = true)
    public class TestModule {
        private final HABApplication mApp;

        public TestModule(HABApplication app) {
            mApp = app;
        }

        @Provides @Singleton
        public IWearCommandHost provideWearCommandHost() {
            return new WearCommandHost(mApp);
        }
    }

    public void tearDown() {
        mIWearCommandHost.unregisterReceiver();
    }

    public void testShowLeakageMessageOnWear() {
//        mWearCommandHost = new WearCommandHost(mHABApplication);
//        mWearCommandHost.registerReceiver();
        mIWearCommandHost.startSession("Leakage alarm", "Kitchen Dishwasher leakage sensor [closed]");
    }
}
