package com.hanyu.hust.testnet.ui.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.can.Can;
import com.hanyu.hust.testnet.can.CanMsg;
import com.hanyu.hust.testnet.can.CanParameter;
import com.hanyu.hust.testnet.ui.view.EToast;

import java.util.List;


/**
 * Created by xinshangqiu on 2017/11/3.
 */

public class FragmentCanMotorCtrlCard extends Fragment {
    /**
     * UI组件
     */
    private TextView MCC_connect_status_text;
    private TextView MCC_error_status_text;
    /* 上料机参数设置UI组件 */
    private TextView feeder_status_text;
    private Button feeder_spd_dec_btn;
    private SeekBar feeder_spd_seek_bar;
    private Button feeder_spd_inc_btn;
    private EditText feeder_spd_edit_text;
    private Button feeder_test_btn;
    private Button feeder_start_btn;
    private Button feeder_stop_btn;
    private int feeder_spd = 0;
    /* 传送带参数设置UI组件 */
    private TextView belter_status_text;
    private Button belter_spd_dec_btn;
    private SeekBar belter_spd_seek_bar;
    private Button belter_spd_inc_btn;
    private EditText belter_spd_edit_text;
    private Button belter_test_btn;
    private Button belter_start_btn;
    private Button belter_stop_btn;
    private int belter_status = 0;

    /** 转盘电机UI组件*/
    private TextView servo_status_text;
    private Button servo_spd_dec_btn;
    private SeekBar servo_spd_seek_bar;
    private Button servo_spd_inc_btn;
    private EditText servo_spd_edit_text;
    private Button servo_test_btn;
    private Button servo_start_btn;
    private Button servo_stop_btn;
    /**
     * 转盘电机实时数据
     */
    private static int servo_spd = 0;
    private final int Servo_Spd_Max = 600;
    private int servo_status = 0;
    private boolean servo_spd_update_from_can = true;            /**标记seekbar由Can更新还是手工滑动更新*/
    public static final int DefaultServoSpd = 400;
    public static final int DefaultBelterSpd = 1;
    public static final int DefaultFeederSpd = 170;
    /**
     * 转盘电机相关PN码
     */
    private static final short Pn_DCN = 0x01;
    private static final int CMD_Update_All = 0x01;
    private static final short Pn_Code_Servo_State = 0x20;
    private static final short Pn_Code_Servo_Real_Spd = 0x21;
    private static final short Pn_Code_Servo_Desired_Spd = 0x22;
    private static final short Pn_Code_Belter_State = 0x30;
    /**
     * 通用状态码
     */
    private final int Status_Code_Starting = 0x01;
    private final int Status_Code_Running = 0x02;
    private final int Status_Code_Stopping = 0x04;
    private final int Status_Code_Stopped = 0x08;

    /**
     * 页面右下角四个按键
     */
    private Button sys_refresh_btn;
    private Button sys_apply_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;

    /**
     * 窗口使能标志
     */
    private boolean widget_enabled = false;

    private String TAG = "motor ctrl card";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_can_motor_ctrl_card, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidget();
        enableWidget(widget_enabled);
        feederListenerInit();
        belterListenerInit();
        servoListenerInit();
        sysBtnListenerInit();
        AppContext.can0.setReceiveHandler(MCC_CanHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppContext.can0.setDefaultReceiveHandler();
    }

    /**
     * 页面窗口初始化
     */
    private void initWidget() {
        MCC_connect_status_text = (TextView) getActivity().findViewById(R.id.MCC_connect_status_text);
        MCC_error_status_text = (TextView) getActivity().findViewById(R.id.MCC_error_status_text);

        feeder_status_text = (TextView) getActivity().findViewById(R.id.feeder_status_text);
        feeder_spd_dec_btn = (Button) getActivity().findViewById(R.id.feeder_spd_dec_btn);
        feeder_spd_seek_bar = (SeekBar) getActivity().findViewById(R.id.feeder_spd_seek_bar);
        feeder_spd_inc_btn = (Button) getActivity().findViewById(R.id.feeder_spd_inc_btn);
        feeder_spd_edit_text = (EditText) getActivity().findViewById(R.id.feeder_spd_edit_text);
        feeder_test_btn = (Button) getActivity().findViewById(R.id.feeder_test_btn);
        feeder_start_btn = (Button) getActivity().findViewById(R.id.feeder_start_btn);
        feeder_stop_btn = (Button) getActivity().findViewById(R.id.feeder_stop_btn);

        belter_status_text = (TextView) getActivity().findViewById(R.id.belter_status_text);
        belter_spd_dec_btn = (Button) getActivity().findViewById(R.id.belter_spd_dec_btn);
        belter_spd_seek_bar = (SeekBar) getActivity().findViewById(R.id.belter_spd_seek_bar);
        belter_spd_inc_btn = (Button) getActivity().findViewById(R.id.belter_spd_inc_btn);
        belter_spd_edit_text = (EditText) getActivity().findViewById(R.id.belter_spd_edit_text);
        belter_test_btn = (Button) getActivity().findViewById(R.id.belter_test_btn);
        belter_start_btn = (Button) getActivity().findViewById(R.id.belter_start_btn);
        belter_stop_btn = (Button) getActivity().findViewById(R.id.belter_stop_btn);

        servo_status_text = (TextView) getActivity().findViewById(R.id.servo_status_text);
        servo_spd_dec_btn = (Button) getActivity().findViewById(R.id.servo_spd_dec_btn);
        servo_spd_seek_bar = (SeekBar) getActivity().findViewById(R.id.servo_spd_seek_bar);
        servo_spd_inc_btn = (Button) getActivity().findViewById(R.id.servo_spd_inc_btn);
        servo_spd_edit_text = (EditText) getActivity().findViewById(R.id.servo_spd_edit_text);
        servo_test_btn = (Button) getActivity().findViewById(R.id.servo_test_btn);
        servo_start_btn = (Button) getActivity().findViewById(R.id.servo_start_btn);
        servo_stop_btn = (Button) getActivity().findViewById(R.id.servo_stop_btn);

        sys_refresh_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_apply_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_save_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);
        sys_save_btn.setEnabled(false);

