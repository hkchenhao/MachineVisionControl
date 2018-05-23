package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HeQian on 2017/8/3.
 * 机器学习界面下表面缺陷自学习界面
 */

public class FragmentSelfLearning extends Fragment implements View.OnClickListener, OperationInterface {
    private String TAG = "FragmentSelfLearning";
    /*通用帧协议常量*/
    private final byte page_addr = 0x03;
    private final byte fr_addr = 0x32;
    private final byte Pn_DCN = 0x01;
    private final int CMD_Start_Learning = 1;

    /*控件声明*/
    private View mView;

    @Bind(R.id.btn_start_self_learning)
    Button bt_start_all;

    @Bind(R.id.sp_cam_choose)
    Spinner sp_cam;
    //正面正品相关控件
    @Bind(R.id.bt_control_learning_1)
    Button button1;
    @Bind(R.id.text_learning_count_1)
    TextView count1;
    @Bind(R.id.text_learning_all_1)
    EditText editText1;

    //正面次品相关控件
    @Bind(R.id.bt_control_learning_2)
    Button button2;
    @Bind(R.id.text_learning_count_2)
    TextView count2;
    @Bind(R.id.text_learning_all_2)
    EditText editText2;

    //反面正品相关控件
    @Bind(R.id.bt_control_learning_3)
    Button button3;
    @Bind(R.id.text_learning_count_3)
    TextView count3;
    @Bind(R.id.text_learning_all_3)
    EditText editText3;

    //反面次品相关控件
    @Bind(R.id.bt_control_learning_4)
    Button button4;
    @Bind(R.id.text_learning_count_4)
    TextView count4;
    @Bind(R.id.text_learning_all_4)
    EditText editText4;
    /*常量*/
    //网络命令常量
    final byte NET_START = 0;
    final byte NET_PAUSE = 1;
    final byte NET_CONTINUE = 2;
    final byte NET_RESTART = 3;

    //当前学习状态
    private enum state {
        NULL, LEARNING, PAUSED, LEARNED
    }

    state camState1 = state.NULL;
    state camState2 = state.NULL;
    state camState3 = state.NULL;
    state camState4 = state.NULL;

    /*界面数据*/
    //输入目标检测值 默认值为30
    private byte countInput1 = 30;
    private byte countInput2 = 30;
    private byte countInput3 = 30;
    private byte countInput4 = 30;


    //当前操作相机
    private enum camList {
    }

