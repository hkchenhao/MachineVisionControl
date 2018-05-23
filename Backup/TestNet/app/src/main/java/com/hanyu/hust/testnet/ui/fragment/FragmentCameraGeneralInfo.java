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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.CameraConfigParam.CameraConfigParam;
import com.hanyu.hust.testnet.CameraConfigParam.GeneralConf;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;

/**
 *  Created by WuXin on 2017/12
 *  系统设置界面下相机参数通用设置碎片
 */
public class FragmentCameraGeneralInfo extends BaseFragment {
    private String TAG = "generalInfo";
    /*界面组件*/
    private Spinner chooseCamera_spinner; //相机选择
    private Spinner inputType_spinner; //输入方式
    private Spinner outputType_spinner; //输出方式
    private EditText horzStartPix_editText; //行读出起始像素
    private EditText vertStartPix_editText; //列读出起始像素
    private EditText inputWidth_editText; //行输入宽度
    private EditText inputHeight_editText; //列输入高度
    private EditText outputWidth_editText;//行输出高度
    private EditText outputHeight_editText;//列输出高度
    //曝光时间配置
    private SeekBar expTime_seekBar;
    private EditText expTime_editText;
    //系统控制按钮
    private Button sys_default_btn;
    private Button sys_apply_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;
    //数据形式
    private RadioGroup bitType_radioGrp;
    private RadioButton[] bitType_radioBtn = new RadioButton[2];
    //初始化
    private RadioGroup inited_radioGrp;
    private RadioButton[] inited_radioBtn = new RadioButton[5];
    //触发方式
    private RadioGroup triggerMode_radioGrp;
    private RadioButton[] triggerMode_radioBtn = new RadioButton[3];

