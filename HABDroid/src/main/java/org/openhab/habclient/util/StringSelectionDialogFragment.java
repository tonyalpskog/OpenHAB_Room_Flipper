package org.openhab.habclient.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.openhab.domain.util.ListStringSearch;
import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class StringSelectionDialogFragment<T> extends DialogFragment implements DialogInterface.OnClickListener {
    protected static final String ARG_SOURCE = "source";
    protected static final String ARG_DIALOG_TITLE = "dialogTitle";
    protected static final String ARG_SHOW_NEXT_BUTTON = "showNextButton";

    protected List<T> mSourceList = new ArrayList<T>();
    String mDialogTitle;
    EditText mEditTextFilter;
    ListView mFilteredListView;
    ArrayAdapter mArrayAdapter;
    protected T mSelectedItem = null;
    String mPreviousSearch = "";
    ListStringSearch mListStringSearch;
    boolean mShowNextButton;

    private static final int MIN_SEARCH_WORD_LENGTH = 3;
    private static final String SEARCH_WORD_DELIMITER = "\\s+";

//    public static <T extends String> StringSelectionDialogFragment newInstance(List<String> source, String dialogTitle, boolean showNextButton) {
//        final StringSelectionDialogFragment fragment = new StringSelectionDialogFragment();
//        final Bundle args = new Bundle();
//        args.putStringArrayList(ARG_SOURCE, new ArrayList<>(source));
//        args.putString(ARG_DIALOG_TITLE, dialogTitle);
//        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static <T>StringSelectionDialogFragment newInstance(String dialogTitle, boolean showNextButton) {
        final StringSelectionDialogFragment<T> fragment = new StringSelectionDialogFragment<T>();
        final Bundle args = new Bundle();
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
        fragment.setArguments(args);
        return fragment;
    }

    public void setSourceList(List<T> source) {
        mSourceList = source;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private SelectionListener getListener() {
        return (SelectionListener) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if(activity == null) throw new IllegalArgumentException("activity is null");

        final Bundle args = getArguments();
        if(args == null)
            return;

//        mSourceList = args.getStringArrayList(ARG_SOURCE);
        mDialogTitle = args.getString(ARG_DIALOG_TITLE);
        mListStringSearch = new ListStringSearch(MIN_SEARCH_WORD_LENGTH, SEARCH_WORD_DELIMITER);
        mShowNextButton = args.getBoolean(ARG_SHOW_NEXT_BUTTON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity == null)
            throw new IllegalStateException("activity is null");

        if(mShowNextButton)
            return new AlertDialog.Builder(activity).setTitle(mDialogTitle)
                    .setView(createCustomView(activity))
                    .setCancelable(true)
                    .setPositiveButton(activity.getString(R.string.choice_next), this)
                    .setNeutralButton(activity.getString(R.string.choice_done), this)
                    .setNegativeButton(activity.getString(R.string.choice_cancel), this)
                    .create();

        return new AlertDialog.Builder(activity).setTitle(mDialogTitle)
                .setView(createCustomView(activity))
                .setNeutralButton(activity.getString(R.string.choice_done), this)
                .setNegativeButton(activity.getString(R.string.choice_cancel), this)
                .create();
    }

    private View createCustomView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_find_string, null);

        if(view != null) {
            mEditTextFilter = (EditText) view.findViewById(R.id.fragment_find_string_edit_search);
            mFilteredListView = (ListView) view.findViewById(R.id.fragment_find_string_list_strings);

            mEditTextFilter.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, 0, 0);
            mEditTextFilter.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    mEditTextFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    //Start filtering when 3 chars has been written and don't re-filter if last char is a word delimiter = <space>
                    if(mEditTextFilter.getText().length() == 0)
                        mEditTextFilter.setBackgroundColor(android.R.drawable.editbox_background_normal);
                    else {
                        mEditTextFilter.setBackgroundColor(mListStringSearch.isSearchPhraseLegal(mEditTextFilter.getText().toString())? android.R.drawable.editbox_background_normal : Color.RED);
                    }

                    if(mEditTextFilter.getText().length() >= 3 && mEditTextFilter.getText().charAt(mEditTextFilter.getText().length() - 1) != ' ') {
                        mPreviousSearch = mEditTextFilter.getText().toString();
                        List<String> tempList = mListStringSearch.getFilteredArray(mSourceList, mEditTextFilter.getText().toString());
                        mArrayAdapter.clear();
                        mArrayAdapter.addAll(tempList);
                        mArrayAdapter.notifyDataSetChanged();
                    } else if(mEditTextFilter.getText().length() < 3 && mPreviousSearch.length() >= 3) {
                        List<T> initialList = new ArrayList<T>();
                        initialList.addAll(mSourceList);
                        mArrayAdapter.clear();
                        mArrayAdapter.addAll(initialList);
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            });

            mFilteredListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFilteredListView.clearChoices();
                    mFilteredListView.setItemChecked(position, true);
                    mFilteredListView.setSelection(position);// mArrayAdapter.getItem(position);
                    mSelectedItem = (T) mFilteredListView.getItemAtPosition(position);
                }
            });

            mFilteredListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mSelectedItem = (T) parent.getAdapter().getItem(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mSelectedItem = null;
                }
            });

        }
        List<T> initialList = new ArrayList<T>();
        initialList.addAll(mSourceList);
        mArrayAdapter = new ArrayAdapter<T>(getActivity(), android.R.layout.simple_list_item_1, initialList);
        mFilteredListView.setAdapter(mArrayAdapter);

        return view;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
            case DialogInterface.BUTTON_NEUTRAL:
                if(getListener() != null) {
                    getListener().onSelected(mSelectedItem);
                }
                break;
            default:
                if(getListener() != null) {
                    getListener().onSelectionAborted();
                }
                break;
        }
    }

    public interface SelectionListener {
        public <T> void onSelected(T selection);
        public void onSelectionAborted();
    }
}
