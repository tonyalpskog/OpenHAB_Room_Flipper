package org.openhab.habclient.dagger;

import org.openhab.domain.INotificationHost;
import org.openhab.domain.INotificationReplyHandler;
import org.openhab.domain.INotificationSender;
import org.openhab.habclient.notification.NotificationHost;
import org.openhab.habclient.notification.NotificationReplyHandler;
import org.openhab.habclient.notification.NotificationSender;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {
    @Provides
    public INotificationHost provideNotificationHost(NotificationHost notificationSender) {
        return notificationSender;
    }

    @Provides
    public INotificationSender provideNotificationSender(NotificationSender notificationSender) {
        return notificationSender;
    }

    @Provides
    public INotificationReplyHandler provideNotificationReplyHandler(NotificationReplyHandler notificationReplyHandler) {
        return notificationReplyHandler;
    }
}
