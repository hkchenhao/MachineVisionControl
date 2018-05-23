package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.can.Can;
import com.hanyu.hust.testnet.can.CanParameter;
import com.hanyu.hust.testnet.entity.ButtonJson;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.DetailInfo;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.task.PingState;
import com.hanyu.hust.testnet.task.PingTask;
import com.hanyu.hust.testnet.task.TCPServerService;
import com.hanyu.hust.testnet.task.TimeTask;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanLightSrcDrv;
import com.hanyu.hust.testnet.ui.fragment.FragmentCanMotorCtrlCard;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.ExcuteTask;
import com.hanyu.hust.testnet.utils.HandlerNetMsg;
import com.hanyu.hust.testnet.utils.MyUtils;
import com.hanyu.hust.testnet.utils.NavigatorUtils;
import com.hanyu.hust.testnet.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hanyu.hust.testnet.utils.Constants.CONNECT_SUCCESS;
import static com.hanyu.hust.testnet.utils.Constants.TIME_TASK;

/**
 * 主界面
 */
public class HomeAcitivity extends Activity {

    /**
     * 是否显示图像标志位
     */
    private volatile boolean isShow = false;

    /**
     * 网络连接监听事件的接口
     */
    PingState mPingListener;


    /**
     * ButterKnife注解框架，相当于tv_qualified1 = findViewById（XXX）
     */

    /**
     * 相机名称以及连接状态显示
     */
    @Bind(R.id.net_link1)
    TextView btn_link1;
    @Bind(R.id.net_link2)
    TextView btn_link2;
    @Bind(R.id.net_link3)
    TextView btn_link3;

    /**
     * 钮扣实时图像显示UI控件
     */
    @Bind(R.id.main_imv_photo1)
    ImageView iv_image1;
    @Bind(R.id.main_imv_photo2)
    ImageView iv_image2;
    @Bind(R.id.main_imv_photo3)
    ImageView iv_image3;
    @Bind(R.id.main_imv_result1)
    ImageView iv_result1;
    @Bind(R.id.main_imv_result2)
    ImageView iv_result2;
    @Bind(R.id.main_imv_result3)
    ImageView iv_result3;

    /**
     * 启停控制按键
     */
    @Bind(R.id.bt_control_start)
    Button btn_start;
    @Bind(R.id.bt_control_stop)
    Button btn_pause;

    /**
     * 钮扣配置信息UI控件
     */
    @Bind(R.id.iv_button_check)
    ImageView iv_check;
    @Bind(R.id.tv_button_id)
    TextView tv_buttonId;
    @Bind(R.id.bt_search)
    Button bt_search;

    @Bind(R.id.realTimeStatus_textView)
    TextView realTimeStatus_textView;
    /**
     * 公用弹框控件
     */
    DialogWindow dialogWindow;
    /**
     * 钮扣尺寸信息UI控件
     */
    TextView tv_detailInfo;
    TextView tv_standardInfo;
    @Bind(R.id.btn_size_info)
    Button btn_size_info;


    /**
     * 钮扣数量信息UI控件
     */
    @Bind(R.id.tv_btn_sum)
    TextView tv_btn_sum;
    @Bind(R.id.tv_btn_pos_sum)
    TextView tv_btn_pos_sum;
    @Bind(R.id.tv_btn_neg_sum)
    TextView tv_btn_neg_sum;

    TextView[] tv_qualified = new TextView[4];
    TextView[] tv_unqualified = new TextView[4];
    TextView[] tv_qualified_rate = new TextView[4];
    TextView[] tv_detected_spd = new TextView[3];

    @Bind(R.id.btn_more)
    Button btn_more;

    /**
     * 点我里面的控制按钮
     */
    @Bind(R.id.btn_other)
    Button btn_other;
    Button btn_display_mode;

    /**
     * 页面下方，五个页面切换按钮
     */
    @Bind(R.id.main_bt_file_manager)
    Button btn_fileManager;
    @Bind(R.id.main_bt_machine_learning)
    Button btn_machineLearn;
    @Bind(R.id.main_bt_statistics)
    Button btn_statistics;
    @Bind(R.id.main_bt_sys_config)
    Button btn_sysConfig;
    @Bind(R.id.main_bt_help)
    Button btn_help;
    @Bind(R.id.main_bt_esc)
    Button btnExit;

    /**
     * 正确和错误的图像资源
     */
    private static Bitmap mBitmapcorrect, mBitmapWrong, mBitmapDefault;

    private DetailInfo sizeInfoFromCamera = null;

    Context mContext;

    /**
     * 三个相机的；连接状态
     */
    private boolean[] cameraConnected = {false, false, false};

    /**
     * 显示模式标志
     */
    static int imageModuleFlag;

    private int[] lastPos = {0, 0, 0};
    private int[] lastNeg = {0, 0, 0};
    private float[] detectedSpd = {0.0f, 0.0f, 0.0f};
    private long lastCalSpdTime = 0;
    private long curTime = 0;
    private final int calSpdTimePeriod = 5000;

    /**
     * 每台相机的统计计数值
     */
    private int pos1, neg1, pos2, neg2, pos3, neg3;//检测计数

    private String rate1, rate2, rate3;//缺陷比例
    private int[] flaw1 = new int[4];//一号相机缺陷类型计数
    private int[] flaw2 = new int[4];//二号相机缺陷类型计数
    private int[] flaw3 = new int[4];//三号相机缺陷类型计数

    static private int[] flawF = new int[8];
    static private int[] countF = new int[6];
    static private int staticsCout = 0;//自动保存次数