    /*重写函数 创建界面*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_selflerning, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }

    @Override
    public void onStart() {
        AppContext.setHandler(mHandler);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
        super.onStart();
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
    }

    /**帧接收处理*/
    /**
     * 1.计数 帧头 标识帧（类型）计数
     * 2.状态帧
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetUtils.MSG_SELFLEARNING_COUNT:
                    parseSelfLearningCount(msg);
                    break;
                case NetUtils.MSG_SELFLEARNING_STATE:
                    parseSelfLearningState(msg);
                    break;

            }
        }
    };

    /**
     * 初始化数据
     * */
    private void initData() {
        String[] cam_list = {"相机2", "相机3"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, cam_list);
        sp_cam.setAdapter(adapter1);

        countInput1 = 30;
        countInput2 = 30;
        countInput3 = 30;
        countInput4 = 30;

        sp_cam.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                button1.setEnabled(true);
                button1.setText("开始");
                editText1.setText("30");
                count1.setText("0");
                countInput1 = 30;
                camState1 = state.NULL;

                button2.setEnabled(true);
                button2.setText("开始");
                editText2.setText("30");
                count2.setText("0");
                countInput2 = 30;
                camState2 = state.NULL;

                button3.setEnabled(true);
                button3.setText("开始");
                editText3.setText("30");
                count3.setText("0");
                countInput3 = 30;
                camState3 = state.NULL;

                button4.setEnabled(true);
                button4.setText("开始");
                editText4.setText("30");
                count4.setText("0");
                countInput4 = 30;
                camState4 = state.NULL;

                initView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**初始化视图*/
    private void initView() {
        editText1.setText(String.valueOf(countInput1));
        editText2.setText(String.valueOf(countInput2));
        editText3.setText(String.valueOf(countInput3));
        editText4.setText(String.valueOf(countInput4));

        buttonset(false);
    }

    /**
     * 设置按键的监听器
     *
     * @param view 点击的按钮视图
     */
    @OnClick({R.id.bt_control_learning_1, R.id.bt_control_learning_2,
            R.id.bt_control_learning_3, R.id.bt_control_learning_4, R.id.btn_start_self_learning})
    public void onClick(View view) {
        byte[] data = new byte[3];
        if (inputJudge() && netJudge()) {
            switch (view.getId()) {
                case R.id.bt_control_learning_1:
                    if (camState1 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 0;
                        data[2] = countInput1;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button1.setEnabled(false);
//                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button1.setText("暂停");
//                        //训练状态设置
                        camState1 = state.LEARNING;
                    } else if (camState1 == state.LEARNING) {
//                        //发送网络包
//                        data[0] = NET_PAUSE;//暂停训练
//                        data[1] = 0;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button1.setEnabled(true);
//                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
//                        button1.setText("继续");
//                        //训练状态设置
//                        camState1 = state.PAUSED;
                    } else if (camState1 == state.PAUSED) {
//                        //发送网络包
//                        data[0] = NET_CONTINUE;//继续训练
//                        data[1] = 0;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button1.setEnabled(true);
//                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button1.setText("暂停");
//                        //训练状态设置
//                        camState1 = state.LEARNING;
                    } else if (camState1 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 0;
                        data[2] = countInput1;//第三字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button1.setEnabled(true);
                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button1.setText("暂停");
                        //训练状态设置
                        camState1 = state.LEARNING;
                    }
                    break;

                case R.id.bt_control_learning_2:
                    if (camState2 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 1;
                        data[2] = countInput2;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button2.setEnabled(false);
//                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button2.setText("暂停");
//                        //训练状态设置
                        camState2 = state.LEARNING;
                    } else if (camState2 == state.LEARNING) {
//                        //发送网络包
//                        data[0] = NET_PAUSE;//暂停训练
//                        data[1] = 1;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button2.setEnabled(true);
//                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
//                        button2.setText("继续");
//                        //训练状态设置
//                        camState2 = state.PAUSED;
                    } else if (camState2 == state.PAUSED) {
//                        //发送网络包
//                        data[0] = NET_CONTINUE;//继续训练
//                        data[1] = 1;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button2.setEnabled(true);
//                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button2.setText("暂停");
//                        //训练状态设置
//                        camState2 = state.LEARNING;
                    } else if (camState2 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 1;
                        data[2] = countInput2;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button2.setEnabled(true);
                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button2.setText("暂停");
                        //训练状态设置
                        camState2 = state.LEARNING;
                    }
                    break;

                case R.id.bt_control_learning_3:
                    if (camState3 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 2;
                        data[2] = countInput3;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button3.setEnabled(false);
//                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button3.setText("暂停");
//                        //训练状态设置
                        camState3 = state.LEARNING;
                    } else if (camState3 == state.LEARNING) {
//                        //发送网络包
//                        data[0] = NET_PAUSE;//暂停训练
//                        data[1] = 2;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button3.setEnabled(true);
//                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
//                        button3.setText("继续");
//                        //训练状态设置
//                        camState3 = state.PAUSED;
                    } else if (camState3 == state.PAUSED) {
//                        //发送网络包
//                        data[0] = NET_CONTINUE;//继续训练
//                        data[1] = 2;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button3.setEnabled(true);
//                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button3.setText("暂停");
//                        //训练状态设置
//                        camState3 = state.LEARNING;
                    } else if (camState3 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 2;
                        data[2] = countInput3;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button3.setEnabled(true);
                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button3.setText("暂停");
                        //训练状态设置
                        camState3 = state.LEARNING;
                    }
                    break;

                case R.id.bt_control_learning_4:
                    if (camState4 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 3;
                        data[2] = countInput4;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button4.setEnabled(false);
//                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button4.setText("暂停");
//                        //训练状态设置
                        camState4 = state.LEARNING;
                    } else if (camState4 == state.LEARNING) {
//                        //发送网络包
//                        data[0] = NET_PAUSE;//暂停训练
//                        data[1] = 3;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button4.setEnabled(true);
//                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
//                        button4.setText("继续");
//                        //训练状态设置
//                        camState4 = state.PAUSED;
                    } else if (camState4 == state.PAUSED) {
//                        //发送网络包
//                        data[0] = NET_CONTINUE;//继续训练
//                        data[1] = 3;
//                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
//                        //按键设置
//                        buttonset(false);
//                        button4.setEnabled(true);
//                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
//                        button4.setText("暂停");
//                        //训练状态设置
//                        camState4 = state.LEARNING;
                    } else if (camState4 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 3;
                        data[2] = countInput4;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button4.setEnabled(true);
                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button4.setText("暂停");
                        //训练状态设置
                        camState4 = state.LEARNING;
                    }
                    break;
                case R.id.btn_start_self_learning:
                    test();
                    buttonset(true);
                    // TODO: 2018/2/2  
                    /**发送启动命令*/
                    byte[] net_tx_data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Start_Learning);
                    if (net_tx_data != null) {
                        CmdHandle.getInstance().sendCmdInfo(getcamID(), net_tx_data);
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JSONObject btnCfgJson = ((MachineLearning) getActivity()).getJson();
                        String btnCfgJsonName = ((MachineLearning) getActivity()).getButtonId();
                        if (btnCfgJson != null) {
                            EToast.showToast(getActivity(), "向相机" + getcamID() + "发送钮扣配置文件:" + btnCfgJsonName);
                            CmdHandle.getInstance().sendBtnCfgJson(getcamID(), btnCfgJson.toString().getBytes());
                        } else {
                            EToast.showToast(getActivity(), "请选择钮扣配置文件！！！");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
    发送函数

    */


    /**
     * 解析网络包计数值，刷新界面学习计数值
     * @param msg Message类型 由type节与count节组成
     * type节：在0-3取值，分别代表正面正品、正面次品、反面正品、反面次品
     *
     */
    private void parseSelfLearningCount(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null)
            return;
        byte type = data[0];
        byte count = data[1];
        switch (type) {
            case 0:
                count1.setText(String.valueOf(count));
                break;
            case 1:
                count2.setText(String.valueOf(count));
                break;
            case 2:
                count3.setText(String.valueOf(count));
                break;
            case 3:
                count4.setText(String.valueOf(count));
                break;
            default:
                break;
        }
    }

    /**
     * 解析DSP网络包状态信息，刷新界面状态信息
     * @param msg Message类型 由type节与state节组成
     * type节：在0-3取值，分别代表正面正品、正面次品、反面正品、反面次品
     * state节：见代码信息
     */
    private void parseSelfLearningState(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null)
            return;
        int type = data[0];
        int state = data[1];
        String str = "";
        switch (type) {
            case 0:
                str = "正面正品";
                break;
            case 1:
                str = "正面次品";
                break;
            case 2:
                str = "反面正品";
                break;
            case 3:
                str = "反面次品";
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                break;
        }
        switch (state) {
            case 0:
                ((MachineLearning) getActivity()).writeStatus(str + "正在进行自学习");
                break;
            case 1:
                ((MachineLearning) getActivity()).writeStatus(str + "自学习完成");
                buttonset(true);
                switch (type) {
                    case 0:
                        camState1 = FragmentSelfLearning.state.LEARNED;
                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button1.setText("开始");//BUG
                        break;
                    case 1:
                        camState2 = FragmentSelfLearning.state.LEARNED;
                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button2.setText("开始");
                        break;
                    case 2:
                        camState3 = FragmentSelfLearning.state.LEARNED;
                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button3.setText("开始");
                        break;
                    case 3:
                        camState4 = FragmentSelfLearning.state.LEARNED;
                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button4.setText("开始");
                        break;
                    default:
                        break;
                }
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                break;
        }
    }
    /**
     * 功能函数集
     */

    /**根据spinner获取相机ID*/
    private String getcamID() {
        String str = "";
        switch (sp_cam.getSelectedItemPosition()) {
            case 0:
                str = "2";
                break;
            case 1:
                str = "3";
                break;
            default:
                break;
        }
        return str;

    }
    /*控制按键使能*/
    private void buttonset(boolean ins) {
        button1.setEnabled(ins);
        button2.setEnabled(ins);
        button3.setEnabled(ins);
        button4.setEnabled(ins);
    }
    /*输入参数判断*/
    private boolean inputJudge() {
        Editable[] s = new Editable[4];
        s[0] = editText1.getText();
        s[1] = editText2.getText();
        s[2] = editText3.getText();
        s[3] = editText4.getText();
        boolean flag = true;
        for (int i = 0; i < 4; i++) {
            if (TextUtils.isEmpty(s[i].toString())) {
                EToast.showToast(getActivity(), "第" + String.valueOf(i) + "训练项目输入为空");
                return false;
            }
        }

        for (int i = 0; i < 4; i++) {
            byte countTem = 30;
            boolean flagTem = true;
            if (((Integer.valueOf(s[i].toString())) < 100) && (Integer.valueOf(s[i].toString()) > 5)) {
                countTem = Byte.valueOf(s[i].toString());
            } else flagTem = false;
            switch (i) {
                case 0:
                    if (flagTem == true) countInput1 = countTem;
                    else {
                        editText1.setText("30");
                        countInput1 = 30;
                        EToast.showToast(getActivity(), "训练数量请输入5-100");
                        flag = false;
                    }
                    break;
                case 1:
                    if (flagTem == true) countInput2 = countTem;
                    else {
                        editText2.setText("30");
                        countInput2 = 30;
                        EToast.showToast(getActivity(), "训练数量请输入5-100");
                        flag = false;
                    }
                    break;
                case 2:
                    if (flagTem == true) countInput3 = countTem;
                    else {
                        editText3.setText("30");
                        countInput3 = 30;
                        EToast.showToast(getActivity(), "训练数量请输入5-100");
                        flag = false;
                    }
                    break;
                case 3:
                    if (flagTem == true) countInput4 = countTem;
                    else {
                        editText4.setText("30");
                        countInput4 = 30;
                        EToast.showToast(getActivity(), "训练数量请输入5-100");
                        flag = false;
                    }
                    break;
                default:
                    break;
            }
        }
        if (flag) return true;
        else return false;
    }

    /**
     * 网络状态判断
     * @return
     */
    private boolean netJudge() {
        if (AppContext.getAppContext().isExist(getcamID())) {
            return true;
        } else {
            EToast.showToast(getActivity(), "相机" + getcamID() + "网络未连接");
            return false;
        }
    }



    private void test() {
    }


    @Override
    public void onSave() {

    }

    @Override
    public void onRun() {

    }

    @Override
    public void onCaptrue() {

    }


}
