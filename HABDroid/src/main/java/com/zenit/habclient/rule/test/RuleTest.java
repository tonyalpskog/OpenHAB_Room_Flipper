package com.zenit.habclient.rule.test;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.UnitEntityDataType;
import com.zenit.habclient.UnitEntityDataTypeProvider;
import com.zenit.habclient.command.test.DocumentHttpResponseHandlerWrapper;
import com.zenit.habclient.rule.IOperator;
import com.zenit.habclient.rule.IUnitEntityDataType;
import com.zenit.habclient.rule.RuleOperation;
import com.zenit.habclient.rule.RuleOperationProvider;
import com.zenit.habclient.rule.RuleOperator;
import com.zenit.habclient.rule.RuleOperatorType;

import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTest extends android.test.ApplicationTestCase<HABApplication> {
    private HashMap<RuleOperatorType, RuleOperator<Number>> ruleOperatorsNumeric;
    private HashMap<RuleOperatorType, RuleOperator<Boolean>> ruleOperatorsBoolean;
    private HashMap<RuleOperatorType, RuleOperator<java.util.Date>> ruleOperatorsDate;
    private UnitEntityDataTypeProvider _unitEntityDataTypeProvider;
    private HABApplication mHABApplication;

    public RuleTest() {
        super(HABApplication.class);
    }

    public void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
        }
        createApplication();
        mHABApplication = getApplication();

        loadHttpDataFromString();

        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        ruleOperatorsNumeric = (HashMap<RuleOperatorType, RuleOperator<Number>>) rop.mOperatorHash.get(Number.class);
        ruleOperatorsBoolean = (HashMap<RuleOperatorType, RuleOperator<Boolean>>) rop.mOperatorHash.get(Boolean.class);
        ruleOperatorsDate = (HashMap<RuleOperatorType, RuleOperator<java.util.Date>>) rop.mOperatorHash.get(java.util.Date.class);

        _unitEntityDataTypeProvider = new UnitEntityDataTypeProvider();
    }

    //================================= UNITS ===================================

    private void requestOpenHABData(String htmlResponseData) {
        DocumentHttpResponseHandlerWrapper documentHandler = new DocumentHttpResponseHandlerWrapper();
        Document document = null;
        try {
            assertTrue("htmlResponseData is NULL!", htmlResponseData != null);
            document = documentHandler.parseResponse(htmlResponseData);
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

        OpenHABWidget rootWidget = new OpenHABWidget();
        int childWidgetsFound = 0, childTitlesFound = 0, childIDsFound = 0, childIconsFound = 0, childLinksFound = 0;

        OpenHABWidgetDataSource openHABWidgetDataSource = new OpenHABWidgetDataSource();

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

        openHABWidgetDataSource = new OpenHABWidgetDataSource(rootNode);

        assertEquals("Number of rootWidget childs is incorrect: ", 4, openHABWidgetDataSource.getRootWidget().getChildren().size());
        assertEquals("Number of total childs is incorrect: ", 13, openHABWidgetDataSource.getWidgets().size());

        mHABApplication.getOpenHABWidgetProvider().setOpenHABWidgets(openHABWidgetDataSource);
        return;
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

//    ===================================================================================================

    public void testSimple1() throws Exception {
        final int expected = 3;
        final int reality = 3;
        assertEquals(expected, reality);
    }

    public void testSimple2() {
        assertTrue(true);
    }

    public void testGetWidgetById() {
        OpenHABWidget unit = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0");
        assertEquals("Ceiling", unit.getLabel());
        assertEquals("Light_GF_Kitchen_Ceiling", unit.getItem().getName());
    }

    public void testMathEpsilonDiff() {
        Float f = Float.valueOf(34.7f);
        Double d = Double.valueOf(34.7);
        assertEquals(true, Math.abs(f.doubleValue() - d.doubleValue()) < 8E-7f);
        assertEquals(true, Math.abs(d.doubleValue() - f.doubleValue()) < 8E-7f);
    }

//    Calculate EPSILON
//    public void testSimple4() {
//        Float f = Float.valueOf(34.7f);
//        Double d = Double.valueOf(34.7);
//        assertEquals(0, Math.abs(f.doubleValue() - d.doubleValue()));
//    }

    public void testNumberEqual() {
        RuleOperator<Number> roEqual =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 equals 10 = True
        assertTrue(roEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(10)));

        //Integer 10 equals Float 10 = True
        assertTrue(roEqual.getOperationResult(Integer.valueOf(10), Float.valueOf(10)));

        //Double 10.1 equals Float 10.1 = True
        assertTrue(roEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Integer 9 equals 10 = False
        assertFalse(roEqual.getOperationResult(Integer.valueOf(9), Integer.valueOf(10)));

        //Float 9 equals 9 = True
        assertTrue(roEqual.getOperationResult(Float.valueOf(9), Float.valueOf(9)));

        //Float 34.7 equals 9 = False
        assertFalse(roEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 equals 34.7 = True
        assertTrue(roEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 10.2 equals 10.2 = True
        assertTrue(roEqual.getOperationResult(Double.valueOf(10.2), Double.valueOf(10.2)));
    }

    public void testNumberNotEqual() {
        RuleOperator<Number> roNotEqual =  ruleOperatorsNumeric.get(RuleOperatorType.NotEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roNotEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roNotEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 != 10 = False
        assertFalse(roNotEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(10)));

        //Double 10.1 != Float 10.1 = False
        assertFalse(roNotEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Integer 9 != 10 = False
        assertTrue(roNotEqual.getOperationResult(Integer.valueOf(9), Integer.valueOf(10)));

        //Float 9 != 9 = False
        assertFalse(roNotEqual.getOperationResult(Float.valueOf(9), Float.valueOf(9)));

        //Float 34.7 != 9 = True
        assertTrue(roNotEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 != 34.7 = False
        assertFalse(roNotEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 10.2 != 10.2 = False
        assertFalse(roNotEqual.getOperationResult(Double.valueOf(10.2), Double.valueOf(10.2)));

        //Double 10.2 != 10.1 = True
        assertTrue(roNotEqual.getOperationResult(Double.valueOf(10.2), Double.valueOf(10.1)));
    }

    public void testNumberBetween() {
        RuleOperator<Number> roBetween =  ruleOperatorsNumeric.get(RuleOperatorType.Between);

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roBetween.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roBetween.getOperationResult(Integer.valueOf(1), Integer.valueOf(2));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roBetween.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Float 9 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(9), Float.valueOf(10), Float.valueOf(13)));

        //Float 10 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(10), Float.valueOf(10), Float.valueOf(13)));

        //Float 10.1 between 10 and 13 = False
        assertTrue(roBetween.getOperationResult(Float.valueOf(10.1f), Float.valueOf(10), Float.valueOf(13)));

        //Float 11 between 10 and 13 = True
        assertTrue(roBetween.getOperationResult(Float.valueOf(11), Float.valueOf(10), Float.valueOf(13)));

        //Float 12.9 between 10 and 13 = True
        assertTrue(roBetween.getOperationResult(Float.valueOf(12.9f), Float.valueOf(10), Float.valueOf(13)));

        //Float 13 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(13), Float.valueOf(10), Float.valueOf(13)));

        //Float 14 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(14), Float.valueOf(10), Float.valueOf(13)));
    }

    public void testNumberWithin() {
        RuleOperator<Number> roWithin =  ruleOperatorsNumeric.get(RuleOperatorType.Within);

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roWithin.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roWithin.getOperationResult(Integer.valueOf(1), Integer.valueOf(2));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roWithin.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Float 9 within 10 and 13 = False
        assertFalse(roWithin.getOperationResult(Float.valueOf(9), Float.valueOf(10), Float.valueOf(13)));

        //Float 10 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(10), Float.valueOf(10), Float.valueOf(13)));

        //Float 11 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(11), Float.valueOf(10), Float.valueOf(13)));

        //Float 12 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(12), Float.valueOf(10), Float.valueOf(13)));

        //Float 13 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(13), Float.valueOf(10), Float.valueOf(13)));

        //Float 14 within 10 and 13 = False
        assertFalse(roWithin.getOperationResult(Float.valueOf(14), Float.valueOf(10), Float.valueOf(13)));
    }

    public void testNumberLessThan() {
        RuleOperator<Number> roLessThan =  ruleOperatorsNumeric.get(RuleOperatorType.LessThan);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roLessThan.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roLessThan.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 < 11 = True
        assertTrue(roLessThan.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 < 10 = False
        assertFalse(roLessThan.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 < Float 11 = True
        assertTrue(roLessThan.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 < Float 10.1 = False
        assertFalse(roLessThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 < Float 10.2 = True
        assertTrue(roLessThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 < Float 9 = True
        assertTrue(roLessThan.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 < Float 9 = False
        assertFalse(roLessThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 < Float 34.7 = False
        assertFalse(roLessThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 < Double 34.7 = False
        assertFalse(roLessThan.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 < Double 10 = True
        assertTrue(roLessThan.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testNumberMoreThan() {
        RuleOperator<Number> roMoreThan =  ruleOperatorsNumeric.get(RuleOperatorType.MoreThan);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roMoreThan.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roMoreThan.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 > 11 = False
        assertFalse(roMoreThan.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 > 10 = True
        assertTrue(roMoreThan.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 > Float 11 = False
        assertFalse(roMoreThan.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 > Float 10.1 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 > Float 10.2 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 > Float 9 = False
        assertFalse(roMoreThan.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 > Float 9 = True
        assertTrue(roMoreThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 > Float 34.7 = False
        assertFalse(roMoreThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 > Double 34.7 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 > Double 10 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testNumberLessOrEqual() {
        RuleOperator<Number> roLessOrEqual =  ruleOperatorsNumeric.get(RuleOperatorType.LessOrEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roLessOrEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roLessOrEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 <= 11 = True
        assertTrue(roLessOrEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 <= 10 = False
        assertFalse(roLessOrEqual.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 <= Float 11 = True
        assertTrue(roLessOrEqual.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 <= Float 10.1 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 <= Float 10.2 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 <= Float 9 = True
        assertTrue(roLessOrEqual.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 <= Float 9 = False
        assertFalse(roLessOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 <= Float 34.7 = True
        assertTrue(roLessOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 <= Double 34.7 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 <= Double 10 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testNumberMoreOrEqual() {
        RuleOperator<Number> roMoreOrEqual =  ruleOperatorsNumeric.get(RuleOperatorType.MoreOrEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roMoreOrEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roMoreOrEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 >= 11 = False
        assertFalse(roMoreOrEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 >= 10 = True
        assertTrue(roMoreOrEqual.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 >= Float 11 = False
        assertFalse(roMoreOrEqual.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 >= Float 10.1 = True
        assertTrue(roMoreOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 >= Float 10.2 = False
        assertFalse(roMoreOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 >= Float 9 = False
        assertFalse(roMoreOrEqual.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 >= Float 9 = True
        assertTrue(roMoreOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 >= Float 34.7 = True
        assertTrue(roMoreOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 >= Double 34.7 = True
        assertTrue(roMoreOrEqual.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 >= Double 10 = False
        assertFalse(roMoreOrEqual.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testBooleanAnd() {
        RuleOperator<Boolean> roAnd =  ruleOperatorsBoolean.get(RuleOperatorType.And);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            assertTrue(roAnd.getOperationResult(Boolean.valueOf(false)));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            assertTrue(roAnd.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true)));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true AND false = False
        assertFalse(roAnd.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false AND true = False
        assertFalse(roAnd.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false AND false = False
        assertFalse(roAnd.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true AND true = True
        assertTrue(roAnd.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void testBooleanOr() {
        RuleOperator<Boolean> roOr =  ruleOperatorsBoolean.get(RuleOperatorType.Or);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roOr.getOperationResult(Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false), Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true OR false = False
        assertTrue(roOr.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false OR true = False
        assertTrue(roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false OR false = False
        assertFalse(roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true OR true = True
        assertTrue(roOr.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void testBooleanEqual() {
        RuleOperator<Boolean> roEqual =  ruleOperatorsBoolean.get(RuleOperatorType.Equal);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roEqual.getOperationResult(Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true), Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true Equal false = False
        assertFalse(roEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false Equal true = False
        assertFalse(roEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false Equal false = False
        assertTrue(roEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true Equal true = True
        assertTrue(roEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void testBooleanNotEqual() {
        RuleOperator<Boolean> roNotEqual =  ruleOperatorsBoolean.get(RuleOperatorType.NotEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roNotEqual.getOperationResult(Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roNotEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true), Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true NotEqual false = False
        assertTrue(roNotEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false NotEqual true = False
        assertTrue(roNotEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false NotEqual false = False
        assertFalse(roNotEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true NotEqual true = True
        assertFalse(roNotEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void test_Verify_value_in_UnitEntityDataType() {
        UnitEntityDataType rue = new UnitEntityDataType("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }
        };

        assertTrue(rue.getValue() != null);
        assertEquals(50.7, rue.getValue());
    }

    private List<IUnitEntityDataType> getOperandsAsList() {
        List<IUnitEntityDataType> operands = new ArrayList<IUnitEntityDataType>();
        operands.add(_unitEntityDataTypeProvider.getUnitDataTypeList().get(2));

        UnitEntityDataType rue = new UnitEntityDataType<Double>("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }
        };
        operands.add(rue);

        return operands;
    }

//    public void test_RuleOperator_parseValue_method() {
//        RuleOperationProvider rop2 = new RuleOperationProvider();
//        HashMap<RuleOperatorType, RuleOperator<Integer>> ruleOperatorsInteger = (HashMap<RuleOperatorType, RuleOperator<Integer>>) rop2.mOperatorHash.get(Number.class);
//
//        RuleOperator<Integer> roEqual =  ruleOperatorsInteger.get(RuleOperatorType.Equal);
//
//        try {
//            assertEquals((Integer)10, roEqual.parseValue("10"));
//        } catch (Exception e) {
//            assertEquals("This should not happen", e.getClass().getName());
//        }
//    }

    public void test_should_be_able_to_use_a_List_of_IUnitEntityDataType_as_RuleOperator_input() {
        RuleOperator<Float> testOperatorNum = new RuleOperator<Float>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Float> args) {
                return true;
            }

            @Override
            public Float parseValue(String valueAsString) {
                return (Float) Float.valueOf(valueAsString);
            }
        };

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("This should not happen", e.toString()/*getClass().getName()*/);
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    public void test_should_be_able_to_use_a_List_of_IUnitEntityDataType_as_RuleOperator_input2() {
        RuleOperator<Number> testOperatorNum = new RuleOperator<Number>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                Number result = 0;
                Iterator iterator = args.iterator();

                if(iterator.hasNext())
                    result = (Number)iterator.next();

                while(iterator.hasNext()) {
                    result = result.floatValue() + ((Number)iterator.next()).floatValue();
                }

                assertEquals(101.4f, result);
                return true;
            }

            @Override
            public Number parseValue(String valueAsString) {
                return (Number) Float.valueOf(valueAsString);
            }
        };

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("This should not happen", e.toString()/*getClass().getName()*/);
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    public void test_should_be_able_to_use_a_List_of_IUnitEntityDataType_as_RuleOperator_input3() {
        RuleOperator<Boolean> testOperatorNum = new RuleOperator<Boolean>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Boolean> args) {
                Boolean result = false;
                Iterator iterator = args.iterator();

                if(iterator.hasNext())
                    result = (Boolean)iterator.next();

                while(iterator.hasNext()) {
                    result = result && (Boolean)iterator.next();
                }

                return result;
            }

            @Override
            public Boolean parseValue(String valueAsString) {
                return (Boolean) Boolean.valueOf(valueAsString);
            }
        };

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals("java.lang.ClassCastException", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    public void test_RuleOperator_should_accept_both_values_in_list_as_operands() {
        IOperator<Number> roEqual =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        List<IUnitEntityDataType> operands = getOperandsAsList();

        assertTrue(operands.get(0) != null);
        assertTrue(operands.get(1) != null);

        assertTrue(operands.get(0).getValue() != null);
        assertTrue(operands.get(1).getValue() != null);

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roEqual.getOperationResult(Double.valueOf(operands.get(0).getValue().toString()), Double.valueOf(operands.get(1).getValue().toString()));
            roEqual.getOperationResult(roEqual.parseValue(operands.get(0).getValue().toString()), roEqual.parseValue(operands.get(1).getValue().toString()));
            roEqual.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("Something whent wrong", e.toString());
//            assertEquals("This should not happen", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roEqual.getOperationResult(Double.valueOf(operands.get(0).getValue().toString()), Double.valueOf(operands.get(1).getValue().toString()));
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("This should not happen", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }

        //Float 12 within 10 and 13 = True
//        assertTrue(roWithin.getOperationResult(Float.valueOf(12), Float.valueOf(10), Float.valueOf(13)));
    }

    public void test_Create_RuleOperation_object_and_validate_operation_result() {
        assertEquals("Humidity percentage", _unitEntityDataTypeProvider.getUnitDataTypeList().get(2).getName());
        assertEquals(50.7, _unitEntityDataTypeProvider.getUnitDataTypeList().get(2).getValue());

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //First operation (Rule A)
        RuleOperator<Number> operator =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        RuleOperation roA = new RuleOperation(operator, operands);
        assertEquals("Humidity percentage = 50.7", roA.toString());
        assertEquals(true, roA.getResult());

        //Second operation (Rule B)
        RuleOperator<Number> operator2 =  ruleOperatorsNumeric.get(RuleOperatorType.LessThan);

        RuleOperation roB = new RuleOperation(operator2, operands);
        assertEquals("Humidity percentage < 50.7", roB.toString());
        assertEquals(false, roB.getResult());

        assertEquals(false, roA.getResult() && roB.getResult());
    }

    private List<IUnitEntityDataType> getOperandsAsList3(int operandPairNumber) {
        List<IUnitEntityDataType> operands = new ArrayList<IUnitEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0")));
                operands.add(getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bath_1")));
                break;

            case 2:
                //Number
                operands.add(getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3")));
                operands.add(getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Toilet_4")));
                break;
        }

        return operands;
    }

    private UnitEntityDataType getUnitEntityDataType(OpenHABWidget openHABWidget) {
        UnitEntityDataType rue = null;

        switch(openHABWidget.getItem().getType()) {
            case Switch:
                Boolean aBoolean;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aBoolean = null;
                else
                    aBoolean = openHABWidget.getItem().getState().equalsIgnoreCase("On");

                rue = new UnitEntityDataType<Boolean>(openHABWidget.getItem().getName(), aBoolean)
                {
                    public String getFormattedString(){
                        return mValue.booleanValue()? "On": "Off";//TODO - Language independent
                    }

                    @Override
                    public Boolean valueOf(String input) {
                        return Boolean.valueOf(input);
                    }
                };
                break;
            case Number:
                Double aNumber;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aNumber = null;
                else
                    aNumber = Double.valueOf(openHABWidget.getItem().getState());

                rue = new UnitEntityDataType<Double>(openHABWidget.getItem().getName(), aNumber)
                {
                    public String getFormattedString(){
                        return mValue.toString();
                    }

                    @Override
                    public Double valueOf(String input) {
                        return Double.valueOf(input);
                    }
                };
                break;
        }

        return rue;
    }

    public void test_Create_RuleOperation_object_from_provider_units_and_validate_operation_result() {
        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        OpenHABWidget widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0");
        RuleOperation roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.Equal), getOperandsAsList3(1));
        assertEquals("Light_GF_Kitchen_Ceiling = Off", roA.toString());//OFF
        assertEquals(true, roA.getResult());

        widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bath_1");
        roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.NotEqual), getOperandsAsList3(1));
        assertEquals("Light_GF_Kitchen_Ceiling != Off", roA.toString());//OFF
        assertEquals(false, roA.getResult());

        widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3");
        RuleOperation roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.LessThan), getOperandsAsList3(2));
        assertEquals("Temperature_FF_Bed < 21.5", roB.toString());//19.2
        assertEquals(true, roB.getResult());

        widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Toilet_4");
        roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.MoreThan), getOperandsAsList3(2));
        assertEquals("Temperature_FF_Bed > 21.5", roB.toString());//19.2
        assertEquals(false, roB.getResult());

        assertEquals(false, roA.getResult() && roB.getResult());
    }

    //mRuleOperator.getOperationResult(mOperands.get(0).getValue(), mRuleOperator.parseValue("10"));

    private List<IUnitEntityDataType> getOperandsAsList2() {
        List<IUnitEntityDataType> operands = new ArrayList<IUnitEntityDataType>();
        operands.add(_unitEntityDataTypeProvider.getUnitDataTypeList().get(2));

        UnitEntityDataType rue = new UnitEntityDataType<Double>("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }
        };
        operands.add(rue);

        UnitEntityDataType rue2 = new UnitEntityDataType<Float>("Test Value2", 16.8f) {
            @Override
            public Float valueOf(String input) {
                return Float.valueOf(input);
            }
        };
        operands.add(rue2);

        return operands;
    }

    public void test_RuleOperator_should_accept_all_3_values_in_list_as_operands() {
        RuleOperator<Number> roWithin =  ruleOperatorsNumeric.get(RuleOperatorType.Within);

        List<IUnitEntityDataType> operands = getOperandsAsList2();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roWithin.getOperationResult((Number)operands.get(0).getValue(), (Number)operands.get(1).getValue(), (Number)operands.get(2).getValue());
            roWithin.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("This should not happen", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }

        //Float 12 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(12), Float.valueOf(10), Float.valueOf(13)));
    }

    private Calendar getCalendar(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second, Integer ms) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new java.util.Date());
        if(year != null) cal.set(Calendar.YEAR, year);
        if(month != null) cal.set(Calendar.MONTH, month);
        if(day != null) cal.set(Calendar.DAY_OF_MONTH, day);
        if(hour != null) cal.set(Calendar.HOUR_OF_DAY, hour);
        if(minute != null) cal.set(Calendar.MINUTE, minute);
        if(second != null) cal.set(Calendar.SECOND, second);
        if(ms != null) cal.set(Calendar.MILLISECOND, ms);
        return cal;
    }

    public void testDateTimeParse() {
        RuleOperator<java.util.Date> roAfter = ruleOperatorsDate.get(RuleOperatorType.After);

        //ParseException shall be thrown if the value cannot be parsed.
        try {
            roAfter.parseValue("12.15");
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("12:15");
            assertEquals(getCalendar(1970, 0, 1, 12, 15, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            assertTrue(e instanceof ParseException);
        }

        //ParseException shall be thrown if the value cannot be parsed.
        try {
            roAfter.parseValue("2014/03/02");
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("2014-03-02");
            assertEquals(getCalendar(2014, 2, 2, 0, 0, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            assertFalse(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("20:39 2014-03-02");
            assertEquals(getCalendar(2014, 2, 2, 20, 39, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            assertFalse(e instanceof ParseException);
        }

    }

    public void testDateAfter() {
        RuleOperator<java.util.Date> roAfter =  ruleOperatorsDate.get(RuleOperatorType.After);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roAfter.getOperationResult(roAfter.parseValue("12:15"));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roAfter.getOperationResult(roAfter.parseValue("12:15"), roAfter.parseValue("13:30"), roAfter.parseValue("14:45"));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Date 10:15 after 11:30 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("10:15"), roAfter.parseValue("11:30")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date 11:30 after 10:15 = True
        try {
            assertTrue(roAfter.getOperationResult(roAfter.parseValue("11:30"), roAfter.parseValue("10:15")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Mon 11:30 after Tue 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("11:30 2014-03-24"), roAfter.parseValue("10:15 2014-03-25")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Sun 11:30 after Mon 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("11:30 2014-03-23"), roAfter.parseValue("10:15 2014-03-24")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Sat 11:30 after Mon 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("11:30 2014-03-22"), roAfter.parseValue("10:15 2014-03-24")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Mon 10:30 after Sun 11:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("10:30 2014-03-24"), roAfter.parseValue("11:15 2014-03-23")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }
    }

}
