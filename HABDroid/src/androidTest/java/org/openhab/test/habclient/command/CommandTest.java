package org.openhab.test.habclient.command;

import android.test.AndroidTestCase;

import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.domain.command.WidgetPhraseMatchResult;
import org.openhab.domain.model.OpenHABItemType;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetDataSource;
import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.domain.model.OpenHABWidgetTypeSet;
import org.openhab.domain.util.DecimalHandler;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.IRegularExpression;
import org.openhab.domain.util.RegExAccuracyResult;
import org.openhab.domain.util.RegExResult;
import org.openhab.habclient.ApplicationMode;
import org.openhab.habclient.IApplicationModeProvider;
import org.openhab.habclient.IRoomProvider;
import org.openhab.habclient.Room;
import org.openhab.habclient.command.CommandAnalyzer;
import org.openhab.habclient.command.CommandAnalyzerResult;
import org.openhab.habclient.command.CommandPhraseMatchResult;
import org.openhab.habclient.command.OpenHABWidgetCommandType;
import org.openhab.habclient.dagger.AndroidModule;
import org.openhab.habclient.dagger.ClientModule;
import org.openhab.domain.IDocumentFactory;
import org.openhab.domain.DocumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

/**
 * Created by Tony Alpskog in 2014.
 */
public class CommandTest extends AndroidTestCase {
    private ArrayList<String> mListOfTestPhrases;
    private ArrayList<String> mListOfTestPhrases2;
    private String[] mTestedRoomsNameArray;
    private int mDoubles;

    final String[] mFullRoomNameArray = new String[]{"Tvättstuga", "Källar hall", "Källar bad", "Bastu"
            , "Hobbyrum", "Källar förråd", "Krypgrund", "Hall", "Städskrubb", "Gästtoa"
            , "Förråd", "Kök", "Matsal", "Vardagsrum"};

    @Inject CommandAnalyzerWrapper mCommandAnalyzer;
    @Inject IRegularExpression mRegularExpression;
    @Inject ILogger mLogger;
    @Inject IColorParser mColorParser;
    @Inject OpenHABWidgetProvider mWidgetProvider;
    @Inject IRoomProvider mRoomProvider;
    @Inject IApplicationModeProvider mApplicationModeProvider;
    @Inject IPopularNameProvider mPopularNameProvider;

    public void setUp() throws Exception {
        super.setUp();

        ObjectGraph graph = ObjectGraph.create(new AndroidModule(getContext()), new TestModule());
        graph.inject(this);

        loadHttpDataFromString();

        mTestedRoomsNameArray = new String[]{mFullRoomNameArray[0], mFullRoomNameArray[1], mFullRoomNameArray[5], mFullRoomNameArray[9], mFullRoomNameArray[13]};
        mDoubles = 2;//"Källar förråd" will also get hit on "förråd" AND "Källar hall" will also get hit on "hall"

        mListOfTestPhrases = new ArrayList<String>();
        mListOfTestPhrases.add(mTestedRoomsNameArray[0]);
        mListOfTestPhrases.add(mTestedRoomsNameArray[1]);
        mListOfTestPhrases.add("hej hopp " + mTestedRoomsNameArray[2]);
        mListOfTestPhrases.add("Det var en gång en " + mTestedRoomsNameArray[3] + " som mådde dåligt");
        mListOfTestPhrases.add(mTestedRoomsNameArray[4] + " är bra att ha");

        mListOfTestPhrases2 = new ArrayList<String>();
        mListOfTestPhrases2.add("Window_FF_Bath");
        mListOfTestPhrases2.add(mTestedRoomsNameArray[0]);
        mListOfTestPhrases2.add(mTestedRoomsNameArray[1] + " Light_FF_Bath_Ceiling");
        mListOfTestPhrases2.add("hej hopp " + mTestedRoomsNameArray[2] + " Windows");
        mListOfTestPhrases2.add("Det var Window_FF_Office_Window en gång en " + mTestedRoomsNameArray[3] + " som mådde dåligt");
        mListOfTestPhrases2.add("Shutter_FF_Bath " + mTestedRoomsNameArray[4] + " är bra Light_Outdoor_Frontdoor att ha");
    }

    @Module(injects = CommandTest.class, includes = ClientModule.class, overrides = true)
    public class TestModule {
        @Provides @Singleton
        public CommandAnalyzer provideCommandAnalyzer(CommandAnalyzerWrapper wrapper) {
            return wrapper;
        }
    }

    public void testFinalArrayOfNamesUsedInTests() {
        assertEquals(14, mFullRoomNameArray.length);
        assertEquals(14, mRoomProvider.getAllRooms().size());

        final Map<String, Room> roomNamesMap = mRoomProvider.getMapOfRoomNames();
        assertFalse("No MapOfRoomNames items available.", roomNamesMap.isEmpty());

        String availableRooms = "";
        for (String roomName : roomNamesMap.keySet()) {
            availableRooms += roomName + " , ";
        }

        String availableRoomsResult = "";
        String unavailableRoomsResult = "";
        for (String roomName : mFullRoomNameArray) {
            if (!roomNamesMap.keySet().contains(roomName.toUpperCase()))
                unavailableRoomsResult += roomName.toUpperCase() + " , ";
            else
                availableRoomsResult += roomName.toUpperCase() + " , ";
        }

        assertEquals("Some tested rooms doesn't match actual rooms: ", "", unavailableRoomsResult);
        if (unavailableRoomsResult.length() > 0)
            assertEquals("Tested rooms doesn't match actual rooms: ", availableRooms, availableRoomsResult);
    }

    public void testThatAllTestedRoomsCanBeFoundInTheTestPhrases() {
        //Test that all rooms can be found inside a phrase
        for (int i = 0; i < mTestedRoomsNameArray.length; i++) {
            assertTrue(mListOfTestPhrases.get(i).toUpperCase().contains(mTestedRoomsNameArray[i].toUpperCase()));
        }
    }

    private String getAllStringItemsInOneString(List items) {
        String result = "";
        boolean firstItem = true;
        for (Object item : items.toArray()) {
            result += firstItem ? item.toString() : ", " + item.toString();
            firstItem = false;
        }

        return result;
    }

    public void testFindRoomInPhrases() throws Exception {
        List<Room> roomList = mCommandAnalyzer.getRoomsFromPhrases(mListOfTestPhrases, ApplicationMode.RoomFlipper);
        assertEquals(mTestedRoomsNameArray.length + mDoubles, roomList.size());

        List<String> roomNameResultList = new ArrayList<String>();
        for (Room room : roomList)
            roomNameResultList.add(room.getName().toUpperCase());

        final Map<String, Room> roomMap = mRoomProvider.getMapOfRoomNames();

        for (int i = 0; i < mTestedRoomsNameArray.length; i++) {
            assertTrue("MapOfRoomNames does not contain <" + mTestedRoomsNameArray[i].toUpperCase() + ">: " + roomMap.keySet().toString(), roomMap.containsKey(mTestedRoomsNameArray[i].toUpperCase()));

            assertTrue("Room name nr " + i + " <" + mTestedRoomsNameArray[i].toUpperCase() + "> could not be found in the result: " + getAllStringItemsInOneString(roomNameResultList)
                    , roomNameResultList.contains(roomMap.get(mTestedRoomsNameArray[i].toUpperCase()).getName().toUpperCase()));
        }

        List<UUID> uuidResultList = new ArrayList<UUID>();
        for (Room room : roomList)
            uuidResultList.add(room.getId());

        for (int i = 0; i < mTestedRoomsNameArray.length; i++) {
            assertTrue("Room UUID nr " + i + " <" + roomMap.get(mTestedRoomsNameArray[i].toUpperCase()).getId()
                    + "> could not be found in the result: " + getAllStringItemsInOneString(uuidResultList)
                    , uuidResultList.contains(roomMap.get(mTestedRoomsNameArray[i].toUpperCase()).getId()));
        }
    }

