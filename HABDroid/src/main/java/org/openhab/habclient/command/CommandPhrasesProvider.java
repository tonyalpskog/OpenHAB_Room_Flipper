package org.openhab.habclient.command;

import android.content.Context;

import org.openhab.domain.command.ICommandPhrasesProvider;
import org.openhab.domain.command.OpenHABWidgetCommandType;
import org.openhab.habdroid.R;

import javax.inject.Inject;

public class CommandPhrasesProvider implements ICommandPhrasesProvider {
    private final Context mContext;

    @Inject
    public CommandPhrasesProvider(Context context) {
        mContext = context;
    }

    @Override
    public String[] getCommandPhrases(OpenHABWidgetCommandType commandType) {
        switch (commandType) {
            case GetStatus:
                return getStringArrayFromResource(R.array.command_phrases_get_status);
            case SwitchOn:
                return getStringArrayFromResource(R.array.command_phrases_switch_on);
            case SwitchOff:
                return getStringArrayFromResource(R.array.command_phrases_switch_off);
            case RollerShutterDown:
                return getStringArrayFromResource(R.array.command_phrases_roller_down);
            case RollerShutterUp:
                return getStringArrayFromResource(R.array.command_phrases_roller_up);
            case RollerShutterStop:
                return getStringArrayFromResource(R.array.command_phrases_roller_stop);
            case SliderSetPercentage:
                return getStringArrayFromResource(R.array.command_phrases_percent);
            case AdjustSetpoint:
                return getStringArrayFromResource(R.array.command_phrases_setpoint);
            default:
                return null;
        }
    }

    private String[] getStringArrayFromResource(int resourceId) {
        return mContext.getResources().getStringArray(resourceId);
    }
}
