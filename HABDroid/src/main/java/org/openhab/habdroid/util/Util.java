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

package org.openhab.habdroid.util;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;

import org.openhab.domain.IOpenHABSitemapProvider;
import org.openhab.domain.model.OpenHABSitemap;
import org.openhab.habdroid.R;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class Util {

    private final static String TAG = "Util";
    
//    @Inject static IOpenHABSitemapProvider mOpenHABSitemapProvider;

	public static void setActivityTheme(Activity activity) {
		if (PreferenceManager.getDefaultSharedPreferences(activity).getString("default_openhab_theme", "dark").equals("dark")) {
//			activity.setTheme(android.R.style.Theme_Holo);
			activity.setTheme(R.style.HABDroid_Dark);
		} else {
//			activity.setTheme(android.R.style.Theme_Holo_Light);
			activity.setTheme(R.style.HABDroid_Light);
		}
	}
	
	public static void overridePendingTransition(Activity activity, boolean reverse) {
		if (PreferenceManager.getDefaultSharedPreferences(activity).getString("default_openhab_animation", "android").equals("android")) {
		} else if (PreferenceManager.getDefaultSharedPreferences(activity).getString("default_openhab_animation", "android").equals("ios")) {
			if (reverse) {
				activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			} else {
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);				
			}
		} else {
			activity.overridePendingTransition(0, 0);
		}
	}

    public static String normalizeUrl(String sourceUrl) {
        String normalizedUrl = "";
        try {
            URL url = new URL(sourceUrl);
            normalizedUrl = url.toString();
            normalizedUrl = normalizedUrl.replace("\n", "");
            normalizedUrl = normalizedUrl.replace(" ", "");
            if (!normalizedUrl.endsWith("/"))
                normalizedUrl = normalizedUrl + "/";
        } catch (MalformedURLException e) {
            Log.e(TAG, "normalizeUrl: invalid URL");
        }
        return normalizedUrl;
    }

//    public static void initCrittercism(Context ctx, String appKey) {
//        // Initialize crittercism reporting
//        JSONObject crittercismConfig = new JSONObject();
//        try {
//            crittercismConfig.put("shouldCollectLogcat", true);
//        } catch (JSONException e) {
//            if (e.getMessage() != null)
//                Log.e(TAG, e.getMessage());
//            else
//                Log.e(TAG, "Crittercism JSON exception");
//        }
//        Crittercism.init(ctx, appKey, crittercismConfig);
//    }

    //TODO - Move this to a sitemap specific class. [Tony Alpskog 2015-02-07]
    public static List<OpenHABSitemap> parseSitemapList(Document document) {
        List<OpenHABSitemap> sitemapList = new ArrayList<OpenHABSitemap>();
            NodeList sitemapNodes = document.getElementsByTagName("sitemap");
            if (sitemapNodes.getLength() > 0) {
                for (int i=0; i < sitemapNodes.getLength(); i++) {
                    Node sitemapNode = sitemapNodes.item(i);
                    OpenHABSitemap openhabSitemap = new OpenHABSitemap(sitemapNode, null, null);
                    sitemapList.add(openhabSitemap);
                }
            }
//        mOpenHABSitemapProvider.setOpenHABSitemaps(sitemapList);
        return sitemapList;
    }

    public static boolean sitemapExists(List<OpenHABSitemap> sitemapList, String sitemapName) {
        for (int i=0; i<sitemapList.size(); i++) {
            if (sitemapList.get(i).getName().equals(sitemapName))
                return true;
        }
        return false;
    }

    public static OpenHABSitemap getSitemapByName(List<OpenHABSitemap> sitemapList, String sitemapName) {
        for (int i=0; i<sitemapList.size(); i++) {
            if (sitemapList.get(i).getName().equals(sitemapName))
                return sitemapList.get(i);
        }
        return null;
    }

}
