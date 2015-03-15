package org.openhab.habclient.command;

import android.content.Context;

import org.openhab.domain.command.ICommandColorProvider;
import org.openhab.habclient.dagger.ApplicationContext;
import org.openhab.habdroid.R;

import javax.inject.Inject;

public class CommandColorProvider implements ICommandColorProvider {
    private final Context mContext;

    @Inject
    public CommandColorProvider(@ApplicationContext Context context) {
        mContext = context;
    }

    @Override
    public String[] getColorNames() {
        return mContext.getResources().getStringArray(R.array.command_colors);
    }
}
