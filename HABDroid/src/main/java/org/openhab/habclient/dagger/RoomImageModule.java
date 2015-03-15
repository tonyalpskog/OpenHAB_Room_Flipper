package org.openhab.habclient.dagger;

import org.openhab.habclient.IRoomImageProvider;
import org.openhab.habclient.RoomImageProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomImageModule {
    @Provides
    public IRoomImageProvider provideRoomImageProvider(RoomImageProvider roomImageProvider) {
        return roomImageProvider;
    }
}
