package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.adapter.ExListViewAdapter;
import com.hanyu.hust.testnet.CameraConfigParam.CameraConfigParam;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.fragment.FragmentCameraAd9849Conf;
import com.hanyu.hust.testnet.ui.fragment.FragmentCalibrateLightSrc;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanLightSrcDrv;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanMotorCtrlCard;
import com.hanyu.hust.testnet.ui.fragment.FragmentCameraGeneralInfo;
import com.hanyu.hust.testnet.ui.fragment.FragmentCalibrateCamera;
import com.hanyu.hust.testnet.ui.fragment.FragmentModeChoose;
import com.hanyu.hust.testnet.ui.fragment.FragmentCameraMt9v032;
import com.hanyu.hust.testnet.ui.fragment.FragmentCameraNetConf;
import com.hanyu.hust.testnet.ui.fragment.FragmentCameraTriggerConf;
import com.hanyu.hust.testnet.ui.view.EToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置界面
 */
public class SystemConfig extends Activity {
    public static String TAG = "systemConfig";
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
                            fragment = new FragmentCameraGeneralInfo();
                            break;
                        case 1:
                            fragment = new FragmentCameraTriggerConf();
                            break;
                        case 2:
                            fragment = new FragmentCameraAd9849Conf();
                            break;
                        case 3:
                            fragment = new FragmentCameraMt9v032();
                            break;
                        case 4:
                            fragment = new FragmentCameraNetConf();
                            break;
                        default:
                            break;
                    }
                } else if (groupPosition == 2) {
                    /**CAN设备管理界面*/
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
                } else if (groupPosition == 3) {
                    switch (childPosition) {
                        case 0: {
                            fragment = new FragmentCalibrateCamera();
                            break;
                        }
                        case 1: {
                            fragment = new FragmentCalibrateLightSrc();
                            break;
                        }
                        default:
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
        mParentData.add("CAN网络");
        mParentData.add("系统标定");
        mChildData = new HashMap<>();

        List<String> list1 = new ArrayList<>();
        list1.add("调试模式");
        mChildData.put("模式选择", list1);

        List<String> list2 = new ArrayList<>();
        list2.add("常规设置");
        list2.add("触发设置");
        list2.add("AD9849");
        list2.add("MT9V032");
        list2.add("网卡设置");
        mChildData.put("相机设置", list2);

        /**20170627新增，用于CAN总线管理*/
        List<String> list3 = new ArrayList<>();
        list3.add("电机控制卡");
        list3.add("光源控制器");
        mChildData.put("CAN网络", list3);

        /**20171124新增，用于系统参数标定*/
        List<String> list4 = new ArrayList<String>();
        list4.add("倍率标定");
        list4.add("光源测试");
        mChildData.put("系统标定", list4);
    }

    public void onSave() {
    }

    public void onApply(View v) {
    }

    public void onExit(View v) {
        finish();
    }


    /**
     * 相机的配置文件
     */
    public CameraConfigParam camera1ConfigParam = null;
    public CameraConfigParam camera2ConfigParam = null;
    public CameraConfigParam camera3ConfigParam = null;
    private Handler updateFrHandler = null;
    public String cameraTag = null;

    /**
     * 由其他类调用，获取当前进行设置的相机号
     * @return
     */
    public String getCameraTag(){
        return cameraTag;
    }

    public void setCameraTag(String tag){
        cameraTag = tag;
    }
    public void getCameraConfigParamFromNet(String cam_tag, Handler update_widget_handler) {
        if (AppContext.getAppContext().isExist(cam_tag)) {
            CmdHandle.getInstance().getParam(cam_tag, getCameraParamHandler);
            updateFrHandler = update_widget_handler;
        } else {
            EToast.showToast(this, "相机" + cam_tag + "网络未连接");
        }
    }
    /**
     * 由其他类调用，获取当前进行设置的相机参数
     * @return
     */
    public CameraConfigParam getCameraConfigParam(String tag) {
        if (tag.equals("1")) {
            return camera1ConfigParam;
        } else if (tag.equals("2")) {
            return camera2ConfigParam;
        } else if (tag.equals("3")) {
            return camera3ConfigParam;
        } else {
            return null;
        }
    }

    /**
     * 网络协议包处理handler
     */
    private Handler getCameraParamHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: " + String.valueOf(msg.what));
            if (msg.what == NetUtils.MSG_NET_GET_PARAM) {
                if (msg.arg1 == 1) {
                    camera1ConfigParam = new CameraConfigParam((byte[]) msg.obj);
                } else if (msg.arg1 == 2) {
                    camera2ConfigParam = new CameraConfigParam((byte[]) msg.obj);
                } else if (msg.arg1 == 3) {
                    camera3ConfigParam = new CameraConfigParam((byte[]) msg.obj);
                }
                if (updateFrHandler != null) {
                    Message message = Message.obtain();
                    updateFrHandler.sendMessage(message);
                    if(camera1ConfigParam != null)
                    Log.d(TAG, "handleMessage: "+ camera1ConfigParam.toString());
                }
            }
        }
    };
}