    private final byte page_addr = 0x00;
    private final byte fr_addr = 0x01;
    private final byte Pn_DCN = 0x01;
    private final int CMD_Camera_Clear = 0x20;
    private final int CMD_Take_A_Picture = 0x21;
    private final byte Pn_Code_Camera1_Brightness_Err = 0x20;
    private final byte Pn_Code_Camera1_ROI_Err = 0x21;
    private final byte Pn_Code_Camera1_Param_Err = 0x22;
    private final byte Pn_Code_Camera1_Calibrate_Err = 0x23;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**modified by heqian on 2017/9*/
                case TIME_TASK: {
                    /**定时保存数据*/
                    saveStatics();
                    break;
                }
                case CONNECT_SUCCESS: {
                    /** 网络连接成功*/
                    if (msg.arg1 == 1 && cameraConnected[0] == false) {
                        EToast.showToast(getApplicationContext(), "相机1连接成功");
                        cameraConnected[0] = true;
                        btn_link1.setText("相机1");
                        btn_link1.setTextColor(Color.BLUE);
                    } else if (msg.arg1 == 2 && cameraConnected[1] == false) {
                        EToast.showToast(getApplicationContext(), "相机2连接成功");
                        cameraConnected[1] = true;
                        btn_link2.setText("相机2");
                        btn_link2.setTextColor(Color.BLUE);
                    } else if (msg.arg1 == 3 && cameraConnected[2] == false) {
                        EToast.showToast(getApplicationContext(), "相机3连接成功");
                        cameraConnected[2] = true;
                        btn_link3.setText("相机3");
                        btn_link3.setTextColor(Color.BLUE);
                    }
                    break;
                }
                case NetUtils.MSG_NET_TOTAL_CNT: {
                    /**计数处理*/
                    parseCount(msg);
                    break;
                }
                case NetUtils.MSG_NET_ALG_IMAGE: {
                    /**ROI图像显示*/
                    parseROI(msg);
                    break;
                }
                case NetUtils.MSG_DETAIL_INFO: {
                    /**详细信息解析*/
                    sizeInfoFromCamera = (DetailInfo) msg.obj;
                    if (tv_detailInfo != null) {
                        if (AppContext.btnCfgDetail != null) {
                            tv_standardInfo.setText(AppContext.btnCfgDetail.sizeInfoToString());
                        }
                        if (sizeInfoFromCamera != null) {
                            tv_detailInfo.setText(sizeInfoFromCamera.toString());
                        }
                    }
                    break;
                }
                //// TODO: 2018/1/31
                case NetUtils.MSG_NET_DETECT_INFO: {
                    byte[] data = (byte[]) msg.obj;
                    /**详细信息解析*/
                    String str = new String(Arrays.copyOfRange(data,
                                91, data.length));
                        try {
                            JsonParser jParser = new JsonParser();
                            Gson gson = new Gson();
                            DetailInfo simpleInfo = gson.fromJson(jParser.parse(str)
                                    .getAsJsonObject().toString(), DetailInfo.class);
                            sizeInfoFromCamera = simpleInfo;
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    if (tv_detailInfo != null) {
                        if (AppContext.btnCfgDetail != null) {
                            tv_standardInfo.setText(AppContext.btnCfgDetail.sizeInfoToString());
                        }
                        if (sizeInfoFromCamera != null) {
                            tv_detailInfo.setText(sizeInfoFromCamera.toString());
                        }
                    }
                    //解析计数
                    msg.obj =  Arrays.copyOfRange(data, 0, 91);
                    parseCount(msg);
                    break;

                }

                case NetUtils.MSG_BTN_CFG_JSON: {
                    /**相机已收到钮扣配置文件*/
                    EToast.showToast(getApplicationContext(), "相机已收到钮扣配置文件");
                    break;
                }
                case NetUtils.MSG_HEART_BEAT: {
                    if (msg.arg1 <= Constants.cameraList.length) {
                        byte[] data = new byte[8];
                        CmdHandle.getInstance().sendHeartBeatAck(Constants.cameraList[msg.arg1 - 1], data);
                    } else {
                        EToast.showToast(getApplicationContext(), "心跳包 相机ID解析错误");
                    }
                }
                case NetUtils.MSG_NET_CMD: {
                    byte[] data = (byte[]) msg.obj;
                    if ((data[0] == page_addr) && (data[1] == fr_addr)) {
                        byte[] pn_code = NetUtils.getNetCmdPnCode(data);
                        int[] param = NetUtils.getNetCmdParam(data);
                        if ((pn_code != null) && (pn_code.length == param.length)) {
                            for (int i = 0; i < pn_code.length; i++) {
                                if (pn_code[i] == Pn_Code_Camera1_Brightness_Err) {
                                    if (param[i] > 0) {
                                        EToast.showToast(getApplicationContext(), "相机" + msg.arg1 + "光照不适，请调整光照！！！");
                                    }
                                } else if (pn_code[i] == Pn_Code_Camera1_Calibrate_Err) {
                                    if (param[i] > 0) {
                                        EToast.showToast(getApplicationContext(), "相机" + msg.arg1 + "未读取到标定因子，请先标定！！！");
                                    }
                                } else if (pn_code[i] == Pn_Code_Camera1_Param_Err) {
                                    if (param[i] > 0) {
                                        EToast.showToast(getApplicationContext(), "相机" + msg.arg1 + "未读取到分类器参数，请先运行学习任务！！！");
                                    }
                                } else if (pn_code[i] == Pn_Code_Camera1_ROI_Err) {
                                    if (param[i] > 0) {
                                        EToast.showToast(getApplicationContext(), "相机" + msg.arg1 + "ROI提取失败！！！");
                                    }
                                } else {
                                    EToast.showToast(getApplicationContext(), "相机" + msg.arg1 + "Pn_Code:" + pn_code[i] + "未定义" + "param:" + param[i]);
                                }
                            }
                        }
                    }
                    break;
                }
                default:
                    Log.d("nettesthome", "handleMessage:" + msg.what);
                    break;
            }
        }
    };

    /**
     * 保存计数值到preference中
     */
    //7.19hq
    private void saveCount() {
        /**检测结果统计数据保存*/
        PreferencesUtils.putInt(this, "neg1", neg1);
        PreferencesUtils.putInt(this, "pos1", pos1);
        PreferencesUtils.putInt(this, "neg2", neg2);
        PreferencesUtils.putInt(this, "pos2", pos2);
        PreferencesUtils.putInt(this, "neg3", neg3);
        PreferencesUtils.putInt(this, "pos3", pos3);
        PreferencesUtils.putString(this, "rate1", rate1);
        PreferencesUtils.putString(this, "rate2", rate2);
        PreferencesUtils.putString(this, "rate3", rate3);
        /**几何缺陷统计数据保存__一号相机*/
        PreferencesUtils.putInt(this, "DEF_EDGE", flaw1[0]);//崩边
        PreferencesUtils.putInt(this, "DEF_HOLE", flaw1[1]);//堵孔->线孔变形
        PreferencesUtils.putInt(this, "DEF_GEOM", flaw1[2]);//几何偏差
        PreferencesUtils.putInt(this, "DEF_FORE", flaw1[3]);//异物
        /**表面缺陷统计数据保存__二号&三号相机*/
        PreferencesUtils.putInt(this, "DEF_PITS", flaw2[0] + flaw3[0]);//表面破损
        PreferencesUtils.putInt(this, "DEF_BLOB", flaw2[1] + flaw3[1]);//气泡
        PreferencesUtils.putInt(this, "DEF_UNFI", flaw2[2] + flaw3[2]);//不均匀
        PreferencesUtils.putInt(this, "DEF_OTHR", flaw2[3] + flaw3[3]);//其他

    }

    /**
     * 保存统计数据
     * reated by heqian on 2017/9
     */
    private void saveStatics() {
        if (AppContext.btnCfgProfile == null) {
            return;/**没有选择钮扣直接返回*/
        }
        final JSONObject STAjson = AppContext.btnCfgProfile.getStatics();
        int[] count = new int[6];
        int[] flaw = new int[8];
        /**解析JSON*/
        if (staticsCount() && STAjson != null) {
            try {
                JSONObject jsonCount = STAjson.getJSONObject(ButtonKeys.Count);
                JSONObject jsonFlaw = STAjson.getJSONObject(ButtonKeys.Flaw);
                for (int i = 0; i < ButtonKeys.countS.length; i++) {
                    countF[i] = jsonCount.getInt(ButtonKeys.countS[i]);
                }
                for (int i = 0; i < ButtonKeys.flawS.length; i++) {
                    flawF[i] = jsonFlaw.getInt(ButtonKeys.flawS[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                userShowToast("钮扣统计数据 读取统计JSON文件出错");
                return;
            }
        }

        count[0] = pos1 + countF[0];
        count[1] = neg1 + countF[1];
        count[2] = pos2 + countF[2];
        count[3] = neg2 + countF[3];
        count[4] = pos3 + countF[4];
        count[5] = neg3 + countF[5];
        for (int i = 0; i < 4; i++) {
            flaw[i] = flaw1[i] + flawF[i];
            flaw[i + 4] = flaw2[i] + flaw3[i] + flawF[i + 4];
        }

        /**写入JSON*/
        JSONObject jsonTime = new JSONObject();
        JSONObject jsonCount = new JSONObject();
        JSONObject jsonFlaw = new JSONObject();

        try {
            for (int i = 0; i < ButtonKeys.countS.length; i++) {
                jsonCount.put(ButtonKeys.countS[i], count[i]);
            }
        } catch (JSONException e) {
            userShowToast("钮扣统计数据 自动保存计数出错");
            e.printStackTrace();
            return;
        }
        try {
            for (int i = 0; i < ButtonKeys.flawS.length; i++) {
                jsonFlaw.put(ButtonKeys.flawS[i], flaw[i]);
            }
        } catch (JSONException e) {
            userShowToast("钮扣统计数据 自动保存缺陷出错");
            e.printStackTrace();
            return;
        }
        try {
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date(System.currentTimeMillis());
            if (STAjson == null)
                jsonTime.put("firstStaticsTime", myFmt.format(now).toString());
            else {
                String str = STAjson.getJSONObject(ButtonKeys.STAtime).getString("firstStaticsTime");
                jsonTime.put("firstStaticsTime", str);
            }
            jsonTime.put("lastStaticsTime", myFmt.format(now).toString());
        } catch (JSONException e) {
            userShowToast("钮扣统计数据 自动保存时间出错");
            e.printStackTrace();
            return;
        }
        try {
            JSONObject TOT = new JSONObject();
            TOT.put(ButtonKeys.STAtime, jsonTime);
            TOT.put(ButtonKeys.Flaw, jsonFlaw);
            TOT.put(ButtonKeys.Count, jsonCount);
            AppContext.btnCfgProfile.writeStatics(TOT);
            userShowToast("钮扣统计数据 保存成功");

        } catch (JSONException e) {
            userShowToast("钮扣统计数据 保存失败");
            e.printStackTrace();
            return;
        }
    }

    /**
     * 统计数据清零方法
     */
    private void dataClear() {
        pos1 = 0;
        neg1 = 0;
        pos2 = 0;
        neg2 = 0;
        pos3 = 0;
        neg3 = 0;
        for (int i = 0; i < 4; i++) {
            flaw1[i] = 0;
            flaw2[i] = 0;
            flaw3[i] = 0;
        }
        for (int i = 0; i < flawF.length; i++)
            flawF[i] = 0;
        for (int i = 0; i < countF.length; i++)
            countF[i] = 0;
    }

    private boolean staticsCount() {
        if (staticsCout == 0) {
            staticsCout = staticsCout + 1;
            return true;
        } else {
            staticsCout = staticsCout + 1;
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /**取消屏幕休眠，add by wuxin 170714*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_home_acitivity);
        ButterKnife.bind(this);
        mContext = this;
        init();

        /**初始化网络监听线程*/
        AppContext.mPingTask = new Thread(new PingTask(mPingListener));
        AppContext.mPingTask.setName("PingTask");
        AppContext.mPingTask.start();

        /**初始化TCP服务器线程*/
        AppContext.mTcpServerTask = new TCPServerService(mHandler).setThreadName("TCPServerService");
        AppContext.mTcpServerTask.start();

        /**初始化CAN线程*/
        AppContext.can0 = new Can();

        /**重置主界面*/
        reset();
    }

    @Override
    protected void onRestart() {
        /**将handler 切换回主界面*/
        AppContext.setHandler(mHandler);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                isShow = false;
                reset();
            }
        });
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.removeLinks();
        AppContext.can0.close();
        finish();
    }

    void init() {
        /**
         * 启动定时任务
         */
        AppContext.timeTask = new TimeTask(mHandler);

        /**
         * 重置IP，非常重要！！！当重启或者连接异常时IP值会恢复默认，需要重新设置
         */
        NetUtils.setIp();

        mBitmapcorrect = HandlerNetMsg.decodeSampledBitmapFromResource(getResources(), R.mipmap.ic_correct, 100, 100);
        mBitmapWrong = HandlerNetMsg.decodeSampledBitmapFromResource(getResources(), R.mipmap.ic_wrong, 100, 100);
        mBitmapDefault = HandlerNetMsg.decodeSampledBitmapFromResource(getResources(), R.mipmap.ic_default, 100, 100);

        /**
         * 设置监听事件
         */
        mPingListener = new PingState() {
            @Override
            public void fail(String socketAddress, final String tag) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        EToast.showToast(getApplicationContext(), "相机" + tag + "网络异常");
                        AppContext.removeLink(tag);
                        if (tag == "1") {
                            cameraConnected[0] = false;
                            btn_link1.setText("相机1 未连接");
                            btn_link1.setTextColor(Color.GRAY);
                        } else if (tag == "2") {
                            cameraConnected[1] = false;
                            btn_link2.setText("相机2 未连接");
                            btn_link2.setTextColor(Color.GRAY);
                        } else if (tag == "3") {
                            cameraConnected[2] = false;
                            btn_link3.setText("相机3 未连接");
                            btn_link3.setTextColor(Color.GRAY);
                        }
                    }
                });
            }

            @Override
            public void success() {
            }
        };
    }

    /**
     * 设置按键的监听器
     *
     * @param view 点击的按钮视图
     */
    @OnClick({R.id.main_bt_file_manager, R.id.main_bt_statistics, R.id.main_bt_sys_config,
            R.id.main_bt_help, R.id.main_bt_machine_learning, R.id.main_bt_esc,
            R.id.bt_control_start, R.id.bt_control_stop,
            R.id.bt_search, R.id.btn_size_info, R.id.btn_more, R.id.btn_other,
            R.id.main_imv_photo1, R.id.main_imv_photo2, R.id.main_imv_photo3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_bt_file_manager: {
                btn_fileManager.setEnabled(false);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setTitle("是否确定进入“文件管理”页面？\n" +
                                "如已确定，请点击“确定”\n" +
                                "如不需要，请点击“取消”")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavigatorUtils.toFileManager(getContext());
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        btn_fileManager.setEnabled(true);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.main_bt_sys_config: {
                btn_sysConfig.setEnabled(false);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_password, null);
                final EditText password_input = (EditText) view1.findViewById(R.id.password_editText);
                password_input.setText(Constants.DefaultPassword);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setView(view1)
                        .setTitle("是否确定进入“系统设置”页面？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (password_input.getText().toString().equals(Constants.Password)){
                                    NavigatorUtils.toSystemConfig(getContext());
                                }else{
                                    password_input.setText("");
                                    EToast.showToast(HomeAcitivity.this, "口令错误，请重新输入");
                                }
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        btn_sysConfig.setEnabled(true);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.main_bt_statistics: {
                btn_statistics.setEnabled(false);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setTitle("是否确定进入“统计分析”页面？\n" +
                                "如已确定，请点击“确定”\n" +
                                "如不需要，请点击“取消”")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveCount();//7.19hq
                                NavigatorUtils.toStatistics(getContext());
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        btn_statistics.setEnabled(true);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.main_bt_machine_learning: {
                btn_machineLearn.setEnabled(false);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_password, null);
                final EditText password_input = (EditText) view1.findViewById(R.id.password_editText);
                password_input.setText(Constants.DefaultPassword);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setView(view1)
                        .setTitle("是否确定进入“机器学习”页面？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (password_input.getText().toString().equals(Constants.Password)){
                                    NavigatorUtils.toMachineLearn(getContext());
                                }else{
                                    password_input.setText("");
                                    EToast.showToast(HomeAcitivity.this, "口令错误，请重新输入");
                                }
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        btn_machineLearn.setEnabled(true);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.main_bt_help: {
                btn_help.setEnabled(false);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setTitle("是否确定进入“帮助”页面？\n" +
                                "如已确定，请点击“确定”\n" +
                                "如不需要，请点击“取消”")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavigatorUtils.toHelp(getContext());
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        btn_help.setEnabled(true);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.main_bt_esc: {
                btnExit.setEnabled(false);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setTitle("是否确定退出？\n" +
                                "如已确定，请点击“确定”\n" +
                                "如不需要，请点击“取消”")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EToast.showToast(HomeAcitivity.this, "您可以关闭系统电源了!");
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        btnExit.setEnabled(true);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.bt_control_start: {
                /**“启动按钮”*/
                startBtnFunc();
                break;
            }
            case R.id.bt_control_stop: {
                /**暂停按钮*/
                stopBtnFunc();
                break;
            }
            case R.id.main_imv_photo1:
            case R.id.main_imv_photo2:
            case R.id.main_imv_photo3: {
                isShow = !isShow;
                reset();
                break;
            }
            /**钮扣基本信息配置*/
            case R.id.bt_search: {
                bt_search.setEnabled(false);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setTitle("是否确定进入“钮扣选择”页面？\n" +
                                "如已确定，请点击“确定”\n" +
                                "如不需要，请点击“取消”")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavigatorUtils.toButtonSearch(getContext());
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        bt_search.setEnabled(true);
                    }
                });
                dialog.show();
                break;
            }

            case R.id.btn_size_info: {
                /**“尺寸信息”弹框*/
                btnSizeInfoDialogShow();
                break;
            }
            case R.id.btn_more: {
                /**“数量信息”弹框*/
                btnMoreDialogShow();
                break;
            }
            case R.id.btn_other: {
                /**“其他设置”弹框*/
                btnOtherDialogShow();
                break;
            }
            default:
                break;
        }
    }

    /**
     * android 框架回调事件
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        传输数据
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**设置成功之后处理设置的数据*/
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                /**从钮扣配置选择界面返回*/
                case Constants.REQUEST_CODE_BTN_CFG: {
                    String fileName = data.getStringExtra(Constants.btnCfgRetStr);
                    if (fileName != null) {
                        AppContext.btnCfgProfile = new SettingProfile(fileName);
                        AppContext.btnCfgJson = AppContext.btnCfgProfile.getJsonStr();
                        AppContext.btnCfgDetail = ButtonJson.getButtonJson(AppContext.btnCfgJson);
                        AppContext.btnBitmapF = AppContext.btnCfgProfile.getBitmap();
                        AppContext.btnBitmapB = AppContext.btnCfgProfile.getBitmapBack();
                        AppContext.btnBitmapGeo = MyUtils.convertGreyImg(AppContext.btnBitmapF);
                        AppContext.btnBitmapGeoB = MyUtils.convertGreyImg(AppContext.btnBitmapB);
                        tv_buttonId.setText(fileName);
                        iv_check.setImageBitmap(AppContext.btnCfgProfile.getBitmap());
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * 返回主界面上下文
     *
     * @return
     */
    private Context getContext() {
        return mContext;
    }

    /**
     * 计算合格率
     *
     * @param qualified   合格数
     * @param unqualified 不合格数
     * @return 合格率
     */
    private String calRate(int qualified, int unqualified) {
        DecimalFormat df = new DecimalFormat("#0.00");
        if ((qualified + unqualified) == 0) {
            return "0.00%";
        } else {
            String result = df.format(qualified / (float) (unqualified + qualified) * 100) + "%";
            return result;
        }
    }


    /**
     * 复位，让主界面恢复默认图像
     */
    private void reset() {
        iv_image1.setImageResource(R.mipmap.qualified);
        iv_image2.setImageResource(R.mipmap.qualified);
        iv_image3.setImageResource(R.mipmap.qualified);

        iv_result1.setImageBitmap(mBitmapDefault);
        iv_result2.setImageBitmap(mBitmapDefault);
        iv_result3.setImageBitmap(mBitmapDefault);
    }

    /**
     * 返回无符号数
     *
     * @param data
     * @return
     */
    private int getUnsigned(byte data) {
        return data & 0x0FF;
    }

    /**
     * 解析计数值
     *
     * @param msg
     */
    private void parseCount(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null || (data.length - 1) % 5 != 0)
            return;
        //// TODO: 2017/11/20
        int result = data[0];
        if (result == 0) {
            /**当前为正品*/
            showDefaultImage(msg.arg1, imageModuleFlag);
            if (msg.arg1 == 1) {
                iv_result1.setImageBitmap(mBitmapcorrect);
            } else if (msg.arg1 == 2) {
                iv_result2.setImageBitmap(mBitmapcorrect);
            } else if (msg.arg1 == 3) {
                iv_result3.setImageBitmap(mBitmapcorrect);
            }
        } else if (result == 1) {
            /**当前为次品*/
            if (msg.arg1 == 1) {
                iv_result1.setImageBitmap(mBitmapWrong);
            } else if (msg.arg1 == 2) {
                iv_result2.setImageBitmap(mBitmapWrong);
            } else if (msg.arg1 == 3) {
                iv_result3.setImageBitmap(mBitmapWrong);
            }
        }

        int datalength = (data.length - 1) / 5;
        int type;
        int count;
        for (int i = 0; i < datalength; i++) {
            type = data[5 * i + 1];
            count = (getUnsigned(data[5 * i + 2])) | (getUnsigned(data[5 * i + 3]) << 8)
                    | (getUnsigned(data[5 * i + 4]) << 16) | (getUnsigned(data[5 * i + 5]) << 24);
            //字段解析
            switch (type) {
                case 0: {
                    if (msg.arg1 == 1) {
                        pos1 = count;
                    } else if (msg.arg1 == 2) {
                        pos2 = count;
                    } else if (msg.arg1 == 3) {
                        pos3 = count;
                    }
                    break;
                }
                case 1: {
                    if (msg.arg1 == 1) {
                        neg1 = count;
                    } else if (msg.arg1 == 2) {
                        neg2 = count;
                    } else if (msg.arg1 == 3) {
                        neg3 = count;
                    }
                    break;
                }
                /**以下为缺陷类型计数解析，相关定义详见saveCount方法或网络协议doc*/
                case 0x21: {
                    flaw1[0] = count;
                    break;
                }
                case 0x22: {
                    flaw1[1] = count;
                    break;
                }
                case 0x23: {
                    flaw1[2] = count;
                    break;
                }
                case 0x24: {
                    flaw1[3] = count;
                    break;
                }
                case 0x25: {
                    if (msg.arg1 == 2)
                        flaw2[0] = count;
                    else if (msg.arg1 == 3)
                        flaw3[0] = count;
                    break;
                }
                case 0x26: {
                    if (msg.arg1 == 2)
                        flaw2[1] = count;
                    else if (msg.arg1 == 3)
                        flaw3[1] = count;
                    break;
                }
                case 0x27: {
                    if (msg.arg1 == 2)
                        flaw2[2] = count;
                    else if (msg.arg1 == 3)
                        flaw3[2] = count;
                    break;
                }
                case 0x28: {
                    if (msg.arg1 == 2)
                        flaw2[3] = count;
                    else if (msg.arg1 == 3)
                        flaw3[3] = count;
                    break;
                }
            }
        }

        /***/
        int pos_sum = pos3;
        int neg_sum = neg1 + neg2 + neg3;
        int btn_sum = pos_sum + neg_sum;
        tv_btn_pos_sum.setText("正品:" + pos_sum);
        tv_btn_neg_sum.setText("次品:" + neg_sum);
        tv_btn_sum.setText("总计:" + btn_sum);

        /**检测速度计算*/
        curTime = System.currentTimeMillis();
        if ((curTime - lastCalSpdTime) > calSpdTimePeriod) {
            if (lastCalSpdTime == 0) {
                detectedSpd[0] = ((neg1 + pos1 - lastNeg[0] - lastPos[0]) * 1000.0f) / calSpdTimePeriod;
                detectedSpd[1] = ((neg2 + pos2 - lastNeg[1] - lastPos[1]) * 1000.0f) / calSpdTimePeriod;
                detectedSpd[2] = ((neg3 + pos3 - lastNeg[2] - lastPos[2]) * 1000.0f) / calSpdTimePeriod;
            } else {
                detectedSpd[0] = ((neg1 + pos1 - lastNeg[0] - lastPos[0]) * 1000.0f) / (curTime - lastCalSpdTime);
                detectedSpd[1] = ((neg2 + pos2 - lastNeg[1] - lastPos[1]) * 1000.0f) / (curTime - lastCalSpdTime);
                detectedSpd[2] = ((neg3 + pos3 - lastNeg[2] - lastPos[2]) * 1000.0f) / (curTime - lastCalSpdTime);
            }

            for (int i = 0; i < detectedSpd.length; i++) {
                if (detectedSpd[i] < 0.0f) {
                    detectedSpd[i] = 0.0f;
                }
            }

            lastCalSpdTime = curTime;
            lastNeg[0] = neg1;
            lastNeg[1] = neg2;
            lastNeg[2] = neg3;
            lastPos[0] = pos1;
            lastPos[1] = pos2;
            lastPos[2] = pos3;
        }

        /**判断是否在弹框中对数量信息进行显示*/
        if (btnMoreDialogOpened && (tv_qualified_rate[3] != null)) {
            tv_qualified[0].setText("正品:" + pos1);
            tv_unqualified[0].setText("次品:" + neg1);
            tv_qualified_rate[0].setText("合格率:" + calRate(pos1, neg1));
            tv_qualified[1].setText("正品:" + pos2);
            tv_unqualified[1].setText("次品:" + neg2);
            tv_qualified_rate[1].setText("合格率:" + calRate(pos2, neg2));
            tv_qualified[2].setText("正品:" + pos3);
            tv_unqualified[2].setText("次品:" + neg3);
            tv_qualified_rate[2].setText("合格率:" + calRate(pos3, neg3));
            tv_qualified[3].setText("总正品:" + pos_sum);
            tv_unqualified[3].setText("总次品:" + neg_sum);
            tv_qualified_rate[3].setText("总合格率:" + calRate(pos_sum, neg_sum));

            DecimalFormat df = new DecimalFormat("#0.0");
            tv_detected_spd[0].setText("检测速度:" + df.format(detectedSpd[0]) + "颗/秒");
            tv_detected_spd[1].setText("检测速度:" + df.format(detectedSpd[1]) + "颗/秒");
            tv_detected_spd[2].setText("检测速度:" + df.format(detectedSpd[2]) + "颗/秒");
        }
    }

    /**
     * 解析ROI图像
     *
     * @param msg
     */
    private void parseROI(Message msg) {
        byte[] packageData = (byte[]) msg.obj;
        if (packageData == null || packageData.length <= 8)
            return;

        int[] data = new int[8];
        for (int i = 0; i < 8; i++) {
            data[i] = packageData[i] & 0xFF;
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8;
        int height = data[6] | data[7] << 8;
        if (len <= 0 || width <= 0 || height <= 0)
            return;

        Bitmap bm = null;
        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {
            bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            int[] image = new int[len];

            if ((len == width * height)) {
                int temp;
                for (int i = 0; i < len; i++) {
                    temp = packageData[i + 8] & 0xff;
                    image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
                }
            } else if (len == 3 * width * height) {
                for (int i = 0; i < len / 3; i++) {
                    int r = packageData[8 + i * 3] & 0xff;
                    int g = packageData[8 + i * 3 + 1] & 0xff;
                    int b = packageData[8 + i * 3 + 2] & 0xff;
                    image[i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            } else
                return;
            bm.setPixels(image, 0, width, 0, 0, width, height);
            if (msg.arg1 == 1) {
                iv_image1.setImageBitmap(bm);
            } else if (msg.arg1 == 2) {
                iv_image2.setImageBitmap(bm);
            } else if (msg.arg1 == 3) {
                iv_image3.setImageBitmap(bm);
            }
        } else
            return;
    }


    private void showDefaultImage(int cam, int module) {
        switch (module) {
            case 0:
                break;
            case 1:
                if (cam == 1) {
                    setIv(cam, AppContext.btnBitmapGeo);
                } else {
                    setIv(cam, AppContext.btnBitmapF);
                }
                break;
            case 2:
                break;
            default:
                break;
        }
        //// TODO: 2017/11/20
    }

    private void setIv(int cam, Bitmap bm) {
        switch (cam) {
            case 1:
                iv_image1.setImageBitmap(bm);
                break;
            case 2:
                iv_image2.setImageBitmap(bm);
                break;
            case 3:
                iv_image3.setImageBitmap(bm);
                break;
            default:
                break;
        }
    }


    /**
     * “尺寸信息”弹窗显示的处理函数
     */

    private void btnSizeInfoDialogShow() {
        if (AppContext.btnCfgProfile == null) {
            EToast.showToast(HomeAcitivity.this, Constants.NoButtonCfgFileWarnning);
            return;
        }
        /**弹框显示钮扣尺寸信息界面*/
        btn_size_info.setEnabled(false);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_btn_size_info, null);
        tv_detailInfo = (TextView) view1.findViewById(R.id.json_real);
        tv_standardInfo = (TextView) view1.findViewById(R.id.json_standard);
        dialogWindow = new DialogWindow.Builder(HomeAcitivity.this)
                .setTitle("钮扣尺寸信息")
                .setView(view1)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialogWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                btn_size_info.setEnabled(true);
            }
        });
        dialogWindow.showWrapContent();

        if (AppContext.btnCfgDetail == null) {
            tv_standardInfo.setText("未选择钮扣");
        } else {
            tv_standardInfo.setText(AppContext.btnCfgDetail.sizeInfoToString());
        }
        if (sizeInfoFromCamera == null) {
            tv_detailInfo.setText("未测试钮扣");
        } else {
            tv_detailInfo.setText(sizeInfoFromCamera.toString());
        }
    }

    /**
     * “数量信息”弹窗显示的处理函数
     */
    private boolean btnMoreDialogOpened = false;

    private void btnMoreDialogShow() {
        btn_more.setEnabled(false);
        btnMoreDialogOpened = true;
        View view1 = getLayoutInflater().inflate(R.layout.dialog_btn_more, null);
        tv_qualified[0] = (TextView) view1.findViewById(R.id.tv_qualified1);
        tv_qualified[1] = (TextView) view1.findViewById(R.id.tv_qualified2);
        tv_qualified[2] = (TextView) view1.findViewById(R.id.tv_qualified3);
        tv_qualified[3] = (TextView) view1.findViewById(R.id.tv_qualified_total);

        tv_unqualified[0] = (TextView) view1.findViewById(R.id.tv_unqualified1);
        tv_unqualified[1] = (TextView) view1.findViewById(R.id.tv_unqualified2);
        tv_unqualified[2] = (TextView) view1.findViewById(R.id.tv_unqualified3);
        tv_unqualified[3] = (TextView) view1.findViewById(R.id.tv_unqualified_total);

        tv_qualified_rate[0] = (TextView) view1.findViewById(R.id.tv_qualified_rate1);
        tv_qualified_rate[1] = (TextView) view1.findViewById(R.id.tv_qualified_rate2);
        tv_qualified_rate[2] = (TextView) view1.findViewById(R.id.tv_qualified_rate3);
        tv_qualified_rate[3] = (TextView) view1.findViewById(R.id.tv_qualified_rate_total);

        tv_detected_spd[0] = (TextView) view1.findViewById(R.id.tv_detect_spd_1);
        tv_detected_spd[1] = (TextView) view1.findViewById(R.id.tv_detect_spd_2);
        tv_detected_spd[2] = (TextView) view1.findViewById(R.id.tv_detect_spd_3);

        /**界面初始化*/
        tv_qualified[0].setText("正品:" + pos1);
        tv_unqualified[0].setText("次品:" + neg1);
        tv_qualified_rate[0].setText("合格率:" + calRate(pos1, neg1));
        tv_qualified[1].setText("正品:" + pos2);
        tv_unqualified[1].setText("次品:" + neg2);
        tv_qualified_rate[1].setText("合格率:" + calRate(pos2, neg2));
        tv_qualified[2].setText("正品:" + pos3);
        tv_unqualified[2].setText("次品:" + neg3);
        tv_qualified_rate[2].setText("合格率:" + calRate(pos3, neg3));

        DecimalFormat df = new DecimalFormat("#0.0");
        tv_detected_spd[0].setText("检测速度:" + df.format(detectedSpd[0]) + "颗/秒");
        tv_detected_spd[1].setText("检测速度:" + df.format(detectedSpd[1]) + "颗/秒");
        tv_detected_spd[2].setText("检测速度:" + df.format(detectedSpd[2]) + "颗/秒");

        int pos_sum = pos3;
        int neg_sum = neg1 + neg2 + neg3;

        tv_qualified[3].setText("总正品:" + pos_sum);
        tv_unqualified[3].setText("总次品:" + neg_sum);
        tv_qualified_rate[3].setText("总合格率:" + calRate(pos_sum, neg_sum));

        dialogWindow = new DialogWindow.Builder(HomeAcitivity.this)
                .setTitle("钮扣数量信息")
                .setView(view1)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialogWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                btnMoreDialogOpened = false;
                btn_more.setEnabled(true);
            }
        });
        dialogWindow.showWrapContent(800);

    }

    /**
     * “其他设置”窗口显示的处理函数
     */
    private void btnOtherDialogShow() {
        btn_other.setEnabled(false);
        /**更改终端的图像显示模式*/
        View view1 = getLayoutInflater().inflate(R.layout.dialog_btn_other, null);
        btn_display_mode = (Button) view1.findViewById(R.id.btn_display_mode);
        if (imageModuleFlag == 0) {
            btn_display_mode.setText("发送全部图像");
        } else if (imageModuleFlag == 1) {
            btn_display_mode.setText("只发送错误图像");
        } else if (imageModuleFlag == 2) {
            btn_display_mode.setText("不发送图像");
        }

        btn_display_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2017/11/20
                changeTerminalDisplayMode();
            }
        });

        /**相机1空拍键*/
        Button btn_create_btn_pulse1 = (Button) view1.findViewById(R.id.btn_create_btn_pulse1);
        btn_create_btn_pulse1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Take_A_Picture);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("1", data);
                }
            }
        });

        /**相机2空拍键*/
        Button btn_create_btn_pulse2 = (Button) view1.findViewById(R.id.btn_create_btn_pulse2);
        btn_create_btn_pulse2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Take_A_Picture);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("2", data);
                }
            }
        });

        /**相机3空拍键*/
        Button btn_create_btn_pulse3 = (Button) view1.findViewById(R.id.btn_create_btn_pulse3);
        btn_create_btn_pulse3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Take_A_Picture);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("3", data);
                }
            }
        });

        /**所有相机空拍键*/
        Button btn_create_btn_pulse_all = (Button) view1.findViewById(R.id.btn_create_btn_pulse_all);
        btn_create_btn_pulse_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Take_A_Picture);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("1", data);
                    CmdHandle.getInstance().sendCmdInfo("2", data);
                    CmdHandle.getInstance().sendCmdInfo("3", data);
                }
            }
        });

        /**相机1清零键*/
        Button btn_camera1_clear = (Button) view1.findViewById(R.id.btn_camera1_clear);
        btn_camera1_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Camera_Clear);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("1", data);
                }
            }
        });

        /**相机2清零键*/
        Button btn_camera2_clear = (Button) view1.findViewById(R.id.btn_camera2_clear);
        btn_camera2_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Camera_Clear);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("2", data);
                }
            }
        });

        /**相机3清零键*/
        Button btn_camera3_clear = (Button) view1.findViewById(R.id.btn_camera3_clear);
        btn_camera3_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Camera_Clear);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("3", data);
                }
            }
        });

        /**所有相机清零键*/
        Button btn_camera_all_clear = (Button) view1.findViewById(R.id.btn_camera_all_clear);
        btn_camera_all_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Camera_Clear);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("1", data);
                    CmdHandle.getInstance().sendCmdInfo("2", data);
                    CmdHandle.getInstance().sendCmdInfo("3", data);
                    staticsCout = 0;
                    dataClear();
                }
            }
        });

        Button btnTrigger1Btn = (Button) view1.findViewById(R.id.btnTrigger1_btn);
        Button btnTrigger2Btn = (Button) view1.findViewById(R.id.btnTrigger2_btn);
        Button btnTrigger3Btn = (Button) view1.findViewById(R.id.btnTrigger3_btn);
        Button motorCtrlCardBtn = (Button) view1.findViewById(R.id.motorCtrlCard_btn);
        Button lightSrcDrvBtn = (Button) view1.findViewById(R.id.lightSrcDrv_btn);

        if (!Can.isValid(CanParameter.BtnTrigger1_Addr)) {
            btnTrigger1Btn.setText("触发1未连接");
            btnTrigger1Btn.setTextColor(Color.RED);
        }
        if (!Can.isValid(CanParameter.BtnTrigger2_Addr)) {
            btnTrigger2Btn.setText("触发2未连接");
            btnTrigger2Btn.setTextColor(Color.RED);
        }
        if (!Can.isValid(CanParameter.BtnTrigger3_Addr)) {
            btnTrigger3Btn.setText("触发3未连接");
            btnTrigger3Btn.setTextColor(Color.RED);
        }
        if (!Can.isValid(CanParameter.MotorCtrlCard_Addr)) {
            motorCtrlCardBtn.setText("电机控制卡未连接");
            motorCtrlCardBtn.setTextColor(Color.RED);
        }
        if (!Can.isValid(CanParameter.LighrSrcDrv_Addr)) {
            lightSrcDrvBtn.setText("光源控制器未连接");
            lightSrcDrvBtn.setTextColor(Color.RED);
        }

        dialogWindow = new DialogWindow.Builder(HomeAcitivity.this)
                .setTitle("其他控制按钮")
                .setView(view1)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialogWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                btn_other.setEnabled(true);
            }
        });
        dialogWindow.showWrapContent(800);

    }


    /**
     * 右边实时状态窗口，增加显示函数
     */
    static String[] status_list = new String[8];
    static int status_list_index = 0;

    private void userShowToast(String status) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());

        /**滑动*/
        status_list[status_list_index++] = new StringBuffer(date.toString() + " " + status).toString();
        if (status_list_index >= status_list.length) {
            status_list_index = 0;
        }

        /**转入textView*/
        StringBuffer textView = new StringBuffer();
        for (int i = status_list_index; i < (status_list.length + status_list_index); i++) {
            if (i < status_list.length) {
                if (status_list[i] != null) {
                    textView.append(status_list[i] + "\n");
                }
            } else {
                if (status_list[i - status_list.length] != null) {
                    textView.append(status_list[i - status_list.length] + "\n");
                }
            }
        }

        /**设置textView*/
        realTimeStatus_textView.setText(textView.toString());
    }


    /**
     * 右上角“启动”按键对应的处理函数
     */
    static int startOrPause = 0;
    static int servoSpd = 0;
    boolean start_need_twice = false;

    private void startBtnFunc() {
        if (startOrPause == 0) {
            /**解析配置文件*/
            if ((AppContext.btnCfgProfile != null) && (AppContext.btnCfgJson != null)) {
                btn_start.setEnabled(false);
                DialogWindow dialog = new DialogWindow.Builder(HomeAcitivity.this)
                        .setTitle("请确认玻璃盘上无异物!\n玻璃盘上的异物会影响检测效果!\n" +
                                "如已确定玻璃盘上无异物，请点击“确定”\n" +
                                "如需进行异物清理，请点击“取消”")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /**检查CAN总线连接情况*/
                                if (start_need_twice == false) {
                                    if (!Can.isValid(CanParameter.BtnTrigger1_Addr)) {
                                        EToast.showToast(HomeAcitivity.this, Constants.BtnTrigger1DisconnectedWarnning + "\n如需强制启动，请再次点击“启动");
                                        start_need_twice = true;
                                        return;
                                    } else if (!Can.isValid(CanParameter.BtnTrigger2_Addr)) {
                                        EToast.showToast(HomeAcitivity.this, Constants.BtnTrigger2DisconnectedWarnning + "\n如需强制启动，请再次点击“启动");
                                        start_need_twice = true;
                                        return;
                                    } else if (!Can.isValid(CanParameter.BtnTrigger3_Addr)) {
                                        EToast.showToast(HomeAcitivity.this, Constants.BtnTrigger3DisconnectedWarnning + "\n如需强制启动，请再次点击“启动");
                                        start_need_twice = true;
                                        return;
                                    } else if (!Can.isValid(CanParameter.MotorCtrlCard_Addr)) {
                                        EToast.showToast(HomeAcitivity.this, Constants.MotorCtrlCardDisconnectedWarnning + "\n如需强制启动，请再次点击“启动");
                                        start_need_twice = true;
                                        return;
                                    } else if (!Can.isValid(CanParameter.LighrSrcDrv_Addr)) {
                                        EToast.showToast(HomeAcitivity.this, Constants.LightSrcDrvDisconnectedWarnning + "\n如需强制启动，请再次点击“启动");
                                        start_need_twice = true;
                                        return;
                                    }
                                } else {
                                    start_need_twice = false;
                                }

                                /**检查是否含有算法配置字段*/
                                if (AppContext.btnCfgDetail.operator != null) {
                                    /**发送钮扣配置文件*/
                                    for (String tmp_tag : Constants.cameraList) {
                                        if (AppContext.getAppContext().isExist(tmp_tag)) {
                                            EToast.showToast(HomeAcitivity.this, "向相机" + tmp_tag + "发送钮扣配置文件:" + tv_buttonId.getText().toString());
                                            CmdHandle.getInstance().sendBtnCfgJson(tmp_tag, AppContext.btnCfgJson.toString().getBytes());
                                            ExcuteTask.sleep(100);
                                        } else {
                                            EToast.showToast(HomeAcitivity.this, "相机" + tmp_tag + "未连接");
                                        }
                                    }

                                    userShowToast("几何:" + AppContext.btnCfgDetail.operator.geometryMethod
                                            + "自定义:" + AppContext.btnCfgDetail.operator.geometryOperatorEnable);
                                    userShowToast("表面:" + AppContext.btnCfgDetail.operator.surfaceMethod
                                            + "自定义:" + AppContext.btnCfgDetail.operator.surfaceOperatorEnable);
                                } else {
                                    EToast.showToast(HomeAcitivity.this, Constants.NoOperatorWarnning);
                                    return;
                                }

                                /**设置转盘、传送带等*/
                                if (AppContext.btnCfgDetail.systemParameter != null) {
                                    /**设置光源*/
                                    FragmentCanLightSrcDrv.setLightSrcBrightness(1, AppContext.btnCfgDetail.systemParameter.light2);
                                    FragmentCanLightSrcDrv.setLightSrcBrightness(2, AppContext.btnCfgDetail.systemParameter.light3);
                                    FragmentCanLightSrcDrv.setLightSrcBrightness(3, AppContext.btnCfgDetail.systemParameter.light4);
                                    FragmentCanLightSrcDrv.setLightSrcBrightness(4, AppContext.btnCfgDetail.systemParameter.light5);
                                    FragmentCanLightSrcDrv.setLightSrcBrightness(5, AppContext.btnCfgDetail.systemParameter.light6);
                                    userShowToast("相机1 光源设定:0/" + AppContext.btnCfgDetail.systemParameter.light2);
                                    userShowToast("相机2 光源设定:" + AppContext.btnCfgDetail.systemParameter.light3 + "/" + AppContext.btnCfgDetail.systemParameter.light4);
                                    userShowToast("相机3 光源设定:" + AppContext.btnCfgDetail.systemParameter.light5 + "/" + AppContext.btnCfgDetail.systemParameter.light6);
                                    /**设置传送带*/
                                    FragmentCanMotorCtrlCard.setBelterSpd(FragmentCanMotorCtrlCard.DefaultBelterSpd);
                                    /**设置玻璃盘*/
                                    FragmentCanMotorCtrlCard.setServoSpd(AppContext.btnCfgDetail.systemParameter.turntable);
                                    userShowToast("玻璃盘 速度设定:" + AppContext.btnCfgDetail.systemParameter.turntable);
                                }

                                /**发送显示模式*/
                                for (String tmp_tag : Constants.cameraList) {
                                    if (AppContext.getAppContext().isExist(tmp_tag)) {
                                        CmdHandle.getInstance().setTerminalDisplayMode(tmp_tag, imageModuleFlag);
                                        ExcuteTask.sleep(100);
                                    }
                                }
                                /**启动按键转成暂停按键*/
                                startOrPause = 1;
                                btn_start.setText("暂停");
                                btn_start.setEnabled(true);
                                btn_pause.setEnabled(true);
                                btn_pause.setTextColor(Color.RED);
                                btn_machineLearn.setEnabled(false);
                                bt_search.setEnabled(false);
                                staticsCout = 0;
                                dataClear();
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        btn_start.setEnabled(true);
                    }
                });
                dialog.show();
            } else {
                EToast.showToast(this, "请选择钮扣配置文件!");
            }
        } else if (startOrPause == 1) {
            /**执行“暂停”键的功能*/
            FragmentCanMotorCtrlCard.setBelterSpd(0);
            userShowToast("传送带 暂停");

            servoSpd = FragmentCanMotorCtrlCard.getServoSpd();
            FragmentCanMotorCtrlCard.setServoSpd(0);
            userShowToast("玻璃盘 暂停");

            startOrPause = 2;                       /**“暂停”转成“继续”*/
            btn_start.setText("继续");
            btn_start.setEnabled(true);
            btn_pause.setEnabled(true);
            btn_pause.setTextColor(Color.RED);
        } else if (startOrPause == 2) {
            /**执行“继续”键的功能*/
            FragmentCanMotorCtrlCard.setBelterSpd(FragmentCanMotorCtrlCard.DefaultBelterSpd);

            FragmentCanMotorCtrlCard.setServoSpd(servoSpd);
            userShowToast("玻璃盘 速度恢复:" + servoSpd);

            startOrPause = 1;                       /**“继续”转成“暂停”*/
            btn_start.setText("暂停");
            btn_start.setEnabled(true);
            btn_pause.setEnabled(true);
            btn_pause.setTextColor(Color.RED);
        }
    }


    /**
     * 右上角“停止”按键对应的处理函数
     */
    private void stopBtnFunc() {
        startOrPause = 0;                       /**转成“启动”*/
        btn_start.setText("启动");
        btn_start.setEnabled(true);
        btn_pause.setEnabled(false);
        btn_pause.setTextColor(Color.GRAY);
        btn_machineLearn.setEnabled(true);
        bt_search.setEnabled(true);

        FragmentCanMotorCtrlCard.setBelterSpd(0);
        userShowToast("传送带 停止");

        FragmentCanMotorCtrlCard.setServoSpd(0);
        userShowToast("玻璃盘 停止");
    }

    /**
     * 检测图像模式更改
     */
    private void changeTerminalDisplayMode() {
        switch (imageModuleFlag) {
            case 0:
                imageModuleFlag = 1;
                for (String tmp_tag : Constants.cameraList) {
                    if (AppContext.getAppContext().isExist(tmp_tag)) {
                        CmdHandle.getInstance().setTerminalDisplayMode(tmp_tag, imageModuleFlag);
                        ExcuteTask.sleep(100);
                    }
                }
                EToast.showToast(this, "只发送错误图像");
                btn_display_mode.setText("只发送错误图像");
                break;
            case 1:
                imageModuleFlag = 2;
                for (String tmp_tag : Constants.cameraList) {
                    if (AppContext.getAppContext().isExist(tmp_tag)) {
                        CmdHandle.getInstance().setTerminalDisplayMode(tmp_tag, imageModuleFlag);
                        ExcuteTask.sleep(100);
                    }
                }
                reset();
                EToast.showToast(this, "不发送图像");
                btn_display_mode.setText("不发送图像");
                break;
            case 2:
                imageModuleFlag = 0;
                for (String tmp_tag : Constants.cameraList) {
                    if (AppContext.getAppContext().isExist(tmp_tag)) {
                        CmdHandle.getInstance().setTerminalDisplayMode(tmp_tag, imageModuleFlag);
                        ExcuteTask.sleep(100);
                    }
                }
                EToast.showToast(this, "发送全部图像");
                btn_display_mode.setText("发送全部图像");
                break;
            default:
                imageModuleFlag = 0;
                for (String tmp_tag : Constants.cameraList) {
                    if (AppContext.getAppContext().isExist(tmp_tag)) {
                        CmdHandle.getInstance().setTerminalDisplayMode(tmp_tag, imageModuleFlag);
                        ExcuteTask.sleep(100);
                    }
                }
                EToast.showToast(this, "发送全部图像");
                btn_display_mode.setText("发送全部图像");
                break;
        }
    }

}





