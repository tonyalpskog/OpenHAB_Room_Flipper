package org.openhab.domain;

public interface IEventBus {
    void post(Object event);

    void postSticky(Object event);

    void registerSticky(Object subscriber);
}
