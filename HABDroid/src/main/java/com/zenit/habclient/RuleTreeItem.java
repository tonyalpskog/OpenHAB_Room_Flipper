package com.zenit.habclient;

import java.util.HashMap;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTreeItem {
    HashMap<Integer, RuleTreeItem> mChildren;
    int mPosition;
    String mName;

    public RuleTreeItem(int position, String name) {
        this(position, name, null);
    }

    public RuleTreeItem(int position, String name, HashMap<Integer, RuleTreeItem> children) {
        mPosition = position;
        mName = name;
        mChildren = children != null? children: new HashMap<Integer, RuleTreeItem>();
    }

    @Override
    public String toString() {
        return mName;
    }
}
