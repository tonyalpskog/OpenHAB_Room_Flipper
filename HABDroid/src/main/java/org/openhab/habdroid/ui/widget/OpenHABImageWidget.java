package org.openhab.habdroid.ui.widget;

import android.view.View;

import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;
import org.openhab.habdroid.util.AutoRefreshImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABImageWidget extends OpenHABWidgetBase {
    ArrayList<AutoRefreshImageView> mRefreshImageList;

    public OpenHABImageWidget(ArrayList<AutoRefreshImageView> refreshImageList, IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
        mRefreshImageList = refreshImageList;
    }

    @Override
    public View getWidget() {
        AutoRefreshImageView imageImage = (AutoRefreshImageView)mViewData.widgetView.findViewById(R.id.imageimage);
        imageImage.setImageUrl(ensureAbsoluteURL(mHABWidgetCommunication.getOpenHABBaseUrl(), mViewData.openHABWidget.getUrl()), false,
                mHABWidgetCommunication.getOpenHABUsername(), mHABWidgetCommunication.getOpenHABPassword());
        //    		ViewGroup.LayoutParams imageLayoutParams = imageImage.getLayoutParams();
        //    		float imageRatio = imageImage.getDrawable().getIntrinsicWidth()/imageImage.getDrawable().getIntrinsicHeight();
        //    		imageLayoutParams.height = (int) (screenWidth/imageRatio);
        //    		imageImage.setLayoutParams(imageLayoutParams);
        if (mViewData.openHABWidget.getRefresh() > 0) {
            imageImage.setRefreshRate(mViewData.openHABWidget.getRefresh());
            mRefreshImageList.add(imageImage);
        }

        return mViewData.widgetView;
    }

    private String ensureAbsoluteURL(String base, String maybeRelative) {
        if (maybeRelative.startsWith("http")) {
            return maybeRelative;
        } else {
            try {
                return new URL(new URL(base), maybeRelative).toExternalForm();
            } catch (MalformedURLException e) {
                return "";
            }
        }
    }
}
