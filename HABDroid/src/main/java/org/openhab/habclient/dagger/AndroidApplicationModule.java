package org.openhab.habclient.dagger;

import android.content.Context;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidApplicationModule {
    private final Context mContext;

    public AndroidApplicationModule(Context context) {
        mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }

    @Provides
    public LayoutInflater provideLayoutInflater(Context context) {
        return LayoutInflater.from(context);
    }
}
