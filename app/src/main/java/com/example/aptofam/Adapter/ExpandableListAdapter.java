package com.example.aptofam.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.aptofam.Model.ChildModel;
import com.example.aptofam.Model.ParentModel;
import com.example.aptofam.R;

import java.util.ArrayList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private ArrayList<ParentModel> childList;

    public ExpandableListAdapter(Context context, ArrayList<ParentModel> childList) {
        this.context = context;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return childList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ChildModel> itemList = childList.get(groupPosition).getItemList();
        return itemList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return childList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ChildModel> itemList = childList.get(groupPosition).getItemList();
        return itemList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ParentModel parentInfo = (ParentModel) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.parent_items, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.headingParent);
        textView.setText(parentInfo.getName().trim());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildModel childInfo = (ChildModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.child_items, null);
        }

        for (int i = 0; i < childInfo.getSize(); i++) {
            Log.d("ExpandableListAdapter", "Child Item: " + childInfo.getName(i));
            Log.d("ExpandableListAdapter", "Child Description: " + childInfo.getDescription(i));

            TextView childItem = convertView.findViewById(
                    context.getResources().getIdentifier("textChildItems" + (i + 1), "id", context.getPackageName())
            );
            TextView childItemDescription = convertView.findViewById(
                    context.getResources().getIdentifier("textChildItemsDescription" + (i + 1), "id", context.getPackageName())
            );

            if (childItem != null && childItemDescription != null) {
                childItem.setText(childInfo.getName(i).trim());
                childItemDescription.setText(childInfo.getDescription(i).trim());
            }
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
