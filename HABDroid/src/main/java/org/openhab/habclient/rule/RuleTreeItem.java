package org.openhab.habclient.rule;

import java.util.HashMap;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTreeItem extends HashMap<Integer, RuleTreeItem> {
    HashMap<Integer, RuleTreeItem> mChildren;
    int mPosition;
    String mName;
    ItemType mItemType;
    String mItemId;

    public RuleTreeItem(int position, String name, ItemType itemType) {
        this(position, name, itemType, null);
    }

    public RuleTreeItem(int position, String name, ItemType itemType, HashMap<Integer, RuleTreeItem> children) {
        this(position, children);
        mName = name;
        mItemType = itemType;
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

    public RuleTreeItem.ItemType getItemType() {
        return mItemType;
    }

    public void setItemType(RuleTreeItem.ItemType itemType) {
        mItemType = itemType;
    }

    public void setItemId(String value) { mItemId = value; }

    public String getItemId() { return mItemId; }

    public enum ItemType {
        OPERAND(0),
        OPERATOR(1);

        public final int Value;

        private ItemType(int value) {
            Value = value;
        }
    }
}
