package org.openhab.domain.model;

/**
 * Created by Tony Alpskog in 2015.
 */
public class RoomConfigEvent {
    public enum EventType {
        ConfigurationChanged,
        Removed
    }

    private Room mRoom;
    private EventType mEventType;

    public RoomConfigEvent(Room room, EventType eventType) {
        mRoom = room;
        mEventType = eventType;
    }

    public Room getRoom() {
        return mRoom;
    }
    
    public EventType getEventType() {
        return mEventType;
    }
}
