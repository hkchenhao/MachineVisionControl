package com.hanyu.hust.testnet.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.HomeAcitivity;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.MyUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  Created by WuXin on 2017/12
 * 系统配置界面下工作模式选择碎片
 */
public class FragmentModeChoose extends BaseFragment {
  /**
   * UI组件
   */
    private Spinner chooseCamera_spinner;//相机选择
    private ImageView imageView;//图像
    /*控制按钮*/
    private Button sys_open_btn; //打开相应功能
    private Button sys_apply_btn; //暂未使用
    private Button sys_close_btn; //关闭
    private Button sys_exit_btn;//退出


    private Timer timer = null;
    private TimerTask timerTask = null;//计时任务

    private String cameraTag = "0";                     //全局变量，自动在spinner选择时更新
    private final int VideoTickTime = 500; //视频任务时长
    private boolean enabled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_mode_choose, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidget(); //绑定界面组件
        listenerInit(); //设置界面点击监听
        timerTaskInit(); //配置定时任务
        AppContext.setHandler(netHandler); //
    }


    /**
     * UI控件绑定初始化
     */
    private void initWidget() {
        chooseCamera_spinner = (Spinner) getActivity().findViewById(R.id.sp_camera_choice);

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

        imageView = (ImageView) getActivity().findViewById(R.id.FMC_imageView);

        sys_apply_btn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        sys_open_btn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        sys_close_btn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        sys_exit_btn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

        sys_apply_btn.setVisibility(View.INVISIBLE);
        sys_open_btn.setText("启用");
        sys_close_btn.setText("关闭");
        sys_exit_btn.setText("退出");

        sys_open_btn.setEnabled(!enabled);
        sys_close_btn.setEnabled(enabled);
    }

    /**
     * UI控件监听器初始化
     */
    private void listenerInit() {
        /**监听相机选择Spinner*/
        chooseCamera_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cameraTag = String.valueOf(position + 1);
                ((SystemConfig) getActivity()).setCameraTag(cameraTag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sys_open_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("请确认所选择的相机触发模式是否为auto？");
                dialog.setMessage("触发模式非auto将会导致相机工作不正常!!!\n如已确认触发模式，请点击“确定”\n如需进行触发模式设定，请点击“取消”");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (AppContext.getAppContext().isExist(cameraTag)) {
                            enabled = true;
                            sys_open_btn.setEnabled(!enabled);
                            sys_close_btn.setEnabled(enabled);
                        } else {
                            int tmp_index = Integer.parseInt(cameraTag) - 1;
                            if ((tmp_index >= 0) && (tmp_index < Constants.CameraDisconnectedWarnning.length)) {
                                EToast.showToast(getActivity(), Constants.CameraDisconnectedWarnning[tmp_index]);
                            }
                        }
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });

        sys_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enabled = false;
                sys_open_btn.setEnabled(!enabled);
                sys_close_btn.setEnabled(enabled);
            }
        });
    }

    /**
     * 处理视频流的异步handler
     */
    private Handler netHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == NetUtils.MSG_NET_GET_VIDEO) {
                imageView.setImageBitmap(MyUtils.handlerVideo(msg));
            }
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        enabled = false;

    }

    /**
     * 开始定时器任务
     */
    private void timerTaskInit() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (enabled) {
                    if (AppContext.getAppContext().isExist(cameraTag)) {
                        CmdHandle.getInstance().getVideo(cameraTag);
                    }
                }
            }
        };
        timer.schedule(timerTask, VideoTickTime, VideoTickTime);
    }

    @Override
    protected void onExit() {
        super.onExit();
        if (timer != null) {
            timer.cancel();
        }
    }
}

