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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.openhab.habdroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class StringSelectionDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    protected static final String ARG_SOURCE = "source";
    protected static final String ARG_DIALOG_TITLE = "dialogTitle";
    protected static final String ARG_SHOW_NEXT_BUTTON = "showNextButton";

    protected List<String> mSourceList = new ArrayList<String>();
    String mDialogTitle;
    EditText mEditTextFilter;
    ListView mFilteredListView;
    ArrayAdapter mArrayAdapter;
    protected String mSelectedString = null;
    String mPreviousSearch = "";
    StringListSearch mStringListSearch;
    boolean mShowNextButton;

    private static final int MIN_SEARCH_WORD_LENGTH = 3;
    private static final String SEARCH_WORD_DELIMITER = "\\s+";

    public static StringSelectionDialogFragment newInstance(List<String> source, String dialogTitle, boolean showNextButton) {
        final StringSelectionDialogFragment fragment = new StringSelectionDialogFragment();
        final Bundle args = new Bundle();
        args.putStringArrayList(ARG_SOURCE, new ArrayList<String>(source));
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        args.putBoolean(ARG_SHOW_NEXT_BUTTON, showNextButton);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private StringSelectionListener getListener() {
        return (StringSelectionListener) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if(activity == null) throw new IllegalArgumentException("activity is null");

        final Bundle args = getArguments();
        if(args == null)
            return;

        mSourceList = args.getStringArrayList(ARG_SOURCE);
        mDialogTitle = args.getString(ARG_DIALOG_TITLE);
        mStringListSearch = new StringListSearch(MIN_SEARCH_WORD_LENGTH, SEARCH_WORD_DELIMITER);
        mShowNextButton = args.getBoolean(ARG_SHOW_NEXT_BUTTON);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity == null)
            throw new IllegalStateException("activity is null");

        if(mShowNextButton)
            return new AlertDialog.Builder(activity).setTitle(mDialogTitle)
                    .setView(createCustomView(activity))
                    .setPositiveButton("Next", this)
                    .setNeutralButton("Done", this)
                    .setNegativeButton("Cancel", this)
                    .create();

        return new AlertDialog.Builder(activity).setTitle(mDialogTitle)
                .setView(createCustomView(activity))
                .setNeutralButton("Done", this)
                .setNegativeButton("Cancel", this)
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
                        mEditTextFilter.setBackgroundColor(mStringListSearch.isSearchPhraseLegal(mEditTextFilter.getText().toString())? android.R.drawable.editbox_background_normal : Color.RED);
                    }

                    if(mEditTextFilter.getText().length() >= 3 && mEditTextFilter.getText().charAt(mEditTextFilter.getText().length() - 1) != ' ') {
                        mPreviousSearch = mEditTextFilter.getText().toString();
                        List<String> tempList = mStringListSearch.getFilteredArray(mSourceList, mEditTextFilter.getText().toString());
                        mArrayAdapter.clear();
                        mArrayAdapter.addAll(tempList);
                        mArrayAdapter.notifyDataSetChanged();
                    } else if(mEditTextFilter.getText().length() < 3 && mPreviousSearch.length() >= 3) {
                        List<String> initialList = new ArrayList<String>();
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
                    mSelectedString = mFilteredListView.getItemAtPosition(position).toString();
                }
            });

            mFilteredListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mSelectedString = parent.getAdapter().getItem(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mSelectedString = null;
                }
            });

        }
        List<String> initialList = new ArrayList<String>();
        initialList.addAll(mSourceList);
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, initialList);
        mFilteredListView.setAdapter(mArrayAdapter);

        return view;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
            case DialogInterface.BUTTON_NEUTRAL:
                if(getListener() != null) {
                    getListener().onStringSelected(mSelectedString);
                }
                break;
            default:
                if(getListener() != null) {
                    getListener().onSelectionAborted();
                }
                break;
        }
    }

    public interface StringSelectionListener {
        public void onStringSelected(String selection);
        public void onSelectionAborted();
    }
}
