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

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.INavDrawerActivity;
import com.zenit.habclient.INavDrawerItem;
import com.zenit.habclient.NavDrawerItemType;

import org.openhab.habdroid.R;
import org.openhab.habdroid.util.AutoRefreshImageView;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OpenHABSitemap implements INavDrawerItem {
	private String name;
    private String label;
	private String link;
    private String icon;
	private String homepageLink;
    private boolean leaf = false;
    private NavDrawerItemType drawerItemType = NavDrawerItemType.Sitemap;
	
	public OpenHABSitemap(Node startNode) {
		if (startNode.hasChildNodes()) {
			NodeList childNodes = startNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i ++) {
				Node childNode = childNodes.item(i);
				if (childNode.getNodeName().equals("name")) {
					this.setName(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("label")) {
                    this.setLabel(childNode.getTextContent());
				} else if (childNode.getNodeName().equals("link")) {
					this.setLink(childNode.getTextContent());
                } else if (childNode.getNodeName().equals("icon")) {
                    this.setIcon(childNode.getTextContent());
				} else if (childNode.getNodeName().equals("homepage")) {
					if (childNode.hasChildNodes()) {
						NodeList homepageNodes = childNode.getChildNodes();
						for (int j = 0; j < homepageNodes.getLength(); j++) {
							Node homepageChildNode = homepageNodes.item(j);
							if (homepageChildNode.getNodeName().equals("link")) {
								this.setHomepageLink(homepageChildNode.getTextContent());
							} else if (homepageChildNode.getNodeName().equals("leaf")) {
                                if (homepageChildNode.getTextContent().equals("true")) {
                                    setLeaf(true);
                                } else {
                                    setLeaf(false);
                                }
                            }
						}
					}
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getHomepageLink() {
		return homepageLink;
	}

	public void setHomepageLink(String homepageLink) {
		this.homepageLink = homepageLink;
	}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean isLeaf) {
        leaf = isLeaf;
    }

// ======================================
//    INavDrawerItem implementation
// ======================================

    @Override
    public void itemClickAction(Context context, INavDrawerActivity activity) {
        Log.d(this.getClass().getSimpleName(), "This is sitemap " + getLink());
        activity.getDrawerLayout().closeDrawers();
        activity.openSitemap(getHomepageLink());
    }

    @Override
    public NavDrawerItemType getType() {
        return drawerItemType;
    }

    @Override
    public View getView(Context context, View convertView) {
        final RelativeLayout drawerItemView;
        TextView drawerItemLabelTextView;
        AutoRefreshImageView drawerItemImage;

        if (convertView == null) {
            drawerItemView = new RelativeLayout(context);
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) context.getSystemService(inflater);
            vi.inflate(R.layout.openhabdrawer_item, drawerItemView, true);//TA - TODO: Change attachToRoot to false ?
        } else {
            drawerItemView = (RelativeLayout) convertView;
        }
        drawerItemLabelTextView = (TextView)drawerItemView.findViewById(R.id.itemlabel);
        drawerItemImage = (AutoRefreshImageView)drawerItemView.findViewById(R.id.itemimage);
        if (getLabel() != null && drawerItemLabelTextView != null) {
            drawerItemLabelTextView.setText(getLabel());
        } else {
            drawerItemLabelTextView.setText(getName());
        }
        if (getIcon() != null && drawerItemImage != null) {
            String iconUrl = HABApplication.getOpenHABSetting(context).getBaseUrl() + "images/" + Uri.encode(getIcon() + ".png");
            drawerItemImage.setImageUrl(iconUrl, R.drawable.openhabiconsmall,
                    HABApplication.getOpenHABSetting(context).getUsername(), HABApplication.getOpenHABSetting(context).getPassword());
        } else {
            String iconUrl = HABApplication.getOpenHABSetting(context).getBaseUrl() + "images/" + ".png";
            drawerItemImage.setImageUrl(iconUrl, R.drawable.openhabiconsmall, HABApplication.getOpenHABSetting(context).getUsername(), HABApplication.getOpenHABSetting(context).getPassword());
        }
        return drawerItemView;
    }
}