    private String cameraTag = "0";                     //全局变量，自动在spinner选择时更新
    public GeneralConf generalInfo = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_camera_general_info, container, false);
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
        if (generalInfo != null) {
            enableWidget(true);
            if (generalInfo.input <= Constants.CameraInputTypeList.length) {
                inputType_spinner.setSelection(generalInfo.input);
            }

            if (generalInfo.output <= Constants.CameraOutputTypeList.length) {
                outputType_spinner.setSelection(generalInfo.output);
            }

            horzStartPix_editText.setText(Integer.toString(generalInfo.horzStartPix));
            vertStartPix_editText.setText(Integer.toString(generalInfo.vertStartPix));

            inputWidth_editText.setText(Integer.toString(generalInfo.inWidth));
            inputHeight_editText.setText(Integer.toString(generalInfo.inHeight));

            outputWidth_editText.setText(Integer.toString(generalInfo.outWidth));
            outputHeight_editText.setText(Integer.toString(generalInfo.outHeight));

            expTime_seekBar.setProgress(generalInfo.expTime);
            expTime_editText.setText(Integer.toString(generalInfo.expTime));

            if ((generalInfo.bitType >> 4) == 0) {
                bitType_radioBtn[0].setChecked(true);
                bitType_radioBtn[1].setChecked(false);
            } else {
                bitType_radioBtn[0].setChecked(false);
                bitType_radioBtn[1].setChecked(true);
            }

            int tmpval = (generalInfo.bitType & 0x07);
            inited_radioBtn[tmpval].setChecked(true);

            if (generalInfo.trigger < 3) {
                triggerMode_radioBtn[generalInfo.trigger].setChecked(true);
            }
        } else {
            /**从网络中获取相机参数*/
            getParamFromNet();
        }
    }


    /**
     * 需要从canmeraConfigParam中提取generaal info
     *
     * @return
     */
    private void getParam() {
        CameraConfigParam camera_cfg_param = (((SystemConfig) getActivity()).getCameraConfigParam(cameraTag));
        if (camera_cfg_param != null) {
            generalInfo = camera_cfg_param.getGeneralInfo();
        }
    }

    /**
     * 从网络中提取触发参数，参数需要在handler中异步更新
     */
    private void getParamFromNet() {
        ((SystemConfig) getActivity()).getCameraConfigParamFromNet(cameraTag, updateWidgetHandler);
    }

    /**
     * 从网络中获取读参数后，用于界面更新
     */
    private Handler updateWidgetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getParam();
            if (generalInfo != null) {
                updateWidget();
                EToast.showToast(getActivity(), "更新成功!");
                sys_default_btn.setEnabled(true);
                sys_apply_btn.setEnabled(true);
                sys_save_btn.setEnabled(true);
            }
        }
    };


    /***
     * 界面初始化，UI控件绑定
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

        inputType_spinner = (Spinner) getActivity().findViewById(R.id.FCGI_inputType_spinner);
        ArrayAdapter<String> tmp_adapter1 = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner, Constants.CameraInputTypeList);
        inputType_spinner.setAdapter(tmp_adapter1);
        inputType_spinner.setEnabled(false);

        outputType_spinner = (Spinner) getActivity().findViewById(R.id.FCGI_outputType_spinner);
        ArrayAdapter<String> tmp_adapter2 = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner, Constants.CameraOutputTypeList);
        outputType_spinner.setAdapter(tmp_adapter2);

        horzStartPix_editText = (EditText) getActivity().findViewById(R.id.FCGI_horzStartPix_editText);
        vertStartPix_editText = (EditText) getActivity().findViewById(R.id.FCGI_vertStartPix_editText);
        inputWidth_editText = (EditText) getActivity().findViewById(R.id.FCGI_inputWidth_editText);
        inputHeight_editText = (EditText) getActivity().findViewById(R.id.FCGI_inputHeight_editText);
        outputWidth_editText = (EditText) getActivity().findViewById(R.id.FCGI_outputWidth_editText);
        outputHeight_editText = (EditText) getActivity().findViewById(R.id.FCGI_outputHeight_editText);

        expTime_seekBar = (SeekBar) getActivity().findViewById(R.id.FCGI_expTime_seekBar);
        expTime_editText = (EditText) getActivity().findViewById(R.id.FCGI_expTime_editText);

        bitType_radioGrp = (RadioGroup) getActivity().findViewById(R.id.FCGI_bitType_radioGroup);
        bitType_radioBtn[0] = (RadioButton) getActivity().findViewById(R.id.FCGI_bitType0_radioBtn);
        bitType_radioBtn[1] = (RadioButton) getActivity().findViewById(R.id.FCGI_bitType1_radioBtn);

        inited_radioGrp = (RadioGroup) getActivity().findViewById(R.id.FCGI_inited_radioGroup);
        inited_radioBtn[0] = (RadioButton) getActivity().findViewById(R.id.FCGI_inited0_radioBtn);
        inited_radioBtn[1] = (RadioButton) getActivity().findViewById(R.id.FCGI_inited1_radioBtn);
        inited_radioBtn[2] = (RadioButton) getActivity().findViewById(R.id.FCGI_inited2_radioBtn);
        inited_radioBtn[3] = (RadioButton) getActivity().findViewById(R.id.FCGI_inited3_radioBtn);
        inited_radioBtn[4] = (RadioButton) getActivity().findViewById(R.id.FCGI_inited4_radioBtn);

        triggerMode_radioGrp = (RadioGroup) getActivity().findViewById(R.id.FCGI_triggerMode_radioGroup);
        triggerMode_radioBtn[0] = (RadioButton) getActivity().findViewById(R.id.FCGI_triggerAuto_radioBtn);
        triggerMode_radioBtn[1] = (RadioButton) getActivity().findViewById(R.id.FCGI_triggerDsp_radioBtn);
        triggerMode_radioBtn[2] = (RadioButton) getActivity().findViewById(R.id.FCGI_triggerOutside_radioBtn);

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
     *  界面控件使能
     * @param enabled 界面控件使能命令
     */
    private void enableWidget(boolean enabled) {
        inputType_spinner.setEnabled(false);

        outputType_spinner.setEnabled(enabled);

        horzStartPix_editText.setEnabled(enabled);
        vertStartPix_editText.setEnabled(enabled);
        inputWidth_editText.setEnabled(enabled);
        inputHeight_editText.setEnabled(enabled);
        outputWidth_editText.setEnabled(enabled);
        outputHeight_editText.setEnabled(enabled);

        expTime_seekBar.setEnabled(enabled);
        expTime_editText.setEnabled(enabled);

        bitType_radioBtn[0].setEnabled(enabled);
        bitType_radioBtn[1].setEnabled(enabled);

        inited_radioBtn[0].setEnabled(enabled);
        inited_radioBtn[1].setEnabled(enabled);
        inited_radioBtn[2].setEnabled(enabled);
        inited_radioBtn[3].setEnabled(enabled);
        inited_radioBtn[4].setEnabled(enabled);

        triggerMode_radioBtn[0].setEnabled(enabled);
        triggerMode_radioBtn[1].setEnabled(enabled);
        triggerMode_radioBtn[2].setEnabled(enabled);
    }

    /**
     * 监听器初始化
     */
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

        outputType_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (generalInfo != null) {
                    generalInfo.output = (byte) (0xFF & position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /**监听start输入*/
        horzStartPix_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (horzStartPix_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                } else {
                    int tmp_val = Integer.parseInt(horzStartPix_editText.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (generalInfo != null) {
                        generalInfo.horzStartPix = (short) tmp_val;
                    }
                }
            }
        });
        vertStartPix_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (vertStartPix_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                } else {
                    int tmp_val = Integer.parseInt(vertStartPix_editText.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (generalInfo != null) {
                        generalInfo.vertStartPix = (short) tmp_val;
                    }
                }
            }
        });

        /**监听input输入*/
        inputWidth_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (inputWidth_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                } else {
                    int tmp_val = Integer.parseInt(inputWidth_editText.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    tmp_val = tmp_val >> 4;
                    tmp_val = tmp_val << 4;
                    if (generalInfo != null) {
                        generalInfo.inWidth = (short) tmp_val;
                    }
                }
            }
        });
        inputHeight_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (inputHeight_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                } else {
                    int tmp_val = Integer.parseInt(inputHeight_editText.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (generalInfo != null) {
                        generalInfo.inHeight = (short) tmp_val;
                    }
                }
            }
        });

        /**
         * 监听output输入
         */
        outputWidth_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (outputWidth_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                } else {
                    int tmp_val = Integer.parseInt(outputWidth_editText.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (tmp_val < 736) {
                        EToast.showToast(getActivity(), "后端显示宽度最少为736像素!");
                        tmp_val = 736;
                    }
                    if (generalInfo != null) {
                        generalInfo.outWidth = (short) tmp_val;
                    }
                }
            }
        });
        outputHeight_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (outputHeight_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                } else {
                    int tmp_val = Integer.parseInt(outputHeight_editText.getText().toString());
                    tmp_val = Math.abs(tmp_val);
                    if (tmp_val < 560) {
                        EToast.showToast(getActivity(), "后端显示高度最少为560像素!");
                        tmp_val = 560;
                    }
                    if (generalInfo != null) {
                        generalInfo.outHeight = (short) tmp_val;
                    }
                }
            }
        });

        /**
         * 监听曝光量seekbar
         */
        expTime_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(expTime_editText.getText().toString())) {
                    expTime_editText.setText(Integer.toString(progress));
                }
                if (generalInfo != null) {
                    generalInfo.expTime = (short) progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        /**监听曝光量文本输入*/
        expTime_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (expTime_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "曝光量输入非法");
                } else {
                    int tmp = Integer.parseInt(expTime_editText.getText().toString());
                    tmp = Math.abs(tmp);
                    if (tmp > 1720) {
                        tmp = 1720;
                    }
                    if (tmp != expTime_seekBar.getProgress()) {
                        expTime_seekBar.setProgress(tmp);
                    }
                }
            }
        });

        /**
         * 监听Bit per Pixel的输入
         */
        bitType_radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (R.id.FCGI_bitType0_radioBtn == i) {
                    if (generalInfo != null) {
                        generalInfo.bitType &= ~(1 << 4);
                    }
                } else if (R.id.FCGI_bitType1_radioBtn == i) {
                    if (generalInfo != null) {
                        generalInfo.bitType |= (1 << 4);
                    }
                }
            }
        });

        /**
         * 监听Left shit bit
         */
        inited_radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                generalInfo.bitType &= 0xF0;
                if (R.id.FCGI_inited0_radioBtn == i) {
                    if (generalInfo != null) {
                        generalInfo.bitType |= 0;
                    }
                } else if (R.id.FCGI_inited1_radioBtn == i) {
                    if (generalInfo != null) {
                        generalInfo.bitType |= 1;
                    }
                } else if (R.id.FCGI_inited2_radioBtn == i) {
                    if (generalInfo != null) {
                        generalInfo.bitType |= 2;
                    }
                } else if (R.id.FCGI_inited3_radioBtn == i) {
                    if (generalInfo != null) {
                        generalInfo.bitType |= 3;
                    }
                } else if (R.id.FCGI_inited4_radioBtn == i) {
                    if (generalInfo != null) {
                        generalInfo.bitType |= 4;
                    }
                }
            }
        });

        /**
         * 监听trigger radioButton
         */
        triggerMode_radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.FCGI_triggerAuto_radioBtn) {
                    if (generalInfo != null) {
                        generalInfo.trigger = 0;
                    }
                } else if (i == R.id.FCGI_triggerDsp_radioBtn) {
                    if (generalInfo != null) {
                        generalInfo.trigger = 1;
                    }
                } else if (i == R.id.FCGI_triggerOutside_radioBtn) {
                    if (generalInfo != null) {
                        generalInfo.trigger = 2;
                    }
                }
            }
        });
        /**监听系统重置键*/
        sys_default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    generalInfo.toDefault();
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
                    if (generalInfo != null) {
                        CmdHandle.getInstance().applyCameraConf(cameraTag, NetUtils.MSG_NET_GENERAL, generalInfo.getBytes());
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
                    if (generalInfo != null) {
                        CmdHandle.getInstance().saveCameraConf(cameraTag, NetUtils.MSG_NET_GENERAL, generalInfo.getBytes());
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
