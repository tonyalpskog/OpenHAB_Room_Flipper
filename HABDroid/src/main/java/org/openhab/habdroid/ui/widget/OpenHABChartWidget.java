package org.openhab.habdroid.ui.widget;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.openhab.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.model.OpenHABItemType;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;
import org.openhab.habdroid.util.AutoRefreshImageView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABChartWidget extends OpenHABWidgetBase {
    private ArrayList<AutoRefreshImageView> mRefreshImageList;
    private int mScreenWidth;

    public OpenHABChartWidget(int screenWidth, ArrayList<AutoRefreshImageView> refreshImageList, IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
        mRefreshImageList = refreshImageList;
        mScreenWidth = screenWidth;
    }

    @Override
    public View getWidget() {
        AutoRefreshImageView chartImage = (AutoRefreshImageView)mViewData.widgetView.findViewById(R.id.chartimage);
        OpenHABItem chartItem = mViewData.openHABWidget.getItem();
        Random random = new Random();
        String chartUrl = "";
        if (chartItem != null) {
            if (chartItem.getType() == OpenHABItemType.Group) {
                chartUrl = mHABWidgetCommunication.getOpenHABBaseUrl() + "rrdchart.png?groups=" + chartItem.getName() +
                        "&period=" + mViewData.openHABWidget.getPeriod() + "&random=" +
                        String.valueOf(random.nextInt());
            } else {
                chartUrl = mHABWidgetCommunication.getOpenHABBaseUrl() + "rrdchart.png?items=" + chartItem.getName() +
                        "&period=" + mViewData.openHABWidget.getPeriod() + "&random=" +
                        String.valueOf(random.nextInt());
            }
        }
        Log.d(HABApplication.getLogTag(), "Chart url = " + chartUrl);
        if (chartImage == null)
            Log.e(HABApplication.getLogTag(), "chartImage == null !!!");
        //    		if (openHABUsername != null && openHABPassword != null)
        chartImage.setImageUrl(chartUrl, false, mHABWidgetCommunication.getOpenHABUsername(), mHABWidgetCommunication.getOpenHABPassword());
        //    		else
        //    			chartImage.setImageUrl(chartUrl, false);
        // TODO: This is quite dirty fix to make charts look full screen width on all displays
        ViewGroup.LayoutParams chartLayoutParams = chartImage.getLayoutParams();
        chartLayoutParams.height = (int) (mScreenWidth/1.88);
        chartImage.setLayoutParams(chartLayoutParams);
        if (mViewData.openHABWidget.getRefresh() > 0) {
            chartImage.setRefreshRate(mViewData.openHABWidget.getRefresh());
            mRefreshImageList.add(chartImage);
        }
        Log.d(HABApplication.getLogTag(), "chart size = " + chartLayoutParams.width + " " + chartLayoutParams.height);

        return mViewData.widgetView;
    }
}
