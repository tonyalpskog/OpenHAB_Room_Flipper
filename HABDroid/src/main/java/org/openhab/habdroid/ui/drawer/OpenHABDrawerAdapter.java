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
package org.openhab.habdroid.ui.drawer;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openhab.domain.model.OpenHABSitemap;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.NavDrawerItemType;
import org.openhab.habdroid.R;
import org.openhab.habdroid.util.AutoRefreshImageView;

import java.util.List;

public class OpenHABDrawerAdapter extends ArrayAdapter<OpenHABSitemap> {
    public static final int TYPE_SITEMAPITEM = 0;
    public static final int TYPES_COUNT = 1;
    private static final String TAG = "OpenHABDrawerAdapter";
    private final IOpenHABSetting mOpenHABSetting;

    private LayoutInflater mInflater;

    public OpenHABDrawerAdapter(Context context, int resource, List<OpenHABSitemap> objects, IOpenHABSetting openHABSetting) {
        super(context, resource, objects);

        mOpenHABSetting = openHABSetting;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return NavDrawerItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.openhabdrawer_item, parent, false);
            holder = new ViewHolder();
            holder.drawerItemLabelTextView = (TextView) convertView.findViewById(R.id.itemlabel);
            holder.drawerItemImage = (AutoRefreshImageView) convertView.findViewById(R.id.itemimage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OpenHABSitemap sitemap = getItem(position);
        if (sitemap.getLabel() != null) {
            holder.drawerItemLabelTextView.setText(sitemap.getLabel());
        } else {
            holder.drawerItemLabelTextView.setText(sitemap.getName());
        }

        if (sitemap.getIcon() != null) {
            String iconUrl = mOpenHABSetting.getBaseUrl() + "images/" + Uri.encode(sitemap.getIcon() + ".png");
            holder.drawerItemImage.setImageUrl(iconUrl, R.drawable.openhabiconsmall,
                    mOpenHABSetting.getUsername(), mOpenHABSetting.getPassword());
        } else {
            String iconUrl = mOpenHABSetting.getBaseUrl() + "images/" + ".png";
            holder.drawerItemImage.setImageUrl(iconUrl, R.drawable.openhabiconsmall,
                    mOpenHABSetting.getUsername(),
                    mOpenHABSetting.getPassword());
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView drawerItemLabelTextView;
        public AutoRefreshImageView drawerItemImage;
    }
}
