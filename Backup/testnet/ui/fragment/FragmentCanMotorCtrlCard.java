package com.hanyu.hust.testnet.ui.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.can.CanBusThread;
import com.hanyu.hust.testnet.can.CanMsg;
import com.hanyu.hust.testnet.entity.CanParameter;

import java.util.List;


/**
 * Created by xinshangqiu on 2017/11/3.
 */

public class FragmentCanMotorCtrlCard extends Fragment {
    public TextView MCC_connect_status_text;
    public TextView MCC_error_status_text;

    public TextView feeder_status_text;
    public Button feeder_spd_dec_btn;
    public SeekBar feeder_spd_seek_bar;
    public Button feeder_spd_inc_btn;
    public EditText feeder_spd_edit_text;
    public Button feeder_test_btn;
    public Button feeder_start_btn;
    public Button feeder_stop_btn;
    public int feeder_spd = 0;

    public TextView belter_status_text;
    public Button belter_spd_dec_btn;
    public SeekBar belter_spd_seek_bar;
    public Button belter_spd_inc_btn;
    public EditText belter_spd_edit_text;
    public Button belter_test_btn;
    public Button belter_start_btn;
    public Button belter_stop_btn;
    public int belter_spd = 0;

    /**
     * 转盘电机UI组件
     */
    public TextView servo_status_text;
    public Button servo_spd_dec_btn;
    public SeekBar servo_spd_seek_bar;
    public Button servo_spd_inc_btn;
    public EditText servo_spd_edit_text;
    public Button servo_test_btn;
    public Button servo_start_btn;
    public Button servo_stop_btn;
    /**
     * 转盘电机实时数据
     */
    public int servo_spd = 0;
    public int Servo_Spd_Max = 600;
    public int servo_status = 0;
    public boolean servo_spd_update_from_can = true;            /*标记seekbar由Can更新还是手工滑动更新*/
    /**
     * 转盘电机相关PN码
     */
    public final int Pn_Code_Servo_Cmd = 0x60;
    public final int Pn_Code_Servo_Spd = 0x68;

    /**
     * 通用状态码
     */
    public final int Status_Code_Starting = 0x10;
    public final int Status_Code_Running = 0x18;
    public final int Status_Code_Stopping = 0x20;
    public final int Status_Code_Stopped = 0x28;

    /**
     * 页面右下角四个按键
     */
    public Button sys_refresh_btn;
    public Button sys_apply_btn;
    public Button sys_save_btn;
    public Button sys_exit_btn;

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
        AppContext.can0Thread.setHandler(MCC_CanHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppContext.can0Thread.setDefaultHandler();
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

        belter_status_text.setEnabled(false);
        belter_spd_dec_btn.setEnabled(false);
        belter_spd_seek_bar.setEnabled(false);
        belter_spd_inc_btn.setEnabled(false);
        belter_spd_edit_text.setEnabled(false);
        belter_test_btn.setEnabled(false);
        belter_start_btn.setEnabled(false);
        belter_stop_btn.setEnabled(false);

        servo_status_text.setEnabled(enabled);
        servo_spd_dec_btn.setEnabled(enabled);
        servo_spd_seek_bar.setEnabled(enabled);
        servo_spd_inc_btn.setEnabled(enabled);
        servo_spd_edit_text.setEnabled(enabled);
        servo_test_btn.setEnabled(enabled);
        servo_start_btn.setEnabled(enabled);
        servo_stop_btn.setEnabled(enabled);
    }

