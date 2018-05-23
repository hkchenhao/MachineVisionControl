package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.adapter.ExListViewAdapter;
import com.hanyu.hust.testnet.ui.fragment.FragmentBarchat;
import com.hanyu.hust.testnet.ui.fragment.FragmentPiechart;
import com.hanyu.hust.testnet.ui.view.EToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by HeQian on 2018/2
 * 统计分析界面
 */

public class StatisticsActivity extends Activity implements View.OnClickListener{
    /**
     * UI组件
     */
    final String TAG ="StatisticsActivity";
    public Fragment mFragment; //碎片组件
    private ExpandableListView mExListViewType ; //左侧统计类型选择栏
    private ExpandableListView mExListViewSrc; //左侧数据来源选择栏
    //统计类型表
    private List<String> mTypeParent = null;
    private Map<String,List<String>> mTypeChild = null;
    private ExListViewAdapter mTypeAdapter;
    //统计数据来源表
    private List<String> mSrcParent = null;
    private Map<String,List<String>> mSrcChild = null;
    private ExListViewAdapter mSrcAdapter;

    private boolean dataFlag = false;
    private int typeFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics);

        initData();

        initView();

        initDefault();

    }

    /**
     * 初始化菜单数据
     */
    public void initData() {

        mTypeParent= new ArrayList<>();
        mTypeParent.add("统计类型");
        mTypeChild = new HashMap<>();
        List<String> typeList = new ArrayList<>();
        typeList.add("数量统计");
        typeList.add("缺陷统计");
        mTypeChild.put("统计类型", typeList);

        mSrcParent= new ArrayList<>();
        mSrcParent.add("数据来源");
        mSrcChild = new HashMap<>();
        List<String> srcList = new ArrayList<>();
        srcList.add("本机当前数据");
        srcList.add("本机历史数据");
        mSrcChild.put("数据来源", srcList);
    }
    /**
    *初始化界面
     *  */
    void  initView(){
        mExListViewType = (ExpandableListView) this.findViewById(R.id.exlist_static_chart_style);
        mExListViewSrc = (ExpandableListView) this.findViewById(R.id.exlist_static_data_src);

        mTypeAdapter = new ExListViewAdapter(this, mTypeParent, mTypeChild);
        mExListViewType.setAdapter(mTypeAdapter);
        mSrcAdapter = new ExListViewAdapter(this, mSrcParent, mSrcChild);
        mExListViewSrc.setAdapter(mSrcAdapter);
        /*统计类型点击事件*/
        mExListViewType.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                mTypeAdapter.setPos(groupPosition, childPosition);
                mTypeAdapter.notifyDataSetChanged();
                Fragment fragment = null;
                if (groupPosition == 0) {
                    switch (childPosition) {
                                                case 0:
                                                    fragment = new FragmentBarchat();
                                                    typeFlag = 0;
                                                    break;
                                                case 1:
                                                    fragment = new FragmentPiechart();
                                                    typeFlag = 1;
                                                    break;
                        default:
                            break;
                    }
                }
                if (mFragment != null) {
                    mFragment = fragment;
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fr_layout_statistics, mFragment);
                    transaction.commit();
                }
                return false;
            }
        });
        /*数据来源选择点击事件*/
            mExListViewSrc.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                mSrcAdapter.setPos(groupPosition, childPosition);
                mSrcAdapter.notifyDataSetChanged();
                if (groupPosition == 0) {
                    switch (childPosition) {
                        case 0:
                            dataFlag = false;
                            break;
                        case 1:
                            dataFlag = true;
                            break;
                        default:
                            break;
                    }
                }

                Fragment fragment = null;

                if(typeFlag == 0)
                    fragment = new FragmentBarchat();
                else if(typeFlag == 1)
                    fragment = new FragmentPiechart();
                else {
                    typeFlag = 0;
                    fragment = new FragmentBarchat();
                }
                if (mFragment != null) {
                    mFragment = fragment;
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fr_layout_statistics, mFragment);
                    transaction.commit();
                }
                return false;
            }
        });

    }

    /**
     * 初始化默认界面
     */
    void initDefault() {
        mExListViewType.expandGroup(0);
        mExListViewSrc.expandGroup(0);
        // TODO: 2018/2/5
        mFragment = new FragmentBarchat();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fr_layout_statistics, mFragment);
        transaction.commit();


    }
    @Override
    public void onClick(View view) {
    }

   public boolean getDataSrc (){
        return dataFlag;
    }
}

