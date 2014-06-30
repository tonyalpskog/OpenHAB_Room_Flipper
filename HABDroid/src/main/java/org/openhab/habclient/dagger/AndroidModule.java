package org.openhab.habclient.dagger;

import android.content.Context;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AndroidModule {
    private final Context mContext;

    public AndroidModule(Context context) {
        mContext = context;
    }

    @Provides
    public Context provideContext() {
        return mContext;
    }

    @Provides
    public LayoutInflater provideLayoutInflater() {
        return LayoutInflater.from(mContext);
    }
}
