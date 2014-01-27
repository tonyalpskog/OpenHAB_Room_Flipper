package com.zenit.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.openhab.habdroid.R;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class RoomProvider {
    HashMap<UUID, Room> roomHash;
    UUID initialRoomId;
    private Context mContext = null;

    public RoomProvider(Context context) {
        mContext = context;
        roomHash = new HashMap<UUID, Room>();
        createRooms();
    }

    public boolean add(Room room) {
        roomHash.put(room.getId(), room);
        return true;
    }

    public Room get(UUID uuid) {
        return roomHash.get(uuid);
    }

    public Room getInitialRoom() {
        return roomHash.get(initialRoomId);
    }

    private Bitmap getBitmap(int bitmapResourceId) {
        return BitmapFactory.decodeResource(mContext.getResources(), bitmapResourceId);
    }

    public Room createNewRoom() {
        //TA: TODO - Fix name problem. (now sitemapID)
        Room room = new Room(null, "New room", getBitmap(R.drawable.empty_room));
        add(room);
        return room;
    }

    public Room addRoom(Room room) {
        add(room);
        return room;
    }

    private void createRooms() {
        Room room0Center = addRoom(new Room("FF_Bath", "Källare mitten", getBitmap(R.drawable.room_0_c)));
        Room room0East = addRoom(new Room("FF_Office", "Källare öster", getBitmap(R.drawable.room_0_e)));
        Room room0North = addRoom(new Room("FF_Child", "Källare norr", getBitmap(R.drawable.room_0_n)));
        Room room0NorthEast = addRoom(new Room("GF_Living", "Källare nordost", getBitmap(R.drawable.room_0_ne)));
        Room room0NorthWest = addRoom(new Room("GF_Kitchen", "Källare nordväst", getBitmap(R.drawable.room_0_nw)));
        Room room0South = addRoom(new Room("FF_Bed", "Källare söder", getBitmap(R.drawable.room_0_s)));
        Room room0SouthEast = addRoom(new Room("GF_Toilet", "Källare sydost", getBitmap(R.drawable.room_0_se)));
        Room room0SouthWest = addRoom(new Room("GF_Corridor", "Källare sydväst", getBitmap(R.drawable.room_0_sw)));
        Room room0West = addRoom(new Room("FF_Corridor", "Källare väster", getBitmap(R.drawable.room_0_w)));

        Room room1Center = addRoom(new Room("Outdoor", "Entré plan mitten", getBitmap(R.drawable.room_1_c)));
        Room room1East = addRoom(new Room("Shutters", "Entré plan öster", getBitmap(R.drawable.room_1_e)));
        Room room1North = addRoom(new Room("Weather", "Entré plan norr", getBitmap(R.drawable.room_1_n)));
        Room room1NorthEast = addRoom(new Room("Status", "Entré plan nordost", getBitmap(R.drawable.room_1_ne)));
        Room room1NorthWest = addRoom(new Room("Lights", "Entré plan nordväst", getBitmap(R.drawable.room_1_nw)));
        Room room1South = addRoom(new Room("Heating", "Entré plan söder", getBitmap(R.drawable.room_1_s)));
        Room room1SouthEast = addRoom(new Room("Temperature", "Entré plan sydost", getBitmap(R.drawable.room_1_se)));
        Room room1SouthWest = addRoom(new Room("Windows", "Entré plan sydväst", getBitmap(R.drawable.room_1_sw)));
        Room room1West = addRoom(new Room("Weather_Chart", "Entré plan väster", getBitmap(R.drawable.room_1_w)));

        Room room2Center = addRoom(new Room("FF_Bath", "Övre plan mitten", getBitmap(R.drawable.room_2_c)));
        Room room2East = addRoom(new Room("FF_Bath", "Övre plan öster", getBitmap(R.drawable.room_2_e)));
        Room room2North = addRoom(new Room("FF_Bath", "Övre plan norr", getBitmap(R.drawable.room_2_n)));
        Room room2NorthEast = addRoom(new Room("FF_Bath", "Övre plan nordost", getBitmap(R.drawable.room_2_ne)));
        Room room2NorthWest = addRoom(new Room("FF_Bath", "Övre plan nordväst", getBitmap(R.drawable.room_2_nw)));
        Room room2South = addRoom(new Room("FF_Bath", "Övre plan söder", getBitmap(R.drawable.room_2_s)));
        Room room2SouthEast = addRoom(new Room("FF_Bath", "Övre plan sydost", getBitmap(R.drawable.room_2_se)));
        Room room2SouthWest = addRoom(new Room("FF_Bath", "Övre plan sydväst", getBitmap(R.drawable.room_2_sw)));
        Room room2West = addRoom(new Room("FF_Bath", "Övre plan väster", getBitmap(R.drawable.room_2_w)));

        initialRoomId = room0Center.getId();

        //Aligning basement
        room0Center.setAlignment(room0West, Direction.LEFT);
        room0Center.setAlignment(room0East, Direction.RIGHT);
        room0Center.setAlignment(room0North, Direction.UP);
        room0Center.setAlignment(room0South, Direction.DOWN);
        room0Center.setAlignment(room1Center, Direction.ABOVE);

        room0East.setAlignment(room0Center, Direction.LEFT);
        room0East.setAlignment(room0NorthEast, Direction.UP);
        room0East.setAlignment(room0SouthEast, Direction.DOWN);
        room0East.setAlignment(room1East, Direction.ABOVE);

        room0SouthEast.setAlignment(room0South, Direction.LEFT);
        room0SouthEast.setAlignment(room0East, Direction.UP);
        room0SouthEast.setAlignment(room1SouthEast, Direction.ABOVE);

        room0NorthEast.setAlignment(room0North, Direction.LEFT);
        room0NorthEast.setAlignment(room0East, Direction.DOWN);
        room0NorthEast.setAlignment(room1NorthEast, Direction.ABOVE);

        room0North.setAlignment(room0NorthWest, Direction.LEFT);
        room0North.setAlignment(room0NorthEast, Direction.RIGHT);
        room0North.setAlignment(room0Center, Direction.DOWN);
        room0North.setAlignment(room1North, Direction.ABOVE);

        room0NorthWest.setAlignment(room0North, Direction.RIGHT);
        room0NorthWest.setAlignment(room0West, Direction.DOWN);
        room0NorthWest.setAlignment(room1NorthWest, Direction.ABOVE);

        room0West.setAlignment(room0Center, Direction.RIGHT);
        room0West.setAlignment(room0NorthWest, Direction.UP);
        room0West.setAlignment(room0SouthWest, Direction.DOWN);
        room0West.setAlignment(room1West, Direction.ABOVE);

        room0SouthWest.setAlignment(room0South, Direction.RIGHT);
        room0SouthWest.setAlignment(room0West, Direction.UP);
        room0SouthWest.setAlignment(room1SouthWest, Direction.ABOVE);

        room0South.setAlignment(room0SouthWest, Direction.LEFT);
        room0South.setAlignment(room0SouthEast, Direction.RIGHT);
        room0South.setAlignment(room0Center, Direction.UP);
        room0South.setAlignment(room1South, Direction.ABOVE);

        //Aligning ground floor
        room1Center.setAlignment(room1West, Direction.LEFT);
        room1Center.setAlignment(room1East, Direction.RIGHT);
        room1Center.setAlignment(room1North, Direction.UP);
        room1Center.setAlignment(room1South, Direction.DOWN);
        room1Center.setAlignment(room0Center, Direction.BELOW);
        room1Center.setAlignment(room2Center, Direction.ABOVE);

        room1North.setAlignment(room1NorthWest, Direction.LEFT);
        room1North.setAlignment(room1NorthEast, Direction.RIGHT);
        room1North.setAlignment(room1Center, Direction.DOWN);
        room1North.setAlignment(room0North, Direction.BELOW);
        room1North.setAlignment(room2North, Direction.ABOVE);

        room1NorthWest.setAlignment(room1North, Direction.RIGHT);
        room1NorthWest.setAlignment(room1West, Direction.DOWN);
        room1NorthWest.setAlignment(room0NorthWest, Direction.BELOW);
        room1NorthWest.setAlignment(room2NorthWest, Direction.ABOVE);

        room1West.setAlignment(room1Center, Direction.RIGHT);
        room1West.setAlignment(room1NorthWest, Direction.UP);
        room1West.setAlignment(room1SouthWest, Direction.DOWN);
        room1West.setAlignment(room0West, Direction.BELOW);
        room1West.setAlignment(room2West, Direction.ABOVE);

        room1SouthWest.setAlignment(room1South, Direction.RIGHT);
        room1SouthWest.setAlignment(room1West, Direction.UP);
        room1SouthWest.setAlignment(room0SouthWest, Direction.BELOW);
        room1SouthWest.setAlignment(room2SouthWest, Direction.ABOVE);

        room1South.setAlignment(room1SouthWest, Direction.LEFT);
        room1South.setAlignment(room1SouthEast, Direction.RIGHT);
        room1South.setAlignment(room1Center, Direction.UP);
        room1South.setAlignment(room0South, Direction.BELOW);
        room1South.setAlignment(room2South, Direction.ABOVE);

        room1SouthEast.setAlignment(room1South, Direction.LEFT);
        room1SouthEast.setAlignment(room1East, Direction.UP);
        room1SouthEast.setAlignment(room0SouthEast, Direction.BELOW);
        room1SouthEast.setAlignment(room2SouthEast, Direction.ABOVE);

        room1East.setAlignment(room1Center, Direction.LEFT);
        room1East.setAlignment(room1NorthEast, Direction.UP);
        room1East.setAlignment(room1SouthEast, Direction.DOWN);
        room1East.setAlignment(room0East, Direction.BELOW);
        room1East.setAlignment(room2East, Direction.ABOVE);

        room1NorthEast.setAlignment(room1North, Direction.LEFT);
        room1NorthEast.setAlignment(room1East, Direction.DOWN);
        room1NorthEast.setAlignment(room0NorthEast, Direction.BELOW);
        room1NorthEast.setAlignment(room2NorthEast, Direction.ABOVE);

        //Alignment upper floor
        room2Center.setAlignment(room2West, Direction.LEFT);
        room2Center.setAlignment(room2East, Direction.RIGHT);
        room2Center.setAlignment(room2North, Direction.UP);
        room2Center.setAlignment(room2South, Direction.DOWN);
        room2Center.setAlignment(room1Center, Direction.BELOW);

        room2North.setAlignment(room2NorthWest, Direction.LEFT);
        room2North.setAlignment(room2NorthEast, Direction.RIGHT);
        room2North.setAlignment(room2Center, Direction.DOWN);
        room2North.setAlignment(room1North, Direction.BELOW);

        room2NorthWest.setAlignment(room2North, Direction.RIGHT);
        room2NorthWest.setAlignment(room2West, Direction.DOWN);
        room2NorthWest.setAlignment(room1NorthWest, Direction.BELOW);

        room2West.setAlignment(room2Center, Direction.RIGHT);
        room2West.setAlignment(room2NorthWest, Direction.UP);
        room2West.setAlignment(room2SouthWest, Direction.DOWN);
        room2West.setAlignment(room1West, Direction.BELOW);

        room2SouthWest.setAlignment(room2South, Direction.RIGHT);
        room2SouthWest.setAlignment(room2West, Direction.UP);
        room2SouthWest.setAlignment(room1SouthWest, Direction.BELOW);

        room2South.setAlignment(room2SouthWest, Direction.LEFT);
        room2South.setAlignment(room2SouthEast, Direction.RIGHT);
        room2South.setAlignment(room2Center, Direction.UP);
        room2South.setAlignment(room1South, Direction.BELOW);

        room2SouthEast.setAlignment(room2South, Direction.LEFT);
        room2SouthEast.setAlignment(room2East, Direction.UP);
        room2SouthEast.setAlignment(room1SouthEast, Direction.BELOW);

        room2East.setAlignment(room2Center, Direction.LEFT);
        room2East.setAlignment(room2NorthEast, Direction.UP);
        room2East.setAlignment(room2SouthEast, Direction.DOWN);
        room2East.setAlignment(room1East, Direction.BELOW);

        room2NorthEast.setAlignment(room2North, Direction.LEFT);
        room2NorthEast.setAlignment(room2East, Direction.DOWN);
        room2NorthEast.setAlignment(room1NorthEast, Direction.BELOW);
    }
}
