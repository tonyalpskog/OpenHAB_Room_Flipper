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

package org.openhab.habdroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABItem;
import org.openhab.domain.model.OpenHABItemType;
import org.openhab.domain.model.OpenHABNFCActionList;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.model.OpenHABWidgetDataSource;
import org.openhab.domain.model.OpenHABWidgetType;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.IOpenHABSetting;
import org.openhab.habclient.dagger.DaggerWidgetListFragmentComponent;
import org.openhab.habdroid.R;
import org.openhab.habdroid.core.DocumentHttpResponseHandler;
import org.openhab.domain.IDocumentFactory;
import org.openhab.habdroid.util.MyAsyncHttpClient;
import org.openhab.habdroid.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.inject.Inject;

//import com.loopj.android.http.AsyncHttpAbortException;//TODO - removed by TA

/**
 * This class is apps' main fragment which displays list of openHAB
 * widgets from sitemap page with further navigation through sitemap and everything else!
 *
 * @author Victor Belov
 *
 */

public class OpenHABWidgetListFragment extends ListFragment {
    private static final String TAG = "OpenHABWidgetListFrgmt";
    private OnWidgetSelectedListener widgetSelectedListener;
    // Datasource, providing list of openHAB widgets
    private OpenHABWidgetDataSource openHABWidgetDataSource;
    // List adapter for list view of openHAB widgets
    private OpenHABWidgetArrayAdapter openHABWidgetAdapter;
    // Url of current sitemap page displayed
    // Url of current sitemap page displayed
    private String displayPageUrl;
    // sitemap root url
    private String sitemapRootUrl = "";
    // openHAB base url
    private String openHABBaseUrl = "https://demo.openhab.org:8443/";
    // List of widgets to display
    private ArrayList<OpenHABWidget> widgetList = new ArrayList<OpenHABWidget>();
    // Username/password for authentication
    private String openHABUsername = "";
    private String openHABPassword = "";
    // selected openhab widget
    private OpenHABWidget selectedOpenHABWidget;
    // widget Id which we got from nfc tag
    private String nfcWidgetId;
    // widget command which we got from nfc tag
    private String nfcCommand;
    // auto close app after nfc action is complete
    private boolean nfcAutoClose = false;
    // parent activity
    private OpenHABMainActivity mActivity;
    // loopj
    private MyAsyncHttpClient mAsyncHttpClient;
    // Am I visible?
    private boolean mIsVisible = false;
    private  OpenHABWidgetListFragment mTag;
    private int mCurrentSelectedItem = -1;
    private int mPosition;
    private int mOldSelectedItem = -1;

    @Inject IOpenHABSetting mOpenHABSetting;
    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IWidgetTypeLayoutProvider mWidgetLayoutProvider;
    @Inject ILogger mLogger;
    @Inject IColorParser mColorParser;
    @Inject IDocumentFactory mDocumentFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerWidgetListFragmentComponent.builder()
                .appComponent(((HABApplication) getActivity().getApplication()).appComponent())
                .build()
                .inject(this);

        mTag = this;
        Log.d(TAG, "onCreate()");

