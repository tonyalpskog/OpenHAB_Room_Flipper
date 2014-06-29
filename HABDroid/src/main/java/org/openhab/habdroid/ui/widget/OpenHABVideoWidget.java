package org.openhab.habdroid.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.VideoView;

import org.openhab.habclient.HABApplication;

import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.OpenHABWidgetArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABVideoWidget extends OpenHABWidgetBase {
    private ArrayList<VideoView> mVideoWidgetList;
    private Context mContext;

    public OpenHABVideoWidget(Context context, ArrayList<VideoView> videoWidgetList, IHABWidgetCommunication habWidgetCommunication, OpenHABWidgetArrayAdapter.ViewData viewData) {
        super(habWidgetCommunication, viewData);
        mContext = context;
        mVideoWidgetList = videoWidgetList;
    }

    @Override
    public View getWidget() {
        VideoView videoVideo = (VideoView)mViewData.widgetView.findViewById(R.id.videovideo);
        Log.d(HABApplication.getLogTag(), "Opening video at " + mViewData.openHABWidget.getUrl());
        // TODO: This is quite dirty fix to make video look maximum available size on all screens
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams videoLayoutParams = videoVideo.getLayoutParams();
        videoLayoutParams.height = (int)(wm.getDefaultDisplay().getWidth()/1.77);
        videoVideo.setLayoutParams(videoLayoutParams);
        // We don't have any event handler to know if the VideoView is on the screen
        // so we manage an array of all videos to stop them when user leaves the page
        if (!mVideoWidgetList.contains(videoVideo))
            mVideoWidgetList.add(videoVideo);
        // Start video
        if (!videoVideo.isPlaying()) {
            videoVideo.setVideoURI(Uri.parse(mViewData.openHABWidget.getUrl()));
            videoVideo.start();
        }
        Log.d(HABApplication.getLogTag(), "Video height is " + videoVideo.getHeight());

        return mViewData.widgetView;    }
}
