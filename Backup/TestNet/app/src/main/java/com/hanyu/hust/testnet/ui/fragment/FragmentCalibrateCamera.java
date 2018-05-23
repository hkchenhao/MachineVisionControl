package com.hanyu.hust.testnet.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Created by WuXin on 2017/12
 *  系统设置界面下相机参数标定碎片
 */

public class FragmentCalibrateCamera extends Fragment {
    /*json文件参数*/
    private final String TAG = "calibrate";
    private final String config_file_name = "Camera1Config"; //配置文件名
    private final String json_item_name = "calibration"; //json文件一级关键字
    private final String[] config_items = {"real_dia", "measured_dia", "cali_factor"}; //json文件二级关键字
    /**
     * 通信协议
     * */

    /*通用通信协议参数*/
    private final byte page_addr = 0x04; //通用帧 页地址
    private final byte fr_addr = 0x41; //通用帧 碎片地址
    private final byte Pn_DCN = 0x01;
    //    private final int CMD_Start_Calibrate = 1;
    //发送给DSP的控制指令
    private final int CMD_Test = 2;//开始标定
    private final int CMD_Cancel_Test = 3;//取消测试指令
    private final int CMD_Cancel_Calibrate = 4;//取消校正指令

    private final byte Pn_Start_Calibrate = 0x20;//开始标定
    private final byte Pn_Real_Dia = 0x21; //实际偏差
    private final byte Pn_Measured_Dia = 0x22; //测量偏差
    private final byte Pn_Cali_Factor = 0x23; //标定结果

    private boolean edit_enabled = false;
    private boolean factor_edited = false;

    private Handler default_handler;

    private Bitmap cali_bitmap;
    /*标定因子值*/
    private float cali_real_dia = 0.0f; //实际偏差
    private float cali_measured_dia = 0.0f; //测量偏差
    private float cali_factor = 0.0f;
    private String pre_cali_factor_string = "";
    /*
    *界面资源
    * */
    private ImageView cali_image_view; //纽扣图像
    private TextView cali_status_text_view; //矫正状态
    private EditText cali_real_dia_edit_text; //实际偏差
    private EditText cali_measured_dia_edit_text; //测量偏差
    private EditText cali_factor_edit_text; //标定因子
    /*控制按钮*/
    private Button cali_test_btn;
    private Button cali_start_btn;
    private Button cali_cancel_btn;

    private Button sys_edit_btn;
    private Button sys_default_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_calibrate_camera, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWidget();//绑定界面资源
        caliBtnListenerInit();//设置矫正监听方法
        sysBtnListenerInit();//设置控制按钮监听
        handlerInit();//添加handler获取命令
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 绑定界面资源
     */
    private void initWidget() {
        cali_image_view = (ImageView) getActivity().findViewById(R.id.FCC_image_view);
        cali_status_text_view = (TextView) getActivity().findViewById(R.id.FCC_cali_status_text_view);
        cali_real_dia_edit_text = (EditText) getActivity().findViewById(R.id.FCC_real_dia_edit_text);
        cali_measured_dia_edit_text = (EditText) getActivity().findViewById(R.id.FCC_measured_dia_edit_text);
        cali_factor_edit_text = (EditText) getActivity().findViewById(R.id.FCC_cali_factor_edit_text);
        cali_test_btn = (Button) getActivity().findViewById(R.id.FCC_test_btn);
        cali_start_btn = (Button) getActivity().findViewById(R.id.FCC_start_btn);
        cali_cancel_btn = (Button) getActivity().findViewById(R.id.FCC_cancel_btn);
        cali_cancel_btn.setEnabled(false);

        cali_measured_dia_edit_text.setEnabled(false);
        cali_factor_edit_text.setEnabled(edit_enabled);

        sys_edit_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_default_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_save_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

        sys_edit_btn.setText("编辑");
        sys_default_btn.setText("重置");

        updateWidgetFromJson();

        /**保存历史*/
        pre_cali_factor_string = cali_factor_edit_text.getText().toString();
    }

