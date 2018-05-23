package com.hanyu.hust.testnet.ui.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.can.Can;
import com.hanyu.hust.testnet.can.CanMsg;
import com.hanyu.hust.testnet.can.CanParameter;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 *  Created by WuXin on 2017/12
 * 系统设置界面下光源参数配置碎片
 */

public class FragmentCanLightSrcDrv extends Fragment {
    public String TAG = "Light Src Drv";
    private TextView LSD_connect_status_text;
    private TextView LSD_error_status_text;
    /**
     * 8个通道的光源强度
     */
    public static final int DefaultBrightness = 200;
    private final int brightness_max = 255;
    private int brightness[] = new int[8];
    /**
     * 标记UI组件由CAN数据更新还是由人手动输入
     * 防止输入数据时，UI数据被Can数据更新
     * 开始手动输入时，此位置false,发送对应的Can数据帧后，此位置true
     */
    private boolean brightness_from_can[] = new boolean[8];
    /**
     * 光源强度的UI组件
     */
    private boolean widget_enabled = true;
    private Spinner brightness_spinner[] = new Spinner[8];
    private Button brightness_dec_btn[] = new Button[8];
    private SeekBar brightness_seek_bar[] = new SeekBar[8];
    private Button brightness_inc_btn[] = new Button[8];
    private EditText brightness_edit_text[] = new EditText[8];

    /**
     * 页面右下角四个按键
     */
    public Button sys_refresh_btn;
    public Button sys_apply_btn;
    public Button sys_save_btn;
    public Button sys_exit_btn;

    public static final String LSD_setting_file_name = "setting";
    public static final String LSD_json_item_name = "light src drv setting";
    public static final String[] LSD_setting_items = {"channel 1", "channel 2", "channel 3", "channel 4", "channel 5", "channel 6", "channel 7", "channel 8"};

    /**
     * Can 报文相关
     */
    public static final int Pn_DCN = 0x01;
    public static final int CMD_Update_All = 0x01;
    public static final int[] Pn_Code_Brightness = {0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27};

