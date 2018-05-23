package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.hanyu.hust.testnet.CameraConfigParam.TriggerConf;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;

/**
 * 触发模式参数配置类
 */
public class FragmentCameraTriggerConf extends BaseFragment {
    private String TAG = "trigger";

    private Spinner chooseCamera_spinner;
    private EditText trigger_delay_edit_text;
    private EditText part_delay_edit_text;
    private EditText depart_wide_edit_text;

    private Button sys_default_btn;
    private Button sys_apply_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;

    private String cameraTag = "1";     //全局变量，自动在spinner选择时更新
    private TriggerConf triggerCfg = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_camera_trig_conf, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidget();
        listnerInit();
    }


    /**
     * 根据相机参数更新界面
     */
    private void updateWidget() {
        if (triggerCfg != null) {
            enableWidget(true);
            trigger_delay_edit_text.setText("" + triggerCfg.trigDelay);
            part_delay_edit_text.setText("" + triggerCfg.partDelay);
            depart_wide_edit_text.setText("" + triggerCfg.departWide);
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
            if (triggerCfg != null) {
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
            triggerCfg = camera_cfg_param.triggerConf;
        }
    }

    /**
     * 从网络中提取触发参数，参数需要在handler中异步更新
     */
    private void getParamFromNet() {
        ((SystemConfig) getActivity()).getCameraConfigParamFromNet(cameraTag, updateWidgetHandler);
        Log.d(TAG, "onItemSelected:dot0 "+String.valueOf(cameraTag));
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

        trigger_delay_edit_text = (EditText) getActivity().findViewById(R.id.FTP_trigger_delay_edit_text);
        part_delay_edit_text = (EditText) getActivity().findViewById(R.id.FTP_part_delay_edit_text);
        depart_wide_edit_text = (EditText) getActivity().findViewById(R.id.FTP_depart_wide_edit_text);

        sys_default_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_default_btn.setText("重置");
        sys_apply_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_save_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

        sys_default_btn.setEnabled(false);
        sys_apply_btn.setEnabled(false);
        sys_save_btn.setEnabled(false);
    }

    private void enableWidget(boolean enabled) {
        trigger_delay_edit_text.setEnabled(enabled);
        part_delay_edit_text.setEnabled(enabled);
        depart_wide_edit_text.setEnabled(enabled);
    }

    /**
     * 界面监听
     */
    private void listnerInit() {
        /**监听相机选择Spinner*/
        chooseCamera_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cameraTag = String.valueOf(position + 1);
                ((SystemConfig) getActivity()).setCameraTag(cameraTag);
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    enableWidget(true);
                    getParamFromNet();
                    Log.d(TAG, "onItemSelected:dot1 ");
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

        trigger_delay_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (trigger_delay_edit_text.getText().length() == 0) {
                    EToast.showToast(getActivity(), "请输入触发距离！！！");
                } else {
                    int tmp_val = Integer.parseInt(trigger_delay_edit_text.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (triggerCfg != null) {
                        triggerCfg.trigDelay = tmp_val;
                    }
                }
            }
        });

        part_delay_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (part_delay_edit_text.getText().length() == 0) {
                    EToast.showToast(getActivity(), "请输入分拣距离！！！");
                } else {
                    int tmp_val = Integer.parseInt(part_delay_edit_text.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (triggerCfg != null) {
                        triggerCfg.partDelay = tmp_val;
                    }
                }
            }
        });


        depart_wide_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (depart_wide_edit_text.getText().length() == 0) {
                    EToast.showToast(getActivity(), "请输入吹气时间！！！");
                } else {
                    int tmp_val = Integer.parseInt(depart_wide_edit_text.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (triggerCfg != null) {
                        triggerCfg.departWide = tmp_val;
                    }
                }
            }
        });

        /**监听系统更新键*/
        sys_default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**从网络中获取相机参数,并对界面进行更新*/
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    triggerCfg.toDefault();
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
                    if (triggerCfg != null) {
                        CmdHandle.getInstance().applyCameraConf(cameraTag, NetUtils.MSG_NET_TRIGGER,triggerCfg.getBytes());
                        EToast.showToast(getActivity(), "应用成功!");
                    } else {
                        return;
                    }
                } else {
                    EToast.showToast(getActivity(), "相机" + cameraTag + "未连接，无法设置触发参数!");
                }
            }
        });

        /**监听系统保存键*/
        sys_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    if (triggerCfg != null) {
                        CmdHandle.getInstance().saveCameraConf(cameraTag, NetUtils.MSG_NET_TRIGGER,triggerCfg.getBytes());
                        EToast.showToast(getActivity(), "保存成功!");
                    } else {
                        return;
                    }
                } else {
                    EToast.showToast(getActivity(), "相机" + cameraTag + "未连接，无法设置触发参数!");
                }
            }
        });
    }

}