        if (savedInstanceState != null) {
            Log.d(TAG, "restoring state from savedInstanceState");
            displayPageUrl = savedInstanceState.getString("displayPageUrl");
            openHABBaseUrl = savedInstanceState.getString("openHABBaseUrl");
            sitemapRootUrl = savedInstanceState.getString("sitemapRootUrl");
            openHABUsername = savedInstanceState.getString("openHABUsername");
            openHABPassword = savedInstanceState.getString("openHABPassword");
            mCurrentSelectedItem = savedInstanceState.getInt("currentSelectedItem", -1);
            mPosition = savedInstanceState.getInt("position", -1);
            Log.d(TAG, String.format("onCreate selected item = %d", mCurrentSelectedItem));
        }
        if (getArguments() != null) {
            displayPageUrl = getArguments().getString("displayPageUrl");
            openHABBaseUrl = getArguments().getString("openHABBaseUrl");
            sitemapRootUrl = getArguments().getString("sitemapRootUrl");
            openHABUsername = getArguments().getString("openHABUsername");
            openHABPassword = getArguments().getString("openHABPassword");
            mPosition = getArguments().getInt("position");
        }
        Log.d(HABApplication.getLogTag(), String.format("[intent] Bundle savedInstanceState =>/ndisplayPageUrl = '%s'/nopenHABBaseUrl = '%s'/nsitemapRootUrl = '%s'", displayPageUrl, openHABBaseUrl, sitemapRootUrl));
        if (savedInstanceState != null)
            if (!displayPageUrl.equals(savedInstanceState.getString("displayPageUrl")))
                mCurrentSelectedItem = -1;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
        mActivity = (OpenHABMainActivity)getActivity();
        openHABWidgetDataSource = new OpenHABWidgetDataSource(mLogger, mColorParser);
        openHABWidgetAdapter = new OpenHABWidgetArrayAdapter(getActivity(),
                R.layout.openhabwidgetlist_genericitem, widgetList,
                mWidgetLayoutProvider,
                OpenHABWidgetArrayAdapter.WidgetLayoutType.IconTextControlList);
        getListView().setAdapter(openHABWidgetAdapter);
        openHABBaseUrl = mOpenHABSetting.getBaseUrl();
        openHABUsername = mOpenHABSetting.getUsername();
        openHABPassword = mOpenHABSetting.getPassword();
        openHABWidgetAdapter.setOpenHABUsername(openHABUsername);
        openHABWidgetAdapter.setOpenHABPassword(openHABPassword);
        openHABWidgetAdapter.setOpenHABBaseUrl(openHABBaseUrl);
        openHABWidgetAdapter.setAsyncHttpClient(mAsyncHttpClient);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d(TAG, "onActivityCreated() -> Widget clicked " + String.valueOf(position));
                OpenHABWidget openHABWidget = openHABWidgetAdapter.getItem(position);
                if (openHABWidget.hasLinkedPage()) {
                    // Widget have a page linked to it
                    String[] splitString;
                    splitString = openHABWidget.getLinkedPage().getTitle().split("\\[|\\]");
                    Log.d(TAG, "onActivityCreated() ->    widget id = " + openHABWidget.getLinkedPage().getId() + "  link = " + openHABWidget.getLinkedPage().getLink());
                    if (OpenHABWidgetListFragment.this.widgetSelectedListener != null) {
                        widgetSelectedListener.onWidgetSelectedListener(openHABWidget.getLinkedPage(),
                                OpenHABWidgetListFragment.this);
                    }
//                        navigateToPage(openHABWidget.getLinkedPage().getLink(), splitString[0]);
                    mOldSelectedItem = position;
                } else {
                    Log.d(TAG, String.format("onActivityCreated() -> Click on item with no linked page, reverting selection to item %d", mOldSelectedItem));
                    // If an item without a linked page is clicked this will clear the selection
                    // and revert it to previously selected item (if any) when CHOICE_MODE_SINGLE
                    // is switched on for widget listview in multi-column mode on tablets
                    getListView().clearChoices();
                    getListView().requestLayout();
                    getListView().setItemChecked(mOldSelectedItem, true);
                }
            }

        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Log.d(TAG, "Widget long-clicked " + String.valueOf(position));
                OpenHABWidget openHABWidget = openHABWidgetAdapter.getItem(position);
                Log.d(TAG, "Widget type = " + openHABWidget.getType());

                switch (openHABWidget.getType()) {
                    case Switch:
                    case Selection:
                    case Color:
                        selectedOpenHABWidget = openHABWidget;
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.nfc_dialog_title);
                        OpenHABNFCActionList nfcActionList = new OpenHABNFCActionList(selectedOpenHABWidget);
                        builder.setItems(nfcActionList.getNames(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent writeTagIntent = new Intent(getActivity().getApplicationContext(),
                                        OpenHABWriteTagActivity.class);
                                writeTagIntent.putExtra("sitemapPage", displayPageUrl);
                                writeTagIntent.putExtra("item", selectedOpenHABWidget.getItem().getName());
                                writeTagIntent.putExtra("itemType", selectedOpenHABWidget.getItem().getType().Name);
                                OpenHABNFCActionList nfcActionList =
                                        new OpenHABNFCActionList(selectedOpenHABWidget);
                                writeTagIntent.putExtra("command", nfcActionList.getCommands()[which]);
                                startActivityForResult(writeTagIntent, 0);
                                Util.overridePendingTransition(getActivity(), false);
                                selectedOpenHABWidget = null;
                            }
                        });
                        builder.show();
                }
                return true;
            }
        });
        if (getResources().getInteger(R.integer.pager_columns) > 1) {
            Log.d(TAG, "More then 1 column, setting selector on");
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(HABApplication.getLogTag(), "onAttach()");
        if (activity instanceof OnWidgetSelectedListener) {
            widgetSelectedListener = (OnWidgetSelectedListener)activity;
            mActivity = (OpenHABMainActivity)activity;
            mAsyncHttpClient = OpenHABMainActivity.getAsyncHttpClient();
        } else {
            Log.e(HABApplication.getLogTag(), "Attached to incompatible activity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(HABApplication.getLogTag(), "onCreateView");
        return inflater.inflate(R.layout.openhabwidgetlist_fragment, container, false);
    }

    @Override
    public void onPause () {
        super.onPause();
        Log.d(HABApplication.getLogTag(), "onPause() " + displayPageUrl);
        mAsyncHttpClient.cancelRequests(mActivity, mTag, true);
        if (openHABWidgetAdapter != null) {
            openHABWidgetAdapter.stopImageRefresh();
            openHABWidgetAdapter.stopVideoWidgets();
        }
        mCurrentSelectedItem = getListView().getCheckedItemPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(HABApplication.getLogTag(), "onResume() " + displayPageUrl);
        if (displayPageUrl != null) {
            Log.d(HABApplication.getLogTag(), "onResume() - Calling showPage() displayPageUrl = " + displayPageUrl);
            showPage(displayPageUrl, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(HABApplication.getLogTag(), "onSaveInstanceState");
        Log.d(HABApplication.getLogTag(), String.format("onSave current selected item = %d", getListView().getCheckedItemPosition()));
        savedInstanceState.putString("displayPageUrl", displayPageUrl);
        savedInstanceState.putString("openHABBaseUrl", openHABBaseUrl);
        savedInstanceState.putString("sitemapRootUrl", sitemapRootUrl);
        savedInstanceState.putString("openHABUsername", openHABUsername);
        savedInstanceState.putString("openHABPassword", openHABPassword);
        savedInstanceState.putInt("currentSelectedItem", getListView().getCheckedItemPosition());
        savedInstanceState.putInt("position", mPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint (boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisible = isVisibleToUser;
        Log.d(HABApplication.getLogTag(), String.format("isVisibleToUser(%B)", isVisibleToUser));
    }

    public static OpenHABWidgetListFragment withPage(String pageUrl, String baseUrl, String rootUrl,
                                                     String username, String password, int position) {
        Log.d(HABApplication.getLogTag(), "withPage(" + pageUrl + ")");
        OpenHABWidgetListFragment fragment = new OpenHABWidgetListFragment();
        Bundle args = new Bundle();
        args.putString("displayPageUrl", pageUrl);
        args.putString("openHABBaseUrl", baseUrl);
        args.putString("sitemapRootUrl", rootUrl);
        args.putString("openHABUsername", username);
        args.putString("openHABPassword", password);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Loads data from sitemap page URL and passes it to processContent
     *
     * @param  pageUrl  an absolute base URL of openHAB sitemap page
     * @param  longPolling  enable long polling when loading page
     * @return      void
     */
    public void showPage(String pageUrl, final boolean longPolling) {
//        pageUrl = "https://demo.openhab.org:8443/rest/sitemaps/demo";
        Log.i(HABApplication.getLogTag(), "[AsyncHttpClient] GET Request for: " + pageUrl + "   longPolling = " + longPolling);
        // Cancel any existing http request to openHAB (typically ongoing long poll)
        Header[] headers = {};
        if (!longPolling)
            startProgressIndicator();
        if (longPolling) {
            headers = new Header[] {new BasicHeader("X-Atmosphere-Transport", "long-polling")};
        }
        //TA - Calling REST Get method, requesting data from server.
        mAsyncHttpClient.get(mActivity, pageUrl, headers, null, new DocumentHttpResponseHandler(mDocumentFactory) {
            @Override
            public void onSuccess(Document document) {
                if (document != null) {
                    Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] Response: "  + document.toString());
                    if (!longPolling)
                        stopProgressIndicator();
                    processContent(document, longPolling);
                } else {
                    Log.e(HABApplication.getLogTag(), "[AsyncHttpClient] Got a null response from openHAB");
                }
            }
            @Override
            public void onFailure(Throwable error, String content) {
                if (!longPolling)
                    stopProgressIndicator();
//                if (error instanceof AsyncHttpAbortException) {//TODO - removed by TA
//                    Log.d(TAG, "Request for " + displayPageUrl + " was aborted");
//                    return;
//                }
                if (error instanceof SocketTimeoutException) {
                    Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] Connection timeout, reconnecting");
                    Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] showPage() - Calling showPage(longPolling = " + longPolling + ") displayPageUrl = " + displayPageUrl);
                    showPage(displayPageUrl, longPolling);
                    return;
                }
                Log.e(HABApplication.getLogTag(), error.getClass().toString());
                Log.e(HABApplication.getLogTag(), "[AsyncHttpClient] Connection error = " + error.getClass().toString() + ", cycle aborted");
            }
        }, mTag);
    }

    /**
     * Parse XML sitemap page and show it
     *
     * @param  document	XML Document
     * @return      void
     */
    public void processContent(Document document, boolean longPolling) {
        // As we change the page we need to stop all videos on current page
        // before going to the new page. This is quite dirty, but is the only
        // way to do that...
        //TA: TODO - Play video/audio in separate threads.
        openHABWidgetAdapter.stopVideoWidgets();
        openHABWidgetAdapter.stopImageRefresh();

        Node rootNode = document.getFirstChild();
        openHABWidgetDataSource.setSourceNode(rootNode);
//        mWidgetProvider.setOpenHABWidgets(openHABWidgetDataSource);
        widgetList.clear();
        for (OpenHABWidget w : openHABWidgetDataSource.getWidgets()) {
            // Remove frame widgets with no label text
            if (w.getType() == OpenHABWidgetType.Frame && TextUtils.isEmpty(w.getLabel()))
                continue;
            widgetList.add(w);
        }
        openHABWidgetAdapter.notifyDataSetChanged();
        if (!longPolling) {
            getListView().clearChoices();
            Log.d(TAG, String.format("processContent selectedItem = %d", mCurrentSelectedItem));
            if (mCurrentSelectedItem >= 0)
                getListView().setItemChecked(mCurrentSelectedItem, true);
        }
        if (getActivity() != null && mIsVisible)
            getActivity().setTitle(openHABWidgetDataSource.getTitle());
//            }
        // Set widget list index to saved or zero position
        // This would mean we got widget and command from nfc tag, so we need to do some automatic actions!
        if (this.nfcWidgetId != null && this.nfcCommand != null) {
            Log.d(TAG, "Have widget and command, NFC action!");
            OpenHABWidget nfcWidget = this.openHABWidgetDataSource.getWidgetById(this.nfcWidgetId);
            OpenHABItem nfcItem = nfcWidget.getItem();
            // Found widget with id from nfc tag and it has an item
            if (nfcWidget != null && nfcItem != null) {
                // TODO: Perform nfc widget action here
                if (this.nfcCommand.equals("TOGGLE")) {
                    if (nfcItem.getType() == OpenHABItemType.Rollershutter) {
                        if (nfcItem.getStateAsBoolean())
                            this.openHABWidgetAdapter.sendItemCommand(nfcItem, "UP");
                        else
                            this.openHABWidgetAdapter.sendItemCommand(nfcItem, "DOWN");
                    } else {
                        if (nfcItem.getStateAsBoolean())
                            this.openHABWidgetAdapter.sendItemCommand(nfcItem, "OFF");
                        else
                            this.openHABWidgetAdapter.sendItemCommand(nfcItem, "ON");
                    }
                } else {
                    this.openHABWidgetAdapter.sendItemCommand(nfcItem, this.nfcCommand);
                }
            }
            this.nfcWidgetId = null;
            this.nfcCommand = null;
            if (this.nfcAutoClose) {
                getActivity().finish();
            }
        }
        Log.d(TAG, "ProcessContent() - Calling showPage() with displayUrl = " + displayPageUrl);
        showPage(displayPageUrl, true);
    }

    private void stopProgressIndicator() {
        if (mActivity != null)
            Log.d(TAG, "Stop progress indicator");
            mActivity.stopProgressIndicator();
    }

    private void startProgressIndicator() {
        if (mActivity != null)
            Log.d(TAG, "Start progress indicator");
            mActivity.startProgressIndicator();
    }

    private void showAlertDialog(String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(alertMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setOpenHABUsername(String openHABUsername) {
        this.openHABUsername = openHABUsername;
    }

    public void setOpenHABPassword(String openHABPassword) {
        this.openHABPassword = openHABPassword;
    }

    public void setDisplayPageUrl(String displayPageUrl) {
        this.displayPageUrl = displayPageUrl;
    }

    public String getDisplayPageUrl() {
        return displayPageUrl;
    }

    public String getTitle() {
        Log.d(TAG, "getPageTitle()");
        if (openHABWidgetDataSource != null)
            return openHABWidgetDataSource.getTitle();
        return "";
    }

    public void clearSelection() {
        getListView().clearChoices();
        getListView().requestLayout();
    }

    public int getPosition() {
        return mPosition;
    }

}
