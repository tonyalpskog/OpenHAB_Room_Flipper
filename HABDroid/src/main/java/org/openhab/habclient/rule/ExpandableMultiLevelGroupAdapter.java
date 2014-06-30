package org.openhab.habclient.rule;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.openhab.domain.rule.RuleTreeItem;

/**
 * Created by Tony Alpskog in 2014.
 */
public class ExpandableMultiLevelGroupAdapter extends BaseExpandableListAdapter
{

    int mLevelNumber;
    Context mContext;

    SparseArray<RuleTreeItem> mTreeData;

    public ExpandableMultiLevelGroupAdapter(Context context, SparseArray<RuleTreeItem> treeData) {
        this(context, treeData, 1);
    }

    private ExpandableMultiLevelGroupAdapter(Context context, SparseArray<RuleTreeItem> treeData, int levelNumber) {
        mContext = context;
        mTreeData = treeData;
        mLevelNumber = levelNumber;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return ((RuleTreeItem) getGroup(groupPosition)).mChildren.get(Integer.valueOf(childPosititon));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        final RuleTreeItem childItem = (RuleTreeItem) getChild(groupPosition, childPosition);
        Log.d("Tree", "Tree.Child = " + childItem.mName);

        if(childItem.mChildren.size() < 1) {
            TextView tv = new TextView(mContext);
            tv.setText(childItem.mName);
            tv.setPadding(50, 7, 7, 7);
            //tv.setBackgroundColor(Color.YELLOW);
            ViewGroup.LayoutParams params = tv.getLayoutParams();
//            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
//            params.height = FrameLayout.LayoutParams.FILL_PARENT;
            tv.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            return tv;
        }

        MultiGroupExpandableListView branchView = new MultiGroupExpandableListView(mContext);
        ViewGroup.LayoutParams params = branchView.getLayoutParams();
        if(params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//        params.width = ViewGroup.LayoutParams.FILL_PARENT;
//        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        branchView.setLayoutParams(params);
        branchView.setPadding(50, 0, 0, 0);
        SparseArray<RuleTreeItem> childAsHash = new SparseArray<RuleTreeItem>();
        childAsHash.put(0, childItem);
        branchView.setAdapter(new ExpandableMultiLevelGroupAdapter(mContext, childAsHash, mLevelNumber + 1));
        //branchView.setGroupIndicator(null);

        // Group expanded listener
        branchView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                mSelectedItem = getTreeData().get(Integer.valueOf(groupPosition));
                Toast.makeText(mContext,
                        getTreeData().get(Integer.valueOf(groupPosition)).getItemId() + " Expanded_2",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Group collasped listener
        branchView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(mContext,
                        getTreeData().get(Integer.valueOf(groupPosition)).getItemId() + " Collapsed:2",
                        Toast.LENGTH_SHORT).show();

            }
        });
        return branchView;
    }

    private boolean hasGrandChildren(RuleTreeItem treeItem) {
        if(treeItem.mChildren.size() < 1)
            return false;

        for (RuleTreeItem ruleTreeItem : treeItem.mChildren.values()) {
            if (!ruleTreeItem.mChildren.isEmpty())
                return true;
        }

        return false;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((RuleTreeItem) getGroup(groupPosition)).mChildren.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getTreeData().get(Integer.valueOf(groupPosition));
    }

    @Override
    public int getGroupCount() {
        return getTreeData().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent)
    {
        Log.d("Tree", "Tree.Group = " + ((RuleTreeItem) getGroup(groupPosition)).mName);

        TextView tv = new TextView(mContext);
        tv.setText(((RuleTreeItem) getGroup(groupPosition)).mName);
        //tv.setBackgroundColor(Color.BLUE);
        tv.setPadding(50, 7, 7, 7);

        return tv;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    private SparseArray<RuleTreeItem> getTreeData() {
        if(mTreeData == null)
            mTreeData = new SparseArray<RuleTreeItem>();
        return mTreeData;
    }


    //====================================================
    //============== Internal classes ====================
    //====================================================

    private class MultiGroupExpandableListView extends ExpandableListView
    {

        int intGroupPosition, intChildPosition, intGroupid;

        public MultiGroupExpandableListView(Context context)
        {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(600, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}