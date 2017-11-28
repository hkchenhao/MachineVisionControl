package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.adapter.ExListViewAdapter;
import com.hanyu.hust.testnet.ui.fragment.FragmentAd9849;
import com.hanyu.hust.testnet.ui.fragment.FragmentAt25040;
import com.hanyu.hust.testnet.ui.fragment.FragmentCameraPara;
import com.hanyu.hust.testnet.ui.fragment.FragmentCan;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanLightSrcDrv;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanMotorCtrlCard;
import com.hanyu.hust.testnet.ui.fragment.FragmentGeneralInfo;
import com.hanyu.hust.testnet.ui.fragment.FragmentIsl12026;
import com.hanyu.hust.testnet.ui.fragment.FragmentLocalNet;
import com.hanyu.hust.testnet.ui.fragment.FragmentModeChoose;
import com.hanyu.hust.testnet.ui.fragment.FragmentMt9v032;
import com.hanyu.hust.testnet.ui.fragment.FragmentNetCard;
import com.hanyu.hust.testnet.ui.fragment.FragmentTrigPara;
import com.hanyu.hust.testnet.ui.fragment.FragmentUart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置
 */
public class SystemConfig extends Activity {

    private ExListViewAdapter mAdapter;
    public int cameraID = 1;
    /**
     * 一级菜单
     */
    private List<String> mParentData = null;

    /**
     * 二级菜单
     */
    private Map<String, List<String>> mChildData = null;

    private ExpandableListView mExListview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*取消屏幕休眠，add by wuxin 170714*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sys_config);

        mExListview = (ExpandableListView) this.findViewById(R.id.main_expandablelistview);

        initData();

        mAdapter = new ExListViewAdapter(this, mParentData, mChildData);
        mExListview.setAdapter(mAdapter);

        mExListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                mAdapter.setPos(groupPosition, childPosition);
                mAdapter.notifyDataSetChanged();
                Fragment fragment = null;
                if (groupPosition == 0) {
                    /**
                     * 调试模式
                     */
                    switch (childPosition) {
                        case 0:
                        case 1:
                        case 2:
                            fragment = new FragmentModeChoose();
                            break;
                        default:
                            break;
                    }
                } else if (groupPosition == 1) {
                    /**
                     * 相机设置
                     */
                    switch (childPosition) {
                        case 0:
                            fragment = new FragmentCameraPara();
                            break;
                        case 1:
                            fragment = new FragmentGeneralInfo();
                            break;
                        case 2:
                            fragment = new FragmentNetCard();
                            break;
                        case 3:
                            fragment = new FragmentCan();
                            break;
                        case 4:
                            fragment = new FragmentUart();
                            break;
                        default:
                            break;
                    }
                } else if (groupPosition == 2) {
                    /**
                     * 系统设置
                     */
                    switch (childPosition) {
                        case 0:
                            fragment = new FragmentTrigPara();
                            break;
                        case 1:
                            fragment = new FragmentLocalNet();
                            break;
                        case 2:
                            fragment = new FragmentAd9849();
                            break;
                        case 3:
                            fragment = new FragmentMt9v032();
                            break;
                        case 4:
                            fragment = new FragmentIsl12026();
                            break;
                        case 5:
                            fragment = new FragmentAt25040();
                            break;
                        default:
                            break;
                    }
                } else if (groupPosition == 3) {
                    /*CAN设备管理界面*/
                    switch (childPosition) {
                        case 0: {
                            fragment = new FragmentCanMotorCtrlCard();
                            break;
                        }
                        case 1: {
                            fragment = new FragmentCanLightSrcDrv();
                            break;
                        }
                        default:
                            break;
                    }

                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.right_layout, fragment);
                    transaction.commit();
                }
                return false;
            }
        });

        initDefault();
    }

    /**
     * 初始化默认界面
     */
    void initDefault() {
        mExListview.expandGroup(0);
        Fragment fragment = new FragmentModeChoose();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.right_layout, fragment);
        transaction.commit();
    }

    /**
     * 生成菜单数据
     */
    public void initData() {
        mParentData = new ArrayList<String>();
        mParentData.add("模式选择");
        mParentData.add("相机设置");
        mParentData.add("系统设置");
        mParentData.add("CAN网络");
        mChildData = new HashMap<String, List<String>>();

        List<String> list1 = new ArrayList<String>();
        list1.add("检测模式");
        list1.add("演示模式");
        list1.add("调试模式");
        mChildData.put("模式选择", list1);

        List<String> list2 = new ArrayList<String>();
        list2.add("图像采集参数");
        list2.add("常规设置");
        list2.add("网卡设置");
        list2.add("CAN设置");
        list2.add("Uart设置");
        mChildData.put("相机设置", list2);

        List<String> list3 = new ArrayList<String>();
        list3.add("触发参数设置"); // 1
        list3.add("本地IP设置");
        list3.add("AD9849");
        list3.add("MT9V032"); // 4
        list3.add("ISL12026"); // 5
        list3.add("AT25040"); // 6
        mChildData.put("系统设置", list3);

        /*20170627新增，用于CAN总线管理*/
        List<String> list4 = new ArrayList<String>();
        list4.add("电机控制卡");
        list4.add("光源控制器");
        mChildData.put("CAN网络", list4);
    }

    public void onSave() {
    }

    public void onApply(View v) {
    }

    public void onExit(View v) {
        finish();
    }
}
