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
import android.widget.SeekBar;
import android.widget.Spinner;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.CameraConfigParam.Ad9849Conf;
import com.hanyu.hust.testnet.CameraConfigParam.CameraConfigParam;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;



/**
 *  Created by WuXin on 2017/12
 *  系统设置界面下Ad9849配置碎片
 */

public class FragmentCameraAd9849Conf extends BaseFragment {
    /*界面资源*/
    private String TAG = "ad9849Conf";
    private Spinner chooseCamera_spinner; //相机选择spinner
    //VGA参数输入
    private SeekBar VGA_seekBar;
    private EditText VGA_editText;
    //RGDRV参数输入
    private SeekBar RGDRV_seekBar;
    private EditText RGDRV_editText;
    //SHP参数配置
    private SeekBar SHP_seekBar;
    private EditText SHP_editText;
    //SHD参数配置
    private SeekBar SHD_seekBar;
    private EditText SHD_editText;
    //HPL参数配置
    private SeekBar HPL_seekBar;
    private EditText HPL_editText;
    private SeekBar HNL_seekBar; //HNL滑条
    private EditText HNL_editText; //HNL参数输入
    //RGPL参数配置
    private SeekBar RGPL_seekBar;
    private EditText RGPL_editText;
    //RGNL参数配置
    private SeekBar RGNL_seekBar;
    private EditText RGNL_editText;
    //PXGA参数配置
    private SeekBar[] PXGA_seekBar = new SeekBar[4];
    private EditText[] PXGA_editText = new EditText[4];
    //HXDRV参数配置
    private SeekBar[] HXDRV_seekBar = new SeekBar[4];
    private EditText[] HXDRV_editText = new EditText[4];
    //按钮资源
    private Button sys_default_btn;
    private Button sys_apply_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;

