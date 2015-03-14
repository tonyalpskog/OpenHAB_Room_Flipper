package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.user.User;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface INotificationHost {
    void startSession(SenderType senderType, String title, String message);
    void startSession(String title, OpenHABWidget openHABWidget, String message);
    void startSession(String title, User user, String message);
    void registerReceivers();
    void unregisterReceivers();
}
