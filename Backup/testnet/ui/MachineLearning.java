package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.adapter.ExListViewAdapter;
import com.hanyu.hust.testnet.entity.SettingProfile;

import com.hanyu.hust.testnet.ui.fragment.FragmentAlgChoose;
import com.hanyu.hust.testnet.ui.fragment.FragmentAlgSetting;
import com.hanyu.hust.testnet.ui.fragment.FragmentButtoInfoBack;
import com.hanyu.hust.testnet.ui.fragment.FragmentButtoInfoFront;
import com.hanyu.hust.testnet.ui.fragment.FragmentGeometryLearning;
import com.hanyu.hust.testnet.ui.fragment.FragmentOptSetting;
import com.hanyu.hust.testnet.ui.fragment.FragmentSelfLearning;
import com.hanyu.hust.testnet.ui.fragment.FragmentSysControl;
import com.hanyu.hust.testnet.ui.fragment.FragmentTaskSize;
import com.hanyu.hust.testnet.ui.fragment.FragmentTradition;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.LogUtil;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * 机器学习
 */
public class MachineLearning extends Activity implements OnClickListener{


    public final static int btnCfgFrontPage = 0;
    public final static int btnCfgBackPage = 1;
    public final static int btnCfgSizePage = 2;
    public final static int btnCfgCtrlPage = 3;
    public final static int algCfgPage = 4;
    public final static int optCfgPage = 5;
    /**
     * 界面控件资源
     */

    private Button bt_return;
    private Button bt_save;

    private TextView tv_status;
    private TextView tv_title;

    private ExListViewAdapter mAdapter;

    /**
     * 左侧一级菜单
     */
    private List<String> mParentData = null;

    /**
     * 左侧二级菜单
     */
    private Map<String, List<String>> mChildData = null;
    /**
     * ListView 控件
     */
    private ExpandableListView mExListview = null;
    /**
     * 右侧视图布局碎片
     */
    public Fragment mFragment;
    private int mFregIndex = 0;

    /**
     * 纽扣配置文件相关
     */
    private String buttonId = "";
    private JSONObject json = new JSONObject();

    /**
     * 纽扣算法配置文件相关
     * */
    private String algCfgFileName = null;

    /**
     * 纽扣ID和状态
     */
    private String status = "";



    /**
     * 纽扣正面图像
     */
    private Bitmap mBitmapF;

    /**
     * 纽扣反面图像
     */
    private Bitmap mBitmapB;
    public Bitmap getmBitmapF() {
        return mBitmapF;
    }
    public void setmBitmapF(Bitmap mBitmapF) {
        this.mBitmapF = mBitmapF;
    }
    public Bitmap getmBitmapB() {
        return mBitmapB;
    }
    public void setmBitmapB(Bitmap mBitmapB) {
        this.mBitmapB = mBitmapB;
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("MachineLearning","onPause");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("MachineLearning","onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("MachineLearning","onResume");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//取消屏幕休眠

        setContentView(R.layout.activity_machine_learn);

        mExListview = (ExpandableListView) this.findViewById(R.id.ex_listview_machine_learn);

        initData();

        Intent intent = getIntent();
        buttonId = intent.getStringExtra(Constants.btnCfgRetStr);
        if (buttonId != null) {
            SettingProfile bp = new SettingProfile(buttonId);
            json = bp.getJsonStr();
            mBitmapF = bp.getBitmap();
            mFregIndex = btnCfgFrontPage;
        } else{
            buttonId = "";
        }
        algCfgFileName = intent.getStringExtra(Constants.algCfgRetStr);
        if(algCfgFileName != null){
            mFregIndex = algCfgPage;
        }
        initView();
        initDefault();

    }

      /**
      * 初始化菜单数据
      */
    public void initData() {

        mParentData = new ArrayList<String>();
        mParentData.add("钮扣配置");
        mParentData.add("算法管理");
        mParentData.add("机器学习");

        mChildData = new HashMap<String, List<String>>();

        List<String> list1 = new ArrayList<String>();
        list1.add("正面基本信息");
        list1.add("反面基本信息");
        list1.add("尺寸测量");
        list1.add("系统控制参数");
        list1.add("算法选择");
        mChildData.put("钮扣配置", list1);

        List<String> list2 = new ArrayList<String>();
        list2.add("算法策略");
        list2.add("算子策略");
        mChildData.put("算法管理", list2);

        List<String> list3 = new ArrayList<String>();
        list3.add("几何自学习");
        list3.add("表面自学习");
        list3.add("传统方法");
        mChildData.put("机器学习", list3);

    }

    void  initView(){
        bt_save = (Button) findViewById(R.id.bt_MachineLearn_save);
        bt_save.setOnClickListener(this);
        bt_return = (Button) findViewById(R.id.bt_MachineLearn_return);
        bt_return.setOnClickListener(this);

        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_title = (TextView) findViewById(R.id.title_button);

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
                     * 纽扣配置
                     */
                    switch (childPosition) {
                        case 0:
                            fragment = new FragmentButtoInfoFront();
                            break;
                        case 1:
                            fragment = new FragmentButtoInfoBack();
                            break;
                        case 2:
                            fragment = new FragmentTaskSize();
                            break;
                        case 3:
                            fragment = new FragmentSysControl();
                            break;
                        case 4:
                            fragment = new FragmentAlgChoose();
                            break;
                        default:
                            break;
                    }
                } else if (groupPosition == 1) {
                    /**
                     * 算法策略
                     */
                    switch (childPosition) {
                        case 0:
                            fragment = new FragmentAlgSetting();
                            break;
                        case 1:
                            fragment = new FragmentOptSetting();
                            break;
                        default:
                            break;
                    }
                } else if (groupPosition == 2) {
                    /**
                     * 机器学习
                     */
                    switch (childPosition) {
                        case 0:
                            fragment = new FragmentGeometryLearning();
                            break;
                        case 1:
                            fragment = new FragmentSelfLearning();
                            break;
                        case 2:
                            fragment = new FragmentTradition();
                            break;
                        default:
                            break;
                    }
                }
                if (mFragment != null) {
                    mFragment = fragment;
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fr_layout_machine_learn, mFragment);
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
        mExListview.expandGroup(0);
        mFragment = new FragmentButtoInfoFront();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fr_layout_machine_learn, mFragment);
        transaction.commit();

        mAdapter.setPos(0,mFregIndex);

    }

//    /**
//     * 写标题
//     */
//    public  void setTitle(String str) {
//        tv_title.setText(str);
//    }
//
    /**
     * 保存状态
     *
     * @param str
     */
    public void writeStatus(String str) {
        tv_status.append(str + "\n");
    }

    /**
     * 配置纽扣基本信息相关
     * */
    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public JSONObject getJson() {
        return json;
    }

    /**
     * 纽扣算法设置相关
     * */
    public String getAlgCfgFileName(){
        return algCfgFileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    /**
     * 设置按键的监听器
     *
     * @param view 点击的按钮视图
     */
    @OnClick({ R.id.bt_MachineLearn_save,R.id.bt_MachineLearn_return})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_MachineLearn_save:
                if (mFragment != null && mFragment instanceof OperationInterface)
                    ((OperationInterface) mFragment).onSave();
                break;
            case R.id.bt_MachineLearn_return:
                finish();
                break;
            default:
                break;
        }
    }

}
