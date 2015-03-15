package org.openhab.habclient.dagger;

import android.content.Context;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidActivityModule {
    private final Context mContext;

    public AndroidActivityModule(Context context) {
        mContext = context;
    }

    @Provides @ActivityContext
    public Context provideContext() {
        return mContext;
    }

    @Provides
    public LayoutInflater provideLayoutInflater(@ActivityContext Context context) {
        return LayoutInflater.from(context);
    }
}
