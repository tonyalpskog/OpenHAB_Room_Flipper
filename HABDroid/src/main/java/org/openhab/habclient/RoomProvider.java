package org.openhab.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.openhab.habdroid.R;
import org.openhab.util.IColorParser;
import org.openhab.util.ILogger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2013.
 */
public class RoomProvider {
    public HashMap<UUID, Room> roomHash;
    public UUID initialRoomId;
    private Context mContext = null;
    private final ILogger mLogger;
    private final IColorParser mColorParser;
    private Room mNewRoom = null;

    public RoomProvider(Context context, ILogger logger, IColorParser colorParser) {
        mContext = context;
        mLogger = logger;
        mColorParser = colorParser;
        roomHash = new HashMap<UUID, Room>();
        createRooms();
    }

    private boolean add(Room room) {
        roomHash.put(room.getId(), room);
        return true;
    }


    public void saveRoom(Room room) {
        if(!roomHash.containsValue(room)) {
            if(room == mNewRoom) {
                addRoom(room.shallowClone());
                mNewRoom = null;//Reset temporary instance.//TODO - Fix this temporary solution when adding DB support
            }
        }
    }

    private boolean remove(Room room) {
        roomHash.remove(room.getId());
        Iterator iterator = roomHash.values().iterator();
        while(iterator.hasNext()) {
            Room tempRoom = (Room) iterator.next();
            tempRoom.removeAlignment(room);
        }
        return true;
    }

    public Room get(UUID uuid) {
        Room room = roomHash.get(uuid);
        if(room != null)
            return room;
        else if(mNewRoom.getId().equals(uuid))
            return mNewRoom;

        return null;
    }

    public Room getInitialRoom() {
        return roomHash.get(initialRoomId);
    }

    private Bitmap getBitmap(int bitmapResourceId) {
        return BitmapFactory.decodeResource(mContext.getResources(), bitmapResourceId);
    }

    public Room createNewRoom() {
        mNewRoom = new Room(null, "New room", getBitmap(R.drawable.empty_room), mLogger, mColorParser);
//        add(room);
        return mNewRoom;
    }

    public Room addRoom(Room room) {
        add(room);
        return room;
    }

    public Room removeRoom(Room room) {
        remove(room);
        return room;
    }

