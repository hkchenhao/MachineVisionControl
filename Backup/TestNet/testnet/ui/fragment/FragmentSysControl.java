package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hanyu on 2017/6/10.
 * 系统设置界面
 */

public class FragmentSysControl extends Fragment implements OperationInterface {
    String TAG = "bug test";
    /**
     * UI组件
     */
    private View mView;
    private TextView tittleView;

    EditText[] edLight = new EditText[6]; //光源参数输入文本
    EditText[] edEngine = new EditText[3]; //电机参数输入文本
    EditText[] edBlow = new EditText[2];//吹气参数输入文本
    final int DefaultLastTime = 30;
    final int DefaultPressure = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_sys_control, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MachineLearning) {
            MachineLearning machineLearning = (MachineLearning) activity;
            tittleView = (TextView) machineLearning.findViewById(R.id.title_button);

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initDefault();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    /*
    *初始化数据
    * */
    private void initData() {
    }

    /*
    *初始化界面
    * */
    private void initView() {
        /**绑定光源参数*/
        edLight[0] = (EditText) getView().findViewById(R.id.edi_cam1_top);
        edLight[1] = (EditText) getView().findViewById(R.id.edi_cam1_bottom);
        edLight[2] = (EditText) getView().findViewById(R.id.edi_cam2_top);
        edLight[3] = (EditText) getView().findViewById(R.id.edi_cam2_bottom);
        edLight[4] = (EditText) getView().findViewById(R.id.edi_cam3_top);
        edLight[5] = (EditText) getView().findViewById(R.id.edi_cam3_bottom);
        /**绑定空压机参数*/
        edBlow[0] = (EditText) getView().findViewById(R.id.edi_blow_time);
        edBlow[1] = (EditText) getView().findViewById(R.id.edi_blow_pressure);
        /**绑定传动装置参数*/
        edEngine[0] = (EditText) getView().findViewById(R.id.edi_charge_speed);
        edEngine[1] = (EditText) getView().findViewById(R.id.edi_transmit_speed);
        edEngine[2] = (EditText) getView().findViewById(R.id.edi_turntable_speed);

        edLight[0].setText("0");
        edLight[0].setEnabled(false);
        edLight[1].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
        edLight[2].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
        edLight[3].setText("0");
        edLight[4].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
        edLight[5].setText("0");

        edBlow[0].setText(Integer.toString(DefaultLastTime));
        edBlow[0].setEnabled(false);

        edBlow[1].setText(Integer.toString(DefaultPressure));
        edBlow[1].setEnabled(false);

        edEngine[0].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultFeederSpd));
        edEngine[0].setEnabled(false);

        edEngine[1].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultBelterSpd));
        edEngine[1].setEnabled(false);

        edEngine[2].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultServoSpd));
    }


    /**
     * 解析钮扣Json，初始化默认参数
     */
    private void initDefault() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null && ((MachineLearning) getActivity()).getButtonId() != "") {
            if (jsonTotal.has(ButtonKeys.SYSTEMPARAMETER)) {
                try {
                    JSONObject json = jsonTotal.getJSONObject(ButtonKeys.SYSTEMPARAMETER);
                    String[] tem = new String[12];
                    for (int i = 0; i < ButtonKeys.litgtSy.length; i++) {
                        tem[i] = json.getString(ButtonKeys.litgtSy[i]);
                        edLight[i].setText(tem[i]);
                    }
                    edBlow[0].setText(Integer.toString(DefaultLastTime));
                    edBlow[1].setText(Integer.toString(DefaultPressure));

                    edEngine[0].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultFeederSpd));
                    edEngine[1].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultBelterSpd));
                    edEngine[2].setText(json.getString(ButtonKeys.enginSpeedSy[2]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tittleView.setText("钮扣配置文件：" + ((MachineLearning) getActivity()).getButtonId());
            } else {
                tittleView.setText("");
                edLight[0].setText("0");
                edLight[1].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
                edLight[2].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
                edLight[3].setText("0");
                edLight[4].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
                edLight[5].setText("0");

                edBlow[0].setText(Integer.toString(DefaultLastTime));
                edBlow[1].setText(Integer.toString(DefaultPressure));

                edEngine[0].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultFeederSpd));
                edEngine[1].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultBelterSpd));
                edEngine[2].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultServoSpd));
            }
        } else {
            tittleView.setText("");
            edLight[0].setText("0");
            edLight[1].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
            edLight[2].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
            edLight[3].setText("0");
            edLight[4].setText(Integer.toString(FragmentCanLightSrcDrv.DefaultBrightness));
            edLight[5].setText("0");

            edBlow[0].setText(Integer.toString(DefaultLastTime));
            edBlow[1].setText(Integer.toString(DefaultPressure));

            edEngine[0].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultFeederSpd));
            edEngine[1].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultBelterSpd));
            edEngine[2].setText(Integer.toString(FragmentCanMotorCtrlCard.DefaultServoSpd));
        }
    }


    @Override
    public void onSave() {
        if (((MachineLearning) getActivity()).getButtonId() == "") {
            EToast.showToast(getActivity(), "先给钮扣取个名字吧！");
            return;
        }
        if (!verify()) {
            EToast.showToast(getActivity(), "输入信息存在问题！");
            return;
        }
        if (!firstEnter()) {
            /**
             * 从钮扣选择界面进入，则提示修改
             */
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改系统参数信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((MachineLearning) getActivity()).writeStatus("系统参数:修改完成");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialogWindow.showWrapContent();
        } else {
            /**
             * 初始化创建
             */
            saveInfo();
            ((MachineLearning) getActivity()).writeStatus("系统参数:配置完成");
        }
    }

    /**
     * 判断是否是第一次创建
     *
     * @return
     */
    private boolean firstEnter() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.SYSTEMPARAMETER);
                if (json == null)
                    return true;
                else
                    return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return true;
            }
        }
        return true;
    }

    /**
     * 判断参数合法性
     *
     * @return
     */
    private boolean verify() {
        int[] lit = new int[edLight.length];
        int[] blow = new int[edBlow.length];
        int[] engine = new int[edEngine.length];
        for (int i = 0; i < edLight.length; i++) {
            if (TextUtils.isEmpty(edLight[i].getText())) return false;
            lit[i] = Integer.parseInt(edLight[i].getText() + "");
            if ((lit[i] > 255) || (lit[i] < 0)) return false;
        }
        for (int i = 0; i < edEngine.length; i++) {
            if (TextUtils.isEmpty(edEngine[i].getText())) return false;
            engine[i] = Integer.parseInt(edEngine[i].getText() + "");
        }
        for (int i = 0; i < edBlow.length; i++) {
            if (TextUtils.isEmpty(edBlow[i].getText())) return false;
            blow[i] = Integer.parseInt(edBlow[i].getText() + "");
        }
        if (engine[2] > 600 || engine[2] < 0) return false;//转盘输入值是否合法


        return true;
    }

    /**
     * 判断字符串能否合法转化成数字
     */
    public boolean isUnQualify(String[] ligtht, String[] blow, String[] engine) {
        int[] a = new int[ligtht.length];
        int[] b = new int[blow.length];
        int[] c = new int[engine.length];
        try {
            for (int i = 0; i < ligtht.length; i++) {
                a[i] = Integer.parseInt(ligtht[i]);
                if ((a[i] > 255) || (a[i] < 0))
                    return true;
            }
            for (int i = 0; i < blow.length; i++) {
                b[i] = Integer.parseInt(blow[i]);
            }
            for (int i = 0; i < engine.length; i++) {
                c[i] = Integer.parseInt(engine[i]);
            }
            if (c[2] > 600 || c[2] < 0) return true;
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 保存配置信息到json文件
     */
    private void saveInfo() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            /**写入光照参数*/
            for (int i = 0; i < ButtonKeys.litgtSy.length; i++) {
                json.put(ButtonKeys.litgtSy[i], Integer.parseInt(edLight[i].getText() + ""));
            }
            for (int i = 0; i < ButtonKeys.enginSpeedSy.length; i++) {
                json.put(ButtonKeys.enginSpeedSy[i], Integer.parseInt(edEngine[i].getText() + ""));
            }
            for (int i = 0; i < ButtonKeys.blowSy.length; i++) {
                json.put(ButtonKeys.blowSy[i], Integer.parseInt(edBlow[i].getText() + ""));
            }
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


    @Override
    public void onRun() {
    }

    @Override
    public void onCaptrue() {
    }


}