    private String cameraTag = "0";                     //全局变量，自动在spinner选择时更新
    private Ad9849Conf ad9849Cfg = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_camera_ad9849_conf, container, false);
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
        if (ad9849Cfg != null) {
            enableWidget(true);
            int tmp_val = ((0xFF & ad9849Cfg.vga[1]) << 8) | (0xFF & ad9849Cfg.vga[0]);
            VGA_seekBar.setProgress(tmp_val);
            RGDRV_seekBar.setProgress(0xFF & ad9849Cfg.rgdrv);
            SHP_seekBar.setProgress(0xFF & ad9849Cfg.shp);
            SHD_seekBar.setProgress(0xFF & ad9849Cfg.shd);
            HPL_seekBar.setProgress(0xFF & ad9849Cfg.hp1);
            HNL_seekBar.setProgress(0xFF & ad9849Cfg.hn1);
            RGPL_seekBar.setProgress(0xFF & ad9849Cfg.rgp1);
            RGNL_seekBar.setProgress(0xFF & ad9849Cfg.rgn1);
            for (int i = 0; i < PXGA_seekBar.length; i++) {
                PXGA_seekBar[i].setProgress(0xFF & ad9849Cfg.pxga[i]);
                HXDRV_seekBar[i].setProgress(0xFF & ad9849Cfg.hxdrv[i]);
            }
        } else {
            /**从网络中获取相机参数*/
            getParamFromNet();
        }
    }


    /**
     * 需要从canmeraConfigParam中提取generaal info
     * @return
     */
    private void getParam() {
        CameraConfigParam camera_cfg_param = (((SystemConfig) getActivity()).getCameraConfigParam(cameraTag));
        if (camera_cfg_param != null) {
            ad9849Cfg = camera_cfg_param.ad9849Conf;
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
            if (ad9849Cfg != null) {
                updateWidget();
                EToast.showToast(getActivity(), "更新成功!");
                sys_default_btn.setEnabled(true);
                sys_apply_btn.setEnabled(true);
                sys_save_btn.setEnabled(true);
            }
        }
    };


    /**
     * 界面初始化
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

        VGA_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_VGA_seekBar);
        VGA_editText = (EditText) getActivity().findViewById(R.id.FCAC_VGA_editText);

        RGDRV_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_RGDRV_seekBar);
        RGDRV_editText = (EditText) getActivity().findViewById(R.id.FCAC_RGDRV_editText);

        SHP_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_SHP_seekBar);
        SHP_editText = (EditText) getActivity().findViewById(R.id.FCAC_SHP_editText);

        SHD_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_SHD_seekBar);
        SHD_editText = (EditText) getActivity().findViewById(R.id.FCAC_SHD_editText);

        HPL_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_HPL_seekBar);
        HPL_editText = (EditText) getActivity().findViewById(R.id.FCAC_HPL_editText);

        HNL_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_HNL_seekBar);
        HNL_editText = (EditText) getActivity().findViewById(R.id.FCAC_HNL_editText);

        RGPL_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_RGPL_seekBar);
        RGPL_editText = (EditText) getActivity().findViewById(R.id.FCAC_RGPL_editText);

        RGNL_seekBar = (SeekBar) getActivity().findViewById(R.id.FCAC_RGNL_seekBar);
        RGNL_editText = (EditText) getActivity().findViewById(R.id.FCAC_RGNL_editTText);

        PXGA_seekBar[0] = (SeekBar) getActivity().findViewById(R.id.FCAC_POGA_seekBar);
        PXGA_seekBar[1] = (SeekBar) getActivity().findViewById(R.id.FCAC_P1GA_seekBar);
        PXGA_seekBar[2] = (SeekBar) getActivity().findViewById(R.id.FCAC_P2GA_seekBar);
        PXGA_seekBar[3] = (SeekBar) getActivity().findViewById(R.id.FCAC_P3GA_seekBar);

        PXGA_editText[0] = (EditText) getActivity().findViewById(R.id.FCAC_P0GA_editText);
        PXGA_editText[1] = (EditText) getActivity().findViewById(R.id.FCAC_P1GA_editText);
        PXGA_editText[2] = (EditText) getActivity().findViewById(R.id.FCAC_P2GA_editText);
        PXGA_editText[3] = (EditText) getActivity().findViewById(R.id.FCAC_P3GA_editText);

        HXDRV_seekBar[0] = (SeekBar) getActivity().findViewById(R.id.FCAC_H1DRV_seekBar);
        HXDRV_seekBar[1] = (SeekBar) getActivity().findViewById(R.id.FCAC_H2DRV_seekBar);
        HXDRV_seekBar[2] = (SeekBar) getActivity().findViewById(R.id.FCAC_H3DRV_seekBar);
        HXDRV_seekBar[3] = (SeekBar) getActivity().findViewById(R.id.FCAC_H4DRV_seekBar);

        HXDRV_editText[0] = (EditText) getActivity().findViewById(R.id.FCAC_H1DRV_editText);
        HXDRV_editText[1] = (EditText) getActivity().findViewById(R.id.FCAC_H2DRV_editText);
        HXDRV_editText[2] = (EditText) getActivity().findViewById(R.id.FCAC_H3DRV_editText);
        HXDRV_editText[3] = (EditText) getActivity().findViewById(R.id.FCAC_H4DRV_editText);

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
     * 界面使能
     * @param enabled   使能标志位
     */
    private void enableWidget(boolean enabled) {
        VGA_seekBar.setEnabled(enabled);
        VGA_editText.setEnabled(enabled);
        RGDRV_seekBar.setEnabled(enabled);
        RGDRV_editText.setEnabled(enabled);
        SHP_seekBar.setEnabled(enabled);
        SHP_editText.setEnabled(enabled);
        SHD_seekBar.setEnabled(enabled);
        SHD_editText.setEnabled(enabled);
        HPL_seekBar.setEnabled(enabled);
        HPL_editText.setEnabled(enabled);
        HNL_seekBar.setEnabled(enabled);
        HNL_editText.setEnabled(enabled);
        RGPL_seekBar.setEnabled(enabled);
        RGPL_editText.setEnabled(enabled);
        RGNL_seekBar.setEnabled(enabled);
        RGNL_editText.setEnabled(enabled);
        for (int i = 0; i < PXGA_seekBar.length; i++) {
            PXGA_seekBar[i].setEnabled(enabled);
            PXGA_editText[i].setEnabled(enabled);
            HXDRV_seekBar[i].setEnabled(enabled);
            HXDRV_editText[i].setEnabled(enabled);
        }
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

        /**VGA*/
        VGA_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(VGA_editText.getText().toString())) {
                    VGA_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.vga[0] = (byte) (0xFF & progress);
                    ad9849Cfg.vga[1] = (byte) (0xFF & (progress >> 8));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        VGA_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (VGA_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    VGA_editText.setText(Integer.toString(VGA_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(VGA_editText.getText().toString());
                    if (tmp_val != VGA_seekBar.getProgress()) {
                        VGA_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**SHP*/
        SHP_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(SHP_editText.getText().toString())) {
                    SHP_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.shp = (byte) (0xFF & progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        SHP_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (SHP_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    SHP_editText.setText(Integer.toString(SHP_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(SHP_editText.getText().toString());
                    if (tmp_val != SHP_seekBar.getProgress()) {
                        SHP_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**HPL*/
        HPL_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(HPL_editText.getText().toString())) {
                    HPL_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.hp1 = (byte) (0xFF & progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        HPL_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (HPL_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    HPL_editText.setText(Integer.toString(HPL_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(HPL_editText.getText().toString());
                    if (tmp_val != HPL_seekBar.getProgress()) {
                        HPL_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**RGPL*/
        RGPL_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(RGPL_editText.getText().toString())) {
                    RGPL_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.rgp1 = (byte) (0xFF & progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        RGPL_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RGPL_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    RGPL_editText.setText(Integer.toString(HPL_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(RGPL_editText.getText().toString());
                    if (tmp_val != RGPL_seekBar.getProgress()) {
                        RGPL_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**PXGA*/
        for (int i = 0; i < PXGA_seekBar.length; i++) {
            final int pxga_index = i;
            PXGA_seekBar[pxga_index].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress != Integer.parseInt(PXGA_editText[pxga_index].getText().toString())) {
                        PXGA_editText[pxga_index].setText(Integer.toString(progress));
                    }
                    if (ad9849Cfg != null) {
                        ad9849Cfg.pxga[pxga_index] = (byte) (0xFF & progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            PXGA_editText[pxga_index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (PXGA_editText[pxga_index].getText().length() == 0) {
                        EToast.showToast(getActivity(), "输入框中参数非法!");
                        PXGA_editText[pxga_index].setText(Integer.toString(PXGA_seekBar[pxga_index].getProgress()));
                    } else {
                        int tmp_val = Integer.parseInt(PXGA_editText[pxga_index].getText().toString());
                        if (tmp_val != PXGA_seekBar[pxga_index].getProgress()) {
                            PXGA_seekBar[pxga_index].setProgress(tmp_val);
                        }
                    }
                }
            });
        }

        /**RGDRV*/
        RGDRV_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(RGDRV_editText.getText().toString())) {
                    RGDRV_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.rgdrv = (byte) (0xFF & progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        RGDRV_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RGDRV_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    RGDRV_editText.setText(Integer.toString(RGDRV_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(RGDRV_editText.getText().toString());
                    if (tmp_val != RGDRV_seekBar.getProgress()) {
                        RGDRV_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**SHD*/
        SHD_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(SHD_editText.getText().toString())) {
                    SHD_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.shd = (byte) (0xFF & progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        SHD_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (SHD_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    SHD_editText.setText(Integer.toString(SHD_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(SHD_editText.getText().toString());
                    if (tmp_val != SHD_seekBar.getProgress()) {
                        SHD_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**HNL*/
        HNL_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(HNL_editText.getText().toString())) {
                    HNL_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.hn1 = (byte) (0xFF & progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        HNL_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (HNL_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    HNL_editText.setText(Integer.toString(HNL_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(HNL_editText.getText().toString());
                    if (tmp_val != HNL_seekBar.getProgress()) {
                        HNL_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**RGNL*/
        RGNL_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != Integer.parseInt(RGNL_editText.getText().toString())) {
                    RGNL_editText.setText(Integer.toString(progress));
                }
                if (ad9849Cfg != null) {
                    ad9849Cfg.rgn1 = (byte) (0xFF & progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        RGNL_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (RGNL_editText.getText().length() == 0) {
                    EToast.showToast(getActivity(), "输入框中参数非法!");
                    RGNL_editText.setText(Integer.toString(RGNL_seekBar.getProgress()));
                } else {
                    int tmp_val = Integer.parseInt(RGNL_editText.getText().toString());
                    if (tmp_val != RGNL_seekBar.getProgress()) {
                        RGNL_seekBar.setProgress(tmp_val);
                    }
                }
            }
        });

        /**HXDRV*/
        for (int j = 0; j < HXDRV_seekBar.length; j++) {
            final int hxdrv_index = j;
            HXDRV_seekBar[hxdrv_index].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress != Integer.parseInt(HXDRV_editText[hxdrv_index].getText().toString())) {
                        HXDRV_editText[hxdrv_index].setText(Integer.toString(progress));
                    }
                    if (ad9849Cfg != null) {
                        ad9849Cfg.hxdrv[hxdrv_index] = (byte) (0xFF & progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            HXDRV_editText[hxdrv_index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (HXDRV_editText[hxdrv_index].getText().length() == 0) {
                        EToast.showToast(getActivity(), "输入框中参数非法!");
                        HXDRV_editText[hxdrv_index].setText(Integer.toString(HXDRV_seekBar[hxdrv_index].getProgress()));
                    } else {
                        int tmp_val = Integer.parseInt(HXDRV_editText[hxdrv_index].getText().toString());
                        if (tmp_val != HXDRV_seekBar[hxdrv_index].getProgress()) {
                            HXDRV_seekBar[hxdrv_index].setProgress(tmp_val);
                        }
                    }
                }
            });
        }

        /**监听系统重置键*/
        sys_default_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isExist(cameraTag)) {
                    ad9849Cfg.toDefault();
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
                    if (ad9849Cfg != null) {
                        CmdHandle.getInstance().applyCameraConf(cameraTag, NetUtils.MSG_NET_AD9849, ad9849Cfg.getBytes());
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
                    if (ad9849Cfg != null) {
                        CmdHandle.getInstance().saveCameraConf(cameraTag, NetUtils.MSG_NET_AD9849, ad9849Cfg.getBytes());
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

