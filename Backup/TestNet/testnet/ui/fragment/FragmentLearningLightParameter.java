package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.ui.view.SeekBarEditLayout;
import com.hanyu.hust.testnet.ui.view.XAxisValueFormatter;
import com.hanyu.hust.testnet.utils.LogUtil;
import com.hanyu.hust.testnet.utils.MyUtils;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HeQian on 2017/11/26.
 * 机器学习界面下光源参数配置碎片
 */

public class FragmentLearningLightParameter extends Fragment implements View.OnClickListener, OperationInterface {

    private static final String TAG = "BUGTEST";
    /**
     * UI组件
     */
    private View mView;
    private BarChart mBarChart; //灰度直方图

    private ImageView imv1; //图像回传
    private Button btnSave, btnRecover, btnConfirm1, btnConfirm2, btnTest, btnGetRoi, btnGetImage; //控制按钮
    private SeekBarEditLayout se1, se2; //光照参数
    private Spinner spCam;//相机选择
    private TextView textNote1, textNote2;//灰度值范围

    private final String[] cam_list = {"相机1", "相机2", "相机3"};
    private int lightPara1, lightPara2;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MachineLearning) {
            MachineLearning machineLearning = (MachineLearning) activity;
        }
        Log.d(TAG, "onCreateView: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_lightparameter_learning, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        Log.d(TAG, "onCreateView: ");
        return mView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initDefault();
        AppContext.setHandler(mHandler);
        Log.d(TAG, "onActivityCreated: 0");
    }

    /**
     * 绑定界面组件
     * */
    private void initView() {

        spCam = (Spinner) getView().findViewById(R.id.sp_cam_choose);
        se1 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra1);
        se2 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra2);
        imv1 = (ImageView) getView().findViewById(R.id.imageBtn1);
        //创建柱状图
        mBarChart = (BarChart) getView().findViewById(R.id.mBarChart);
        mBarChart.setEnabled(false);

        btnConfirm1 = (Button) getView().findViewById(R.id.bt_confirm1);
        btnConfirm2 = (Button) getView().findViewById(R.id.bt_confirm2);
        btnRecover = (Button) getView().findViewById(R.id.btn_recover);
        btnSave = (Button) getView().findViewById(R.id.btn_save);
        btnTest = (Button) getView().findViewById(R.id.btn_TEST);
        textNote1 = (TextView) getView().findViewById(R.id.tx_note1);
        textNote2 = (TextView) getView().findViewById(R.id.tx_note2);

        initSpinner();
        initse();
        initBarChart();
    }

    /*
    *初始化参数*/
    private void initDefault() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null && ((MachineLearning) getActivity()).getButtonId() != "") return;
        else {
            btnConfirm1.setEnabled(false);
            btnConfirm2.setEnabled(false);
            btnSave.setEnabled(false);
            btnRecover.setEnabled(false);
            imv1.setEnabled(false);
            se1.setEnabled(false);
            se2.setEnabled(false);
            spCam.setEnabled(false);
        }
    }


    /**
     * 异步事件处理器
     * 1.接收显示图像数据
     * 2.图像光照度信息
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.d(TAG, "handleMessage");
            switch (msg.what) {
                case NetUtils.MSG_NET_GET_IMAGE:
                    handlerImage(msg);
                    break;
                case NetUtils.MSG_NET_CMD:
                    handlerCmd(msg);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    @OnClick({R.id.btn_TEST, R.id.btn_save, R.id.btn_recover, R.id.imageBtn1, R.id.bt_confirm1, R.id.bt_confirm2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_TEST:
                byte[] data = NetUtils.createNetCmdData((byte) 3, (byte) 0x33, (byte) 0, 0);
                if (data != null) {
                    CmdHandle.getInstance().sendCmdInfo(String.valueOf(getCamId()), data);
                }
//                // TODO: 2017/11/27
//                drawBarChart(MyUtils.getGrayArray(AppContext.btnBitmapF));
                break;
            case R.id.btn_save:
                DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                        .setTitle("确定保存" + "相机" + String.valueOf(getCamId()) + "光源参数信息?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                saveParameter(getCamId());
                                ((MachineLearning) getActivity()).writeStatus("相机" + String.valueOf(getCamId()) + "光源参数:修改完成");
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialogWindow.showWrapContent();
                break;
            case R.id.btn_recover:
                DialogWindow dialogWindow2 = new DialogWindow.Builder(getActivity())
                        .setTitle("确定恢复" + "相机" + String.valueOf(getCamId()) + "光源参数信息?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                recoverParameter(getCamId());
                                ((MachineLearning) getActivity()).writeStatus("相机" + String.valueOf(getCamId()) + "光源参数:恢复完成");
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialogWindow2.showWrapContent();
                break;
            case R.id.bt_confirm1:
            case R.id.bt_confirm2:
                lightPara1 = se1.getValue();
                lightPara2 = se2.getValue();
                FragmentCanLightSrcDrv.setLightSrcBrightness(getCamId(), lightPara1, lightPara2);
                break;
            case R.id.imageBtn1:
                CmdHandle.getInstance().getImage(String.valueOf(getCamId()), mHandler);
                break;
            default:
                break;
        }
    }


    /*
    *界面资源初始化方法集合
    * **/
    private void initse() {
        se1.setMax(255);
        se2.setMax(255);
        se1.setonActionListener(new SeekBarEditLayout.ActionListener() {
            @Override
            public void onChange(SeekBar seekBar) {
                lightPara1 = se1.getValue();
            }
        });
        se2.setonActionListener(new SeekBarEditLayout.ActionListener() {
            @Override
            public void onChange(SeekBar seekBar) {
                lightPara2 = se2.getValue();
            }
        });
    }

    private void initSpinner() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, cam_list);
        spCam.setAdapter(adapter1);
        spCam.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                                int pos = spCam.getSelectedItemPosition();
                                                JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();

                                                if (jsonTotal != null) {
                                                    try {
                                                        JSONObject json = jsonTotal.getJSONObject(ButtonKeys.SYSTEMPARAMETER);
                                                        lightPara1 = json.getInt(ButtonKeys.litgtSy[pos * 2]);
                                                        lightPara2 = json.getInt(ButtonKeys.litgtSy[pos * 2 + 1]);
                                                        se1.setValue(lightPara1);
                                                        se2.setValue(lightPara2);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                                JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
                                                if (jsonTotal != null) {
                                                    try {
                                                        JSONObject json = jsonTotal.getJSONObject(ButtonKeys.SYSTEMPARAMETER);
                                                        lightPara1 = json.getInt(ButtonKeys.litgtSy[0]);
                                                        lightPara2 = json.getInt(ButtonKeys.litgtSy[1]);
                                                        se1.setValue(lightPara1);
                                                        se2.setValue(lightPara2);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                }
                                            }
                                        }
        );
    }

    /*
    *点击事件方法集合
    * */
    private void saveParameter(int cam) {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            if (!jsonTotal.isNull(ButtonKeys.SYSTEMPARAMETER))
                json = jsonTotal.getJSONObject(ButtonKeys.SYSTEMPARAMETER);
            json.put(ButtonKeys.litgtSy[cam * 2 - 2], se1.getValue());
            json.put(ButtonKeys.litgtSy[cam * 2 - 1], se2.getValue());
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
        try {
            jsonTotal.put(ButtonKeys.SYSTEMPARAMETER, json);
            SettingProfile buttonProfile = new SettingProfile(((MachineLearning) getActivity()).getButtonId());
            buttonProfile.write2sd(jsonTotal);
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
    }

    /**
     * 回读JS文件光照度配置值
     * @param cam
     */
    private void recoverParameter(int cam) {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.SYSTEMPARAMETER);
                lightPara1 = json.getInt(ButtonKeys.litgtSy[cam * 2 - 2]);
                lightPara2 = json.getInt(ButtonKeys.litgtSy[cam * 2 - 1]);
                se1.setValue(lightPara1);
                se2.setValue(lightPara2);
                FragmentCanLightSrcDrv.setLightSrcBrightness(cam, lightPara1, lightPara2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    /*
    *功能函数方法集合
    * */
    private int getCamId() {
        return spCam.getSelectedItemPosition() + 1;
    }

    private void setLightPara() {

    }

    private void handlerImage(Message msg) {
        Bitmap bitmap = MyUtils.handlerRoi(msg);
        imv1.setImageBitmap(bitmap);
        drawBarChart(MyUtils.getGrayArray(bitmap));
    }

    /**
     * 解析DSP分析结果帧
     * @param msg
     */
    private void handlerCmd(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null)
            return;
        byte type = data[0];
        byte function = data[1];
        byte lightType = data[3];
        int lightPara = (data[4] & 0xFF);
        if ((type != 3) || (function != 0x33)) return;
        switch (lightType) {
            case 0:
                textNote2.setText("亮度：" + String.valueOf(lightPara) + "（合适）");
                break;
            case 1:
                textNote2.setText("亮度：" + String.valueOf(lightPara) + "（过亮）");
                break;
            case 2:
                textNote2.setText("亮度：" + String.valueOf(lightPara) + "（过暗）");
                break;
            case 15:
                textNote2.setText("亮度：" + "未知");
            default:
                textNote2.setText("亮度：" + "未知");
                break;
        }
        byte[] dat = NetUtils.createNetCmdData((byte) 3, (byte) 0x33, (byte) 0, 1);
        if (dat != null) {
            CmdHandle.getInstance().sendCmdInfo(String.valueOf(getCamId()), dat);
        }
    }

    /**
     * 初始化灰度直方图设置
     */
    private void initBarChart() {

        //基本属性设置
        mBarChart.setEnabled(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setExtraOffsets(3, 5, 3, 5);

        // 创建X轴
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10);
        //创建Y轴
        YAxis yAxis = mBarChart.getAxisLeft();
        mBarChart.getAxisRight().setEnabled(false);//关闭右侧y轴
        mBarChart.getAxisLeft().setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setTextSize(5);
        //关闭图例
        mBarChart.getLegend().setEnabled(false);
        //设置数据
    }

    /**
     * 获取阵列最大值
     * @param array
     * @return
     */
    private int getMax(int[] array) {
        if (array != null) {
            int max = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] > max) {
                    max = array[i];
                }
            }
            return max;
        } else {
            return 0;
        }
    }

    /**
     * 绘制灰度直方图
     * @param greyData 灰度直方图矩阵
     */
    private void drawBarChart(int[] greyData) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        if (greyData != null) {
            int max = getMax(greyData);
            YAxis yAxis = mBarChart.getAxisLeft();
            yAxis.setAxisMaximum((1 + max / 100) * 100);
        }

        for (int i = 0; i < greyData.length; i++) {
            yVals1.add(new BarEntry(i, greyData[i]));
        }
        BarDataSet set1;
        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "灰度直方图");
            //设置多彩 也可以单一颜色
            set1.setColors(Color.rgb(0, 0, 0));
            set1.setDrawValues(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            mBarChart.setData(data);
            mBarChart.setFitBars(true);
        }
        mBarChart.invalidate();
    }

    @Override
    public void onSave() {
    }

    @Override
    public void onRun() {
    }

    @Override
    public void onCaptrue() {
    }
}
