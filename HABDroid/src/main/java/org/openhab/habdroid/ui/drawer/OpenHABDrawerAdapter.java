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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenit.habclient.INavDrawerItem;
import com.zenit.habclient.NavDrawerItemType;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABSitemap;
import org.openhab.habdroid.util.MySmartImageView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OpenHABDrawerAdapter extends ArrayAdapter<INavDrawerItem> {

    public static final int TYPE_SITEMAPITEM = 0;
    public static final int TYPES_COUNT = 1;
    private static final String TAG = "OpenHABDrawerAdapter";
//    private String openHABBaseUrl = "http://demo.openhab.org:8080/";//TA - TODO: Saved this for later...
    private List<INavDrawerItem> mObjectList;

    public OpenHABDrawerAdapter(Context context, int resource,
                                List<INavDrawerItem> objects) {
        super(context, resource, objects);
        mObjectList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//TA - TODO: Move this to an implementation of INavDrawerItem => OpenHABSitemap
//        final RelativeLayout drawerItemView;
//        TextView drawerItemLabelTextView;
//        MySmartImageView drawerItemImage;
//        int drawerItemLayout;
//
//        OpenHABSitemap openHABSitemap = getItem(position);
//        switch (this.getItemViewType(position)) {
//            case TYPE_SITEMAPITEM:
//                drawerItemLayout = R.layout.openhabdrawer_item;
//                break;
//            default:
//                drawerItemLayout = R.layout.openhabdrawer_item;
//                break;
//        }
//        if (convertView == null) {
//            drawerItemView = new RelativeLayout(getContext());
//            String inflater = Context.LAYOUT_INFLATER_SERVICE;
//            LayoutInflater vi;
//            vi = (LayoutInflater)getContext().getSystemService(inflater);
//            vi.inflate(drawerItemLayout, drawerItemView, true);
//        } else {
//            drawerItemView = (RelativeLayout) convertView;
//        }
//        drawerItemLabelTextView = (TextView)drawerItemView.findViewById(R.id.itemlabel);
//        drawerItemImage = (MySmartImageView)drawerItemView.findViewById(R.id.itemimage);
//        if (openHABSitemap.getLabel() != null && drawerItemLabelTextView != null) {
//            drawerItemLabelTextView.setText(openHABSitemap.getLabel());
//        } else {
//            drawerItemLabelTextView.setText(openHABSitemap.getName());
//        }
//        if (openHABSitemap.getIcon() != null && drawerItemImage != null) {
//            String iconUrl = openHABBaseUrl + "images/" + Uri.encode(openHABSitemap.getIcon() + ".png");
//            drawerItemImage.setImageUrl(iconUrl, R.drawable.openhabiconsmall,
//                    openHABUsername, openHABPassword);
//        } else {
//            String iconUrl = openHABBaseUrl + "images/" + ".png";
//            drawerItemImage.setImageUrl(iconUrl, R.drawable.openhabiconsmall,
//                    openHABUsername, openHABPassword);
//        }
//        return drawerItemView;

        //TA - TODO: Sort items in type order.
//        Iterator<INavDrawerItem> iterator = mObjectList.iterator();
//        while (iterator.hasNext()) {
//            INavDrawerItem object = iterator.next();
//            if(object.getType().equals(NavDrawerItemType.Sitemap))
//        }
        return getItem(position).getView(getContext(), convertView);
    }

    @Override
    public int getViewTypeCount() {
        return NavDrawerItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return mObjectList.get(position).getType().Value;
    }
    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return true;
    }
}