        sys_refresh_btn.setVisibility(View.INVISIBLE);
    }

    /**
     * 是否使能页面窗口
     */
    private void enableWidget(boolean enabled) {
        feeder_status_text.setEnabled(false);
        feeder_spd_dec_btn.setEnabled(false);
        feeder_spd_seek_bar.setEnabled(false);
        feeder_spd_inc_btn.setEnabled(false);
        feeder_spd_edit_text.setEnabled(false);
        feeder_test_btn.setEnabled(false);
        feeder_start_btn.setEnabled(false);
        feeder_stop_btn.setEnabled(false);

        belter_status_text.setEnabled(enabled);
        belter_spd_dec_btn.setEnabled(false);
        belter_spd_seek_bar.setEnabled(false);
        belter_spd_inc_btn.setEnabled(false);
        belter_spd_edit_text.setEnabled(false);
        belter_test_btn.setEnabled(enabled);
        belter_start_btn.setEnabled(enabled);
        belter_stop_btn.setEnabled(enabled);

        servo_status_text.setEnabled(enabled);
        servo_spd_dec_btn.setEnabled(enabled);
        servo_spd_seek_bar.setEnabled(enabled);
        servo_spd_inc_btn.setEnabled(enabled);
        servo_spd_edit_text.setEnabled(false);
        servo_test_btn.setEnabled(enabled);
        servo_start_btn.setEnabled(enabled);
        servo_stop_btn.setEnabled(enabled);
    }

    /**
     * 页面窗口更新
     */
    private void updateWidget() {
        /**传送电机窗口更新*/
        if (belter_status > 0){
            belter_status_text.setText("运行中");
            belter_start_btn.setEnabled(false);
            belter_stop_btn.setEnabled(true);
        }else{
            belter_status_text.setText("停止中");
            belter_start_btn.setEnabled(true);
            belter_stop_btn.setEnabled(false);
        }

        /**转盘电机窗口更新*/
        if (servo_status < 0) {
            servo_status_text.setText("无法获取状态");
        } else {
            servoStatusUpdate(servo_status);
        }

        if (servo_spd > 0) {
            servo_start_btn.setEnabled(true);
            servo_stop_btn.setEnabled(true);
        } else {
            servo_start_btn.setEnabled(true);
            servo_stop_btn.setEnabled(false);
        }
        servo_spd_seek_bar.setProgress(servo_spd);

        /**定时向设备发送数据请求，保持界面实时更新*/
        CanMsg tmp_msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_DCN, CMD_Update_All);
        AppContext.can0.write(tmp_msg);
    }

    /**
     * 页面右下角四个系统按键的监听函数
     */
    private void sysBtnListenerInit() {

        sys_apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**将界面上的速度写入控制卡*/
                CanMsg msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_Code_Servo_Desired_Spd, servo_spd);
                AppContext.can0.write(msg);
                servo_spd_update_from_can = true;

                EToast.showToast(getActivity(), "应用成功!");
            }
        });

        sys_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widgetDataSave();
            }
        });

    }

    /**
     * 将页面数据保存到本地文件中
     */
    private void widgetDataSave() {
        /**发送读上料信息*/
        /**发送读输送信息*/
        /**发送读转盘信息*/
    }

    private void feederListenerInit() {

    }

    private void belterListenerInit() {
        belter_status_text.setText("已停止");

        belter_start_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CanMsg msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_Code_Belter_State, 0x01);
                AppContext.can0.write(msg);
                belter_start_btn.setEnabled(false);
                belter_stop_btn.setEnabled(true);
            }
        });

        belter_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CanMsg msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_Code_Belter_State, 0x00);
                AppContext.can0.write(msg);
                belter_start_btn.setEnabled(true);
                belter_stop_btn.setEnabled(false);
            }
        });
    }

    /**
     * 伺服电机相关监听初始化
     */
    private void servoListenerInit() {
        servo_status_text.setText("停止");
        servo_spd_seek_bar.setMax(Servo_Spd_Max);
        servo_spd_edit_text.setEnabled(false);
        servo_test_btn.setEnabled(false);

        servo_spd_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                servo_spd = progress;
                servo_spd_edit_text.setText(progress + "r/min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                servo_spd_update_from_can = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        servo_spd_dec_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servo_spd > 0) {
                    servo_spd = servo_spd - 1;
                    servo_spd_seek_bar.setProgress(servo_spd);
                }
            }
        });

        servo_spd_inc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servo_spd < Servo_Spd_Max) {
                    servo_spd = servo_spd + 1;
                    servo_spd_seek_bar.setProgress(servo_spd);
                }
            }
        });

        servo_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**向电机控制卡发送当前屏幕上的servo spd*/
        servo_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CanMsg msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_Code_Servo_Desired_Spd, servo_spd);
                AppContext.can0.write(msg);
                servo_spd_update_from_can = true;
            }
        });

        /**servo spd置零，并向电机控制卡发送速度置零报文*/
        servo_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servo_spd = 0;
                CanMsg msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_Code_Servo_Desired_Spd, servo_spd);
                AppContext.can0.write(msg);
                servo_spd_update_from_can = true;
            }
        });

    }

    /**
     * 电机控制卡定时处理Can消息Handler
     */
    private Handler MCC_CanHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (Can.isValid(CanParameter.MotorCtrlCard_Addr)) {
                MCC_connect_status_text.setText("已连接");
                MCC_connect_status_text.setTextColor(Color.GREEN);

                if (!widget_enabled) {
                    widget_enabled = true;
                    enableWidget(widget_enabled);
                }

                List<CanMsg> can_msg_list = (List<CanMsg>) msg.obj;
                for (int index = 0; index < can_msg_list.size(); index++) {
                    /**for循环内严禁添加UI更新相关的内容，防止高吞吐量时UI卡死*/
                    CanMsg can_msg = can_msg_list.get(index);
                    if ((can_msg.dlc > 2) && (can_msg.data[1] == CanParameter.MotorCtrlCard_Addr)) {
                        switch (can_msg.data[2]) {
                            case CanParameter.PF_HB:
                                break;
                            case CanParameter.PF_HBA:
                                break;
                            case CanParameter.PF_PR:
                                break;
                            case CanParameter.PF_PRA:
                                paramReadAckMsgHandler(can_msg);
                                break;
                            case CanParameter.PF_PW:
                                break;
                            case CanParameter.PF_PWA:
                                paramWriteAckMsgHandler(can_msg);
                                break;
                            default:
                                break;
                        }
                    }
                }
                /**清空list*/
                can_msg_list.clear();
                /**在这里添加UI更新相关的函数*/
                updateWidget();
            } else {
                MCC_connect_status_text.setText("未连接");
                MCC_connect_status_text.setTextColor(Color.BLACK);

                if (widget_enabled) {
                    widget_enabled = false;
                    enableWidget(widget_enabled);
                }
            }
        }
    };


    /**
     * 处理读应答帧
     */
    private void paramReadAckMsgHandler(CanMsg can_msg) {
        if (can_msg != null) {
            if (can_msg.dlc == 8) {
                short pn_code = Can.getPnCode(can_msg);
                int param = Can.getParam(can_msg);
                if ((pn_code >= 0) && (param >= 0)) {
                    switch (pn_code) {
                        case Pn_Code_Servo_State: {
                            servo_status = param;
                            break;
                        }
                        case Pn_Code_Servo_Real_Spd: {
                            if (servo_spd_update_from_can) {
                                servo_spd = param;
                            }
                            break;
                        }
                        case Pn_Code_Servo_Desired_Spd: {
                            break;
                        }
                        case Pn_Code_Belter_State:{
                            belter_status = param;
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 处理写应答帧
     */
    private void paramWriteAckMsgHandler(CanMsg can_msg) {
        if (can_msg != null) {

        }
    }

    /**
     * 伺服电机状态更新
     */
    private void servoStatusUpdate(int status_code) {
        switch (status_code) {
            case Status_Code_Starting:
                servo_status_text.setText("加速中");
                break;
            case Status_Code_Running:
                servo_status_text.setText("运行中");
                break;
            case Status_Code_Stopping:
                servo_status_text.setText("减速中");
                break;
            case Status_Code_Stopped:
                servo_status_text.setText("已停止");
                break;
            default:
                servo_status_text.setText("未知状态码:" + status_code);
                break;
        }
    }

    /**
     * 设置震动上料的速度
     * @param spd
     * @return
     */
    public static int setFeederSpd(int spd){
        return 0;
    }

    /**
     * 设置转送带的速度
     * @param spd
     * @return
     */
    public static int setBelterSpd(int spd){
        CanMsg msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_Code_Belter_State, spd);
        AppContext.can0.write(msg);
        return 0;
    }

    /**
     * 设置玻璃盘的转速
     * @param spd
     * @return
     */
    public static int setServoSpd(int spd){
        servo_spd = spd;
        CanMsg msg = Can.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, Pn_Code_Servo_Desired_Spd, spd);
        AppContext.can0.write(msg);
        return 0;
    }

    public static int getServoSpd(){
        return servo_spd;
    }
}