    /**
     * 全局变量:通道映射表
     */
    public static int[] channels_map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_can_light_src_drv, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidget();
        brightnessListenerInit();
        sysBtnListenerInit();
        AppContext.can0.setReceiveHandler(LSD_CanHandler);         /**设置Handler*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppContext.can0.setDefaultReceiveHandler();
    }

    /**
     * Can消息处理handler，定时由CanBusThread调用
     */
    private Handler LSD_CanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /**检查连接状态*/
            if (Can.isValid(CanParameter.LighrSrcDrv_Addr)) {
                LSD_connect_status_text.setText("已连接");
                LSD_connect_status_text.setTextColor(Color.GREEN);
                if (!widget_enabled) {
                    widget_enabled = true;
                    enableWidget(widget_enabled);
                }
                /**处理接收到的Can msg*/
                List<CanMsg> can_msg_list = (List<CanMsg>) msg.obj;
                for (int index = 0; index < can_msg_list.size(); index++) {
                    /**for循环内严禁添加UI更新相关的内容，防止高吞吐量时UI卡死*/
                    CanMsg can_msg = can_msg_list.get(index);
                    if ((can_msg.dlc > 2) && (can_msg.data[1] == CanParameter.LighrSrcDrv_Addr)) {
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
                updateWidget();
            } else {
                LSD_connect_status_text.setText("未连接");
                LSD_connect_status_text.setTextColor(Color.BLACK);
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
        }
    }

    /**
     * 处理读应答帧
     */
    private void paramReadAckMsgHandler(CanMsg can_msg) {
        if (can_msg != null) {
            if (can_msg.dlc == 8) {
                int byte0 = (can_msg.data[4] & 0xFF);
                int byte1 = (can_msg.data[5] & 0xFF) << 8;
                int byte2 = (can_msg.data[6] & 0xFF) << 16;
                int byte3 = (can_msg.data[7] & 0xFF) << 24;
                int param_val = byte0 + byte1 + byte2 + byte3;
                if ((can_msg.data[3] >= Pn_Code_Brightness[0])
                        && (can_msg.data[3] <= Pn_Code_Brightness[Pn_Code_Brightness.length - 1])) {
                    if (brightness_from_can[can_msg.data[3] - Pn_Code_Brightness[0]]) {
                        if (param_val > brightness_max) {
                            param_val = brightness_max;
                        }
                        brightness[can_msg.data[3] - Pn_Code_Brightness[0]] = param_val;
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
     * 页面窗口更新
     */
    private void updateWidget() {

        for (int i = 0; i < brightness.length; i++) {
            if (brightness[i] < 0) {
                brightness[i] = 0;
            } else if (brightness[i] > brightness_max) {
                brightness[i] = brightness_max;
            }
            brightness_seek_bar[i].setProgress(brightness[i]);
        }
        /**定时向设备发送数据请求，保持界面实时更新*/
        CanMsg tmp_msg = Can.createParamWriteMsg(CanParameter.LighrSrcDrv_Addr, (short) Pn_DCN, CMD_Update_All);
        AppContext.can0.write(tmp_msg);
    }


    /**
     * UI窗口组件初始化
     */
    private void initWidget() {
        LSD_connect_status_text = (TextView) getActivity().findViewById(R.id.LSD_connect_status_text);
        LSD_error_status_text = (TextView) getActivity().findViewById(R.id.LSD_error_status_text);

        for (int i = 0; i < brightness_from_can.length; i++) {
            brightness_from_can[i] = true;
        }

        brightness_spinner[0] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner1);
        brightness_spinner[1] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner2);
        brightness_spinner[2] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner3);
        brightness_spinner[3] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner4);
        brightness_spinner[4] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner5);
        brightness_spinner[5] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner6);
        brightness_spinner[6] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner7);
        brightness_spinner[7] = (Spinner) getActivity().findViewById(R.id.LSD_brightness_spinner8);

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner, Constants.LSDSettingCnList);
        for (int i = 0; i < brightness_spinner.length; i++) {
            brightness_spinner[i].setAdapter(spinner_adapter);
        }

        brightness_dec_btn[0] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn1);
        brightness_dec_btn[1] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn2);
        brightness_dec_btn[2] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn3);
        brightness_dec_btn[3] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn4);
        brightness_dec_btn[4] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn5);
        brightness_dec_btn[5] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn6);
        brightness_dec_btn[6] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn7);
        brightness_dec_btn[7] = (Button) getActivity().findViewById(R.id.LSD_brightness_dec_btn8);

        brightness_seek_bar[0] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar1);
        brightness_seek_bar[1] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar2);
        brightness_seek_bar[2] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar3);
        brightness_seek_bar[3] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar4);
        brightness_seek_bar[4] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar5);
        brightness_seek_bar[5] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar6);
        brightness_seek_bar[6] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar7);
        brightness_seek_bar[7] = (SeekBar) getActivity().findViewById(R.id.LSD_brightness_seek_bar8);

        for (int i = 0; i < brightness_seek_bar.length; i++) {
            brightness_seek_bar[i].setMax(brightness_max);
        }

        brightness_inc_btn[0] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn1);
        brightness_inc_btn[1] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn2);
        brightness_inc_btn[2] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn3);
        brightness_inc_btn[3] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn4);
        brightness_inc_btn[4] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn5);
        brightness_inc_btn[5] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn6);
        brightness_inc_btn[6] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn7);
        brightness_inc_btn[7] = (Button) getActivity().findViewById(R.id.LSD_brightness_inc_btn8);

        brightness_edit_text[0] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text1);
        brightness_edit_text[1] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text2);
        brightness_edit_text[2] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text3);
        brightness_edit_text[3] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text4);
        brightness_edit_text[4] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text5);
        brightness_edit_text[5] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text6);
        brightness_edit_text[6] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text7);
        brightness_edit_text[7] = (EditText) getActivity().findViewById(R.id.LSD_brightness_edit_text8);

        for (int i = 0; i < brightness_edit_text.length; i++) {
            brightness_edit_text[i].setEnabled(false);
        }

        sys_refresh_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_apply_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_save_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

        updateWidgetFromJson();
    }

    /**
     * 根据js文件更新界面，主要是更新spinner
     */
    private void updateWidgetFromJson() {
        SettingProfile profile = new SettingProfile(AppDirectory.getSystemConfigDirectory(), LSD_setting_file_name);
        if (profile != null) {
            try {
                JSONObject jsonTotal = profile.getJsonStr();
                if (jsonTotal != null) {
                    JSONObject settingJson = jsonTotal.getJSONObject(LSD_json_item_name);
                    if (settingJson != null) {
                        int items;
                        for (int i = 0; i < LSD_setting_items.length; i++) {
                            items = (int) settingJson.get(LSD_setting_items[i]);
                            brightness_spinner[i].setSelection(items);

                            if (channels_map == null) {
                                channels_map = new int[8];
                            }
                            synchronized (channels_map) {
                                channels_map[i] = items;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将界面信息保存到SD卡的json文件上
     */
    private void saveWidget2Json() {
        JSONObject jsonTotal = new JSONObject();
        JSONObject json = new JSONObject();
        SettingProfile profile = new SettingProfile(AppDirectory.getSystemConfigDirectory(), LSD_setting_file_name);

        try {
            for (int i = 0; i < LSD_setting_items.length; i++) {
                json.put(LSD_setting_items[i], brightness_spinner[i].getSelectedItemPosition());

                if (channels_map == null) {
                    channels_map = new int[8];
                }
                synchronized (channels_map) {
                    channels_map[i] = brightness_spinner[i].getSelectedItemPosition();
                }
            }
            jsonTotal.put(LSD_json_item_name, json);
            profile.write2sd(jsonTotal);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 窗口使能
     */
    private void enableWidget(boolean enabled) {
        for (int i = 0; i < brightness_spinner.length; i++) {
            brightness_spinner[i].setEnabled(enabled);
            brightness_dec_btn[i].setEnabled(enabled);
            brightness_seek_bar[i].setEnabled(enabled);
            brightness_inc_btn[i].setEnabled(enabled);
            brightness_edit_text[i].setEnabled(enabled);
        }
    }


    /**
     * 通道亮度监听器初始化
     */
    private void brightnessListenerInit() {
        for (int i = 0; i < brightness_dec_btn.length; i++) {
            final int channel_no = i;
            brightness_dec_btn[channel_no].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    brightness_from_can[channel_no] = false;
                    if (brightness[channel_no] > 0) {
                        brightness[channel_no]--;
                        brightness_seek_bar[channel_no].setProgress(brightness[channel_no]);
                    }
                }
            });

            brightness_inc_btn[channel_no].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    brightness_from_can[channel_no] = false;
                    if (brightness[channel_no] < brightness_max) {
                        brightness[channel_no]++;
                        brightness_seek_bar[channel_no].setProgress(brightness[channel_no]);
                    }
                }
            });

            brightness_seek_bar[channel_no].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    brightness[channel_no] = progress;
                    brightness_edit_text[channel_no].setText("" + brightness[channel_no]);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    brightness_from_can[channel_no] = false;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    private void sysBtnListenerInit() {
        sys_refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**读取js文件，更新界面配置*/
                updateWidgetFromJson();

                /**界面数据更新来自Can*/
                for (int i = 0; i < brightness_from_can.length; i++) {
                    brightness_from_can[i] = true;
                }
            }
        });

        sys_apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**发送写入指令*/
                for (int i = 0; i < brightness.length; i++) {
                    CanMsg msg = Can.createParamWriteMsg(
                            CanParameter.LighrSrcDrv_Addr,
                            (short) Pn_Code_Brightness[i],
                            brightness[i]);
                    AppContext.can0.write(msg);
                }

                /**界面数据更新来自Can*/
                for (int i = 0; i < brightness_from_can.length; i++) {
                    brightness_from_can[i] = true;
                }
                EToast.showToast(getActivity(), "应用成功!");
            }
        });

        sys_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**保存js文件*/
                saveWidget2Json();
                EToast.showToast(getActivity(), "保存成功!");
            }
        });
    }


    /**
     * 初始化通道映射表，避免每次都需要去读取json文件
     *
     * @return
     */
    public static int[] channelsMapInit() {
        JSONObject jsonTotal;
        JSONObject json;
        SettingProfile profile = new SettingProfile(AppDirectory.getSystemConfigDirectory(), LSD_setting_file_name);
        if (profile != null) {
            jsonTotal = profile.getJsonStr();
            if (jsonTotal != null) {
                try {
                    json = jsonTotal.getJSONObject(LSD_json_item_name);
                    int[] tmp_map = new int[8];
                    for (int i = 0; i < LSD_setting_items.length; i++) {
                        int items = (int) json.get(LSD_setting_items[i]);
                        tmp_map[i] = items;
                    }
                    return tmp_map;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * @param config_index 配置的索引号
     * @param brightness
     * @return 0:设置成功
     *         -1:设置失败
     */
    public static int setLightSrcBrightness(int config_index, int brightness) {
        if (channels_map == null) {
            channels_map = channelsMapInit();
            if (channels_map == null) {
                return -1;
            }
        }

        for (int i = 0; i < channels_map.length; i++) {
            if (config_index == channels_map[i]) {
                CanMsg msg = Can.createParamWriteMsg(
                        CanParameter.LighrSrcDrv_Addr,
                        (short) Pn_Code_Brightness[i],
                        brightness);
                AppContext.can0.write(msg);
                return 0;
            }
        }

        return -2;
    }

    /**
     * @param camera_id        相机ID，取值范围1、2、3
     * @param front_brightness 前置光源亮度
     * @param rear_brightness  后置光源亮度
     * @return 返回0：光源设置成功
     * 返回-1：没有对光源进行通道分配
     */
    public static int setLightSrcBrightness(int camera_id, int front_brightness, int rear_brightness) {
        if (camera_id == 1) {
            if (setLightSrcBrightness(1, rear_brightness) < 0) {
                return -1;
            }
        } else if (camera_id == 2) {
            if (setLightSrcBrightness(2, front_brightness) < 0) {
                return -1;
            }
            if (setLightSrcBrightness(3, rear_brightness) < 0) {
                return -1;
            }
        } else if (camera_id == 3) {
            if (setLightSrcBrightness(4, front_brightness) < 0) {
                return -1;
            }
            if (setLightSrcBrightness(5, rear_brightness) < 0) {
                return -1;
            }
        } else {
            return -2;
        }
        return 0;
    }
}
