package com.hanyu.hust.testnet.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.MyUtils;

import org.w3c.dom.Text;

/**
 *  Created by WuXin on 2017/12
 * 系统设置界面下光源校正碎片
 */

public class FragmentCalibrateLightSrc extends Fragment {


    private int light_src_index = 0;
    private int index_max = 0;

    private String camera_tag = "0";

    private final int standard_brightness = 179;//参考光照值
    //通用控制帧参数
    private final byte page_addr = 0x04;//界面地址 用于通用控制帧
    private final byte fr_addr = 0x42; //碎片地址 用于通用控制帧
    private final byte Pn_DCN = 0x01;
    private final int CMD_Start_Test = 1;
    private final int CMD_Cancel_Test = 2;
    //通用空指针默认数值
    private final byte Pn_Even_Test_Result = 0x20;
    private final byte Pn_Brightness_Test_Result = 0x21;
    private final byte Pn_Average_Brightness = 0x22;
    private final byte Pn_Right_Brightness = 0x23;
    private final byte Pn_Brightness_Result = 0x24;

    private static Bitmap image_bitmap; //图像
    /*界面组件*/
    private Spinner choose_light_src_spinner;
    private TextView even_result_text_view;//任务结果
    private TextView brightness_result_text_view;//光照测试结果
    private Button start_test_button; //开始测试按钮
    private Button cancel_test_button; //结束测试按钮
    private ImageView image_view; //图像显示
    private TextView average_brightness_text_view;//平均光照度显示
    private TextView right_brightness_text_view; //光照度显示
    private TextView brightness_ok_text_view; //光照是否合格

    private Button sys_edit_btn;
    private Button sys_default_btn;
    private Button sys_save_btn;
    private Button sys_exit_btn;

    private Handler default_net_handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_calibrate_light_src, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidget();//绑定界面组件
        listenerInit();//初始化界面监听函数
        handlerInit();//建立通信
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initWidget() {
        choose_light_src_spinner = (Spinner) getActivity().findViewById(R.id.FCLS_choose_light_src_spinner);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner, Constants.LSDSettingCnList);
        choose_light_src_spinner.setAdapter(spinner_adapter);
        index_max = Constants.LSDSettingCnList.length - 1;

        even_result_text_view = (TextView) getActivity().findViewById(R.id.FCLS_even_test_result_text_view);
        brightness_result_text_view = (TextView) getActivity().findViewById(R.id.FCLS_brightness_test_result_text_view);
        start_test_button = (Button) getActivity().findViewById(R.id.FCLS_start_btn);
        cancel_test_button = (Button) getActivity().findViewById(R.id.FCLS_cancel_btn);

        start_test_button.setEnabled(true);
        cancel_test_button.setEnabled(false);

        image_view = (ImageView) getActivity().findViewById(R.id.FCLS_image_view);
        average_brightness_text_view = (TextView) getActivity().findViewById(R.id.FCLS_average_brightness_text_view);
        right_brightness_text_view = (TextView) getActivity().findViewById(R.id.FCLS_right_brightness_text_view);
        brightness_ok_text_view = (TextView) getActivity().findViewById(R.id.FCLS_brightness_ok_text_view);

        sys_edit_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_default_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_save_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

        sys_edit_btn.setVisibility(View.INVISIBLE);
        sys_default_btn.setVisibility(View.INVISIBLE);
        sys_save_btn.setVisibility(View.INVISIBLE);
        sys_exit_btn.setText("退出");
    }

    /**
     * 添加界面点击监听
     */
    private void listenerInit() {
        /**监听光源选择Spinner*/
        choose_light_src_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                light_src_index = position;
                if (position == 1) {
                    camera_tag = "1";
                } else if ((position == 2) || (position == 3)) {
                    camera_tag = "2";
                } else if ((position == 4) || (position == 5)) {
                    camera_tag = "3";
                } else {
                    camera_tag = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        start_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (light_src_index != 0) {
                    /**设定光源强度*/
                    for (int i = 1; i <= index_max; i++) {
                        if (light_src_index != i) {
                            if (FragmentCanLightSrcDrv.setLightSrcBrightness(i, 0) < 0) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setTitle("警告:请在系统设置中完善光源通道分配!!!");
                                dialog.setPositiveButton("确定", null);
                                dialog.setNegativeButton("取消", null);
                                dialog.show();
                                return;
                            }
                        } else {
                            if (FragmentCanLightSrcDrv.setLightSrcBrightness(i, standard_brightness) < 0) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setTitle("警告:请在系统设置中完善光源通道分配!!!");
                                dialog.setPositiveButton("确定", null);
                                dialog.setNegativeButton("取消", null);
                                dialog.show();
                                return;
                            }
                        }
                    }

                    /**发送启动测试命令*/
                    byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Start_Test);
                    if (data != null) {
                        CmdHandle.getInstance().sendCmdInfo(camera_tag, data);
                        start_test_button.setEnabled(false);
                        cancel_test_button.setEnabled(true);
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("警告:请选择光源!!!");
                    dialog.setPositiveButton("确定", null);
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                }

            }
        });

        cancel_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (light_src_index != 0) {
                    /**发送取消测试命令*/
                    byte[] data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Cancel_Test);
                    if (data != null) {
                        CmdHandle.getInstance().sendCmdInfo(camera_tag, data);
                        start_test_button.setEnabled(true);
                        cancel_test_button.setEnabled(false);
                    }
                } else {
                    start_test_button.setEnabled(true);
                    cancel_test_button.setEnabled(false);
                }
            }
        });

        /**点击图像视窗获取图像*/
        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (light_src_index != 0) {
                    CmdHandle.getInstance().getImage(camera_tag, default_net_handler);
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("警告:请选择光源!!!");
                    dialog.setPositiveButton("确定", null);
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                }
            }
        });
    }

    /**
     * 建立通信连接
     */
    private void handlerInit() {
        default_net_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case NetUtils.MSG_NET_GET_IMAGE: {  /**当前原图像帧*/
                        image_bitmap = MyUtils.handlerRoi(msg);
                        if (image_bitmap != null) {
                            image_view.setImageBitmap(image_bitmap);
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
                                        case Pn_Even_Test_Result: {
                                            if (param[i] > 0) {
                                                even_result_text_view.setText("合格");
                                            } else {
                                                even_result_text_view.setText("不合格");
                                            }
                                            break;
                                        }
                                        case Pn_Brightness_Test_Result: {
                                            if (param[i] > 0) {
                                                brightness_result_text_view.setText("合格");
                                            } else {
                                                brightness_result_text_view.setText("不合格");
                                            }
                                            break;
                                        }
                                        case Pn_Average_Brightness: {
                                            average_brightness_text_view.setText(String.valueOf(param[i]));
                                            break;
                                        }
                                        case Pn_Right_Brightness: {
                                            right_brightness_text_view.setText(String.valueOf(param[i]));
                                            break;
                                        }
                                        case Pn_Brightness_Result: {
                                            if (param[i] > 0) {
                                                brightness_ok_text_view.setText("合格");
                                            } else {
                                                brightness_ok_text_view.setText("不合格");
                                            }
                                            /**回复取消帧*/
                                            byte[] tx_data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Cancel_Test);
                                            if (tx_data != null) {
                                                CmdHandle.getInstance().sendCmdInfo(camera_tag, tx_data);
                                            }
                                            start_test_button.setEnabled(true);
                                            cancel_test_button.setEnabled(false);
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

        AppContext.setHandler(default_net_handler);
    }
}