    //-------------------------- UNITS -------------------------------
    private void requestOpenHABData(String htmlResponseData) {
        final IDocumentFactory responseParser = new DocumentFactory();
        Document document = null;
        try {
            assertTrue("htmlResponseData is NULL!", htmlResponseData != null);
            document = responseParser.build(htmlResponseData);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


        assertEquals("sitemap", document.getFirstChild().getNodeName());
        Node rootNode = document.getElementsByTagName("homepage").item(0);


        assertTrue(rootNode.hasChildNodes());
        assertEquals(8, rootNode.getChildNodes().getLength());

        int childWidgetsFound = 0, childTitlesFound = 0, childIDsFound = 0, childIconsFound = 0, childLinksFound = 0;

        OpenHABWidgetDataSource openHABWidgetDataSource = new OpenHABWidgetDataSource(mLogger, mColorParser);

        for (int i = 0; i < rootNode.getChildNodes().getLength(); i++) {
            Node childNode = rootNode.getChildNodes().item(i);
            if (childNode.getNodeName().equals("widget")) {
                childWidgetsFound++;
            } else if (childNode.getNodeName().equals("title")) {
                openHABWidgetDataSource.setTitle(childNode.getTextContent());
                childTitlesFound++;
            } else if (childNode.getNodeName().equals("id")) {
                openHABWidgetDataSource.setId(childNode.getTextContent());
                childIDsFound++;
            } else if (childNode.getNodeName().equals("icon")) {
                openHABWidgetDataSource.setIcon(childNode.getTextContent());
                childIconsFound++;
            } else if (childNode.getNodeName().equals("link")) {
                openHABWidgetDataSource.setLink(childNode.getTextContent());
                childLinksFound++;
            }
        }

        assertEquals(4, childWidgetsFound);
        assertEquals(1, childTitlesFound);
        assertEquals(1, childIDsFound);
        assertEquals(0, childIconsFound);
        assertEquals(1, childLinksFound);
        assertEquals("https://demo.openhab.org:8443/rest/sitemaps/demo/demo", openHABWidgetDataSource.getLink());

        openHABWidgetDataSource = new OpenHABWidgetDataSource(rootNode, mLogger, mColorParser);
        
        assertEquals("Number of rootWidget childs is incorrect: ", 4, openHABWidgetDataSource.getRootWidget().getChildren().size());
        assertEquals("Number of total childs is incorrect: ", 13, openHABWidgetDataSource.getWidgets().size());

        mWidgetProvider.setOpenHABWidgets(openHABWidgetDataSource);
    }

    private void loadHttpDataFromString() {
        StringBuffer htmlResponseData = new StringBuffer();
        htmlResponseData.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><sitemap><name>demo</name><label>Main Menu</label><link>https://demo.openhab.org:8443/rest/sitemaps/demo</link><homepage><id>demo</id><title>Main Menu</title><link>https://demo.openhab.org:8443/rest/sitemaps/demo/demo</link><leaf>false</leaf><widget><widgetId>demo_0</widgetId><type>Frame</type><label></label><icon>frame</icon><widget><widgetId>demo_0_0</widgetId><type>Group</type><label>First Floor</label><icon>firstfloor</icon><item><type>GroupItem</type><name>gFF</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/gFF</link></item><linkedPage><id>0000</id><title>First Floor</title><icon>firstfloor</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0000</link><leaf>false</leaf><widget><widgetId>0000_0</widgetId><type>Group</type><label>Bathroom</label><icon>bath</icon><item><type>GroupItem</type><name>FF_Bath</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/FF_Bath</link></item><linkedPage><id>FF_Bath</id><title>Bathroom</title><icon>bath</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/FF_Bath</link><leaf>true</leaf><widget><widgetId>FF_Bath_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_FF_Bath_Ceiling</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_FF_Bath_Ceiling</link></item></widget><widget><widgetId>FF_Bath_1</widgetId><type>Switch</type><label>Mirror</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_FF_Bath_Mirror</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_FF_Bath_Mirror</link></item></widget><widget><widgetId>FF_Bath_2</widgetId><type>Switch</type><label>Bath</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_FF_Bath</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_FF_Bath</link></item></widget><widget><widgetId>FF_Bath_3</widgetId><type>Switch</type><label>Bath</label><icon>rollershutter-100</icon><item><type>RollershutterItem</type><name>Shutter_FF_Bath</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/Shutter_FF_Bath</link></item></widget><widget><widgetId>FF_Bath_4</widgetId><type>Text</type><label>Temperature [20.2 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_FF_Bath</name><state>20.20000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_FF_Bath</link></item></widget><widget><widgetId>FF_Bath_5</widgetId><type>Text</type><label>Bath [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_FF_Bath</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Bath</link></item></widget></linkedPage></widget><widget><widgetId>0000_1</widgetId><type>Group</type><label>Office</label><icon>office</icon><item><type>GroupItem</type><name>FF_Office</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/FF_Office</link></item><linkedPage><id>FF_Office</id><title>Office</title><icon>office</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/FF_Office</link><leaf>true</leaf><widget><widgetId>FF_Office_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_FF_Office_Ceiling</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_FF_Office_Ceiling</link></item></widget><widget><widgetId>FF_Office_1</widgetId><type>Switch</type><label>Office</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_FF_Office</name><state>OFF</state>");
        htmlResponseData.append("<link>https://demo.openhab.org:8443/rest/items/Heating_FF_Office</link></item></widget><widget><widgetId>FF_Office_2</widgetId><type>Switch</type><label>Office Window</label><icon>rollershutter-100</icon><item><type>RollershutterItem</type><name>Shutter_FF_Office_Window</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/Shutter_FF_Office_Window</link></item></widget><widget><widgetId>FF_Office_3</widgetId><type>Switch</type><label>Office Door</label><icon>rollershutter-100</icon><item><type>RollershutterItem</type><name>Shutter_FF_Office_Door</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/Shutter_FF_Office_Door</link></item></widget><widget><widgetId>FF_Office_4</widgetId><type>Text</type><label>Temperature [18.6 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_FF_Office</name><state>18.60000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_FF_Office</link></item></widget><widget><widgetId>FF_Office_5</widgetId><type>Text</type><label>Office Window [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_FF_Office_Window</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Office_Window</link></item></widget><widget><widgetId>FF_Office_6</widgetId><type>Text</type><label>Balcony Door [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_FF_Office_Door</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Office_Door</link></item></widget></linkedPage></widget><widget><widgetId>0000_2</widgetId><type>Group</type><label>Child's Room</label><icon>boy1</icon><item><type>GroupItem</type><name>FF_Child</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/FF_Child</link></item><linkedPage><id>FF_Child</id><title>Child's Room</title><icon>boy1</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/FF_Child</link><leaf>true</leaf><widget><widgetId>FF_Child_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_FF_Child_Ceiling</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_FF_Child_Ceiling</link></item></widget><widget><widgetId>FF_Child_1</widgetId><type>Switch</type><label>Child's Room</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_FF_Child</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_FF_Child</link></item></widget><widget><widgetId>FF_Child_2</widgetId><type>Text</type><label>Temperature [18.9 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_FF_Child</name><state>18.90000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_FF_Child</link></item></widget></linkedPage></widget><widget><widgetId>0000_3</widgetId><type>Group</type><label>Bedroom</label><icon>bedroom</icon><item><type>GroupItem</type><name>FF_Bed</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/FF_Bed</link></item><linkedPage><id>FF_Bed</id><title>Bedroom</title><icon>bedroom</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/FF_Bed</link><leaf>true</leaf><widget><widgetId>FF_Bed_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_FF_Bed_Ceiling</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_FF_Bed_Ceiling</link></item></widget><widget><widgetId>FF_Bed_1</widgetId><type>Switch</type><label>Bedroom</label><icon>heating-on</icon><item><type>SwitchItem</type><name>Heating_FF_Bed</name><state>ON</state>");
        htmlResponseData.append("<link>https://demo.openhab.org:8443/rest/items/Heating_FF_Bed</link></item></widget><widget><widgetId>FF_Bed_2</widgetId><type>Switch</type><label>Bedroom</label><icon>rollershutter-100</icon><item><type>RollershutterItem</type><name>Shutter_FF_Bed</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/Shutter_FF_Bed</link></item></widget><widget><widgetId>FF_Bed_3</widgetId><type>Text</type><label>Temperature [19.2 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_FF_Bed</name><state>19.20000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_FF_Bed</link></item></widget><widget><widgetId>FF_Bed_4</widgetId><type>Text</type><label>Bedroom [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_FF_Bed</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Bed</link></item></widget></linkedPage></widget><widget><widgetId>0000_4</widgetId><type>Group</type><label>Corridor</label><icon>corridor</icon><item><type>GroupItem</type><name>FF_Corridor</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/FF_Corridor</link></item><linkedPage><id>FF_Corridor</id><title>Corridor</title><icon>corridor</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/FF_Corridor</link><leaf>true</leaf><widget><widgetId>FF_Corridor_0</widgetId><type>Switch</type><label>Corridor</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_FF_Corridor_Ceiling</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_FF_Corridor_Ceiling</link></item></widget></linkedPage></widget></linkedPage></widget><widget><widgetId>demo_0_0_1</widgetId><type>Group</type><label>Ground Floor</label><icon>groundfloor</icon><item><type>GroupItem</type><name>gGF</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/gGF</link></item><linkedPage><id>0001</id><title>Ground Floor</title><icon>groundfloor</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0001</link><leaf>false</leaf><widget><widgetId>0001_0</widgetId><type>Group</type><label>Living Room</label><icon>video</icon><item><type>GroupItem</type><name>GF_Living</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/GF_Living</link></item><linkedPage><id>GF_Living</id><title>Living Room</title><icon>video</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/GF_Living</link><leaf>true</leaf><widget><widgetId>GF_Living_0</widgetId><type>Slider</type><label>Table</label><icon>slider-0</icon><switchSupport>true</switchSupport><sendFrequency>0</sendFrequency><item><type>DimmerItem</type><name>Light_GF_Living_Table</name><state>2</state><link>https://demo.openhab.org:8443/rest/items/Light_GF_Living_Table</link></item></widget><widget><widgetId>GF_Living_1</widgetId><type>Switch</type><label>Livingroom</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_GF_Living</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Living</link></item></widget><widget><widgetId>GF_Living_2</widgetId><type>Switch</type><label>Livingroom</label><icon>rollershutter-0</icon><item><type>RollershutterItem</type><name>Shutter_GF_Living</name><state>0</state><link>https://demo.openhab.org:8443/rest/items/Shutter_GF_Living</link></item></widget><widget><widgetId>GF_Living_3</widgetId><type>Text</type><label>Temperature [18.8 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_GF_Living</name><state>18.80000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_GF_Living</link></item></widget><widget>");
        htmlResponseData.append("<widgetId>GF_Living_4</widgetId><type>Text</type><label>Terrace door [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_GF_Living</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Living</link></item></widget></linkedPage></widget><widget><widgetId>0001_1</widgetId><type>Group</type><label>Kitchen</label><icon>kitchen</icon><item><type>GroupItem</type><name>GF_Kitchen</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/GF_Kitchen</link></item><linkedPage><id>GF_Kitchen</id><title>Kitchen</title><icon>kitchen</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/GF_Kitchen</link><leaf>true</leaf><widget><widgetId>GF_Kitchen_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_GF_Kitchen_Ceiling</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_GF_Kitchen_Ceiling</link></item></widget><widget><widgetId>GF_Kitchen_1</widgetId><type>Switch</type><label>Table</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_GF_Kitchen_Table</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_GF_Kitchen_Table</link></item></widget><widget><widgetId>GF_Kitchen_2</widgetId><type>Switch</type><label>Kitchen</label><icon>heating-on</icon><item><type>SwitchItem</type><name>Heating_GF_Kitchen</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Kitchen</link></item></widget><widget><widgetId>GF_Kitchen_3</widgetId><type>Switch</type><label>Kitchen</label><icon>rollershutter-0</icon><item><type>RollershutterItem</type><name>Shutter_GF_Kitchen</name><state>0</state><link>https://demo.openhab.org:8443/rest/items/Shutter_GF_Kitchen</link></item></widget><widget><widgetId>GF_Kitchen_4</widgetId><type>Text</type><label>Temperature [18.8 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_GF_Kitchen</name><state>18.80000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_GF_Kitchen</link></item></widget><widget><widgetId>GF_Kitchen_5</widgetId><type>Text</type><label>Kitchen [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_GF_Kitchen</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Kitchen</link></item></widget></linkedPage></widget><widget><widgetId>0001_2</widgetId><type>Group</type><label>Toilet</label><icon>bath</icon><item><type>GroupItem</type><name>GF_Toilet</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/GF_Toilet</link></item><linkedPage><id>GF_Toilet</id><title>Toilet</title><icon>bath</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/GF_Toilet</link><leaf>true</leaf><widget><widgetId>GF_Toilet_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_GF_Toilet_Ceiling</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_GF_Toilet_Ceiling</link></item></widget><widget><widgetId>GF_Toilet_1</widgetId><type>Switch</type><label>Mirror</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_GF_Toilet_Mirror</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_GF_Toilet_Mirror</link></item></widget><widget><widgetId>GF_Toilet_2</widgetId><type>Switch</type><label>Toilet</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_GF_Toilet</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Toilet</link></item></widget><widget><widgetId>GF_Toilet_3</widgetId><type>Switch</type>");
        htmlResponseData.append("<label>Toilet</label><icon>rollershutter-100</icon><item><type>RollershutterItem</type><name>Shutter_GF_Toilet</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/Shutter_GF_Toilet</link></item></widget><widget><widgetId>GF_Toilet_4</widgetId><type>Text</type><label>Temperature [21.5 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_GF_Toilet</name><state>21.50000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_GF_Toilet</link></item></widget><widget><widgetId>GF_Toilet_5</widgetId><type>Text</type><label>Toilet [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_GF_Toilet</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Toilet</link></item></widget></linkedPage></widget><widget><widgetId>0001_3</widgetId><type>Group</type><label>Corridor</label><icon>corridor</icon><item><type>GroupItem</type><name>GF_Corridor</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/GF_Corridor</link></item><linkedPage><id>GF_Corridor</id><title>Corridor</title><icon>corridor</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/GF_Corridor</link><leaf>true</leaf><widget><widgetId>GF_Corridor_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_GF_Corridor_Ceiling</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_GF_Corridor_Ceiling</link></item></widget><widget><widgetId>GF_Corridor_1</widgetId><type>Switch</type><label>Wardrobe</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_GF_Corridor_Wardrobe</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_GF_Corridor_Wardrobe</link></item></widget><widget><widgetId>GF_Corridor_2</widgetId><type>Switch</type><label>GF Corridor</label><icon>heating-on</icon><item><type>SwitchItem</type><name>Heating_GF_Corridor</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Corridor</link></item></widget><widget><widgetId>GF_Corridor_3</widgetId><type>Text</type><label>Temperature [22.2 °C]</label><icon>temperature</icon><item><type>NumberItem</type><name>Temperature_GF_Corridor</name><state>22.20000000</state><link>https://demo.openhab.org:8443/rest/items/Temperature_GF_Corridor</link></item></widget><widget><widgetId>GF_Corridor_4</widgetId><type>Text</type><label>Frontdoor [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_GF_Frontdoor</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Frontdoor</link></item></widget></linkedPage></widget></linkedPage></widget><widget><widgetId>demo_0_0_1_2</widgetId><type>Group</type><label>Cellar</label><icon>cellar</icon><item><type>GroupItem</type><name>gC</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/gC</link></item><linkedPage><id>0002</id><title>Cellar</title><icon>cellar</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0002</link><leaf>true</leaf><widget><widgetId>0002_0</widgetId><type>Switch</type><label>Ceiling</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_C_Corridor_Ceiling</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_C_Corridor_Ceiling</link></item></widget><widget><widgetId>0002_1</widgetId><type>Switch</type><label>Staircase</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_C_Staircase</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_C_Staircase</link></item></widget><widget><widgetId>0002_2</widgetId><type>Switch</type>");
        htmlResponseData.append("<label>Washing</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_C_Washing_Ceiling</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_C_Washing_Ceiling</link></item></widget><widget><widgetId>0002_3</widgetId><type>Switch</type><label>Workshop</label><icon>switch-off</icon><item><type>SwitchItem</type><name>Light_C_Workshop</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Light_C_Workshop</link></item></widget></linkedPage></widget><widget><widgetId>demo_0_0_1_2_3</widgetId><type>Group</type><label>Outdoor</label><icon>garden</icon><item><type>GroupItem</type><name>Outdoor</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/Outdoor</link></item><linkedPage><id>0003</id><title>Outdoor</title><icon>garden</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0003</link><leaf>true</leaf><widget><widgetId>0003_0</widgetId><type>Switch</type><label>Garage</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_Outdoor_Garage</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_Outdoor_Garage</link></item></widget><widget><widgetId>0003_1</widgetId><type>Switch</type><label>Terrace</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_Outdoor_Terrace</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_Outdoor_Terrace</link></item></widget><widget><widgetId>0003_2</widgetId><type>Switch</type><label>Frontdoor</label><icon>switch-on</icon><item><type>SwitchItem</type><name>Light_Outdoor_Frontdoor</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Light_Outdoor_Frontdoor</link></item></widget><widget><widgetId>0003_3</widgetId><type>Text</type><label>Garage Door [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Garage_Door</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Garage_Door</link></item></widget></linkedPage></widget></widget><widget><widgetId>demo_1</widgetId><type>Frame</type><label>Weather</label><icon>frame</icon><widget><widgetId>demo_1_0</widgetId><type>Text</type><label>Outside Temperature [10.0 °C]</label><icon>temperature</icon><valuecolor>#ffa500</valuecolor><item><type>NumberItem</type><name>Weather_Temperature</name><state>10</state><link>https://demo.openhab.org:8443/rest/items/Weather_Temperature</link></item><linkedPage><id>0100</id><title>Outside Temperature [10.0 °C]</title><icon>temperature</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0100</link><leaf>false</leaf><widget><widgetId>0100_0</widgetId><type>Frame</type><label></label><icon>frame</icon><widget><widgetId>0100_0_0</widgetId><type>Text</type><label>Todays Maximum [14.0 °C]</label><icon>temperature</icon><valuecolor>#ffa500</valuecolor><item><type>NumberItem</type><name>Weather_Temp_Max</name><state>14</state><link>https://demo.openhab.org:8443/rest/items/Weather_Temp_Max</link></item></widget><widget><widgetId>0100_0_0_1</widgetId><type>Text</type><label>Todays Minimum [7.0 °C]</label><icon>temperature</icon><valuecolor>#ffa500</valuecolor><item><type>NumberItem</type><name>Weather_Temp_Min</name><state>7</state><link>https://demo.openhab.org:8443/rest/items/Weather_Temp_Min</link></item></widget><widget><widgetId>0100_0_0_1_2</widgetId><type>Text</type><label>Last Update [Sat 22:16]</label><icon>clock</icon><item><type>DateTimeItem</type><name>Weather_LastUpdate</name><state>2014-04-05T22:16:15</state><link>https://demo.openhab.org:8443/rest/items/Weather_LastUpdate</link></item></widget></widget><widget><widgetId>0100_1</widgetId><type>Frame</type><label></label><icon>frame</icon>");
        htmlResponseData.append("<widget><widgetId>0100_1_0</widgetId><type>Switch</type><label>Chart Period</label><icon>none</icon><mapping><command>0</command><label>Hour</label></mapping><mapping><command>1</command><label>Day</label></mapping><mapping><command>2</command><label>Week</label></mapping><item><type>NumberItem</type><name>Weather_Chart_Period</name><state>1</state><link>https://demo.openhab.org:8443/rest/items/Weather_Chart_Period</link></item></widget><widget><widgetId>0100_1_0_1_1</widgetId><type>Chart</type><label>Weather_Chart</label><icon>chart</icon><refresh>3600</refresh><period>D</period><item><type>GroupItem</type><name>Weather_Chart</name><state>Undefined</state><link>https://demo.openhab.org:8443/rest/items/Weather_Chart</link></item></widget></widget></linkedPage></widget></widget><widget><widgetId>demo_2</widgetId><type>Frame</type><label>Date</label><icon>frame</icon><widget><widgetId>demo_2_0</widgetId><type>Text</type><label>Date [Saturday, 05.04.2014]</label><icon>calendar</icon><item><type>DateTimeItem</type><name>Date</name><state>2014-04-05T22:05:15</state><link>https://demo.openhab.org:8443/rest/items/Date</link></item></widget></widget><widget><widgetId>demo_3</widgetId><type>Frame</type><label>Demo</label><icon>frame</icon><widget><widgetId>demo_3_0</widgetId><type>Text</type><label>Group Demo</label><icon>firstfloor</icon><linkedPage><id>0300</id><title>Group Demo</title><icon>firstfloor</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0300</link><leaf>false</leaf><widget><widgetId>0300_0</widgetId><type>Switch</type><label>All Lights [(11)]</label><icon>switch-on</icon><mapping><command>OFF</command><label>All Off</label></mapping><item><type>GroupItem</type><name>Lights</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Lights</link></item></widget><widget><widgetId>0300_1</widgetId><type>Group</type><label>No. of Active Heatings [(3)]</label><icon>heating-on</icon><item><type>GroupItem</type><name>Heating</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Heating</link></item><linkedPage><id>030001</id><title>No. of Active Heatings [(3)]</title><icon>heating-on</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/030001</link><leaf>true</leaf><widget><widgetId>030001_0</widgetId><type>Switch</type><label>GF Corridor</label><icon>heating-on</icon><item><type>SwitchItem</type><name>Heating_GF_Corridor</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Corridor</link></item></widget><widget><widgetId>030001_1</widgetId><type>Switch</type><label>Toilet</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_GF_Toilet</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Toilet</link></item></widget><widget><widgetId>030001_2</widgetId><type>Switch</type><label>Livingroom</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_GF_Living</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Living</link></item></widget><widget><widgetId>030001_3</widgetId><type>Switch</type><label>Kitchen</label><icon>heating-on</icon><item><type>SwitchItem</type><name>Heating_GF_Kitchen</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Heating_GF_Kitchen</link></item></widget><widget><widgetId>030001_4</widgetId><type>Switch</type><label>Bath</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_FF_Bath</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_FF_Bath</link></item></widget><widget><widgetId>030001_5</widgetId><type>Switch</type><label>Office</label><icon>heating-off</icon><item>");
        htmlResponseData.append("<type>SwitchItem</type><name>Heating_FF_Office</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_FF_Office</link></item></widget><widget><widgetId>030001_6</widgetId><type>Switch</type><label>Child's Room</label><icon>heating-off</icon><item><type>SwitchItem</type><name>Heating_FF_Child</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/Heating_FF_Child</link></item></widget><widget><widgetId>030001_7</widgetId><type>Switch</type><label>Bedroom</label><icon>heating-on</icon><item><type>SwitchItem</type><name>Heating_FF_Bed</name><state>ON</state><link>https://demo.openhab.org:8443/rest/items/Heating_FF_Bed</link></item></widget></linkedPage></widget><widget><widgetId>0300_2</widgetId><type>Group</type><label>Open windows [(4)]</label><icon>contact-open</icon><item><type>GroupItem</type><name>Windows</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Windows</link></item><linkedPage><id>030002</id><title>Open windows [(4)]</title><icon>contact-open</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/030002</link><leaf>true</leaf><widget><widgetId>030002_0</widgetId><type>Text</type><label>Frontdoor [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_GF_Frontdoor</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Frontdoor</link></item></widget><widget><widgetId>030002_1</widgetId><type>Text</type><label>Kitchen [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_GF_Kitchen</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Kitchen</link></item></widget><widget><widgetId>030002_2</widgetId><type>Text</type><label>Terrace door [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_GF_Living</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Living</link></item></widget><widget><widgetId>030002_3</widgetId><type>Text</type><label>Toilet [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_GF_Toilet</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_GF_Toilet</link></item></widget><widget><widgetId>030002_4</widgetId><type>Text</type><label>Bath [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_FF_Bath</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Bath</link></item></widget><widget><widgetId>030002_5</widgetId><type>Text</type><label>Bedroom [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_FF_Bed</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Bed</link></item></widget><widget><widgetId>030002_6</widgetId><type>Text</type><label>Office Window [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Window_FF_Office_Window</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Office_Window</link></item></widget><widget><widgetId>030002_7</widgetId><type>Text</type><label>Balcony Door [open]</label><icon>contact-open</icon><item><type>ContactItem</type><name>Window_FF_Office_Door</name><state>OPEN</state><link>https://demo.openhab.org:8443/rest/items/Window_FF_Office_Door</link></item></widget><widget><widgetId>030002_8</widgetId><type>Text</type><label>Garage Door [closed]</label><icon>contact-closed</icon><item><type>ContactItem</type><name>Garage_Door</name><state>CLOSED</state><link>https://demo.openhab.org:8443/rest/items/Garage_Door</link></item></widget></linkedPage></widget><widget><widgetId>0300_3</widgetId>");
        htmlResponseData.append("<type>Text</type><label>Avg. Room Temperature [19.8 °C]</label><icon>temperature</icon><item><type>GroupItem</type><name>Temperature</name><state>19.77500000</state><link>https://demo.openhab.org:8443/rest/items/Temperature</link></item></widget></linkedPage></widget><widget><widgetId>demo_3_0_1</widgetId><type>Text</type><label>Widget Overview</label><icon>chart</icon><linkedPage><id>0301</id><title>Widget Overview</title><icon>chart</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0301</link><leaf>false</leaf><widget><widgetId>0301_0</widgetId><type>Frame</type><label>Binary Widgets</label><icon>frame</icon><widget><widgetId>0301_0_0</widgetId><type>Switch</type><label>Toggle Switch</label><icon>switch-off</icon><item><type>SwitchItem</type><name>DemoSwitch</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/DemoSwitch</link></item></widget><widget><widgetId>0301_0_0_1</widgetId><type>Switch</type><label>Button Switch</label><icon>switch-off</icon><mapping><command>ON</command><label>On</label></mapping><item><type>SwitchItem</type><name>DemoSwitch</name><state>OFF</state><link>https://demo.openhab.org:8443/rest/items/DemoSwitch</link></item></widget></widget><widget><widgetId>0301_1</widgetId><type>Frame</type><label>Discrete Widgets</label><icon>frame</icon><widget><widgetId>0301_1_0</widgetId><type>Selection</type><label>Scene Selection</label><icon>sofa</icon><mapping><command>0</command><label>off</label></mapping><mapping><command>1</command><label>TV</label></mapping><mapping><command>2</command><label>Dinner</label></mapping><mapping><command>3</command><label>Reading</label></mapping><item><type>NumberItem</type><name>Scene_General</name><state>3</state><link>https://demo.openhab.org:8443/rest/items/Scene_General</link></item></widget><widget><widgetId>0301_1_0_1</widgetId><type>Switch</type><label>Scene</label><icon>sofa</icon><mapping><command>1</command><label>TV</label></mapping><mapping><command>2</command><label>Dinner</label></mapping><mapping><command>3</command><label>Reading</label></mapping><item><type>NumberItem</type><name>Scene_General</name><state>3</state><link>https://demo.openhab.org:8443/rest/items/Scene_General</link></item></widget><widget><widgetId>0301_1_0_1_2</widgetId><type>Setpoint</type><label>Temperature [21.0 °C]</label><icon>temperature</icon><minValue>16</minValue><maxValue>28</maxValue><step>0.5</step><item><type>NumberItem</type><name>Temperature_Setpoint</name><state>21.0</state><link>https://demo.openhab.org:8443/rest/items/Temperature_Setpoint</link></item></widget></widget><widget><widgetId>0301_2</widgetId><type>Frame</type><label>Percent-based Widgets</label><icon>frame</icon><widget><widgetId>0301_2_0</widgetId><type>Slider</type><label>Dimmer [100 %]</label><icon>slider-100</icon><switchSupport>true</switchSupport><sendFrequency>0</sendFrequency><item><type>DimmerItem</type><name>DimmedLight</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/DimmedLight</link></item></widget><widget><widgetId>0301_2_0_1</widgetId><type>Colorpicker</type><label>RGB Light</label><icon>slider-40</icon><item><type>ColorItem</type><name>RGBLight</name><state>125.51724,74.35898,45.882355</state><link>https://demo.openhab.org:8443/rest/items/RGBLight</link></item></widget><widget><widgetId>0301_2_0_1_2</widgetId><type>Switch</type><label>Roller Shutter</label><icon>rollershutter-100</icon><item><type>RollershutterItem</type><name>DemoShutter</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/DemoShutter</link></item></widget><widget><widgetId>0301_2_0_1_2_3</widgetId><type>Slider</type><label>Blinds [100 %]</label>");
        htmlResponseData.append("<icon>rollershutter-100</icon><switchSupport>false</switchSupport><sendFrequency>0</sendFrequency><item><type>DimmerItem</type><name>DemoBlinds</name><state>100</state><link>https://demo.openhab.org:8443/rest/items/DemoBlinds</link></item></widget></widget></linkedPage></widget><widget><widgetId>demo_3_0_1_2</widgetId><type>Text</type><label>Multimedia</label><icon>video</icon><linkedPage><id>0302</id><title>Multimedia</title><icon>video</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/0302</link><leaf>false</leaf><widget><widgetId>0302_0</widgetId><type>Frame</type><label>Radio Control</label><icon>frame</icon><widget><widgetId>0302_0_0</widgetId><type>Selection</type><label>Radio</label><icon>network</icon><mapping><command>0</command><label>off</label></mapping><mapping><command>1</command><label>HR3</label></mapping><mapping><command>2</command><label>SWR3</label></mapping><mapping><command>3</command><label>FFH</label></mapping><item><type>NumberItem</type><name>Radio_Station</name><state>1</state><link>https://demo.openhab.org:8443/rest/items/Radio_Station</link></item></widget><widget><widgetId>0302_0_0_1</widgetId><type>Slider</type><label>Volume [62.0 %]</label><icon>slider-60</icon><switchSupport>false</switchSupport><sendFrequency>0</sendFrequency><item><type>DimmerItem</type><name>Volume</name><state>62</state><link>https://demo.openhab.org:8443/rest/items/Volume</link></item></widget></widget><widget><widgetId>0302_1</widgetId><type>Frame</type><label>Multimedia Widgets</label><icon>frame</icon><widget><widgetId>0302_1_0</widgetId><type>Image</type><label>openHAB</label><icon>image</icon><url>https://demo.openhab.org:8443/proxy?sitemap=demo.sitemap&amp;widgetId=03020100</url><linkedPage><id>03020100</id><title>openHAB</title><icon>image</icon><link>https://demo.openhab.org:8443/rest/sitemaps/demo/03020100</link><leaf>true</leaf><widget><widgetId>03020100_0</widgetId><type>Text</type><label>http://www.openHAB.org</label><icon>icon</icon></widget></linkedPage></widget><widget><widgetId>0302_1_0_1</widgetId><type>Video</type><label></label><icon>video</icon><url>https://demo.openhab.org:8443/proxy?sitemap=demo.sitemap&amp;widgetId=03020101</url></widget><widget><widgetId>0302_1_0_1_2</widgetId><type>Webview</type><label></label><icon>webview</icon><height>8</height><url>http://heise-online.mobi/</url></widget></widget></linkedPage></widget></widget></homepage></sitemap>");

        requestOpenHABData(htmlResponseData.toString());
    }

    public void testGettingAllWidgetsLoadedFromDocument() {
        assertEquals(122, mWidgetProvider.getWidgetList((Set<OpenHABWidgetType>) null).size());

        String result = "";
        for (OpenHABWidget item : mWidgetProvider.getWidgetList((Set<OpenHABWidgetType>) null))
            result += (item.hasItem() ? "Item-" + (item.getItem().getType() != null ? item.getItem().getType().Name + "-" : "NULL-") + item.getItem().getName() : "Widget-" + (item.getType() != null ? item.getType().Name + "-" : "NULL-") + item.getId()) + ", ";

        assertTrue(result.startsWith("Item-ContactItem-Window_GF_Frontdoor, Item-NumberItem-Temperature_GF_Corridor, Item-SwitchItem-Heating_GF_Corridor, Item-SwitchItem-Light_GF_Corridor_Wardrobe"));
    }

    public void testGettingUnitItemWidgetsLoadedFromDocument() {
        assertEquals(92, mWidgetProvider.getWidgetList(OpenHABWidgetTypeSet.UnitItem).size());

        String result = "";
        for (OpenHABWidget item : mWidgetProvider.getWidgetList(OpenHABWidgetTypeSet.UnitItem))
            result += (item.hasItem() ? "Item-" + (item.getItem().getType() != null ? item.getItem().getType().Name + "-" : "NULL-") + item.getItem().getName() : "Widget-" + (item.getType() != null ? item.getType().Name + "-" : "NULL-") + item.getId()) + ", ";

        assertTrue(result.startsWith("Widget--03020100_0, Item-SwitchItem-Light_FF_Bath_Ceiling, Item-SwitchItem-Light_FF_Bath_Mirror, Item-SwitchItem-Heating_FF_Bath, Item-RollershutterItem-Shutter_FF_Bath, Item-SwitchItem-Light_FF_Office_Ceiling, Item-SwitchItem-Heating_FF_Office"));
    }

    public void testMethod_getListOfWidgetsFromListOfRooms() {
        assertEquals(122, mWidgetProvider.getWidgetList((Set<OpenHABWidgetType>) null).size());

        assertFalse("getListOfWidgetsFromListOfRooms(null) returned an empty list of units", mCommandAnalyzer.getListOfWidgetsFromListOfRooms(null).isEmpty());
        List<String> ls = new ArrayList<String>();
        ls.add("Terrace door");

        List<OpenHABWidget> resultingUnitList = mCommandAnalyzer.getUnitsFromPhrases(ls, null);
        StringBuilder sb = new StringBuilder();
        for (OpenHABWidget widget : resultingUnitList)
            sb.append(sb.length() > 0 ? ", " : "").append(widget.getId());
        assertEquals("'" + ls.get(0) + "' was found in: " + sb.toString() + " as => " + getAllStringItemsInOneString(resultingUnitList), 1, resultingUnitList.size());
        OpenHABWidget foundOhw = resultingUnitList.get(0);
        assertFalse("Returned OpenHABWidget was NULL", foundOhw == null);
        assertEquals("Non-matching widget name: " + foundOhw.getLabel(), ls.get(0), foundOhw.getLabel());
        assertEquals("First item name: " + (foundOhw.hasItem() ? foundOhw.getItem().getName() : foundOhw.getId()), 5, 1);
        assertFalse(mCommandAnalyzer.getUnitsFromPhrases(mListOfTestPhrases2, null).isEmpty());
    }

    private String getCommandPhraseMatchResultStringData(CommandPhraseMatchResult commandMatchResult) {
        StringBuilder sb = new StringBuilder();
        sb.append("Points = ").append(commandMatchResult.getPoint());
        sb.append("   Tags: ");
        for (String tag : commandMatchResult.getTags())
            sb.append(tag).append(", ");
        sb.append("   Phrases: ");
        for (String phrase : commandMatchResult.getTagPhrases())
            sb.append("'").append(phrase).append("', ");
        return sb.toString();
    }

    public void testGetSubStringsFromWidgetLabel() {
        String testLabel = "Temperature [20.1 °C]";
        assertEquals("Temperature", mPopularNameProvider.getPopularNameFromWidgetLabel(testLabel));

        testLabel = "Temperature [20.1 °C] Something else [Joo% man]";
        assertEquals("Temperature  Something else", mPopularNameProvider.getPopularNameFromWidgetLabel(testLabel));

        testLabel = "Temperature [20.1 °C] Something else [Joo% man] yeah";
        assertEquals("Temperature  Something else  yeah", mPopularNameProvider.getPopularNameFromWidgetLabel(testLabel));

        testLabel = "[bas€) (#umba] Temperature [20.1 °C] Something else [Joo% man] yeah";
        assertEquals("Temperature  Something else  yeah", mPopularNameProvider.getPopularNameFromWidgetLabel(testLabel));
    }

    public void testGetWidgetByLabel() {
        List<WidgetPhraseMatchResult> resultList = mWidgetProvider.getWidgetByLabel("TERRACE DOOR");
        assertEquals(getAllStringItemsInOneString(resultList), 3, resultList.size());
        assertEquals(100, resultList.get(0).getMatchPercent());
        assertEquals("GF_Living_4", resultList.get(0).getWidget().getId());
    }

    public void testMatchUnitNamesWithCommandPhrases() {
        String expectedUnitName = "Terrace door";
        List<String> commandPhrases = new ArrayList<String>();
        commandPhrases.add("Get " + expectedUnitName + " status");


        List<OpenHABWidget> widgetList = mWidgetProvider.getWidgetList(OpenHABWidgetTypeSet.UnitItem);
        assertTrue(widgetList.get(58).getId(), widgetList.get(58).getLabel().startsWith("Terrace door ["));

        Iterator<OpenHABWidget> iterator = widgetList.iterator();
        Map<String, OpenHABWidget> widgetNameMap = new HashMap<String, OpenHABWidget>();
        List<String> widgetLabelList = new ArrayList<String>();
        while (iterator.hasNext()) {
            OpenHABWidget nextWidget = iterator.next();
            if (!nextWidget.getLabel().isEmpty()) {
                String popularName = mPopularNameProvider.getPopularNameFromWidgetLabel(nextWidget.getLabel()).toUpperCase();
                widgetLabelList.add(popularName);
                widgetNameMap.put(/*nextWidget.hasItem()? nextWidget.getItem().getName() : */popularName, nextWidget);
            }
        }

        assertTrue("'" + expectedUnitName + "' doesn't exist in list if widgets", widgetLabelList.contains(expectedUnitName.toUpperCase()));

        StringBuilder sbMatchingWidgetLabels = new StringBuilder();
        final Collection<String> widgetNameArray = widgetNameMap.keySet();

        //Look for match
        int matchPoint = 0;
        for (String match : commandPhrases) {
            for (String aWidgetNameArray : widgetNameArray) {
                if (match.toUpperCase().contains(aWidgetNameArray) && matchPoint < aWidgetNameArray.length()) {
                    OpenHABWidget foundWidget = widgetNameMap.get(aWidgetNameArray);
                    matchPoint = aWidgetNameArray.length();
                    sbMatchingWidgetLabels.append(sbMatchingWidgetLabels.length() > 0 ? ", " : "").append(mPopularNameProvider.getPopularNameFromWidgetLabel(foundWidget.getLabel()));
                }
            }
        }

        assertEquals(expectedUnitName, sbMatchingWidgetLabels.toString());
    }

    public void test_getCommandsFromPhrases() {
        List<String> inputValue = new ArrayList<String>();
        inputValue.add("Switch on kitchen ceiling lights");
        List<CommandPhraseMatchResult> result = mCommandAnalyzer.getCommandsFromPhrases(inputValue);
        assertEquals("Resulting List size = " + result.size(), 1, result.size());
        assertEquals(OpenHABWidgetCommandType.SwitchOn, result.get(0).getCommandType());
        assertEquals(2, result.get(0).getPoint());
        assertTrue(result.get(0).getTags().length == 1);
        assertEquals("<unit>".toUpperCase(), result.get(0).getTags()[0]);
        assertTrue(result.get(0).getTagPhrases().length == 1);
        assertEquals("kitchen ceiling lights".toUpperCase(), result.get(0).getTagPhrases()[0]);

        inputValue.clear();
        inputValue.add("Get terrace door status");
        result = mCommandAnalyzer.getCommandsFromPhrases(inputValue);
        assertEquals("Resulting List size = " + result.size(), 2, result.size());
        assertEquals(OpenHABWidgetCommandType.GetStatus, result.get(0).getCommandType());
        assertEquals(2, result.get(0).getPoint());
        assertTrue(result.get(0).getTags().length == 1);
        assertEquals("<unit>".toUpperCase(), result.get(0).getTags()[0]);
        assertTrue(result.get(0).getTagPhrases().length == 1);
        assertEquals("terrace door".toUpperCase(), result.get(0).getTagPhrases()[0]);

        assertEquals(OpenHABWidgetCommandType.GetStatus, result.get(1).getCommandType());
        assertEquals(1, result.get(1).getPoint());
        assertTrue(result.get(1).getTags().length == 1);
        assertEquals("<unit>".toUpperCase(), result.get(1).getTags()[0]);
        assertTrue(result.get(1).getTagPhrases().length == 1);
        assertEquals("terrace door status".toUpperCase(), result.get(1).getTagPhrases()[0]);

        inputValue.clear();
        inputValue.add("Just some mambo jumbo");
        result = mCommandAnalyzer.getCommandsFromPhrases(inputValue);
        assertEquals("Resulting List size = " + result.size(), 0, result.size());
    }

    public void testGetUnitPhrase() {
        List<String> inputValue = new ArrayList<String>();
        inputValue.add("Get terrace door status");
        List<CommandPhraseMatchResult> listOfCommandResult = mCommandAnalyzer.getCommandsFromPhrases(inputValue);
        assertEquals(2, listOfCommandResult.size());
        assertEquals(2, listOfCommandResult.get(0).getPoint());
        assertEquals(1, listOfCommandResult.get(1).getPoint());
        assertEquals(1, listOfCommandResult.get(0).getTags().length);
        assertEquals("<UNIT>", listOfCommandResult.get(0).getTags()[0]);
        String result = mCommandAnalyzer.getUnitPhrase(listOfCommandResult.get(0));
        assertEquals("TERRACE DOOR", result);
    }

    public void testGetWidgetFromCommandMatchResult() {
        List<String> inputValue = new ArrayList<String>();
        inputValue.add("Get terrace door status");
        List<CommandPhraseMatchResult> result = mCommandAnalyzer.getCommandsFromPhrases(inputValue);
        WidgetPhraseMatchResult widgetMatch = mCommandAnalyzer.getMostProbableWidgetFromCommandMatchResult(result.get(0));
        assertEquals(100, widgetMatch.getMatchPercent());
        assertEquals("GF_Living_4", widgetMatch.getWidget().getId());
    }

    public void testGetHighestWidgetsFromCommandMatchResult() {

    }

    public void testThatCommandReallyMatch() {
        List<String> commandPhrases = new ArrayList<String>();
        commandPhrases.add("Get terrace door status");
        List<CommandPhraseMatchResult> commandMatchResultList = mCommandAnalyzer.getCommandsFromPhrases(commandPhrases);

        assertEquals(2, commandMatchResultList.size());
        assertEquals(OpenHABWidgetCommandType.GetStatus, commandMatchResultList.get(0).getCommandType());
    }

    public void test_getCommandValue() {
        List<String> commandPhrases = new ArrayList<String>();
        commandPhrases.add("Set widget temperature to 15.0");
        List<CommandPhraseMatchResult> commandMatchResultList = mCommandAnalyzer.getCommandsFromPhrases(commandPhrases);
        assertEquals(2, commandMatchResultList.size());
        assertEquals("15.0", mCommandAnalyzer.getCommandValue(commandMatchResultList.get(0)));
    }

    public void test_getCommandReply_getTemperature() {
        final List<String> commandPhrases = Arrays.asList("Get outside temperature");
        final CommandAnalyzerResult commandAnalyzerResult = mCommandAnalyzer.analyzeCommand(commandPhrases, mApplicationModeProvider.getAppMode());
        assertEquals("Outside Temperature is 10.0 °C", mCommandAnalyzer.getCommandReply(commandAnalyzerResult));
    }

    public void test_getCommandReply_setTemperature() {
        final List<String> commandPhrases = Arrays.asList("Set toilet temperature to 15.0");
        final CommandAnalyzerResult commandAnalyzerResult = mCommandAnalyzer.analyzeCommand(commandPhrases, mApplicationModeProvider.getAppMode());
        assertEquals("Toilet Temperature was set to 15.0", mCommandAnalyzer.getCommandReply(commandAnalyzerResult));
    }

    public void testGetCommandAndUnit() {
        List<String> commandPhrases = new ArrayList<String>();
        commandPhrases.add(0, "Get outside temperature");//0
        commandPhrases.add(1, "Just some mambo jumbo");
        commandPhrases.add(2, "Get terrace door status");//4
        commandPhrases.add(3, "Switch on kitchen ceiling lights");//6
        commandPhrases.add(4, "Get outdoor temperature");//8
        commandPhrases.add(5, "Set widget temperature to 15.0");//11

        List<CommandPhraseMatchResult> commandMatchResultList = mCommandAnalyzer.getCommandsFromPhrases(commandPhrases);

        assertEquals(7, commandMatchResultList.size());
        assertEquals(OpenHABWidgetCommandType.GetStatus, commandMatchResultList.get(0).getCommandType());
        assertEquals(OpenHABWidgetCommandType.SwitchOn, commandMatchResultList.get(1).getCommandType());
        assertEquals(OpenHABWidgetCommandType.AdjustSetpoint, commandMatchResultList.get(2).getCommandType());
        assertEquals(OpenHABWidgetCommandType.GetStatus, commandMatchResultList.get(3).getCommandType());
        assertEquals(OpenHABWidgetCommandType.GetStatus, commandMatchResultList.get(4).getCommandType());
        assertEquals(OpenHABWidgetCommandType.GetStatus, commandMatchResultList.get(5).getCommandType());
        assertEquals(OpenHABWidgetCommandType.AdjustSetpoint, commandMatchResultList.get(6).getCommandType());

        assertEquals("Points = 2   Tags: <UNIT>,    Phrases: 'TERRACE DOOR', ", getCommandPhraseMatchResultStringData(commandMatchResultList.get(0)));
        assertEquals("Points = 2   Tags: <UNIT>,    Phrases: 'KITCHEN CEILING LIGHTS', ", getCommandPhraseMatchResultStringData(commandMatchResultList.get(1)));
        assertEquals("Points = 2   Tags: <UNIT>, <DECIMAL>,    Phrases: 'WIDGET TEMPERATURE', '15.0', ", getCommandPhraseMatchResultStringData(commandMatchResultList.get(2)));
        assertEquals("Points = 1   Tags: <UNIT>,    Phrases: 'OUTSIDE TEMPERATURE', ", getCommandPhraseMatchResultStringData(commandMatchResultList.get(3)));
        assertEquals("Points = 1   Tags: <UNIT>,    Phrases: 'TERRACE DOOR STATUS', ", getCommandPhraseMatchResultStringData(commandMatchResultList.get(4)));
        assertEquals("Points = 1   Tags: <UNIT>,    Phrases: 'OUTDOOR TEMPERATURE', ", getCommandPhraseMatchResultStringData(commandMatchResultList.get(5)));
        assertEquals("Points = 1   Tags: <UNIT>, <DECIMAL>,    Phrases: 'SET WIDGET TEMPERATURE TO', '15.0', ", getCommandPhraseMatchResultStringData(commandMatchResultList.get(6)));

        Map<CommandPhraseMatchResult, WidgetPhraseMatchResult> unitMatchResult = mCommandAnalyzer.getHighestWidgetsFromCommandMatchResult(commandMatchResultList);

        assertEquals(6, unitMatchResult.size());

        assertEquals(100, unitMatchResult.get(commandMatchResultList.get(0)).getMatchPercent());
        assertEquals("GF_Living_4", unitMatchResult.get(commandMatchResultList.get(0)).getWidget().getId());

        assertEquals(76, unitMatchResult.get(commandMatchResultList.get(1)).getMatchPercent());
        assertEquals("GF_Kitchen_0", unitMatchResult.get(commandMatchResultList.get(1)).getWidget().getId());

        assertEquals(91, unitMatchResult.get(commandMatchResultList.get(2)).getMatchPercent());
        assertEquals("0301_1_0_1_2", unitMatchResult.get(commandMatchResultList.get(2)).getWidget().getId());

        assertEquals(100, unitMatchResult.get(commandMatchResultList.get(3)).getMatchPercent());
        assertEquals("demo_1_0", unitMatchResult.get(commandMatchResultList.get(3)).getWidget().getId());

        assertEquals(77, unitMatchResult.get(commandMatchResultList.get(4)).getMatchPercent());
        assertEquals("GF_Living_4", unitMatchResult.get(commandMatchResultList.get(4)).getWidget().getId());

        //commandMatchResultList.get(5) ==> Widget = NULL ????

        assertEquals("Item: " + commandMatchResultList.get(5).toString() + "\nList: " + getAllStringItemsInOneString(commandMatchResultList), true, unitMatchResult.get(commandMatchResultList.get(5)) == null);
    }

    public void testExecuteCommandAsPhrase() {
        List<String> inputValue = new ArrayList<String>();
        inputValue.add("Switch on kitchen ceiling lights");
        ExecuteCommandAsPhrase(inputValue, "KITCHEN CEILING LIGHTS", 1, "GF_Kitchen_0", "Ceiling", OpenHABItemType.Switch, "OFF");

        inputValue.clear();
        inputValue.add("Get terrace door status");
        ExecuteCommandAsPhrase(inputValue, "TERRACE DOOR", 3, "GF_Living_4", "Terrace door [closed]", OpenHABItemType.Contact, "closed");

        inputValue.clear();
        inputValue.add("Get terrace door");
        ExecuteCommandAsPhrase(inputValue, "TERRACE DOOR", 3, "GF_Living_4", "Terrace door [closed]", OpenHABItemType.Contact, "closed");

        inputValue.clear();
        inputValue.add("Get outside temperature");
        ExecuteCommandAsPhrase(inputValue, "OUTSIDE TEMPERATURE", 1, "demo_1_0", "Outside Temperature [10.0 °C]", OpenHABItemType.Number, "10.0 °C");

        inputValue.clear();
        inputValue.add("Get temperature outside");
        ExecuteCommandAsPhrase(inputValue, "TEMPERATURE OUTSIDE", 1, "demo_1_0", "Outside Temperature [10.0 °C]", OpenHABItemType.Number, "10.0 °C");
    }

    private void ExecuteCommandAsPhrase(List<String> inputValue, String test_UnitToLookFor, int test_NoOfFoundUnitMatches, String test_WidgetID
            , String test_WholeWidgetLabel, OpenHABItemType test_WidgetItemType, String test_WidgetLabelValue) {
        List<CommandPhraseMatchResult> result = mCommandAnalyzer.getCommandsFromPhrases(inputValue);
        assertEquals(122, mWidgetProvider.getWidgetList((Set<OpenHABWidgetType>) null).size());
        assertEquals(test_UnitToLookFor, result.get(0).getTagPhrases()[0]);
        List<WidgetPhraseMatchResult> resultList = mWidgetProvider.getWidgetByLabel(result.get(0).getTagPhrases()[0]);
        assertEquals(getAllStringItemsInOneString(resultList), test_NoOfFoundUnitMatches, resultList.size());
        assertEquals(test_WidgetID, resultList.get(0).getWidget().getId());
        assertEquals(test_WholeWidgetLabel, resultList.get(0).getWidget().getLabel());
        assertEquals(test_WidgetItemType, resultList.get(0).getWidget().getItem().getType());
        assertEquals(test_WidgetLabelValue, resultList.get(0).getWidget().getLabelValue());
    }

    public void test_getRegExStringForMatchAccuracySource() {
        String[] input = {"hej", "hopp", "allihopa"};
        assertEquals("(HEJ)|(HOPP)|(ALLIHOPA)", mRegularExpression.getRegExStringForMatchAccuracySource(input));
    }

    public void testMatchForGetPatternForMatchAccuracySource() {
        String[] input = {"hej", "hopp", "allihopa"};
        RegExResult result = mRegularExpression.getAllNextMatchAsList(mRegularExpression.getRegExStringForMatchAccuracySource(input), "hej hopp allihopa", true);
        assertEquals(3, result.GroupList.size());
        assertEquals("hej", result.GroupList.get(0));
        assertEquals("hopp", result.GroupList.get(1));
        assertEquals("allihopa", result.GroupList.get(2));
    }

    public void testGetStringMatchAccuracy() {
        assertEquals(1d, doTestGetStringMatchAccuracy("hej hopp allihopa", "hej hopp allihopa").getAccuracy());
        assertEquals(0.78d, DecimalHandler.getFixNumberOfDecimals(doTestGetStringMatchAccuracy("hej hopp allihopa", "dfg hopp i hejallihopa då").getAccuracy(), 2));
    }

    public RegExAccuracyResult doTestGetStringMatchAccuracy(String source, String target) {
        String[] splittedSource = source.split(" ");
        List<String> sourceWordsList = new ArrayList<String>();
        for(String sourceWord : splittedSource)
            sourceWordsList.add(sourceWord.toUpperCase());

        return mRegularExpression.getStringMatchAccuracy(sourceWordsList, target);
    }

    public void testSplittedStringAndWordsList2() {
        String[] splittedSource = "Terrace door".split(" ");
        List<String> sourceWordsList = new ArrayList<String>();
        for(String sourceWord : splittedSource)
            sourceWordsList.add(sourceWord.toUpperCase());

        assertEquals(2, splittedSource.length);
        assertEquals("Terrace", splittedSource[0]);
        assertEquals("door", splittedSource[1]);

        assertEquals(2, sourceWordsList.size());
        assertEquals("TERRACE", sourceWordsList.get(0));
        assertEquals("DOOR", sourceWordsList.get(1));

        String regExString = mRegularExpression.getRegExStringForMatchAccuracySource(splittedSource);
        assertEquals("(TERRACE)|(DOOR)", regExString);

        assertEquals(1d, doGetStringMatchAccuracy2("Terrace door", sourceWordsList, regExString, "Terrace door"));
    }

    public double doGetStringMatchAccuracy2(String source, List<String> sourceWordsList, String regEx, String target) {
        double wordCountAccuracy;
        double orderAccuracy = 0;
        double lengthDifferenceAccuracy;
        int totalMatchLength = 0;

        target = target.toUpperCase();
        assertEquals("(TERRACE)|(DOOR)", regEx);
        assertEquals("TERRACE DOOR", target);
        RegExResult regExResult = mRegularExpression.getAllNextMatchAsList(regEx, target, true);
        assertEquals(2, sourceWordsList.size());
        assertEquals(2, regExResult.GroupList.size());
        wordCountAccuracy = regExResult.GroupList.size() / sourceWordsList.size();
        assertEquals(1d, wordCountAccuracy);
        int lastListMatchIndex = -1;
        for (int i = 0; i < regExResult.GroupList.size(); i++) {
            totalMatchLength += regExResult.GroupList.get(i).length() + 1;
            assertEquals(sourceWordsList.get(i), regExResult.GroupList.get(i));
            int listMatchIndex = sourceWordsList.indexOf(regExResult.GroupList.get(i));
            if (listMatchIndex > lastListMatchIndex) {
                lastListMatchIndex = listMatchIndex;
                orderAccuracy++;
            }
        }
        assertEquals(2d, orderAccuracy);
        if (orderAccuracy > 0)
            orderAccuracy = orderAccuracy / sourceWordsList.size();
        assertEquals(1d, orderAccuracy);

        totalMatchLength -= 1;
        assertEquals(12, totalMatchLength);
        assertEquals(12, target.length());
        lengthDifferenceAccuracy = Math.abs(totalMatchLength - target.length());
        if(lengthDifferenceAccuracy >= 0)
            lengthDifferenceAccuracy = 1 - (lengthDifferenceAccuracy * 0.15);
        if(lengthDifferenceAccuracy < 0)
            lengthDifferenceAccuracy = 0;
        assertEquals(1d, lengthDifferenceAccuracy);
        return sourceWordsList.size() > 0? (wordCountAccuracy + orderAccuracy + lengthDifferenceAccuracy) / 3 : 0;
    }

    public void testSplittedStringAndWordsList() {
        String[] splittedSource = "hej hopp allihopa".split(" ");
        List<String> sourceWordsList = new ArrayList<String>();
        for(String sourceWord : splittedSource)
            sourceWordsList.add(sourceWord.toUpperCase());

        assertEquals(3, splittedSource.length);
        assertEquals("hej", splittedSource[0]);
        assertEquals("hopp", splittedSource[1]);
        assertEquals("allihopa", splittedSource[2]);

        assertEquals(3, sourceWordsList.size());
        assertEquals("HEJ", sourceWordsList.get(0));
        assertEquals("HOPP", sourceWordsList.get(1));
        assertEquals("ALLIHOPA", sourceWordsList.get(2));

        String regExString = mRegularExpression.getRegExStringForMatchAccuracySource(splittedSource);
        assertEquals("(HEJ)|(HOPP)|(ALLIHOPA)", regExString);

        assertEquals(1d, doGetStringMatchAccuracy("hej hopp allihopa", sourceWordsList, regExString, "hej hopp allihopa"));
    }

    public double doGetStringMatchAccuracy(String source, List<String> sourceWordsList, String regEx, String target) {
        double wordCountAccuracy;
        double orderAccuracy = 0;
        double lengthDifferenceAccuracy;
        int totalMatchLength = 0;

        target = target.toUpperCase();
        assertEquals("(HEJ)|(HOPP)|(ALLIHOPA)", regEx);
        assertEquals("HEJ HOPP ALLIHOPA", target);
        RegExResult regExResult = mRegularExpression.getAllNextMatchAsList(regEx, target, true);
        assertEquals(3, sourceWordsList.size());
        assertEquals(3, regExResult.GroupList.size());
        wordCountAccuracy = regExResult.GroupList.size() / sourceWordsList.size();
        assertEquals(1d, wordCountAccuracy);
        int lastListMatchIndex = -1;
        for (int i = 0; i < regExResult.GroupList.size(); i++) {
            totalMatchLength += regExResult.GroupList.get(i).length() + 1;
            assertEquals(sourceWordsList.get(i), regExResult.GroupList.get(i));
            int listMatchIndex = sourceWordsList.indexOf(regExResult.GroupList.get(i));
            if (listMatchIndex > lastListMatchIndex) {
                lastListMatchIndex = listMatchIndex;
                orderAccuracy++;
            }
        }
        assertEquals(3d, orderAccuracy);
        if (orderAccuracy > 0)
            orderAccuracy = orderAccuracy / sourceWordsList.size();
        assertEquals(1d, orderAccuracy);

        totalMatchLength -= 1;
        assertEquals(17, totalMatchLength);
        assertEquals(17, target.length());
        lengthDifferenceAccuracy = Math.abs(totalMatchLength - target.length());
        if(lengthDifferenceAccuracy >= 0)
            lengthDifferenceAccuracy = 1 - (lengthDifferenceAccuracy * 0.15);
        if(lengthDifferenceAccuracy < 0)
            lengthDifferenceAccuracy = 0;
        assertEquals(1d, lengthDifferenceAccuracy);
        return sourceWordsList.size() > 0? (wordCountAccuracy + orderAccuracy + lengthDifferenceAccuracy) / sourceWordsList.size() : 0;
    }

    public void test_getRegExMatch() {
        assertEquals("kitchen ceiling lights", mCommandAnalyzer.getRegExMatch("Switch on kitchen ceiling lights", Pattern.compile("Switch on (.+)", Pattern.CASE_INSENSITIVE)));

        assertEquals("Switch", mCommandAnalyzer.getRegExMatch("Switch on kitchen ceiling lights", Pattern.compile("(.+) on", Pattern.CASE_INSENSITIVE)));

        String result2 = mCommandAnalyzer.getRegExMatch("Switch on <unit>".toUpperCase(), Pattern.compile("Switch on (.+)".toUpperCase(), Pattern.CASE_INSENSITIVE));
        assertEquals("<UNIT>", result2);

        String result = mCommandAnalyzer.getRegExMatch("One <two> three <four> five", Pattern.compile("One (.+) three (.+) five", Pattern.CASE_INSENSITIVE));
        assertEquals("<two> <four>", result);
    }

    public void test_RegExMatch() {
        Matcher matcher = Pattern.compile("One (.+) three (.+) five", Pattern.CASE_INSENSITIVE).matcher("One <two> three <four> five");
        assertTrue(matcher.find());
        assertEquals(2, matcher.groupCount());
        assertEquals("<two>", matcher.group(1));
        assertEquals("<four>", matcher.group(2));

        matcher = Pattern.compile("Switch on (.+)", Pattern.CASE_INSENSITIVE).matcher("Switch on kitchen ceiling lights");
        assertTrue(matcher.find());
        assertEquals(1, matcher.groupCount());
        assertEquals("kitchen ceiling lights", matcher.group(1));

        matcher = Pattern.compile("(.+) on", Pattern.CASE_INSENSITIVE).matcher("Switch on kitchen ceiling lights");
        assertTrue(matcher.find());
        assertEquals(1, matcher.groupCount());
        assertEquals("Switch", matcher.group(1));

        matcher = Pattern.compile("GET (.+) STATUS", Pattern.CASE_INSENSITIVE).matcher("GET TERRACE DOOR STATUS");
        assertTrue(matcher.find());
        assertEquals(1, matcher.groupCount());
        assertEquals("TERRACE DOOR", matcher.group(1));

        matcher = Pattern.compile("\\AGET (.+)\\z", Pattern.CASE_INSENSITIVE).matcher("SET WIDGET TEMPERATURE TO 15.0");
        assertFalse(matcher.find());

        matcher = Pattern.compile("\\A(.+) ([0-9.,]+)\\z", Pattern.CASE_INSENSITIVE).matcher("SET WIDGET TEMPERATURE TO 15.0");
        assertTrue(matcher.find());
        assertEquals(2, matcher.groupCount());
        assertEquals("SET WIDGET TEMPERATURE TO", matcher.group(1));
        assertEquals("15.0", matcher.group(2));
    }

    public void testGetWidgetParentWithAccuracy() {
        //"Switch on kitchen ceiling lights" => "KITCHEN CEILING LIGHTS" => "KITCHEN LIGHTS"
        OpenHABWidget resultingParentWidget = null;

        String[] splittedSource = "KITCHEN LIGHTS".split(" ");
        List<String> sourceWordsList = new ArrayList<String>();
        for(String sourceWord : splittedSource)
            sourceWordsList.add(sourceWord.toUpperCase());

        String regExString = mRegularExpression.getRegExStringForMatchAccuracySource(splittedSource);
        assertEquals("(KITCHEN)|(LIGHTS)", regExString);
        double maxResult = 0;
        OpenHABWidget unit = mWidgetProvider.getWidgetByID("GF_Kitchen_0");
        while(unit.hasParent()) {
            unit = unit.getParent();
            if(!unit.hasLinkedPage())
                continue;
            String linkTitle = unit.getLinkedPage().getTitle();
            double result = doGetStringMatchAccuracy(sourceWordsList, regExString, linkTitle);
            if (result > maxResult) {
                maxResult = result;
                resultingParentWidget = unit;
            }
        }
        assertTrue(resultingParentWidget != null);
        assertEquals("Kitchen", resultingParentWidget.getLinkedPage().getTitle());
        assertEquals("GF_Kitchen", resultingParentWidget.getLinkedPage().getId());
        assertEquals("Kitchen", resultingParentWidget.getLabel());
        assertEquals("0001_1",  resultingParentWidget.getId());
        assertEquals(0.67, DecimalHandler.getFixNumberOfDecimals(maxResult, 2));
    }

    private double doGetStringMatchAccuracy(List<String> sourceWordsList, String regEx, String target) {
        double wordCountAccuracy;
        double orderAccuracy = 0;
        double lengthDifferenceAccuracy = 0;
        int totalMatchLength = 0;

        target = target.toUpperCase();
        RegExResult regExResult = mRegularExpression.getAllNextMatchAsList(regEx, target, true);
        if (target.equalsIgnoreCase("Kitchen"))
            assertEquals(1, regExResult.GroupList.size());
        assertEquals(2, sourceWordsList.size());
        wordCountAccuracy = (double) regExResult.GroupList.size() / (double) sourceWordsList.size();
        if (target.equalsIgnoreCase("Kitchen"))
            assertEquals(0.5, wordCountAccuracy);
        int lastListMatchIndex = -1;
        for (int i = 0; i < regExResult.GroupList.size(); i++) {
            totalMatchLength += regExResult.GroupList.get(i).length() + 1;
            int listMatchIndex = sourceWordsList.indexOf(regExResult.GroupList.get(i));
            if (listMatchIndex > lastListMatchIndex) {
                lastListMatchIndex = listMatchIndex;
                orderAccuracy++;
            }
        }
        if (orderAccuracy > 0)
            orderAccuracy = orderAccuracy / sourceWordsList.size();

        totalMatchLength -= 1;
        if (target.length() > 0)
            lengthDifferenceAccuracy = totalMatchLength > target.length() ? target.length() / totalMatchLength : totalMatchLength / target.length();
        if (lengthDifferenceAccuracy < 0)
            lengthDifferenceAccuracy = 0;

        if (target.equalsIgnoreCase("Kitchen")) {
            assertEquals(0.5, orderAccuracy);
            assertEquals(1.0, lengthDifferenceAccuracy);
        }

        return sourceWordsList.size() > 0? (wordCountAccuracy + orderAccuracy + lengthDifferenceAccuracy) / 3 : 0;
    }

    public void test_replaceCommandTagsWithRegEx() {
        assertEquals("\\Ahello there ([0-9]+)\\z", mCommandAnalyzer.replaceCommandTagsWithRegEx("hello there <INTEGER>"));
        assertEquals("\\AHow ([0-9.,]+) are you\\z", mCommandAnalyzer.replaceCommandTagsWithRegEx("How <DECIMAL> are you"));
        assertEquals("\\APlease (.+) me\\z", mCommandAnalyzer.replaceCommandTagsWithRegEx("Please <text> me"));
        assertEquals("\\A(.+) delta force\\z", mCommandAnalyzer.replaceCommandTagsWithRegEx("<UNIT> delta force"));
        assertEquals("\\A(.+) ([0-9]+)\\z", mCommandAnalyzer.replaceCommandTagsWithRegEx("<selection> <integer>"));
        assertEquals("\\Awhat is your (blue|green|red|black|white|yellow|brown|purple|pink|gray|orange)\\z", mCommandAnalyzer.replaceCommandTagsWithRegEx("what is your <color>"));
    }
}
