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

// TODO: javadoc

package org.openhab.habdroid.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import com.crittercism.app.Crittercism;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;

public class AsyncServiceResolver extends Thread implements ServiceListener {
	private final static String TAG = "AsyncServiceResolver";
	private Context mCtx;
	// Multicast lock for mDNS
	private MulticastLock mMulticastLock;
	// mDNS service
	private JmDNS mJmdns;
	private String mServiceType;
	private ServiceInfo mResolvedServiceInfo;
	private static Thread mSleepingThread;
	private boolean mIsResolved = false;
    private AsyncServiceResolverListener mListener;
    private final static int mDefaultDiscoveryTimeout = 3000;
	
	public AsyncServiceResolver(Context context, String serviceType) {
		super();
		mCtx = context;
		mServiceType = serviceType;
        if (context instanceof AsyncServiceResolverListener)
            mListener = (AsyncServiceResolverListener)context;
	}

    public AsyncServiceResolver(Context context, AsyncServiceResolverListener listener, String serviceType) {
        super();
        mCtx = context;
        mServiceType = serviceType;
        mListener = listener;
    }

    public void run() {
		WifiManager wifi =
		           (android.net.wifi.WifiManager)
		              mCtx.getSystemService(android.content.Context.WIFI_SERVICE);
		mMulticastLock = wifi.createMulticastLock("HABDroidMulticastLock");
		mMulticastLock.setReferenceCounted(true);
		try {
			mMulticastLock.acquire();
		} catch (SecurityException e) {
			Log.i(TAG, "Security exception during multicast lock");
			Crittercism.logHandledException(e);
		}
		mSleepingThread = Thread.currentThread();
		Log.i(TAG, "Discovering service " + mServiceType);
		try {
//			Log.i(TAG, "Local IP:"  + getLocalIpv4Address().getHostAddress().toString());
			/* TODO: This is a dirty fix of some crazy ipv6 incompatibility
			   This workaround makes JMDNS work on local ipv4 address an thus
			   discover openHAB on ipv4 address. This should be fixed to fully
			   support ipv6 in future. */
            mJmdns = JmDNS.create(getLocalIpv4Address());
			mJmdns.addServiceListener(mServiceType, this);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		try {
			// Sleep for specified timeout
			Thread.sleep(mDefaultDiscoveryTimeout);
			if (!mIsResolved) {
				((Activity) mCtx).runOnUiThread(new Runnable() {
					public void run() {
						mListener.onServiceResolveFailed();
					}
				});
				shutdown();
			}
		} catch (InterruptedException e) {
		}
	}

	public void serviceAdded(ServiceEvent event) {
        Log.d(TAG, "Service Added " + event.getName());
		mJmdns.requestServiceInfo(event.getType(), event.getName(), 1);
	}

	public void serviceRemoved(ServiceEvent event) {
	}

	public void serviceResolved(ServiceEvent event) {
		mResolvedServiceInfo = event.getInfo();
		mIsResolved = true;
		((Activity) mCtx).runOnUiThread(new Runnable() {
			public void run() {
				mListener.onServiceResolved(mResolvedServiceInfo);
			}
		});
		shutdown();
		mSleepingThread.interrupt();
	}

	private void shutdown() {
		if (mMulticastLock != null)
			mMulticastLock.release();
		if (mJmdns != null) {
			mJmdns.removeServiceListener(mServiceType, this);
			try {
				mJmdns.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
	
	private InetAddress getLocalIpv4Address() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                Log.i(TAG, "IP: " + inetAddress.getHostAddress().toString());
	                Log.i(TAG, "Is IPV4 = " + (inetAddress instanceof Inet4Address));
	                if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        Log.i(TAG, "Selected " + inetAddress.getHostAddress().toString());
	                    return inetAddress;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e(TAG, ex.toString());
	    }
	    return null;
	}
}
