package org.openhab.habclient.rule;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.IRuleOperationProvider;
import org.openhab.habclient.InjectUtils;
import org.openhab.habclient.util.StringSelectionDialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OperatorSelectionDialogFragment extends StringSelectionDialogFragment {
    private static final String ARG_OPEN_HAB_ITEM_NAME = "openHABItemName";
    private static final String ARG_SOURCE_TYPE = "EntityDataTypeSource";
    private String mOpenHABItemName;

    @Inject IRuleOperationProvider mRuleOperationProvider;
    @Inject IOpenHABWidgetProvider mWidgetProvider;
    @Inject IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;

    public static OperatorSelectionDialogFragment newInstance(String openHABItemName,
                                                              EntityDataTypeSource sourceType,
                                                              String dialogTitle,
                                                              boolean showNextButton,
                                                              List<String> ruleOperatorList) {
        final OperatorSelectionDialogFragment fragment = new OperatorSelectionDialogFragment();

        final Bundle args = new Bundle();
        args.putStringArrayList(ARG_SOURCE, new ArrayList<String>(ruleOperatorList));
        args.putString(ARG_SOURCE_TYPE, sourceType.name());
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
        args.putString(ARG_OPEN_HAB_ITEM_NAME, openHABItemName);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtils.inject(this);

        final Bundle args = getArguments();
        if(args == null)
            return;

        mOpenHABItemName = args.getString(ARG_OPEN_HAB_ITEM_NAME);
        EntityDataTypeSource sourceType = EntityDataTypeSource.valueOf(args.getString(ARG_SOURCE_TYPE));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
            case DialogInterface.BUTTON_NEUTRAL:
                break;
            default:
                break;
        }
    }
}
