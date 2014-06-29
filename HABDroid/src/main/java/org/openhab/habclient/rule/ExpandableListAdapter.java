package org.openhab.habclient.rule;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.openhab.habdroid.R;
import org.openhab.domain.business.rule.RuleTreeItem;

import java.util.HashMap;

/**
 * Created by Tony Alpskog in 2014.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private HashMap<Integer, RuleTreeItem> mTreeData;

    public ExpandableListAdapter(Context context, HashMap<Integer, RuleTreeItem> treeData) {
        this._context = context;
        mTreeData = treeData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return mTreeData.get(Integer.valueOf(groupPosition)).getChildren().get(Integer.valueOf(childPosititon));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final RuleTreeItem childItem = (RuleTreeItem) getChild(groupPosition, childPosition);

        if(childItem.getChildren().size() > 10) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.rule_tree_child_group, parent, false);
            }
            ExpandableListView childTree = new ExpandableListView(_context);//(ExpandableListView) convertView.findViewById(R.id.rule_if_tree);
            childTree.setAdapter(new ExpandableListAdapter(_context, childItem.mChildren));
            return childTree;
        } else {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.rule_tree_child, parent, false);
            }
            TextView txtListChild = (TextView) convertView.findViewById(R.id.child_text);
            txtListChild.setText(childItem.mName);
            return convertView;
        }

        //return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mTreeData.get(Integer.valueOf(groupPosition)).mChildren.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mTreeData.get(Integer.valueOf(groupPosition));
    }

    @Override
    public int getGroupCount() {
        return mTreeData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        RuleTreeItem groupItem = (RuleTreeItem) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rule_tree_group, parent, false);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.group_text);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(groupItem.mName);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
