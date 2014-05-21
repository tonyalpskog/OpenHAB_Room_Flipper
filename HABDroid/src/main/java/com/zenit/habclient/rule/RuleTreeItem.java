package com.zenit.habclient.rule;

import java.util.HashMap;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTreeItem extends HashMap<Integer, RuleTreeItem> {
    HashMap<Integer, RuleTreeItem> mChildren;
    int mPosition;
    String mName;

    public RuleTreeItem(int position, String name) {
        this(position, name, null);
    }

    public RuleTreeItem(int position, String name, HashMap<Integer, RuleTreeItem> children) {
        this(position, children);
        mName = name;
    }

    private RuleTreeItem(int position, HashMap<Integer, RuleTreeItem> children) {
        mPosition = position;
        mChildren = children != null? children: new HashMap<Integer, RuleTreeItem>();
    }

    @Override
    public String toString() {
        return mName;
    }

    public int getPosition() {
        return mPosition;
    }

    public HashMap<Integer, RuleTreeItem> getChildren() {
        return mChildren;
    }

    public void setPosition(Integer position) {
        mPosition = position;
    }
}