    private void createRooms() {
        if (!HABApplication.getOpenHABSetting(mContext).runningInDemoMode()) {
            //Initializing basement
            Room roomBasementLaundry = addRoom(new Room("FF_Bath", "Tvättstuga", R.drawable.basement_laundry, mContext, mLogger, mColorParser));
            Room roomBasementStaircase = addRoom(new Room("FF_Office", "Källar hall", R.drawable.basement_staircase, mContext, mLogger, mColorParser));
            Room roomBasementBath = addRoom(new Room("FF_Child", "Källar bad", R.drawable.basement_bath, mContext, mLogger, mColorParser));
            Room roomBasementHobby = addRoom(new Room("GF_Living", "Hobbyrum", R.drawable.basement_hobby, mContext, mLogger, mColorParser));
            Room roomBasementSauna = addRoom(new Room("GF_Kitchen", "Bastu", R.drawable.basement_sauna, mContext, mLogger, mColorParser));
            Room roomBasementStorage = addRoom(new Room("FF_Bed", "Källar förråd", R.drawable.basement_storage, mContext, mLogger, mColorParser));
            Room roomBasementSouth = addRoom(new Room("GF_Toilet", "Krypgrund", R.drawable.basement_south, mContext, mLogger, mColorParser));

            //Initializing ground floor
            Room roomGroundFloorHallway = addRoom(new Room("FF_Bath", "Hall", R.drawable.groundfloor_hallway, mContext, mLogger, mColorParser));
            Room roomGroundFloorBath = addRoom(new Room("FF_Child", "Gästtoa", R.drawable.groundfloor_bath, mContext, mLogger, mColorParser));
            Room roomGroundFloorStorage = addRoom(new Room("GF_Living", "Förråd", R.drawable.groundfloor_storage, mContext, mLogger, mColorParser));
            Room roomGroundFloorCleaning = addRoom(new Room("GF_Kitchen", "Städskrubb", R.drawable.groundfloor_cleaning, mContext, mLogger, mColorParser));
            Room roomGroundFloorKitchen = addRoom(new Room("FF_Bed", "Kök", R.drawable.groundfloor_kitchen, mContext, mLogger, mColorParser));
            Room roomGroundFloorLivingroom = addRoom(new Room("GF_Toilet", "Vardagsrum", R.drawable.groundfloor_livingroom, mContext, mLogger, mColorParser));
            Room roomGroundFloorDiningroom = addRoom(new Room("GF_Toilet", "Matsal", R.drawable.groundfloor_diningroom, mContext, mLogger, mColorParser));

            initialRoomId = roomBasementStaircase.getId();

            //Aligning basement
            roomBasementLaundry.setAlignment(roomBasementStaircase, Direction.RIGHT);
            roomBasementLaundry.setAlignment(roomBasementHobby, Direction.UP);
            roomBasementLaundry.setAlignment(roomBasementHobby, Direction.UP_RIGHT);
            roomBasementLaundry.setAlignment(roomGroundFloorBath, Direction.ABOVE);

            roomBasementStaircase.setAlignment(roomBasementHobby, Direction.UP);
            roomBasementStaircase.setAlignment(roomBasementHobby, Direction.UP_LEFT);
            roomBasementStaircase.setAlignment(roomBasementLaundry, Direction.LEFT);
            roomBasementStaircase.setAlignment(roomBasementBath, Direction.RIGHT);
            roomBasementStaircase.setAlignment(roomBasementSauna, Direction.UP_RIGHT);
            roomBasementStaircase.setAlignment(roomGroundFloorHallway, Direction.ABOVE);

            roomBasementBath.setAlignment(roomBasementStaircase, Direction.LEFT);
            roomBasementBath.setAlignment(roomBasementSauna, Direction.UP);
            roomBasementBath.setAlignment(roomBasementHobby, Direction.UP_LEFT);
            roomBasementBath.setAlignment(roomGroundFloorHallway, Direction.ABOVE);

            roomBasementHobby.setAlignment(roomBasementSouth, Direction.UP);
            roomBasementHobby.setAlignment(roomBasementStorage, Direction.UP_RIGHT);
            roomBasementHobby.setAlignment(roomBasementStaircase, Direction.DOWN);
            roomBasementHobby.setAlignment(roomBasementSauna, Direction.RIGHT);
            roomBasementHobby.setAlignment(roomBasementLaundry, Direction.DOWN_LEFT);
            roomBasementHobby.setAlignment(roomBasementBath, Direction.DOWN_RIGHT);
            roomBasementHobby.setAlignment(roomGroundFloorKitchen, Direction.ABOVE);

            roomBasementSauna.setAlignment(roomBasementStorage, Direction.UP);
            roomBasementSauna.setAlignment(roomBasementStorage, Direction.UP_LEFT);
            roomBasementSauna.setAlignment(roomBasementHobby, Direction.LEFT);
            roomBasementSauna.setAlignment(roomBasementBath, Direction.DOWN);
            roomBasementSauna.setAlignment(roomBasementStaircase, Direction.DOWN_LEFT);
            roomBasementSauna.setAlignment(roomGroundFloorLivingroom, Direction.ABOVE);

            roomBasementStorage.setAlignment(roomBasementHobby, Direction.LEFT);
            roomBasementStorage.setAlignment(roomBasementSouth, Direction.UP);
            roomBasementStorage.setAlignment(roomBasementSouth, Direction.UP_LEFT);
            roomBasementStorage.setAlignment(roomBasementSauna, Direction.DOWN);
            roomBasementStorage.setAlignment(roomBasementHobby, Direction.DOWN_LEFT);
            roomBasementStorage.setAlignment(roomGroundFloorLivingroom, Direction.ABOVE);

            roomBasementSouth.setAlignment(roomBasementHobby, Direction.DOWN);
            roomBasementSouth.setAlignment(roomBasementHobby, Direction.DOWN_LEFT);
            roomBasementSouth.setAlignment(roomBasementStorage, Direction.DOWN_RIGHT);
            roomBasementSouth.setAlignment(roomGroundFloorDiningroom, Direction.ABOVE);

            //Aligning ground floor
            roomGroundFloorHallway.setAlignment(roomGroundFloorCleaning, Direction.LEFT);
            roomGroundFloorHallway.setAlignment(roomGroundFloorBath, Direction.DOWN_LEFT);
            roomGroundFloorHallway.setAlignment(roomGroundFloorKitchen, Direction.UP_LEFT);
            roomGroundFloorHallway.setAlignment(roomGroundFloorLivingroom, Direction.UP_RIGHT);
            roomGroundFloorHallway.setAlignment(roomGroundFloorStorage, Direction.UP);
            roomGroundFloorHallway.setAlignment(roomBasementStaircase, Direction.BELOW);

            roomGroundFloorBath.setAlignment(roomGroundFloorHallway, Direction.UP);
            roomGroundFloorBath.setAlignment(roomGroundFloorHallway, Direction.UP_RIGHT);
            roomGroundFloorBath.setAlignment(roomGroundFloorCleaning, Direction.RIGHT);
            roomGroundFloorBath.setAlignment(roomBasementLaundry, Direction.BELOW);

            roomGroundFloorCleaning.setAlignment(roomGroundFloorBath, Direction.LEFT);
            roomGroundFloorCleaning.setAlignment(roomGroundFloorHallway, Direction.UP);
            roomGroundFloorCleaning.setAlignment(roomGroundFloorHallway, Direction.UP_RIGHT);
            roomGroundFloorCleaning.setAlignment(roomGroundFloorHallway, Direction.UP_LEFT);
            roomGroundFloorCleaning.setAlignment(roomGroundFloorHallway, Direction.RIGHT);
            roomGroundFloorCleaning.setAlignment(roomBasementLaundry, Direction.BELOW);

            roomGroundFloorStorage.setAlignment(roomGroundFloorKitchen, Direction.LEFT);
            roomGroundFloorStorage.setAlignment(roomGroundFloorDiningroom, Direction.UP);
            roomGroundFloorStorage.setAlignment(roomGroundFloorDiningroom, Direction.UP_LEFT);
            roomGroundFloorStorage.setAlignment(roomGroundFloorDiningroom, Direction.UP_RIGHT);
            roomGroundFloorStorage.setAlignment(roomGroundFloorLivingroom, Direction.RIGHT);
            roomGroundFloorStorage.setAlignment(roomGroundFloorHallway, Direction.DOWN);
            roomGroundFloorStorage.setAlignment(roomGroundFloorHallway, Direction.DOWN_LEFT);
            roomGroundFloorStorage.setAlignment(roomGroundFloorHallway, Direction.DOWN_RIGHT);
            roomGroundFloorStorage.setAlignment(roomBasementHobby, Direction.BELOW);

            roomGroundFloorKitchen.setAlignment(roomGroundFloorDiningroom, Direction.UP);
            roomGroundFloorKitchen.setAlignment(roomGroundFloorDiningroom, Direction.UP_RIGHT);
            roomGroundFloorKitchen.setAlignment(roomGroundFloorStorage, Direction.RIGHT);
            roomGroundFloorKitchen.setAlignment(roomGroundFloorHallway, Direction.DOWN);
            roomGroundFloorKitchen.setAlignment(roomGroundFloorHallway, Direction.DOWN_RIGHT);
            roomGroundFloorKitchen.setAlignment(roomBasementHobby, Direction.BELOW);

            roomGroundFloorDiningroom.setAlignment(roomGroundFloorStorage, Direction.DOWN);
            roomGroundFloorDiningroom.setAlignment(roomGroundFloorKitchen, Direction.DOWN_LEFT);
            roomGroundFloorDiningroom.setAlignment(roomGroundFloorLivingroom, Direction.DOWN_RIGHT);
            roomGroundFloorDiningroom.setAlignment(roomBasementSouth, Direction.BELOW);

            roomGroundFloorLivingroom.setAlignment(roomGroundFloorDiningroom, Direction.UP);
            roomGroundFloorLivingroom.setAlignment(roomGroundFloorDiningroom, Direction.UP_LEFT);
            roomGroundFloorLivingroom.setAlignment(roomGroundFloorStorage, Direction.LEFT);
            roomGroundFloorLivingroom.setAlignment(roomGroundFloorHallway, Direction.DOWN);
            roomGroundFloorLivingroom.setAlignment(roomGroundFloorHallway, Direction.DOWN_LEFT);
            roomGroundFloorLivingroom.setAlignment(roomBasementStorage, Direction.BELOW);
        } else {
            //Demo
            Room room0Center = addRoom(new Room("FF_Bath", "Källare mitten", getBitmap(R.drawable.room_0_c), mLogger, mColorParser));
            Room room0East = addRoom(new Room("FF_Office", "Källare öster", getBitmap(R.drawable.room_0_e), mLogger, mColorParser));
            Room room0North = addRoom(new Room("FF_Child", "Källare norr", getBitmap(R.drawable.room_0_n), mLogger, mColorParser));
            Room room0NorthEast = addRoom(new Room("GF_Living", "Källare nordost", getBitmap(R.drawable.room_0_ne), mLogger, mColorParser));
            Room room0NorthWest = addRoom(new Room("GF_Kitchen", "Källare nordväst", getBitmap(R.drawable.room_0_nw), mLogger, mColorParser));
            Room room0South = addRoom(new Room("FF_Bed", "Källare söder", getBitmap(R.drawable.room_0_s), mLogger, mColorParser));
            Room room0SouthEast = addRoom(new Room("GF_Toilet", "Källare sydost", getBitmap(R.drawable.room_0_se), mLogger, mColorParser));
            Room room0SouthWest = addRoom(new Room("GF_Corridor", "Källare sydväst", getBitmap(R.drawable.room_0_sw), mLogger, mColorParser));
            Room room0West = addRoom(new Room("FF_Corridor", "Källare väster", getBitmap(R.drawable.room_0_w), mLogger, mColorParser));

            Room room1Center = addRoom(new Room("Outdoor", "Entré plan mitten", getBitmap(R.drawable.room_1_c), mLogger, mColorParser));
            Room room1East = addRoom(new Room("Shutters", "Entré plan öster", getBitmap(R.drawable.room_1_e), mLogger, mColorParser));
            Room room1North = addRoom(new Room("Weather", "Entré plan norr", getBitmap(R.drawable.room_1_n), mLogger, mColorParser));
            Room room1NorthEast = addRoom(new Room("Status", "Entré plan nordost", getBitmap(R.drawable.room_1_ne), mLogger, mColorParser));
            Room room1NorthWest = addRoom(new Room("Lights", "Entré plan nordväst", getBitmap(R.drawable.room_1_nw), mLogger, mColorParser));
            Room room1South = addRoom(new Room("Heating", "Entré plan söder", getBitmap(R.drawable.room_1_s), mLogger, mColorParser));
            Room room1SouthEast = addRoom(new Room("Temperature", "Entré plan sydost", getBitmap(R.drawable.room_1_se), mLogger, mColorParser));
            Room room1SouthWest = addRoom(new Room("Windows", "Entré plan sydväst", getBitmap(R.drawable.room_1_sw), mLogger, mColorParser));
            Room room1West = addRoom(new Room("Weather_Chart", "Entré plan väster", getBitmap(R.drawable.room_1_w), mLogger, mColorParser));

            Room room2Center = addRoom(new Room("FF_Bath", "Övre plan mitten", getBitmap(R.drawable.room_2_c), mLogger, mColorParser));
            Room room2East = addRoom(new Room("FF_Bath", "Övre plan öster", getBitmap(R.drawable.room_2_e), mLogger, mColorParser));
            Room room2North = addRoom(new Room("FF_Bath", "Övre plan norr", getBitmap(R.drawable.room_2_n), mLogger, mColorParser));
            Room room2NorthEast = addRoom(new Room("FF_Bath", "Övre plan nordost", getBitmap(R.drawable.room_2_ne), mLogger, mColorParser));
            Room room2NorthWest = addRoom(new Room("FF_Bath", "Övre plan nordväst", getBitmap(R.drawable.room_2_nw), mLogger, mColorParser));
            Room room2South = addRoom(new Room("FF_Bath", "Övre plan söder", getBitmap(R.drawable.room_2_s), mLogger, mColorParser));
            Room room2SouthEast = addRoom(new Room("FF_Bath", "Övre plan sydost", getBitmap(R.drawable.room_2_se), mLogger, mColorParser));
            Room room2SouthWest = addRoom(new Room("FF_Bath", "Övre plan sydväst", getBitmap(R.drawable.room_2_sw), mLogger, mColorParser));
            Room room2West = addRoom(new Room("FF_Bath", "Övre plan väster", getBitmap(R.drawable.room_2_w), mLogger, mColorParser));

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
}
