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

package org.openhab.domain.model;

import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import static org.openhab.domain.util.StringHandler.isNullOrEmpty;

/**
 * This is a class to hold basic information about openHAB widget.
 * 
 * @author Victor Belov
 *
 */

public class OpenHABWidget {
	private String id;
	private String label;
	private String icon;
	private OpenHABWidgetType type;
	private String url;
	private String period = "";
	private float minValue =0;
	private float maxValue = 100;
	private float step = 1;
	private int refresh = 0;
	private int height = 0;
	private OpenHABWidget parent;
	private OpenHABItem item;
	private OpenHABLinkedPage linkedPage;
	private ArrayList<OpenHABWidget> children;
	private ArrayList<OpenHABWidgetMapping> mappings;
    private Integer iconcolor;
    private Integer labelcolor;
    private Integer valuecolor;
    private UUID updateUUID;
    private final ILogger logger;
    @Inject private ILogger mLogger;
    @Inject private IColorParser mColorParser;

    public OpenHABWidget(ILogger logger) {
        if(logger == null) throw new IllegalArgumentException("logger is null");

        this.logger = logger;
        this.children = new ArrayList<OpenHABWidget>();
		this.mappings = new ArrayList<OpenHABWidgetMapping>();
	}
	
	public OpenHABWidget(OpenHABWidget parent, Node startNode, ILogger logger,
                         IColorParser colorParser) {
        if(logger == null) throw new IllegalArgumentException("logger is null");
        if(colorParser == null) throw new IllegalArgumentException("colorParser is null");

        this.logger = logger;
		this.parent = parent;//TODO - Check if it's possible to not add parents that is root (type = null)
        this.children = new ArrayList<OpenHABWidget>();
		this.mappings = new ArrayList<OpenHABWidgetMapping>();
		if (startNode.hasChildNodes()) {
			NodeList childNodes = startNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i ++) {
				Node childNode = childNodes.item(i);
				if (childNode.getNodeName().equals("item")) {
					this.setItem(new OpenHABItem(childNode, logger));
				} else if (childNode.getNodeName().equals("linkedPage")) {					
					this.setLinkedPage(new OpenHABLinkedPage(this, childNode, logger, colorParser));
				} else if (childNode.getNodeName().equals("widget")) {
					new OpenHABWidget(this, childNode, logger, colorParser);
				} else if (childNode.getNodeName().equals("type")) {
						this.setType(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("widgetId")) {
                    this.setId(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("label")) {
                    logger.v("OpenHABWidget", String.format("Creating openHABWidget (label): '%s'. Widget ID = %s", childNode.getTextContent(), getId()));
//                    Washing
                    this.setLabel(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("icon")) {
                    this.setIcon(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("url")) {
                    this.setUrl(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("minValue")) {
                    setMinValue(Float.valueOf(childNode.getTextContent()));
                } else if (childNode.getNodeName().equals("maxValue")) {
                    setMaxValue(Float.valueOf(childNode.getTextContent()));
                } else if (childNode.getNodeName().equals("step")) {
                    setStep(Float.valueOf(childNode.getTextContent()));
                } else if (childNode.getNodeName().equals("refresh")) {
                    setRefresh(Integer.valueOf(childNode.getTextContent()));
                } else if (childNode.getNodeName().equals("period")) {
                    setPeriod(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("height")) {
                    setHeight(Integer.valueOf(childNode.getTextContent()));
                } else if (childNode.getNodeName().equals("mapping")) {
                    NodeList mappingChildNodes = childNode.getChildNodes();
                    String mappingCommand = "";
                    String mappingLabel = "";
                    for (int k = 0; k < mappingChildNodes.getLength(); k++) {
                        if (mappingChildNodes.item(k).getNodeName().equals("command"))
                            mappingCommand = mappingChildNodes.item(k).getTextContent();
                        if (mappingChildNodes.item(k).getNodeName().equals("label"))
                            mappingLabel = mappingChildNodes.item(k).getTextContent();
                    }
                    OpenHABWidgetMapping mapping = new OpenHABWidgetMapping(mappingCommand, mappingLabel);
                    mappings.add(mapping);
                } else if (childNode.getNodeName().equals("iconcolor")) {
                    setIconColor(colorParser.parseColor(childNode.getTextContent()));
                } else if (childNode.getNodeName().equals("labelcolor")) {
                    setLabelColor(colorParser.parseColor(childNode.getTextContent()));
                } else if (childNode.getNodeName().equals("valuecolor")) {
                    setValueColor(colorParser.parseColor(childNode.getTextContent()));
                }
			}
		}

        if(getType() == OpenHABWidgetType.ItemText || getType() == OpenHABWidgetType.SitemapText) {
            if(hasItem())
                setType(OpenHABWidgetType.ItemText);
            else if(hasLinkedPage())
                setType(OpenHABWidgetType.SitemapText);
            else {
                //Unknown widget type found => Generic
                logger.e("OpenHABWidget", String.format("Unknown openHABWidget type '%s'. Widget ID = %s", getType().name(), getId()));
                setType(OpenHABWidgetType.GenericItem);
            }
        }

        if(this.parent != null)
            this.parent.addChildWidget(this);
	}
	
	public void addChildWidget(OpenHABWidget child) {
		if (child != null) {
			this.children.add(child);
		}
	}

	public boolean hasChildren() {
        return this.children.size() > 0;
	}
	
	public ArrayList<OpenHABWidget> getChildren() {
		return this.children;
	}

    public boolean hasParent() {
        return parent != null;
    }

    public OpenHABWidget getParent() {
        return parent;
    }

    public void setParent(OpenHABWidget newParent) {//TODO - Just temporary test code
        parent = newParent;
    }
    
	public boolean hasItem() {
        return this.getItem() != null;
	}
	
	public boolean hasLinkedPage() {
        return this.linkedPage != null;
	}
	
	public OpenHABWidgetType getType() {
		return type;
	}

	public void setType(OpenHABWidgetType type) {
		this.type = type;
	}

    public void setType(String type) {

        //Check if widget type is any of the two special types "Switch with mappings" or "Switch with RollershutterItem"
        if (type.equalsIgnoreCase(OpenHABWidgetType.Switch.Name)) {
            if (hasMappings()) {
                setType(OpenHABWidgetType.SelectionSwitch);
            } else if (getItem() != null && getItem().getType()!= null && getItem().getType() == OpenHABItemType.Rollershutter) {
                setType(OpenHABWidgetType.RollerShutter);
            } else {
                setType(OpenHABWidgetType.Switch);
            }
            return;
        }

        for(OpenHABWidgetType oType : OpenHABWidgetType.values()) {
            if(type.equalsIgnoreCase(oType.Name)) {
                setType(oType);
                logger.v("OpenHABWidget", String.format("Found openHABWidget type '%s'. Widget ID = %s", oType.Name, getId()));
                return;
            }
        }

        //Type not found by name => Generic
        logger.e("OpenHABWidget", String.format("Unknown openHABWidget type '%s'. Widget ID = %s", type, getId()));
        setType(OpenHABWidgetType.GenericItem);
    }

    public OpenHABItem getItem() {
		return item;
	}

	public void setItem(OpenHABItem item) {
		this.item = item;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public OpenHABLinkedPage getLinkedPage() {
		return linkedPage;
	}

	public void setLinkedPage(OpenHABLinkedPage linkedPage) {
		this.linkedPage = linkedPage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean hasMappings() {
        return mappings.size() > 0;
    }
	
	public OpenHABWidgetMapping getMapping(int index) {
		return mappings.get(index);
	}
	
	public ArrayList<OpenHABWidgetMapping> getMappings() {
		return mappings;
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public float getStep() {
		return step;
	}

	public void setStep(float step) {
		this.step = step;
	}

	public int getRefresh() {
		return refresh;
	}

	public void setRefresh(int refresh) {
		this.refresh = refresh;
	}

	public String getPeriod() {
		if (period.length() == 0) {
			return "D";
		}
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public boolean childrenHasLinkedPages() {
        if (this.hasChildren()) {
            for (OpenHABWidget w : this.getChildren()) {
                if (w.hasLinkedPage())
                    return true;
            }
        }
        return false;
    }

    public boolean childrenHasNonlinkedPages() {
        if (this.hasChildren()) {
            for (OpenHABWidget w : this.getChildren()) {
                if (!w.hasLinkedPage())
                    return true;
            }
        }
        return false;
    }

    public Integer getLabelColor() {
        return labelcolor;
    }

    public void setLabelColor(int color) {
        try {
            this.labelcolor = color;
        } catch(IllegalArgumentException e) {
            this.logger.e("OpenHABWidget", "Color was " + color);
            this.logger.e("OpenHABWidget", e.getMessage());
            this.labelcolor = null;
        }
    }

    public Integer getValueColor() {
        return valuecolor;
    }

    public void setValueColor(int color) {
        try {
            this.valuecolor = color;
        } catch(IllegalArgumentException e) {
            this.logger.e("OpenHABWidget", "Color was " + color);
            this.logger.e("OpenHABWidget", e.getMessage());
            this.valuecolor = null;
        }
    }

    public Integer getIconColor() {
        return iconcolor;
    }

    public void setIconColor(int color) {
        try {
            this.iconcolor = color;
        } catch(IllegalArgumentException e) {
            this.logger.e("OpenHABWidget", "Color was " + color);
            this.logger.e("OpenHABWidget", e.getMessage());
            this.iconcolor = null;
        }
    }

    private String fixColorName(String colorName) {
        if (colorName.equals("orange"))
            return "#FFA500";
        return colorName;
    }

    public UUID getUpdateUUID() {
        return updateUUID;
    }

    public void setUpdateUUID(UUID updateUUID) {
        this.updateUUID = updateUUID;
    }

    public void removeAllChildren() {
        children.clear();
    }

    public String toString() {
        return String.format("(%s) %s::%s", getType() != null? getType().name() : "NULL", hasParent()? getParent().getLabel() : "NULL", getLabel());/*getLabel();*/
    }

    public String getItemName() {
        return hasItem()? getItem().getName() : getLabel();
    }

    public String getLabelValue() {
        String result = getRegExMatch(getLabel(), Pattern.compile("\\[.*\\]", Pattern.CASE_INSENSITIVE));
        if(isNullOrEmpty(result))
            result = getItem().getState();
        return result;
    }

    private String getRegExMatch(String source, Pattern pattern) {
        String result = "";

        Matcher matcher = pattern.matcher(source);
        if(matcher.find())
            result = (matcher.group().subSequence(1, matcher.group().length()-1)).toString();

        return result;
    }
}
