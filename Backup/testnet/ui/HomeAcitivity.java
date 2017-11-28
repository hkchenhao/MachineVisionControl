package com.hanyu.hust.testnet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.can.CanBusThread;
import com.hanyu.hust.testnet.entity.AlgCfgJsonKeys;
import com.hanyu.hust.testnet.entity.BitmapCache;
import com.hanyu.hust.testnet.entity.ButtonJson;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.entity.DetailInfo;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.task.PingState;
import com.hanyu.hust.testnet.task.PingTask;
import com.hanyu.hust.testnet.task.TCPServerService;
import com.hanyu.hust.testnet.task.TimeTask;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.ui.view.SeekBarEditLayout;
import com.hanyu.hust.testnet.ui.view.SeekBarEditLayout.ActionListener;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.ExcuteTask;
import com.hanyu.hust.testnet.utils.HandlerNetMsg;
import com.hanyu.hust.testnet.utils.LogUtil;
import com.hanyu.hust.testnet.utils.MyUtils;
import com.hanyu.hust.testnet.utils.NavigatorUtils;
import com.hanyu.hust.testnet.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

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

    DialogWindow dialogWindow;

    TextView tv_detailInfo;

    TextView tv_standardInfo;

    /**
     * ButterKnife注解框架，相当于tv_qualified1 = findViewById（XXX）
     */
    @Bind(R.id.tv_qualified1)
    TextView tv_qualified1;

    @Bind(R.id.tv_unqualified1)
    TextView tv_unqualified1;

    @Bind(R.id.tv_qualified_rate1)
    TextView tv_qualified_rate1;

    @Bind(R.id.tv_qualified2)
    TextView tv_qualified2;

    @Bind(R.id.tv_unqualified2)
    TextView tv_unqualified2;

    @Bind(R.id.tv_qualified_rate2)
    TextView tv_qualified_rate2;

    @Bind(R.id.tv_qualified3)
    TextView tv_qualified3;

    @Bind(R.id.tv_unqualified3)
    TextView tv_unqualified3;

    @Bind(R.id.tv_qualified_rate3)
    TextView tv_qualified_rate3;

    @Bind(R.id.tv_fps1)
    TextView tv_fps1;

    @Bind(R.id.tv_fps2)
    TextView tv_fps2;

    @Bind(R.id.tv_fps3)
    TextView tv_fps3;

    @Bind(R.id.tv_button_id)
    TextView tv_buttonId;

    @Bind(R.id.net_link1)
    Button btn_link1;

    @Bind(R.id.net_link2)
    Button btn_link2;

    @Bind(R.id.net_link3)
    Button btn_link3;

    @Bind(R.id.bt_control_test)
    Button btn_check;

    @Bind(R.id.bt_control_pause)
    Button btn_pause;

    @Bind(R.id.iv_button_check)
    ImageView iv_check;

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

    @Bind(R.id.se_fps_set)
    SeekBarEditLayout se_fps;

    @Bind(R.id.main_bt_file_manager)
    Button btn_fileManager;

    @Bind(R.id.main_bt_sys_config)
    Button btn_sysConfig;

    @Bind(R.id.main_bt_help)
    Button btn_help;

    @Bind(R.id.main_bt_machine_learning)
    Button btn_machineLearn;

    @Bind(R.id.main_bt_statistics)
    Button btn_statistics;

    /*纽扣配置信息、算法信息等，两个按键*/
    @Bind(R.id.bt_search)
    Button bt_search;           /*基本信息配置*/

    @Bind(R.id.alg_search)
    Button alg_search;          /*算法配置*/


    @Bind(R.id.btn_more)
    ImageView bt_more;

    /**
     * 正确和错误的图像资源
     */
    private static Bitmap mBitmapcorrect, mBitmapWrong;

    private String JsonButton = null;
    private DetailInfo detailInfo = null ;

    private ProgressDialog mProgressDialog;

    Context mContext;

    /*三个相机的；连接状态*/
    private ConnectState connect1 = ConnectState.Unconnect,
            connect2 = ConnectState.Unconnect,
            connect3 = ConnectState.Unconnect;

    public static long mStartTime, mEndTime1, mEndTime2, mEndTime3,
            mCurTime1, mCurTime2, mCurTime3;

    private enum ConnectState {
        Unconnect, Connected
    }

    BitmapCache bmc1, bmc2, bmc3;

    // 每台相机的统计计数值
    private int pos1, neg1, pos2, neg2, pos3, neg3;//检测计数
    private String rate1, rate2, rate3;//缺陷比例
    private int[] flaw1 = new int[4];//一号相机缺陷类型计数
    private int[] flaw2 = new int[4];//二号相机缺陷类型计数
    private int[] flaw3 = new int[4];//三号相机缺陷类型计数

    static private int[] flawF = new int[8];
    static private int[] countF = new int[6];
    static private int staticsCout = 0;//自动保存次数

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //modifi by heqian on 2017/9
                case TIME_TASK: // 定时保存数据
                {
                   saveStatics();
                }
                break;

                case CONNECT_SUCCESS: // 网络连接成功
                    if (msg.arg1 == 1 && connect1 == ConnectState.Unconnect) {
                        EToast.showToast(getApplicationContext(), "相机1连接成功");
                        connect1 = ConnectState.Connected;
                        btn_link1.setBackgroundResource(R.mipmap.switch_on_normal);
                    } else if (msg.arg1 == 2 && connect2 == ConnectState.Unconnect) {
                        EToast.showToast(getApplicationContext(), "相机2连接成功");
                        connect2 = ConnectState.Connected;
                        btn_link2.setBackgroundResource(R.mipmap.switch_on_normal);
                    } else if (msg.arg1 == 3 && connect3 == ConnectState.Unconnect) {
                        EToast.showToast(getApplicationContext(), "相机3成功");
                        connect3 = ConnectState.Connected;
                        btn_link3.setBackgroundResource(R.mipmap.switch_on_normal);
                    }
                    break;
                case NetUtils.MSG_NET_STATE:
                    // 温度显示
                    tv_qualified_rate1.setText(HandlerNetMsg.handlerState(msg));
                    break;
                case NetUtils.MSG_NET_RESULT:
                    // 模拟数据测试帧
                    Bundle bundle = msg.getData();
                    boolean result = bundle.getBoolean("result");
                    int qualified = bundle.getInt("qualified");       // 获取合格数
                    int disQualified = bundle.getInt("disqualified"); // 获取不合格数
                    if (msg.arg1 == 1) {
                        pos1 = qualified;
                        neg1 = disQualified;
                        rate1 = calRate(pos1, neg1);

                        mEndTime1 = System.currentTimeMillis();
                        iv_result1.setImageBitmap(result ? mBitmapcorrect : mBitmapWrong);
                        tv_qualified1.setText("合格：" + qualified);
                        tv_unqualified1.setText("不合格：" + disQualified);
                        tv_qualified_rate1.setText("合格率：" + calRate(qualified, disQualified));

                        tv_fps1.setText("fps1:" + calFps(qualified, disQualified, mEndTime1 - mStartTime));
                        if (isShow) {
                            // 显示配置，大于150ms才显示
                            if ((mEndTime1 - mCurTime1) > 150) {
                                long time1 = System.currentTimeMillis();
                                if (bmc1 != null)
                                    bmc1.recycle();
                                bmc1 = HandlerNetMsg.handlerResult2(msg);
                                if (bmc1 != null) {
                                    iv_image1.setImageBitmap(bmc1.getBm());
                                }
                                long time2 = System.currentTimeMillis();
                                mCurTime1 = mEndTime1;
                            }
                        }
                    } else if (msg.arg1 == 2) {
                        pos2 = qualified;
                        neg2 = disQualified;
                        rate2 = calRate(pos2, neg2);

                        mEndTime2 = System.currentTimeMillis();
                        iv_result2.setImageBitmap(result ? mBitmapcorrect : mBitmapWrong);
                        tv_qualified2.setText("合格：" + qualified);
                        tv_unqualified2.setText("不合格：" + disQualified);
                        tv_qualified_rate2.setText("合格率：" + calRate(qualified, disQualified));

                        tv_fps2.setText("fps2:" + calFps(qualified, disQualified, mEndTime2 - mStartTime));
                        if (isShow) {
                            if ((mEndTime2 - mCurTime2) > 200) {
                                if (bmc2 != null)
                                    bmc2.recycle();
                                bmc2 = HandlerNetMsg.handlerResult2(msg);
                                if (bmc2 != null) {
                                    iv_image2.setImageBitmap(bmc2.getBm());
                                }
                                mCurTime2 = mEndTime2;
                            }
                        }
                    } else if (msg.arg1 == 3) {
                        pos3 = qualified;
                        neg3 = disQualified;
                        rate3 = calRate(pos3, neg3);

                        mEndTime3 = System.currentTimeMillis();
                        iv_result3.setImageBitmap(result ? mBitmapcorrect : mBitmapWrong);
                        tv_qualified3.setText("合格：" + qualified);
                        tv_unqualified3.setText("不合格：" + disQualified);
                        tv_qualified_rate3.setText("合格率：" + calRate(qualified, disQualified));

                        tv_fps3.setText("fps3:" + calFps(qualified, disQualified, mEndTime3 - mStartTime));
                        if (isShow) {
                            if ((mEndTime3 - mCurTime3) > 200) {
                                if (bmc3 != null)
                                    bmc3.recycle();
                                bmc3 = HandlerNetMsg.handlerResult2(msg);
                                if (bmc3 != null) {
                                    iv_image3.setImageBitmap(bmc3.getBm());
                                }
                                mCurTime3 = mEndTime3;
                            }
                        }
                    }
                    break;

                case NetUtils.MSG_NET_GET_VIDEO:
                    /**
                     * 视频流处理
                     */
                    Bitmap bitmap = HandlerNetMsg.handlerVedio(msg);
                    if (msg.arg1 == 1)
                        iv_image1.setImageBitmap(bitmap);
                    else if (msg.arg1 == 2)
                        iv_image2.setImageBitmap(bitmap);
                    else if (msg.arg1 == 3)
                        iv_image3.setImageBitmap(bitmap);
                    break;
                case NetUtils.MSG_NET_TOTAL_CNT:
                    /**
                     * 计数处理
                     */
                    parseCount(msg);
                    break;
                case NetUtils.MSG_ALG_RESULT:
                    /**
                     * 判决结果处理
                     */
                    parseResult(msg);
                    break;
                case NetUtils.MSG_NET_ALG_IMAGE:
                    /**
                     * ROI图像显示
                     */
                    parseROI(msg);
                    break;
                case NetUtils.MSG_DETAIL_INFO:{
                    /**
                     * 详细信息解析
                     */
                    parseDetailInfo(msg);
                    break;
                }
                /*相机已收到纽扣配置文件*/
                case NetUtils.MSG_BTN_CFG_JSON: {
                    EToast.showToast(HomeAcitivity.this, "相机已收到纽扣配置文件");
                    break;
                }
                /*相机已收到算法配置文件*/
                case NetUtils.MSG_ALG_CFG_JSON:{
                    EToast.showToast(HomeAcitivity.this, "相机已收到算法配置文件");
                    break;
                }
                case NetUtils.MSG_HEART_BEAT:{
                    if(msg.arg1 == 1){
                        byte[] data = new byte[8];
                        CmdHandle.getInstance().sendHeartBeatAck(Constants.cameraList[0],data);
                    }
                    else if(msg.arg1 == 2){
                        byte[] data = new byte[8];
                        CmdHandle.getInstance().sendHeartBeatAck(Constants.cameraList[1],data);
                    }
                    else if(msg.arg1 == 3){
                        byte[] data = new byte[8];
                        CmdHandle.getInstance().sendHeartBeatAck(Constants.cameraList[2],data);
                    }
                    else{
                        EToast.showToast(HomeAcitivity.this,"心跳包 相机ID解析错误");
                    }
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
        //检测结果统计数据保存
        PreferencesUtils.putInt(this, "neg1", neg1);
        PreferencesUtils.putInt(this, "pos1", pos1);
        PreferencesUtils.putInt(this, "neg2", neg2);
        PreferencesUtils.putInt(this, "pos2", pos2);
        PreferencesUtils.putInt(this, "neg3", neg3);
        PreferencesUtils.putInt(this, "pos3", pos3);

        PreferencesUtils.putString(this, "rate1", rate1);
        PreferencesUtils.putString(this, "rate2", rate2);
        PreferencesUtils.putString(this, "rate3", rate3);

        //几何缺陷统计数据保存__一号相机
        PreferencesUtils.putInt(this, "DEF_EDGE",flaw1[0]);//崩边
        PreferencesUtils.putInt(this, "DEF_HOLE",flaw1[1]);//堵孔
        PreferencesUtils.putInt(this, "DEF_GEOM",flaw1[2]);//几何偏差
        PreferencesUtils.putInt(this, "DEF_FORE",flaw1[3]);//异物
        //表面缺陷统计数据保存__二号&三号相机
        PreferencesUtils.putInt(this, "DEF_PITS",flaw2[0] + flaw3[0]);//表面破损
        PreferencesUtils.putInt(this, "DEF_BLOB",flaw2[1] + flaw3[1]);//气泡
        PreferencesUtils.putInt(this, "DEF_UNFI",flaw2[2] + flaw3[2]);//不均匀
        PreferencesUtils.putInt(this, "DEF_OTHR",flaw2[3] + flaw3[3]);//其他

    }
   //created by heqian on 2017/9
   private void saveStatics() {
       if (AppContext.btnCfgProfile == null)//没有选择纽扣直接返回
           return;
       final JSONObject STAjson = AppContext.btnCfgProfile.getStatics() ;
       int[] count = new int[6];
       int[] flaw = new int[8];
            /*解析JSON*/
       if (staticsCount() && STAjson !=null ){
           try {
              JSONObject jsonCount = STAjson.getJSONObject(ButtonKeys.Count);
              JSONObject jsonFlaw = STAjson.getJSONObject(ButtonKeys.Flaw);
               for (int i = 0; i < ButtonKeys.countS.length ; i++) {
                   countF[i] = jsonCount.getInt(ButtonKeys.countS[i]);
               }
               for (int i = 0; i < ButtonKeys.flawS.length ; i++) {
                   flawF[i] = jsonFlaw.getInt(ButtonKeys.flawS[i]);
               }
           }
           catch (JSONException e) {
               e.printStackTrace();
               EToast.showToast(getApplicationContext(), "读取统计JSON文件出错");
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
           flaw[i + 4] = flaw2[i] + flaw3[i] + flawF[i+4];
       }

       /*写入JSON*/
       JSONObject jsonTime = new JSONObject();
       JSONObject jsonCount = new JSONObject();
       JSONObject jsonFlaw = new JSONObject();
       //
       try {
//           Log.d("saveStatics: count[]",String.valueOf(STAjson.getJSONObject(ButtonKeys.Count).getInt(ButtonKeys.countS[1])));
           for (int i = 0; i < ButtonKeys.countS.length ; i++) {
               jsonCount.put(ButtonKeys.countS[i],count[i]);
           }
       } catch (JSONException e) {
           EToast.showToast(getApplicationContext(), "自动保存计数出错");
           e.printStackTrace();
           return;
       }
       try {
           for(int i=0 ; i<ButtonKeys.flawS.length ; i++) {
               jsonFlaw.put(ButtonKeys.flawS[i], flaw[i]);
           }
       } catch (JSONException e) {
           EToast.showToast(getApplicationContext(), "自动保存缺陷出错");
           e.printStackTrace();
           return;
       }
       try{
           SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
           Date now = new Date(System.currentTimeMillis());
           Log.d("saveStatics: count[]","boom");
           if(STAjson == null)
                jsonTime.put("firstStaticsTime", myFmt.format(now).toString());
           else {
               String str = STAjson.getJSONObject(ButtonKeys.STAtime).getString("firstStaticsTime");
               jsonTime.put("firstStaticsTime", str);
           }
           Log.d("saveStatics: count[]","back");
           jsonTime.put("lastStaticsTime", myFmt.format(now).toString());
       } catch (JSONException e) {
           EToast.showToast(getApplicationContext(), "自动保存时间出错");
           e.printStackTrace();
           return;
       }
       try{
           JSONObject TOT =  new JSONObject();
           TOT.put(ButtonKeys.STAtime,jsonTime);
           TOT.put(ButtonKeys.Flaw, jsonFlaw);
           TOT.put(ButtonKeys.Count, jsonCount);
           AppContext.btnCfgProfile.writeStatics(TOT);
           EToast.showToast(getApplicationContext(), "保存成功");

   } catch (JSONException e) {
        EToast.showToast(getApplicationContext(), "保存失败");
        e.printStackTrace();
        return;
    }
   }
   private void dataClear(){
       pos1 = 0; neg1 = 0; pos2 = 0; neg2 = 0;pos3 = 0; neg3 = 0;
       for (int i = 0 ; i< 4;i++ ){
           flaw1[i] = 0;
           flaw2[i] = 0;
           flaw3[i] = 0;
       }
       for (int i = 0 ; i < flawF.length; i++)
           flawF[i] = 0;
       for (int i = 0 ; i < countF.length; i++)
           countF[i] = 0;
   }
   private boolean staticsCount(){
       if (staticsCout == 0){
           staticsCout = staticsCout + 1;
           return true;
       }
       else {
        staticsCout = staticsCout + 1;
           return false ;
       }
   }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*取消屏幕休眠，add by wuxin 170714*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home_acitivity);
        ButterKnife.bind(this);
        mContext = this;
        init();

        /**
         * 初始化网络监听线程
         */
        AppContext.mPingTask = new Thread(new PingTask(mPingListener));
        AppContext.mPingTask.setName("PingTask");
        AppContext.mPingTask.start();

        /**
         * 初始化TCP服务器线程
         */
        AppContext.mTcpServerTask = new TCPServerService(mHandler).setThreadName("TCPServerService");
        AppContext.mTcpServerTask.start();

        /**初始化CAN服务线程
         * */
        AppContext.can0Thread = new CanBusThread();
        AppContext.can0Thread.start();
    }

    @Override
    protected void onRestart() {
        // 将handler 切换回主界面
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
        AppContext.can0Thread.close();
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

        /**
         * 设置DSP发送图像的频率
         * 仅调试用，后面需要删除
         */
        se_fps.setMax(25);
        se_fps.setonActionListener(new ActionListener() {
            @Override
            public void onChange(SeekBar seekBar) {
                sendTestTask();
                // 清除计数
                mCurTime1 = mCurTime2 = mCurTime3 = mStartTime = System.currentTimeMillis();
                AppContext.clearCount();
            }
        });

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
                            btn_link1.setBackgroundResource(R.mipmap.switch_off_normal);
                            connect1 = ConnectState.Unconnect;
                        } else if (tag == "2") {
                            btn_link2.setBackgroundResource(R.mipmap.switch_off_normal);
                            connect2 = ConnectState.Unconnect;
                        } else if (tag == "3") {
                            btn_link3.setBackgroundResource(R.mipmap.switch_off_normal);
                            connect3 = ConnectState.Unconnect;
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
            R.id.main_bt_help, R.id.main_bt_machine_learning,
            R.id.bt_control_test, R.id.bt_control_pause,
            R.id.bt_search, R.id.alg_search, R.id.btn_more,
            R.id.main_imv_photo1, R.id.main_imv_photo2, R.id.main_imv_photo3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_bt_file_manager: {
                NavigatorUtils.toFileManager(getContext());
                break;
            }
            case R.id.main_bt_sys_config: {
                NavigatorUtils.toSystemConfig(getContext());
                break;
            }
            case R.id.main_bt_statistics: {
                saveCount();//7.19hq
                NavigatorUtils.toStatistics(getContext());
                break;
            }
            case R.id.main_bt_machine_learning: {
                NavigatorUtils.toMachineLearn(getContext());
                break;
            }
            case R.id.main_bt_help: {
                NavigatorUtils.toHelp(getContext());
                break;
            }
            case R.id.bt_control_test: {
                btn_check.setBackgroundColor(Color.GREEN);
                btn_pause.setBackgroundResource(R.drawable.bg_control_bt);
//                {
                CmdHandle.getInstance().normal("1", 4);
                ExcuteTask.sleep(100);
                CmdHandle.getInstance().normal("2", 4);
                ExcuteTask.sleep(100);
                CmdHandle.getInstance().normal("3", 4);
                ExcuteTask.sleep(100);
//                }
//                new VedioTask("2", mHandler, 200).setThreadName("VedioTask2").start();
                sendTestTask();
            }

            break;
            case R.id.bt_control_pause: {
                btn_check.setBackgroundResource(R.drawable.bg_control_bt);
                btn_pause.setBackgroundColor(Color.GREEN);
//                CmdHandle.getInstance().normal("3", 0);
//                new StateTask("2", mHandler, 1000).setThreadName("StateTask2").start();
            }
            break;
            case R.id.main_imv_photo1:
            case R.id.main_imv_photo2:
            case R.id.main_imv_photo3: {
                isShow = !isShow;
                reset();
            }
            break;
            /*纽扣基本信息配置*/
            case R.id.bt_search: {
                NavigatorUtils.toButtonSearch(getContext());
                break;
            }
            /*纽扣算法配置*/
            case R.id.alg_search: {
                saveStatics();
//                NavigatorUtils.toAlgSearch(getContext()); //test 2017/9/25
                break;
            }

            case R.id.btn_more: {
                /**
                 * 显示详细信息界面
                 */
                View view1 = getLayoutInflater().inflate(R.layout.dialog_info, null);
                tv_detailInfo = (TextView) view1.findViewById(R.id.json_real);
                tv_standardInfo = (TextView) view1.findViewById(R.id.json_standard);
                dialogWindow = new DialogWindow.Builder(HomeAcitivity.this)
                        .setTitle("纽扣信息")
                        .setView(view1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialogWindow.showWrapContent();
                if(JsonButton == null) tv_standardInfo.setText("未选择纽扣" + "");
                else tv_standardInfo.setText(JsonButton + "");
                if(detailInfo == null) tv_detailInfo.setText("未测试纽扣" + "");
                else tv_detailInfo.setText(detailInfo + "");
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
        // 设置成功之后处理设置的数据
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                /*从纽扣配置选择界面返回*/
                case Constants.REQUEST_CODE_BTN_CFG: {
                    String fileName = data.getStringExtra(Constants.btnCfgRetStr);
                    SettingProfile profile = new SettingProfile(fileName);
                    tv_buttonId.setText("纽扣配置文件:" + fileName);
                    iv_check.setImageBitmap(profile.getBitmap());
                    JsonButton = parseButtonJson(profile);
                    staticsCout = 0 ;
                    dataClear();
                    if (AppContext.getAppContext().isExist("1")) {
                        EToast.showToast(HomeAcitivity.this, "向相机1发送钮扣配置文件:" + fileName);
                        CmdHandle.getInstance().sendBtnCfgJson(Constants.cameraList[0], profile.getJsonStr().toString().getBytes());
                        ExcuteTask.sleep(100);
                    }
                    else
                        EToast.showToast(HomeAcitivity.this, "相机1未连接");

                    if (AppContext.getAppContext().isExist("2")) {
                        EToast.showToast(HomeAcitivity.this, "向相机2发送钮扣配置文件:" + fileName);
                        CmdHandle.getInstance().sendBtnCfgJson(Constants.cameraList[1], profile.getJsonStr().toString().getBytes());
                        ExcuteTask.sleep(100);
                    }
                    else
                        EToast.showToast(HomeAcitivity.this, "相机2未连接");

                    if (AppContext.getAppContext().isExist("3")) {
                        EToast.showToast(HomeAcitivity.this, "向相机3发送钮扣配置文件:" + fileName);
                        CmdHandle.getInstance().sendBtnCfgJson(Constants.cameraList[2], profile.getJsonStr().toString().getBytes());
                        ExcuteTask.sleep(100);
                    }
                    else
                        EToast.showToast(HomeAcitivity.this, "相机3未连接");

                    break;
                }
                /*从算法配置选择界面返回*/
                case Constants.REQUEST_CODE_ALG_CFG: {
                    String fileName = data.getStringExtra(Constants.algCfgRetStr);
                    SettingProfile profile = new SettingProfile(AppDirectory.getAlgDirectory()+fileName,fileName);
                    JSONObject jObj = profile.getJsonStr();
                    String camNum = null;
                    try {
                        camNum = jObj.getString(AlgCfgJsonKeys.cameraNum);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (camNum.equals(Constants.cameraList[0])) {
                        CmdHandle.getInstance().sendAlgCfgJson(Constants.cameraList[0],profile.getJsonStr().toString().getBytes());
                        EToast.showToast(HomeAcitivity.this, "向相机"+camNum+"发送算法配置文件:" + fileName);
                    }else if (camNum.equals(Constants.cameraList[1])) {
                        CmdHandle.getInstance().sendAlgCfgJson(Constants.cameraList[1],profile.getJsonStr().toString().getBytes());
                        EToast.showToast(HomeAcitivity.this, "向相机"+camNum+"发送算法配置文件:" + fileName);
                    }else if (camNum.equals(Constants.cameraList[2])) {
                        CmdHandle.getInstance().sendAlgCfgJson(Constants.cameraList[2],profile.getJsonStr().toString().getBytes());
                        EToast.showToast(HomeAcitivity.this, "向相机"+camNum+"发送算法配置文件:" + fileName);
                    }else {
                        EToast.showToast(HomeAcitivity.this,"加载失败：无法解析算法配置文件");
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
     * 隐藏进度条
     */
    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
        String result = df.format(qualified / (float) (unqualified + qualified) * 100) + "%";
        return result;
    }

    /**
     * 计算帧率
     *
     * @param qualified   合格数
     * @param unqualified 不合格数
     * @param interval    时间间隔
     * @return 帧率
     */
    private String calFps(int qualified, int unqualified, long interval) {
        return String.format("%.1f", ((qualified + unqualified) * 1000.0 / (interval + 1)));
    }

    /**
     * 根据控件seekbar值得到Json配置文件
     *
     * @return
     */
    private JSONObject getFps() {
        JSONObject json = new JSONObject();
        try {
            json.put("height", 128);
            json.put("width", 128);
            json.put("bpp", 24);
            json.put("format", 1);

            int value = se_fps.getValue();
            if (value == 0) {
                return null;
            }
            json.put("period", (int) (1000 / value));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * 配置DSP，启动发送测试任务
     */
    private void sendTestTask() {
        JSONObject json = getFps();
        if (json == null) {
            EToast.showToast(getApplicationContext(), "设置失败");
            return;
        }

        CmdHandle.getInstance().AlgTestConfigure("1", json.toString().getBytes());
        ExcuteTask.sleep(100);

        CmdHandle.getInstance().AlgTestConfigure("2", json.toString().getBytes());
        ExcuteTask.sleep(100);

        CmdHandle.getInstance().AlgTestConfigure("3", json.toString().getBytes());
        ExcuteTask.sleep(100);
    }

    /**
     * 复位，让主界面恢复默认图像
     */
    private void reset() {
        iv_image1.setImageResource(R.mipmap.qualified);
        iv_image2.setImageResource(R.mipmap.qualified);
        iv_image3.setImageResource(R.mipmap.qualified);
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
    //7.19hq
    private void parseCount(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null || data.length % 5 != 0)
            return;

        int datalength = data.length / 5 ;

        int type;
        int count;
        for(int i = 0 ; i < datalength ; i++){
            type = data[5*i];
            count = (getUnsigned(data[5*i+1])) | (getUnsigned(data[5*i+2]) << 8)
                    | (getUnsigned(data[5*i+3]) << 16) | (getUnsigned(data[5*i+4]) << 24);
            //字段解析
            switch (type) {
                case 0 : {
                    if (msg.arg1 == 1) {
                        tv_qualified1.setText("合格：" + count);
                        pos1 = count;
                        tv_qualified_rate1.setText("合格率：" + calRate(pos1, neg1));
                    } else if (msg.arg1 == 2) {
                        tv_qualified2.setText("合格：" + count);
                        pos2 = count;
                        tv_qualified_rate2.setText("合格率：" + calRate(pos2, neg2));
                    } else if (msg.arg1 == 3) {
                        tv_qualified3.setText("合格：" + count);
                        pos3 = count;
                        tv_qualified_rate3.setText("合格率：" + calRate(pos3, neg3));
                    }
                    break;
                }
                case 1:{
                    if (msg.arg1 == 1) {
                        tv_unqualified1.setText("不合格：" + count);
                        neg1 = count;
                        tv_qualified_rate1.setText("合格率：" + calRate(pos1, neg1));
                    } else if (msg.arg1 == 2) {
                        tv_unqualified2.setText("不合格：" + count);
                        neg2 = count;
                        tv_qualified_rate2.setText("合格率：" + calRate(pos2, neg2));
                    } else if (msg.arg1 == 3) {
                        tv_unqualified3.setText("不合格：" + count);
                        neg3 = count;
                        tv_qualified_rate3.setText("合格率：" + calRate(pos3, neg3));
                    }
                    break;
                }
                //以下为缺陷类型计数解析，相关定义详见saveCount方法或网络协议doc
                case 0x21:{
                    flaw1[0] = count;
                    break;
                }
                case 0x22:{
                    flaw1[1] = count;
                    break;
                }
                case 0x23:{
                    flaw1[2] = count;
                    break;
                }
                case 0x24:{
                    flaw1[3] = count;
                    break;
                }
                case 0x25:{
                    if(msg.arg1 == 2)
                        flaw2[0] = count;
                    else if(msg.arg1 == 3)
                        flaw3[0] = count;
                    break;
                }
                case 0x26:{
                    if(msg.arg1 == 2)
                        flaw2[1] = count;
                    else if(msg.arg1 == 3)
                        flaw3[1] = count;
                    break;
                }
                case 0x27:{
                    if(msg.arg1 == 2)
                        flaw2[2] = count;
                    else if(msg.arg1 == 3)
                        flaw3[2] = count;
                    break;
                }
                case 0x28:{
                    if(msg.arg1 == 2)
                        flaw2[3] = count;
                    else if(msg.arg1 == 3)
                        flaw3[3] = count;
                    break;
                }
            }
        }
    }

    /**
     * 解析判决结果
     *
     * @param msg
     */
    private void parseResult(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null || data.length != 1)
            return;
        int type = data[0];
        if (type == 0) {
            if (msg.arg1 == 1) {
                iv_result1.setImageBitmap(mBitmapcorrect);
            } else if (msg.arg1 == 2) {
                iv_result2.setImageBitmap(mBitmapcorrect);
            } else if (msg.arg1 == 3) {
                iv_result3.setImageBitmap(mBitmapcorrect);
            }
        } else if (type == 1) {
            if (msg.arg1 == 1) {
                iv_result1.setImageBitmap(mBitmapWrong);
            } else if (msg.arg1 == 2) {
                iv_result2.setImageBitmap(mBitmapWrong);
            } else if (msg.arg1 == 3) {
                iv_result3.setImageBitmap(mBitmapWrong);
            }
        } else if (type == 2) {
            if (msg.arg1 == 1) {

            } else if (msg.arg1 == 2) {

            } else if (msg.arg1 == 3) {

            }
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
                    int r = 0, g = 0, b = 0;
                    r = packageData[8 + i * 3] & 0xff;
                    g = packageData[8 + i * 3 + 1] & 0xff;
                    b = packageData[8 + i * 3 + 2] & 0xff;
                    image[i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            } else
                return;
            bm.setPixels(image, 0, width, 0, 0, width, height);
            if (msg.arg1 == 1)
                iv_image1.setImageBitmap(bm);
            if (msg.arg1 == 2)
                iv_image2.setImageBitmap(bm);
            if (msg.arg1 == 3)
                iv_image3.setImageBitmap(bm);
        } else
            return;
    }

    /**
     * 解析得到1号相机简介版信息
     *
     * @param msg
     */
    private void parseSimpleInfo(Message msg) {
        DetailInfo simpleInfo = (DetailInfo) msg.obj;
        if (simpleInfo == null)
            return;
        double outDia = simpleInfo.getOutDia();
        double outDiaDev = simpleInfo.getOutDiaDev();
        String str = outDia + "/" + outDiaDev;
        if (msg.arg1 == 1)
            tv_fps1.setText(str);
        if (msg.arg1 == 2)
            tv_fps2.setText(str);
        if (msg.arg1 == 3)
            tv_fps3.setText(str);
    }

    /**
     * 解析得到纽扣配置信息字符串
     *
     * @param profile 纽扣配置信息工具类，用于保存Json文件和图像
     * @return
     */
    private String parseButtonJson(SettingProfile profile) {
        JsonParser jParser = new JsonParser();
        Gson gson = new Gson();
        ButtonJson buttonJson = gson.fromJson(jParser.parse(profile.getJsonStr() + "")
                .getAsJsonObject().toString(), ButtonJson.class);

        return buttonJson.toString();
    }

    /**
     * 解析得到1号相机详细版信息
     *
     * @param msg
     */
    private void parseDetailInfo(Message msg) {
        detailInfo = (DetailInfo) msg.obj;
        if (detailInfo == null)
            return;

        int outDia = detailInfo.getOutDia();
        int outDiaDev = detailInfo.getOutDiaDev();
        String str = MyUtils.getsDouble(outDia) + "/" + MyUtils.getsDouble(outDiaDev);
        if (msg.arg1 == 1)
            tv_fps1.setText(str);
        if (msg.arg1 == 2)
            tv_fps2.setText(str);
        if (msg.arg1 == 3)
            tv_fps3.setText(str);

        if (tv_detailInfo != null) {
            tv_standardInfo.setText(JsonButton + "");
            tv_detailInfo.setText(detailInfo + "");
        }
    }

    /**
     * 取消系统自动休眠
     */
    public void unLock() {

    }
}
