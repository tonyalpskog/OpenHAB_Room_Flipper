package org.openhab.domain;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface ITestInformation {
    void setRunningDomainTest(boolean isDomainTest);
    boolean isRunningDomainTest();
}
