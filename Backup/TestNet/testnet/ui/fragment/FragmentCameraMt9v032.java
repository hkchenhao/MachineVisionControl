package com.hanyu.hust.testnet.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.CameraConfigParam.Mt9v032Conf;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;

/**
 *  Created by HanYu on 2017/4
 *  系统配置界面下MT9v032碎片
 */

public class FragmentCameraMt9v032 extends BaseFragment {
    /*界面资源*/
    private String TAG = "mt9v032Conf";
    private Spinner chooseCamera_spinner;//相机选择

    private CheckBox autoAgc_checkBox;
    private SeekBar autoAgc_seekBar;
    private EditText autoAgc_editText;

    private CheckBox autoAec_checkBox;
    private SeekBar autoAec_seekBar;
    private EditText autoAec_editText;
    //界面控制按键
    private Button sys_default_btn;
    private Button sys_apply_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;

    private String cameraTag = "0";                     //全局变量，自动在spinner选择时更新
    private Mt9v032Conf mt9v032Cfg = null;
    private final int agcMin = 16;
    private final int agcMax = 64;
    private final int aecMin = 1;
    private final int aecMax = 480;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_camera_mt9v032, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWidget();
        initListener();
    }

    /**
     * 根据相机参数更新界面
     */
    private void updateWidget() {
        if (mt9v032Cfg == null) {
            mt9v032Cfg = new Mt9v032Conf();
        }

        enableWidget(true);

        if (mt9v032Cfg.isAgc > 0) {
            autoAgc_checkBox.setChecked(true);
        } else {
            autoAgc_checkBox.setChecked(false);
        }

        autoAgc_seekBar.setProgress(mt9v032Cfg.agVal);

        autoAgc_editText.setText(Integer.toString(mt9v032Cfg.agVal));

        if (mt9v032Cfg.isAec > 0) {
            autoAec_checkBox.setChecked(true);
        } else {
            autoAec_checkBox.setChecked(false);
        }

        autoAec_seekBar.setProgress(mt9v032Cfg.aeVal);

        autoAec_editText.setText(Integer.toString(mt9v032Cfg.aeVal));
    }


    /**
     * 需要从canmeraConfigParam中提取generaal info
     *
     * @return
     */
    private void getParam() {
        mt9v032Cfg = new Mt9v032Conf();
    }


    /**
     * 初始化界面，UI控件绑定
     */
    private void initWidget() {
        chooseCamera_spinner = (Spinner) getActivity().findViewById(R.id.sp_camera_choice);
        ArrayAdapter<String> tmp_adapter0 = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner, Constants.camList);
        chooseCamera_spinner.setAdapter(tmp_adapter0);

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

        autoAgc_checkBox = (CheckBox) getActivity().findViewById(R.id.FCMC_autoAgc_checkBox);
        autoAgc_seekBar = (SeekBar) getActivity().findViewById(R.id.FCMC_autoAgc_seekBar);
        autoAgc_editText = (EditText) getActivity().findViewById(R.id.FCMC_autoAgc_editText);

        autoAec_checkBox = (CheckBox) getActivity().findViewById(R.id.FCMC_autoAec_checkBox);
        autoAec_seekBar = (SeekBar) getActivity().findViewById(R.id.FCMC_autoAec_seekBar);
        autoAec_editText = (EditText) getActivity().findViewById(R.id.FCMC_autoAec_editText);

        sys_default_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_default_btn.setText("重置");
        sys_apply_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_save_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

    }

    /***
     * 界面使能
     * @param enabled
     */
    private void enableWidget(boolean enabled) {
        autoAgc_checkBox.setEnabled(enabled);
        autoAgc_seekBar.setEnabled(enabled);
        autoAgc_editText.setEnabled(enabled);

        autoAec_checkBox.setEnabled(enabled);
        autoAec_seekBar.setEnabled(enabled);
        autoAec_editText.setEnabled(enabled);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        /**监听相机选择Spinner*/
        chooseCamera_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cameraTag = String.valueOf(position + 1);
                ((SystemConfig) getActivity()).setCameraTag(cameraTag);
                if (AppContext.getAppContext().isExist(cameraTag)){
                    enableWidget(true);
                    updateWidget();
                }else{
                    enableWidget(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /**监听auto agc check box*/
        autoAgc_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoAgc_checkBox.isChecked()) {
                    if (mt9v032Cfg != null) {
                        mt9v032Cfg.isAgc = 1;
                    }
                } else {
                    if (mt9v032Cfg != null) {
                        mt9v032Cfg.isAgc = 0;
                    }
                }
            }
        });

        /**监听auto agc seekbar*/
        autoAgc_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int tmp_val = progress;
                if (tmp_val < agcMin) {
                    tmp_val = agcMin;
                    autoAgc_seekBar.setProgress(tmp_val);
                }
                if (tmp_val != Integer.parseInt(autoAgc_editText.getText().toString())) {
                    autoAgc_editText.setText(Integer.toString(tmp_val));
                }
                if (mt9v032Cfg != null) {
                    mt9v032Cfg.agVal = (short) tmp_val;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        autoAgc_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {//校验输入参数
                if (autoAgc_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "参数非法");
                    autoAgc_editText.setText(Integer.toString(autoAgc_seekBar.getProgress()));
                } else {
                    int tmp = Integer.parseInt(autoAgc_editText.getText().toString());
                    tmp = Math.abs(tmp);
                    if (tmp > agcMax) {
                        tmp = agcMax;
                    } else if (tmp < agcMin) {
                        tmp = agcMin;
                    }
                    if (tmp != autoAgc_seekBar.getProgress()) {
                        autoAgc_seekBar.setProgress(tmp);
                    }
                }
            }
        });

        /**监听auto aec check box*/
        autoAec_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoAec_checkBox.isChecked()) {
                    if (mt9v032Cfg != null) {
                        mt9v032Cfg.isAec = 1;
                    }
                } else {
                    if (mt9v032Cfg != null) {
                        mt9v032Cfg.isAec = 0;
                    }
                }
            }
        });

        /**监听auto aec seekbar*/
        autoAec_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int tmp_val = progress;
                if (tmp_val < aecMin) {
                    tmp_val = aecMin;
                    autoAec_seekBar.setProgress(tmp_val);
                }
                if (tmp_val != Integer.parseInt(autoAec_editText.getText().toString())) {
                    autoAec_editText.setText(Integer.toString(tmp_val));
                }
                if (mt9v032Cfg != null) {
                    mt9v032Cfg.aeVal = (short) tmp_val;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        autoAec_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (autoAec_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "参数非法");
                    autoAec_editText.setText(Integer.toString(autoAec_seekBar.getProgress()));
                } else {
                    int tmp = Integer.parseInt(autoAec_editText.getText().toString());
                    tmp = Math.abs(tmp);
                    if (tmp > aecMax) {
                        tmp = aecMax;
                    } else if (tmp < aecMin) {
                        tmp = aecMin;
                    }
                    if (tmp != autoAec_seekBar.getProgress()) {
                        autoAec_seekBar.setProgress(tmp);
                    }
                }
            }
        });

        /**监听系统重置键*/
        sys_default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mt9v032Cfg.toDefault();
                updateWidget();
            }
        });

        /**监听系统应用键*/
        sys_apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    if (mt9v032Cfg != null) {
                        CmdHandle.getInstance().applyCameraConf(cameraTag, NetUtils.MSG_NET_MT9V032,mt9v032Cfg.getBytes());
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
                    if (mt9v032Cfg != null) {
                        CmdHandle.getInstance().saveCameraConf(cameraTag, NetUtils.MSG_NET_MT9V032,mt9v032Cfg.getBytes());
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
