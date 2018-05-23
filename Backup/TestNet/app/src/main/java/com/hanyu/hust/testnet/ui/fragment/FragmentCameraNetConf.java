package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.CameraConfigParam.CameraConfigParam;
import com.hanyu.hust.testnet.CameraConfigParam.NetConf;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;

/**
 *  Created by HanYu on 2017/4
 * 系统设置界面下相机参数配置碎片
 */

public class FragmentCameraNetConf extends BaseFragment {
    private String TAG = "netCfg";
    /*界面资源*/
    private Spinner chooseCamera_spinner; //相机选择
    private EditText[] mac_editText; //mc地址
    private EditText[] ip_editText; //ip地址
    private EditText[] remote_ip_editText; //远程ip配置
    private EditText port_editText; //端口

    /* 控制按键*/
    private Button sys_default_btn;
    private Button sys_apply_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;

    private String cameraTag = "0";          //全局变量，自动在spinner选择时更新

    private NetConf netCfg = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_camera_net_conf, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidget();
        listenerInit();
    }


    /**
     * 根据相机参数更新界面
     */
    private void updateWidget() {
        if (netCfg != null) {

            enableWidget(true);

            /**更新MAC地址*/
            for (int i = 0; i < netCfg.mac_address.length; i++) {
                mac_editText[i].setText(Integer.toHexString(0xFF & netCfg.mac_address[i]));
            }
            /**更新ip地址*/
            for (int i = 0; i < netCfg.ip_address.length; i++) {
                ip_editText[i].setText(Integer.toString(0xFF & netCfg.ip_address[i]));
            }
            /**更新服务器地址*/
            for (int i = 0; i < netCfg.remote_ip.length; i++) {
                remote_ip_editText[i].setText(Integer.toString(0xFF & netCfg.remote_ip[i]));
            }
            /**更新端口号*/
            port_editText.setText(Integer.toString(0xFFFF & netCfg.port));
        } else {
            /**从网络中获取相机参数*/
            getParamFromNet();
        }
    }

    /**
     * 从网络中获取读参数后，用于界面更新
     */
    private Handler updateWidgetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getParam();
            if (netCfg != null) {
                updateWidget();
                EToast.showToast(getActivity(), "更新成功!");
                sys_default_btn.setEnabled(true);
                sys_apply_btn.setEnabled(true);
                sys_save_btn.setEnabled(true);
            }
        }
    };


    /**
     * 从缓存中提取触发参数
     *
     * @return
     */
    private void getParam() {
        CameraConfigParam camera_cfg_param = (((SystemConfig) getActivity()).getCameraConfigParam(cameraTag));
        if (camera_cfg_param != null) {
            netCfg = camera_cfg_param.netConf;
        }
    }

    /**
     * 从网络中提取触发参数，参数需要在handler中异步更新
     */
    private void getParamFromNet() {
        ((SystemConfig) getActivity()).getCameraConfigParamFromNet(cameraTag, updateWidgetHandler);
    }

    private void initWidget() {
        chooseCamera_spinner = (Spinner) getActivity().findViewById(R.id.sp_camera_choice);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner, Constants.camList);
        chooseCamera_spinner.setAdapter(spinner_adapter);

        cameraTag = ((SystemConfig) getActivity()).getCameraTag();
        if (cameraTag != null) {
            if (cameraTag.equals("1")) {
                chooseCamera_spinner.setSelection(0);
            } else if (cameraTag.equals("2")) {
                chooseCamera_spinner.setSelection(1);
            } else if (cameraTag.equals("3")) {
                chooseCamera_spinner.setSelection(2);
            }
        }

        mac_editText = new EditText[6];
        mac_editText[0] = (EditText) getActivity().findViewById(R.id.FCNC_mac_0_edit_text);
        mac_editText[1] = (EditText) getActivity().findViewById(R.id.FCNC_mac_1_edit_text);
        mac_editText[2] = (EditText) getActivity().findViewById(R.id.FCNC_mac_2_edit_text);
        mac_editText[3] = (EditText) getActivity().findViewById(R.id.FCNC_mac_3_edit_text);
        mac_editText[4] = (EditText) getActivity().findViewById(R.id.FCNC_mac_4_edit_text);
        mac_editText[5] = (EditText) getActivity().findViewById(R.id.FCNC_mac_5_edit_text);

        ip_editText = new EditText[4];
        ip_editText[0] = (EditText) getActivity().findViewById(R.id.FCNC_ip_0_edit_text);
        ip_editText[1] = (EditText) getActivity().findViewById(R.id.FCNC_ip_1_edit_text);
        ip_editText[2] = (EditText) getActivity().findViewById(R.id.FCNC_ip_2_edit_text);
        ip_editText[3] = (EditText) getActivity().findViewById(R.id.FCNC_ip_3_edit_text);

        remote_ip_editText = new EditText[4];
        remote_ip_editText[0] = (EditText) getActivity().findViewById(R.id.FCNC_remote_ip_0_edit_text);
        remote_ip_editText[1] = (EditText) getActivity().findViewById(R.id.FCNC_remote_ip_1_edit_text);
        remote_ip_editText[2] = (EditText) getActivity().findViewById(R.id.FCNC_remote_ip_2_edit_text);
        remote_ip_editText[3] = (EditText) getActivity().findViewById(R.id.FCNC_remote_ip_3_edit_text);

        port_editText = (EditText) getActivity().findViewById(R.id.FCNC_tcp_port_edit_text);

        sys_default_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_default_btn.setText("重置");
        sys_apply_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_save_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

        sys_default_btn.setEnabled(false);
        sys_apply_btn.setEnabled(false);
        sys_save_btn.setEnabled(false);
    }

    /**
     * 界面组件使能
     * @param enabled 使能
     */
    private void enableWidget(boolean enabled) {
        for (int i = 0; i < mac_editText.length; i++) {
            mac_editText[i].setEnabled(enabled);
        }

        for (int i = 0; i < ip_editText.length; i++) {
            ip_editText[i].setEnabled(enabled);
        }

        for (int i = 0; i < remote_ip_editText.length; i++) {
            remote_ip_editText[i].setEnabled(enabled);
        }
        port_editText.setEnabled(enabled);
    }

    private void listenerInit() {
        /**监听相机选择Spinner*/
        chooseCamera_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cameraTag = String.valueOf(position + 1);
                ((SystemConfig) getActivity()).setCameraTag(cameraTag);
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    enableWidget(true);
                    getParamFromNet();
                } else {
                    enableWidget(false);
                }
                sys_default_btn.setEnabled(false);
                sys_apply_btn.setEnabled(false);
                sys_save_btn.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**监听mac地址输入*/
        for (int i = 0; i < mac_editText.length; i++) {
            final int index = i;
            mac_editText[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mac_editText[index].getText().length() == 0) {
                        EToast.showToast(getActivity(), "输入框中参数非法!");
                    } else {
                        int tmp_val = Integer.parseInt(mac_editText[index].getText().toString(), 16);
                        tmp_val = Math.abs(tmp_val);
                        if (netCfg != null) {
                            netCfg.mac_address[index] = (byte) (0xFF & tmp_val);
                        }
                    }
                }
            });
        }

        /**监听IP地址输入*/
        for (int i = 0; i < ip_editText.length; i++) {
            final int index = i;
            ip_editText[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ip_editText[index].getText().length() == 0) {
                        EToast.showToast(getActivity(), "输入框中参数非法!");
                    } else {
                        int tmp_val = Integer.parseInt(ip_editText[index].getText().toString());
                        tmp_val = Math.abs(tmp_val);
                        if (netCfg != null) {
                            netCfg.ip_address[index] = (byte) (0xFF & tmp_val);
                        }
                    }
                }
            });
        }

        /**监听服务器IP地址输入*/
        for (int i = 0; i < remote_ip_editText.length; i++) {
            final int index = i;
            remote_ip_editText[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (remote_ip_editText[index].getText().length() == 0) {
                        EToast.showToast(getActivity(), "输入框中参数非法!");
                    } else {
                        int tmp_val = Integer.parseInt(remote_ip_editText[index].getText().toString());
                        tmp_val = Math.abs(tmp_val);
                        if (netCfg != null) {
                            netCfg.remote_ip[index] = (byte) (0xFF & tmp_val);
                        }
                    }
                }
            });
        }

        /**监听端口输入*/
        port_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (port_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                } else {
                    int tmp_val = Integer.parseInt(port_editText.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (netCfg != null) {
                        netCfg.port = (short) (0xFFFF & tmp_val);
                    }
                }
            }
        });

        /**监听系统重置键*/
        sys_default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    netCfg.toDeault();
                    updateWidget();
                } else {
                    EToast.showToast(getActivity(), "相机" + cameraTag + "网络未连接");
                }
            }
        });

        /**监听系统应用键*/
        sys_apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    if (netCfg != null) {
                        CmdHandle.getInstance().applyCameraConf(cameraTag, NetUtils.MSG_NET_SETNET, netCfg.getBytes());
                        EToast.showToast(getActivity(), "已向相机" + cameraTag + "发送网络参数");
                    } else {
                        return;
                    }
                } else {
                    EToast.showToast(getActivity(), "相机" + cameraTag + "未连接，无法设置触发参数！！！");
                }
            }
        });

        /**监听系统保存键*/
        sys_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    if (netCfg != null) {
                        CmdHandle.getInstance().saveCameraConf(cameraTag, NetUtils.MSG_NET_SETNET, netCfg.getBytes());
                        EToast.showToast(getActivity(), "已向相机" + cameraTag + "发送网络参数");
                    } else {
                        return;
                    }
                } else {
                    EToast.showToast(getActivity(), "相机" + cameraTag + "未连接，无法设置触发参数！！！");
                }
            }
        });
    }

}
