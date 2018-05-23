package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.AlgSettingUtils;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.ExcuteTask;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HeQian on 2017/9
 * 机器学习界面下算法配置碎片.
 * 最后更新时间2018/1，by吴鑫
 */

public class FragmentAlgOpretor extends Fragment implements View.OnClickListener, OperationInterface {
    /*界面资源*/
    private View mView;
    private TextView tittleView;
    private Spinner[] spGeo = new Spinner[3]; //几何检测方法配置
    private Spinner[] spSur = new Spinner[3]; //表面检测方法配置
    private Spinner spGeoMethod, spSurMethod;
    private CheckBox checkGeo, checkSur, checkGeoAnti, checkSurAnti;
    private Button btGeo, btSur; //控制按钮

    /* 通用帧参数定义*/
    private final byte PageAddr = 0x03;
    private final byte FrAddr = 0x22;
    private final byte PnDCN = 0x01;
    private final int CMD_GetGeoMethod = 1;
    private final int CMD_GetSurMethod = 2;
    private final byte PnGeoMethod = 0x02;
    private final byte PnSurMethod = 0x03;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MachineLearning) {
            MachineLearning machineLearning = (MachineLearning) activity;
            tittleView = (TextView) machineLearning.findViewById(R.id.title_button);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_alg_operator, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initDefault();
        AppContext.setHandler(netHandler);
    }

    /*
    * 绑定界面控件
    * */
    private void initView() {
        btGeo = (Button) getView().findViewById(R.id.bt_get_geo_method);
        btSur = (Button) getView().findViewById(R.id.bt_get_sur_method);

        checkGeo = (CheckBox) getView().findViewById(R.id.checkbox_geo);
        checkGeoAnti = (CheckBox) getView().findViewById(R.id.checkbox_geo_);
        checkSur = (CheckBox) getView().findViewById(R.id.checkbox_sur);
        checkSurAnti = (CheckBox) getView().findViewById(R.id.checkbox_sur_);

        spGeo[0] = (Spinner) getView().findViewById(R.id.sp_geometry_1);
        spGeo[1] = (Spinner) getView().findViewById(R.id.sp_geometry_2);
        spGeo[2] = (Spinner) getView().findViewById(R.id.sp_geometry_3);
        spGeoMethod = (Spinner) getView().findViewById(R.id.sp_geometry);

        spSur[0] = (Spinner) getView().findViewById(R.id.sp_surface_1);
        spSur[1] = (Spinner) getView().findViewById(R.id.sp_surface_2);
        spSur[2] = (Spinner) getView().findViewById(R.id.sp_surface_3);
        spSurMethod = (Spinner) getView().findViewById(R.id.sp_surface);

        for (int i = 0; i < Constants.ALGGeoOperatorCnList.length; i++) {
            spGeo[i].setAdapter(new ArrayAdapter<>(getActivity(), R.layout.layout_spinnnr_operator, Constants.ALGGeoOperatorCnList[i]));
        }
        for (int i = 0; i < Constants.ALGSurOperatorCnList.length; i++) {
            spSur[i].setAdapter(new ArrayAdapter<>(getActivity(), R.layout.layout_spinnnr_operator, Constants.ALGSurOperatorCnList[i]));
        }
        spGeoMethod.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.layout_spinnnr_operator, Constants.ALGGeoMethodNewCnList));
        spSurMethod.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.layout_spinnnr_operator, Constants.ALGSurMethodNewCnList));
        checkBoxListener();

        checkGeoAnti.setChecked(true);
        checkSurAnti.setChecked(true);
    }
    /*
    * 根据纽扣配置文件初始化界面
    * */
    private void initDefault() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null && ((MachineLearning) getActivity()).getButtonId() != "") {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.OPERATOR);
                if (Integer.valueOf(json.getString(ButtonKeys.GeoOperator[3])) == 1)
                    setGeoOperatorEnable(true);
                else setGeoOperatorEnable(false);
                if (Integer.valueOf(json.getString(ButtonKeys.SurOperator[3])) == 1)
                    setSurOperatorEnable(true);
                else setSurOperatorEnable(false);

                for (int i = 0; i < spGeo.length; i++) {
                    spGeo[i].setSelection(AlgSettingUtils.findIndex(Constants.ALGGeoOperatorCnList[i], json.getString(ButtonKeys.GeoOperator[i])));
                }
                for (int i = 0; i < spSur.length; i++) {
                    spSur[i].setSelection(AlgSettingUtils.findIndex(Constants.ALGSurOperatorEnList[i], json.getString(ButtonKeys.SurOperator[i])));
                }
                spGeoMethod.setSelection(AlgSettingUtils.findIndex(Constants.ALGGeoMethodEnList, json.getString(ButtonKeys.GeoMethod)));
                spSurMethod.setSelection(AlgSettingUtils.findIndex(Constants.ALGSurMethodEnList, json.getString(ButtonKeys.SurMethod)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tittleView.setText("钮扣配置文件：" + ((MachineLearning) getActivity()).getButtonId());
        } else {
            tittleView.setText("");
        }
    }
    /*
    * 点击保存按钮调用方法
    * */
    @Override
    public void onSave() {
        if (((MachineLearning) getActivity()).getButtonId() == "") {
            EToast.showToast(getActivity(), "请先完成配置钮扣基本信息");
            return;
        }
        if (!firstEnter()) {
            /**
             * 从钮扣选择界面进入，则提示修改
             */
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改自定义方法信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((MachineLearning) getActivity()).writeStatus("自定义方法:修改完成");
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
            ((MachineLearning) getActivity()).writeStatus("自定义方法:配置完成");
        }
    }

    /**
     * 保存配置信息到json文件
     */
    private void saveInfo() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            if (!jsonTotal.isNull(ButtonKeys.OPERATOR))
                json = jsonTotal.getJSONObject(ButtonKeys.OPERATOR);
            for (int i = 0; i < spGeo.length; i++) {
                json.put(ButtonKeys.GeoOperator[i], Constants.ALGGeoOperatorEnList[i][spGeo[i].getSelectedItemPosition()]);
            }
            for (int i = 0; i < spSur.length; i++) {
                json.put(ButtonKeys.SurOperator[i], Constants.ALGSurOperatorEnList[i][spSur[i].getSelectedItemPosition()]);
            }
            if (checkGeo.isChecked()) json.put(ButtonKeys.GeoOperator[3], "1" + "");
            else json.put(ButtonKeys.GeoOperator[3], "0");
            if (checkSur.isChecked()) json.put(ButtonKeys.SurOperator[3], "1" + "");
            else json.put(ButtonKeys.SurOperator[3], "0");
            json.put(ButtonKeys.GeoMethod, Constants.ALGGeoMethodEnList[spGeoMethod.getSelectedItemPosition()]);
            json.put(ButtonKeys.SurMethod, Constants.ALGSurMethodEnList[spSurMethod.getSelectedItemPosition()]);
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
        try {
            jsonTotal.put(ButtonKeys.OPERATOR, json);
            SettingProfile buttonProfile = new SettingProfile(((MachineLearning) getActivity()).getButtonId());
            buttonProfile.write2sd(jsonTotal);
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
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
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.OPERATOR);
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
     * checkBox监听器
     */
    private void checkBoxListener() {
        checkGeo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setGeoOperatorEnable(true);
                } else {
                }
            }
        });

        checkGeoAnti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setGeoOperatorEnable(false);
                } else {
                }
            }
        });

        checkSur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSurOperatorEnable(true);
                } else {
                }
            }
        });


        checkSurAnti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSurOperatorEnable(false);
                } else {

                }
            }
        });


    }

    /**
     * 监听“获取方法”按钮
     *
     * @param v
     */
    @Override
    @OnClick({R.id.bt_get_sur_method, R.id.bt_get_geo_method})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get_geo_method:
                /**检查是否有钮扣配置文件*/
                if (((MachineLearning) getActivity()).getButtonId().length() == 0){
                    EToast.showToast(getActivity(), Constants.NoButtonCfgFileWarnning);
                    return;
                }
                /**相机1是否连接*/
                if (AppContext.getAppContext().isExist(Constants.cameraList[0])) {
                    JSONObject btnCfgJson = ((MachineLearning) getActivity()).getJson();
                    String btnCfgJsonName = ((MachineLearning) getActivity()).getButtonId();
                    if (btnCfgJson != null) {
                        /**发送通用帧，使相机进入算法自动选择任务*/
                        byte[] net_tx_data = NetUtils.createNetCmdData(PageAddr, FrAddr, PnDCN, CMD_GetGeoMethod);
                        if (net_tx_data != null) {
                            CmdHandle.getInstance().sendCmdInfo(Constants.cameraList[0], net_tx_data);
                        }
                        ExcuteTask.sleep(500);
                        /**发送钮扣配置文件*/
                        CmdHandle.getInstance().sendBtnCfgJson(Constants.cameraList[0], btnCfgJson.toString().getBytes());
                        EToast.showToast(getActivity(), "向相机" + Constants.cameraList[0] + "发送钮扣配置文件:" + btnCfgJsonName);
                    } else {
                        EToast.showToast(getActivity(), Constants.NoButtonCfgFileWarnning);
                    }
                } else {
                    EToast.showToast(getActivity(), Constants.Camera1DisconnectedWarnning);
                }
                break;
            case R.id.bt_get_sur_method:
                /**检查是否有钮扣配置文件*/
                if (((MachineLearning) getActivity()).getButtonId().length() == 0){
                    EToast.showToast(getActivity(), Constants.NoButtonCfgFileWarnning);
                    return;
                }
                /**相机2是否连接*/
                if (AppContext.getAppContext().isExist(Constants.cameraList[1])) {
                    JSONObject btnCfgJson = ((MachineLearning) getActivity()).getJson();
                    String btnCfgJsonName = ((MachineLearning) getActivity()).getButtonId();
                    if (btnCfgJson != null) {
                        /**发送通用帧，使相机进入算法自动选择任务*/
                        byte[] net_tx_data = NetUtils.createNetCmdData(PageAddr, FrAddr, PnDCN, CMD_GetSurMethod);
                        if (net_tx_data != null) {
                            CmdHandle.getInstance().sendCmdInfo(Constants.cameraList[1], net_tx_data);
                        }
                        ExcuteTask.sleep(500);
                        /**发送钮扣配置文件*/
                        CmdHandle.getInstance().sendBtnCfgJson(Constants.cameraList[1], btnCfgJson.toString().getBytes());
                        EToast.showToast(getActivity(), "向相机" + Constants.cameraList[1] + "发送钮扣配置文件:" + btnCfgJsonName);
                    } else {
                        EToast.showToast(getActivity(), Constants.NoButtonCfgFileWarnning);
                    }
                } else {
                    EToast.showToast(getActivity(), Constants.Camera2DisconnectedWarnning);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 网络监听
     */
    private Handler netHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NetUtils.MSG_NET_CMD: {
                    byte[] data = (byte[]) msg.obj;
                    if ((data[0] == PageAddr) && (data[1] == FrAddr)) {
                        byte[] pn_code = NetUtils.getNetCmdPnCode(data);
                        int[] param = NetUtils.getNetCmdParam(data);
                        if ((pn_code != null) && (pn_code.length == param.length)) {
                            for (int i = 0; i < pn_code.length; i++) {
                                if (pn_code[i] == PnGeoMethod) {
                                    if ((param[i] >= 0) && (param[i] < Constants.ALGGeoMethodNewCnList.length)) {
                                        spGeoMethod.setSelection(param[i]);
                                    } else {
                                        EToast.showToast(getActivity(), "不明数据" + Integer.toString(pn_code[i], 16) + ":" + Integer.toString(param[i], 16));
                                    }
                                } else if (pn_code[i] == PnSurMethod) {
                                    if ((param[i] >= 0) && (param[i] < Constants.ALGSurMethodNewCnList.length)) {
                                        spSurMethod.setSelection(param[i]);
                                    } else {
                                        EToast.showToast(getActivity(), "不明数据" + Integer.toString(pn_code[i], 16) + ":" + Integer.toString(param[i], 16));
                                    }
                                } else {
                                    EToast.showToast(getActivity(), "不明数据" + Integer.toString(pn_code[i], 16) + ":" + Integer.toString(param[i], 16));
                                }
                            }
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
    };
    /**
     * 点击方法选择方式checkbox调用该方法，切换几何检测方法配置方式
     * @param ins
     */
    private void setGeoOperatorEnable(boolean ins) {
        if (ins) {
            checkGeo.setEnabled(false);
            checkGeo.setChecked(true);
            for (int i = 0; i < spGeo.length; i++) {
                spGeo[i].setEnabled(true);
            }
            checkGeoAnti.setChecked(false);
            checkGeoAnti.setEnabled(true);
            spGeoMethod.setEnabled(false);
            btGeo.setEnabled(false);

        } else {
            checkGeoAnti.setEnabled(false);
            checkGeoAnti.setChecked(true);
            spGeoMethod.setEnabled(true);
            btGeo.setEnabled(true);

            checkGeo.setEnabled(true);
            for (int i = 0; i < spGeo.length; i++) {
                spGeo[i].setEnabled(false);
            }
            checkGeo.setChecked(false);
        }
    }
    /**
     * 点击方法选择方式checkbox调用该方法，切换表面检测方法配置方式
     * @param ins
     */
    private void setSurOperatorEnable(boolean ins) {
        if (ins) {
            checkSur.setEnabled(false);
            checkSur.setChecked(true);
            for (int i = 0; i < spSur.length; i++) {
                spSur[i].setEnabled(true);
            }

            checkSurAnti.setChecked(false);
            checkSurAnti.setEnabled(true);
            spSurMethod.setEnabled(false);
            btSur.setEnabled(false);

        } else {
            checkSurAnti.setEnabled(false);
            checkSurAnti.setChecked(true);
            spSurMethod.setEnabled(true);
            btSur.setEnabled(true);

            for (int i = 0; i < spGeo.length; i++) {
                spSur[i].setEnabled(false);
            }
            checkSur.setChecked(false);
            checkSur.setEnabled(true);
        }
    }

    @Override
    public void onRun() {
    }

    @Override
    public void onCaptrue() {
    }
}
