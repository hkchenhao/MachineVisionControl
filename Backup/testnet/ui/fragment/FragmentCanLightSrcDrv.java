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
import com.hanyu.hust.testnet.can.CanMsg;
import com.hanyu.hust.testnet.entity.CanParameter;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xinshangqiu on 2017/9/23.
 */

public class FragmentCanLightSrcDrv extends Fragment {
    public String TAG = "Light Src Drv";
    public Button readBtn, writeBtn, saveBtn, exitBtn;
    public SeekBar seekBar[] = new SeekBar[8];
    public EditText editText[] = new EditText[8];
    public Button decBtn[] = new Button[8];
    public Button incBtn[] = new Button[8];

    private Timer timer;
    private TimerTask timerTask;
    private boolean heartBeatAckFlag = false;
    private int missConnectTimeout = 0;
    public TextView canConectStatusText;

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
        seekBarListenerInit();
        decBtnListenerInit();
        incBtnListenerInit();
        sysBtnListenerInit();
        timerTaskInit();

        AppContext.can0Thread.setHandler(canRxHandler);         /**设置Handler*/
    }

    private void initWidget() {
        readBtn = (Button) getActivity().findViewById(R.id.bt_fg_refresh);
        readBtn.setText("刷新");
        writeBtn = (Button) getActivity().findViewById(R.id.bt_fg_apply);
        writeBtn.setText("应用");
        saveBtn = (Button) getActivity().findViewById(R.id.bt_fg_save);
        exitBtn = (Button) getActivity().findViewById(R.id.bt_fg_exit);

        editText[0] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText1);
        editText[1] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText2);
        editText[2] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText3);
        editText[3] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText4);
        editText[4] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText5);
        editText[5] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText6);
        editText[6] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText7);
        editText[7] = (EditText) getActivity().findViewById(R.id.lightSrcDrvEditText8);

        editText[0].setEnabled(false);
        editText[1].setEnabled(false);
        editText[2].setEnabled(false);
        editText[3].setEnabled(false);
        editText[4].setEnabled(false);
        editText[5].setEnabled(false);
        editText[6].setEnabled(false);
        editText[7].setEnabled(false);

        seekBar[0] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar1);
        seekBar[1] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar2);
        seekBar[2] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar3);
        seekBar[3] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar4);
        seekBar[4] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar5);
        seekBar[5] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar6);
        seekBar[6] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar7);
        seekBar[7] = (SeekBar) getActivity().findViewById(R.id.lightSrcDrvSeekBar8);

        decBtn[0] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn1);
        decBtn[1] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn2);
        decBtn[2] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn3);
        decBtn[3] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn4);
        decBtn[4] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn5);
        decBtn[5] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn6);
        decBtn[6] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn7);
        decBtn[7] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessDecBtn8);

        incBtn[0] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn1);
        incBtn[1] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn2);
        incBtn[2] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn3);
        incBtn[3] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn4);
        incBtn[4] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn5);
        incBtn[5] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn6);
        incBtn[6] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn7);
        incBtn[7] = (Button) getActivity().findViewById(R.id.lightSrcDrvBrightnessIncBtn8);

        canConectStatusText = (TextView) getActivity().findViewById(R.id.lightSrcDrvConnectStatusText);
    }

    private void seekBarListenerInit() {
        seekBar[0].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[0].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBar[1].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[1].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar[2].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[2].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar[3].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[3].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar[4].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[4].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar[5].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[5].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar[6].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[6].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar[7].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText[7].setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void decBtnListenerInit() {
        decBtn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[0].setProgress(seekBar[0].getProgress() - 1);
            }
        });

        decBtn[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[1].setProgress(seekBar[1].getProgress() - 1);
            }
        });

        decBtn[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[2].setProgress(seekBar[2].getProgress() - 1);
            }
        });

        decBtn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[3].setProgress(seekBar[3].getProgress() - 1);
            }
        });

        decBtn[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[4].setProgress(seekBar[4].getProgress() - 1);
            }
        });

        decBtn[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[5].setProgress(seekBar[5].getProgress() - 1);
            }
        });

        decBtn[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[6].setProgress(seekBar[6].getProgress() - 1);
            }
        });

        decBtn[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[7].setProgress(seekBar[7].getProgress() - 1);
            }
        });
    }

    private void incBtnListenerInit() {
        incBtn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[0].setProgress(seekBar[0].getProgress() + 1);
            }
        });

        incBtn[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[1].setProgress(seekBar[1].getProgress() + 1);
            }
        });

        incBtn[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[2].setProgress(seekBar[2].getProgress() + 1);
            }
        });

        incBtn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[3].setProgress(seekBar[3].getProgress() + 1);
            }
        });

        incBtn[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[4].setProgress(seekBar[4].getProgress() + 1);
            }
        });

        incBtn[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[5].setProgress(seekBar[5].getProgress() + 1);
            }
        });

        incBtn[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[6].setProgress(seekBar[6].getProgress() + 1);
            }
        });

        incBtn[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar[7].setProgress(seekBar[7].getProgress() + 1);
            }
        });
    }

    private void sysBtnListenerInit() {
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 8; i++) {
                    AppContext.can0Thread.write(AppContext.can0Thread.createParamReadMsg(CanParameter.LighrSrcDrv_Addr,(short) (CanParameter.PN_BRIGHTNESS0 + i)));
                }
            }
        });

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 8; i++) {
                    AppContext.can0Thread.write(
                            AppContext.can0Thread.createParamWriteMsg(CanParameter.LighrSrcDrv_Addr,(short) (CanParameter.PN_BRIGHTNESS0 + i), seekBar[i].getProgress())
                    );
                }
            }
        });
    }

    private void timerTaskInit() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (heartBeatAckFlag == true) {
                    heartBeatAckFlag = false;
                    missConnectTimeout = 0;
                } else {
                    missConnectTimeout++;
                }
                Message message = new Message();
                if (missConnectTimeout < CanParameter.HBA_Timeout) {
                    message.what = 0;
                } else {
                    message.what = 1;
                }
                timerTaskHandler.sendMessage(message);
            }
        };
        timer.schedule(timerTask, 500, 1000);
    }

    private Handler timerTaskHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                canConectStatusText.setText("已连接");
                canConectStatusText.setTextColor(Color.GREEN);
            } else {
                canConectStatusText.setText("未连接");
                canConectStatusText.setTextColor(Color.BLACK);
            }
        }
    };

    private Handler canRxHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == CanParameter.Terminal_Addr) {
                CanMsg rxMsg = new CanMsg();
                rxMsg = (CanMsg) msg.obj;
                if (rxMsg.data[0] == CanParameter.LighrSrcDrv_Addr) {
                    switch (rxMsg.data[1]) {
                        case CanParameter.PF_HB: {
                            heartBeatAckFlag = true;
                            break;
                        }
                        case CanParameter.PF_PRA: {
                            seekBar[rxMsg.data[2] - CanParameter.PN_BRIGHTNESS0].setProgress(rxMsg.data[3]);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        }
    };

    private Handler timeTaskHandler = new Handler() {
        public void handleMessage(Message msg) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppContext.can0Thread.setDefaultHandler();
    }
}
