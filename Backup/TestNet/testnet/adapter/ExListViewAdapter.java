package com.hanyu.hust.testnet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;

import java.util.List;
import java.util.Map;

/**
 * ExListView的适配器
 * 其中，MachineLearning和SystemConfig中用到ExListView
 */
public class ExListViewAdapter extends BaseExpandableListAdapter {

    private Context context;

    // 父菜单索引
    public int curGroup;

    // 子菜单索引
    public int curChild;

    private Bitmap mClose, mOpen;

    // 父菜单数据
    private List<String> mParentData = null;

    // 子菜单数据
    private Map<String, List<String>> mChildData = null;

    public ExListViewAdapter(Context context, List<String> mParentData, Map<String, List<String>> mChildData) {
        this.mParentData = mParentData;
        this.mChildData = mChildData;
        this.context = context;
        mOpen = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_open);
        mClose = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_close);
        curGroup = 0;
        curChild = 0;
    }

    public void setPos(int group, int child) {
        curGroup = group;
        curChild = child;
    }

    public Object getChild(int groupPosition, int childPosition) {
        String key = mParentData.get(groupPosition);
        return (mChildData.get(key).get(childPosition));
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 获取子菜单视图
     *
     * @param groupPosition 组号
     * @param childPosition 当前组的位置
     * @param isLastChild   是否是最后的子菜单
     * @param convertView   缓存的View
     * @param parent        父布局
     * @return
     */
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String key = mParentData.get(groupPosition);
        String info = mChildData.get(key).get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_children, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.second_textview);
        tv.setText(info);

        ImageView mImageView = (ImageView) convertView.findViewById(R.id.mChildimage);

        if (curChild == childPosition && curGroup == groupPosition) {
            convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
            mImageView.setImageResource(R.mipmap.ic_selected);
        } else {
            mImageView.setImageResource(R.mipmap.ic_check_off);
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        String key = mParentData.get(groupPosition);
        int size = mChildData.get(key).size();
        return size;
    }

    public Object getGroup(int groupPosition) {
        return mParentData.get(groupPosition);
    }

    public int getGroupCount() {
        return mParentData.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取父菜单布局
     *
     * @param groupPosition 组号
     * @param isExpanded    是否展开
     * @param convertView   缓存的View
     * @param parent        父布局
     * @return
     */
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // 是否存在父布局
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_parent, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.parent_textview);
        tv.setText(mParentData.get(groupPosition));
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.mGroupimage);
        if (!isExpanded) {
            mImageView.setImageBitmap(mClose);
        } else {
            mImageView.setImageBitmap(mOpen);
        }
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