    /**
     * 页面窗口更新
     */
    private void updateWidget() {
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
        CanMsg tmp_msg = CanBusThread.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, (short) Pn_Code_Servo_Cmd, 0x01);
        AppContext.can0Thread.write(tmp_msg);
    }

    /**
     * 页面右下角四个系统按键的监听函数
     */
    private void sysBtnListenerInit() {
        sys_refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**请求转盘电机速度*/
                CanMsg msg = CanBusThread.createParamReadMsg(CanParameter.MotorCtrlCard_Addr, (short) Pn_Code_Servo_Spd);
                AppContext.can0Thread.write(msg);
                servo_spd_update_from_can = true;
            }
        });

        sys_apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**将界面上的速度写入控制卡*/
                CanMsg msg = CanBusThread.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, (short) Pn_Code_Servo_Spd, servo_spd);
                AppContext.can0Thread.write(msg);
                servo_spd_update_from_can = true;
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

    }

    /**
     * 伺服电机相关监听初始化
     */
    private void servoListenerInit() {
        servo_status_text.setText("停止");
        servo_spd_seek_bar.setMax(Servo_Spd_Max);
        servo_spd_edit_text.setEnabled(false);

        servo_spd_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                servo_spd = progress;
                servo_spd_edit_text.setText("" + progress);
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
                CanMsg msg = CanBusThread.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, (short) Pn_Code_Servo_Spd, servo_spd);
                AppContext.can0Thread.write(msg);
                servo_spd_update_from_can = true;
            }
        });

        /**servo spd置零，并向电机控制卡发送速度置零报文*/
        servo_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servo_spd = 0;
                CanMsg msg = CanBusThread.createParamWriteMsg(CanParameter.MotorCtrlCard_Addr, (short) Pn_Code_Servo_Spd, servo_spd);
                AppContext.can0Thread.write(msg);
                servo_spd_update_from_can = true;
            }
        });

    }

    /**
     * 电机控制卡定时处理Can消息Handler
     */
    private Handler MCC_CanHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (CanBusThread.isValid(CanParameter.MotorCtrlCard_Addr)) {
                MCC_connect_status_text.setText("已连接");
                MCC_connect_status_text.setTextColor(Color.GREEN);

                if (!widget_enabled) {
                    widget_enabled = true;
                    enableWidget(widget_enabled);
                }

                if (msg.what == 1) {
                    List<CanMsg> can_msg_list = (List<CanMsg>) msg.obj;
                    for (int index = 0; index < can_msg_list.size(); index++) {
                        /**for循环内严禁添加UI更新相关的内容，防止高吞吐量时UI卡死*/
                        CanMsg can_msg = can_msg_list.get(index);
                        if ((can_msg.dlc > 2) && (can_msg.data[1] == CanParameter.MotorCtrlCard_Addr)) {
                            switch (can_msg.data[2]) {
                                case CanParameter.PF_HB:
                                    heartBeatMsgHandler(can_msg);
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
                    can_msg_list.clear();/**清空list*/
                }
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
     * 处理心跳帧
     */
    private void heartBeatMsgHandler(CanMsg can_msg) {
        if (can_msg != null) {
            switch (can_msg.dlc) {
                case 4:
                    servo_status = -1;
                    break;
                case 5:
                    servo_status = -1;
                    break;
                case 6:
                    servo_status = -1;
                    break;
                case 7:
                    servo_status = can_msg.data[6];
                    break;
                default:
                    servo_status = -1;
                    break;
            }
        }
    }

    /**
     * 处理读应答帧
     */
    private void paramReadAckMsgHandler(CanMsg can_msg) {
        if (can_msg != null) {
            if (can_msg.dlc == 8) {
                int param_val = can_msg.data[7];
                param_val = (param_val << 8);
                param_val += can_msg.data[6];
                param_val = (param_val << 8);
                param_val += can_msg.data[5];
                param_val = (param_val << 8);
                param_val += can_msg.data[4];
                switch (can_msg.data[3]) {
                    case Pn_Code_Servo_Spd:
                        if (servo_spd_update_from_can) {
                            servo_spd = param_val;
                        }
                        break;
                    default:
                        break;
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
                servo_status_text.setText("启动中");
                break;
            case Status_Code_Running:
                servo_status_text.setText("运行中");
                break;
            case Status_Code_Stopping:
                servo_status_text.setText("停止中");
                break;
            case Status_Code_Stopped:
                servo_status_text.setText("已停止");
                break;
            default:
                servo_status_text.setText("未知状态码:" + status_code);
                break;
        }
    }


}
