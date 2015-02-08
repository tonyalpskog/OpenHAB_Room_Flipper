package org.openhab.domain;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2015.
 */
//@Singleton
public class TestInformation implements ITestInformation {
    private boolean mIsRunningDomainTest = false;

    @Inject
    public TestInformation() {}
    
    @Override
    public void setRunningDomainTest(boolean isDomainTest) {
        mIsRunningDomainTest = isDomainTest;
    }

    @Override
    public boolean isRunningDomainTest() {
        return mIsRunningDomainTest;
    }
}