    private void caliBtnListenerInit() {
        cali_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**发送启动测试命令*/
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Test);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("1", data);
                    cali_status_text_view.setText("测试中");
                    cali_test_btn.setEnabled(false);
                }
            }
        });

        cali_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float tmp_dia = Float.valueOf(cali_real_dia_edit_text.getText().toString());
                int dia = (int) (tmp_dia * 100);
                /**发送标定直径（等同于启动标定命令）*/
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_Start_Calibrate, dia);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("1", data);
                    cali_status_text_view.setText("标定中");
                    cali_start_btn.setEnabled(false);
                    cali_cancel_btn.setEnabled(true);
                }
            }
        });

        cali_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Cancel_Calibrate);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo("1", data);
                    cali_status_text_view.setText("已取消");
                    cali_start_btn.setEnabled(true);
                    cali_cancel_btn.setEnabled(false);
                }
            }
        });
        /**点击图像视窗获取图像*/
        cali_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CmdHandle.getInstance().getImage("1", default_handler);
            }
        });

    }

    /**
     * 系统设置监听
     */
    private void sysBtnListenerInit() {
        sys_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("是否需要进行标定因子编辑？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit_enabled = true;
                        cali_factor_edit_text.setEnabled(edit_enabled);
                        factor_edited = true;
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });

        sys_default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("是否需要重置标定因子？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit_enabled = false;
                        factor_edited = false;
                        cali_factor_edit_text.setEnabled(edit_enabled);
                        cali_test_btn.setEnabled(true);
                        cali_start_btn.setEnabled(true);
                        cali_cancel_btn.setEnabled(false);
                        cali_factor_edit_text.setText(pre_cali_factor_string);
                    }
                });

                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });

        sys_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("是否需要保存当前标定因子？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit_enabled = false;
                        cali_factor_edit_text.setEnabled(edit_enabled);
                        if (factor_edited) {
                            factor_edited = false;

                            float tmp_factor = Float.valueOf(cali_factor_edit_text.getText().toString());
                            int factor = (int) (tmp_factor * 1000000);
                            /**发送标定因子*/
                            byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_Cali_Factor, factor);
                            if (data != null) {
                                CmdHandle.getInstance().sendCmdInfo("1", data);
                            }
                        }
                        saveWidget2Json();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });

        sys_exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("是否退出当前标定？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });
    }

    /**
     * 网络接收handler
     */
    private void handlerInit() {
        default_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case NetUtils.MSG_NET_GET_IMAGE: {  /**当前原图像帧*/
                        cali_bitmap = MyUtils.handlerRoi(msg);
                        if (cali_bitmap != null) {
                            cali_image_view.setImageBitmap(cali_bitmap);
                        }
                        break;
                    }
                    case NetUtils.MSG_NET_CMD: {     /**当前为通用参数传递帧*/
                        byte[] data = (byte[]) msg.obj;
                        if ((data[0] == page_addr) && (data[1] == fr_addr)) {
                            byte[] pn_code = NetUtils.getNetCmdPnCode(data);
                            int[] param = NetUtils.getNetCmdParam(data);
                            if ((pn_code != null) && (pn_code.length == param.length)) {
                                for (int i = 0; i < pn_code.length; i++) {
                                    switch (pn_code[i]) {
                                        case Pn_DCN: {
                                            break;
                                        }
                                        case Pn_Start_Calibrate: {
                                            break;
                                        }
                                        case Pn_Real_Dia: {
                                            cali_real_dia = (param[i] / 100.0f);
                                            cali_real_dia_edit_text.setText("" + cali_real_dia);
                                            break;
                                        }
                                        case Pn_Measured_Dia: {
                                            cali_measured_dia = (param[i] / 100.0f);
                                            cali_measured_dia_edit_text.setText("" + cali_measured_dia);
                                            cali_test_btn.setEnabled(true);
                                            cali_status_text_view.setText("已测试");
                                            /**回复取消测试帧*/
                                            byte[] tx_data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Cancel_Test);
                                            if (tx_data != null) {
                                                CmdHandle.getInstance().sendCmdInfo("1", tx_data);
                                            }
                                            break;
                                        }
                                        case Pn_Cali_Factor: {
                                            cali_factor = param[i] / 1000000.0f;
                                            cali_factor_edit_text.setText("" + cali_factor);
                                            cali_status_text_view.setText("已标定");
                                            cali_start_btn.setEnabled(true);
                                            cali_cancel_btn.setEnabled(false);
                                            pre_cali_factor_string = cali_factor_edit_text.getText().toString();
                                            /**回复取消标定帧*/
                                            byte[] tx_data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Cancel_Calibrate);
                                            if (tx_data != null) {
                                                CmdHandle.getInstance().sendCmdInfo("1", tx_data);
                                            }
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                return;
                            }

                        }
                    }
                    default: {
                        break;
                    }
                }
            }
        };

        AppContext.setHandler(default_handler);

    }

    /**
     * 根据js文件更新界面，主要是更新edit_text
     */
    private void updateWidgetFromJson() {
        SettingProfile profile = new SettingProfile(AppDirectory.getSystemConfigDirectory(), config_file_name);
        if (profile != null) {
            JSONObject json_total = profile.getJsonStr();
            if (json_total != null) {
                try {
                    JSONObject json_config_item = json_total.getJSONObject(json_item_name);
                    cali_real_dia_edit_text.setText((String) json_config_item.get(config_items[0]));
                    cali_measured_dia_edit_text.setText((String) json_config_item.get(config_items[1]));
                    cali_factor_edit_text.setText((String) json_config_item.get(config_items[2]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将界面信息保存到SD卡的json文件上
     */
    private void saveWidget2Json() {
        JSONObject jsonTotal = new JSONObject();
        JSONObject json = new JSONObject();
        SettingProfile profile = new SettingProfile(AppDirectory.getSystemConfigDirectory(), config_file_name); //根据地址获取json文件

        try {
            /*将参数信息写入json*/
            json.put(config_items[0], cali_real_dia_edit_text.getText().toString());
            json.put(config_items[1], cali_measured_dia_edit_text.getText().toString());
            json.put(config_items[2], cali_factor_edit_text.getText().toString());
            jsonTotal.put(json_item_name, json);
            profile.write2sd(jsonTotal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
