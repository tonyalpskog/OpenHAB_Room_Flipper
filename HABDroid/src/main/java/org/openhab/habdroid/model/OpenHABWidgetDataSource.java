/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.habdroid.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

import org.openhab.habclient.HABApplication;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class provides datasource for openHAB widgets from sitemap page.
 * It uses a sitemap page XML document to create a list of widgets
 * 
 * @author Victor Belov
 *
 */

public class OpenHABWidgetDataSource {

	private static final String TAG = "OpenHABWidgetDataSource";

	private OpenHABWidget rootWidget;
	private String title;
	private String id;
	private String icon;
	private String link;

	public OpenHABWidgetDataSource() {
		
	}
	
	public OpenHABWidgetDataSource(Node rootNode) {
		setSourceNode(rootNode);
	}

    public OpenHABWidgetDataSource(Node rootNode, OpenHABWidget widget) {
        widget.removeAllChildren();
        setSourceNode(rootNode, widget);
    }

    public void setSourceNode(Node rootNode) {
        OpenHABWidget widget = new OpenHABWidget();
        widget.setType(OpenHABWidgetType.Root);
        setSourceNode(rootNode, new OpenHABWidget());
    }

    private void setSourceNode(Node rootNode, OpenHABWidget widget) {
		Log.i(TAG, "Loading new data");
        rootWidget = widget;
		if (rootNode.hasChildNodes()) {
			NodeList childNodes = rootNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i ++) {
				Node childNode = childNodes.item(i);
				if (childNode.getNodeName().equals("widget")) {
					new OpenHABWidget(rootWidget, childNode);
				} else if (childNode.getNodeName().equals("title")) {
					this.setTitle(childNode.getTextContent());
				} else if (childNode.getNodeName().equals("id")) {
					this.setId(childNode.getTextContent());
				} else if (childNode.getNodeName().equals("icon")) {
					this.setIcon(childNode.getTextContent());
				} else if (childNode.getNodeName().equals("link")) {
					this.setLink(childNode.getTextContent());
				}
			}
		}
	}

    private List<OpenHABSitemap> parseSitemapList(String xmlContent) {
        List<OpenHABSitemap> sitemapList = new ArrayList<OpenHABSitemap>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document;
            document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));
            NodeList sitemapNodes = document.getElementsByTagName("sitemap");
            if (sitemapNodes.getLength() > 0) {
                for (int i=0; i < sitemapNodes.getLength(); i++) {
                    Node sitemapNode = sitemapNodes.item(i);
                    OpenHABSitemap openhabSitemap = new OpenHABSitemap(sitemapNode);
                    sitemapList.add(openhabSitemap);
                }
            }
        } catch (ParserConfigurationException e) {
            Log.e(HABApplication.getLogTag(), e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e(HABApplication.getLogTag(), e.getMessage());
        } catch (SAXException e) {
            Log.e(HABApplication.getLogTag(), e.getMessage());
        } catch (IOException e) {
            Log.e(HABApplication.getLogTag(), e.getMessage());
        }
        return sitemapList;
    }

	public OpenHABWidget getRootWidget() {
		return this.rootWidget;
	}

	public OpenHABWidget getWidgetById(String widgetId) {
		ArrayList<OpenHABWidget> widgets = this.getWidgets();
		for (int i = 0; i < widgets.size(); i++) {
			if (widgets.get(i).getId().equals(widgetId))
				return widgets.get(i);
		}
		return null;
	}
	
	public ArrayList<OpenHABWidget> getWidgets() {
		ArrayList<OpenHABWidget> result = new ArrayList<OpenHABWidget>();
		if (rootWidget != null)
		if (this.rootWidget.hasChildren()) {
			for (int i = 0; i < rootWidget.getChildren().size(); i++) {
				OpenHABWidget openHABWidget = this.rootWidget.getChildren().get(i);
				result.add(openHABWidget);
				if (openHABWidget.hasChildren()) {
					for (int j = 0; j < openHABWidget.getChildren().size(); j++) {
						result.add(openHABWidget.getChildren().get(j));
					}
				}
			}
		}
		return result;
	}

    public ArrayList<OpenHABWidget> getLinkWidgets() {
        ArrayList<OpenHABWidget> result = new ArrayList<OpenHABWidget>();
        if (rootWidget != null)
            if (this.rootWidget.hasChildren()) {
                for (int i = 0; i < rootWidget.getChildren().size(); i++) {
                    OpenHABWidget openHABWidget = this.rootWidget.getChildren().get(i);
                    if (openHABWidget.hasLinkedPage() || openHABWidget.childrenHasLinkedPages())
                    result.add(openHABWidget);
                    if (openHABWidget.hasChildren()) {
                        for (int j = 0; j < openHABWidget.getChildren().size(); j++) {
                            if (openHABWidget.getChildren().get(j).hasLinkedPage())
                                result.add(openHABWidget.getChildren().get(j));
                        }
                    }
                }
            }
        return result;
    }

    public ArrayList<OpenHABWidget> getNonlinkWidgets() {
        ArrayList<OpenHABWidget> result = new ArrayList<OpenHABWidget>();
        if (rootWidget != null)
            if (this.rootWidget.hasChildren()) {
                for (int i = 0; i < rootWidget.getChildren().size(); i++) {
                    OpenHABWidget openHABWidget = this.rootWidget.getChildren().get(i);
                    if ((openHABWidget.getType() == OpenHABWidgetType.Frame && openHABWidget.childrenHasNonlinkedPages()) ||
                            (openHABWidget.getType() != OpenHABWidgetType.Frame && !openHABWidget.hasLinkedPage()))
                        result.add(openHABWidget);
                    if (openHABWidget.hasChildren()) {
                        for (int j = 0; j < openHABWidget.getChildren().size(); j++) {
                            if (!openHABWidget.getChildren().get(j).hasLinkedPage())
                                result.add(openHABWidget.getChildren().get(j));
                        }
                    }
                }
            }
        return result;
    }


    public void logWidget(OpenHABWidget widget) {
		Log.i(TAG, "Widget <" + widget.getLabel() + "> (" + widget.getType() + ")");
		if (widget.hasChildren()) {
			for (int i = 0; i < widget.getChildren().size(); i++) {
				logWidget(widget.getChildren().get(i));
			}
		}
	}

	public String getTitle() {
		String[] splitString;
        if (title != null) {
    		splitString = title.split("\\[|\\]");
		    return splitString[0];
        }
        return "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
