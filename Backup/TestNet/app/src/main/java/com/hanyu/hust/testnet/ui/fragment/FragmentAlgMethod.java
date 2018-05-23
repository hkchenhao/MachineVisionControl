package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;


/**
 * Created by HeQian on 2017/9
 *  机器学习界面下检测项目检测碎片
 */

public class FragmentAlgMethod  extends Fragment implements OperationInterface {
    private final String TAG = "BUGTEST";
    /*界面资源*/
    private TextView tittleView; //标题资源
    private View mView;
    //项目选择控件
    private CheckBox[]  checkGeo = new CheckBox[4];
    private CheckBox[]  checkSur = new CheckBox[7];
    private CheckBox checkGeoAll , checkGeoAnti , checkSurAll , checkSurAnti;//正选反选控件

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof com.hanyu.hust.testnet.ui.MachineLearning) {
            com.hanyu.hust.testnet.ui.MachineLearning machineLearning = (com.hanyu.hust.testnet.ui.MachineLearning) activity;
            tittleView = (TextView) machineLearning.findViewById(R.id.title_button);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_alg_method, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initDefault();
    }
    /*checkbox控件监听器*/
    private void checkBoxListener() {
        checkGeoAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    for(int i = 0 ; i< checkGeo.length ; i++ ) checkGeo[i].setChecked(true);
                }else{
                    for(int i = 0 ; i< checkGeo.length ; i++ ) checkGeo[i].setChecked(false);
                }
            }
        });

        checkGeoAnti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for(int i = 0 ; i< checkGeo.length ; i++ ) {
                        if(checkGeo[i].isChecked()) checkGeo[i].setChecked(false);
                        else checkGeo[i].setChecked(true);
                    }
            }
        });

        checkSurAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    for(int i = 0 ; i< checkSur.length ; i++ ) checkSur[i].setChecked(true);
                }else{
                    for(int i = 0 ; i< checkSur.length ; i++ ) checkSur[i].setChecked(false);
                }
            }
        });

        checkSurAnti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for(int i = 0 ; i< checkSur.length ; i++ ) {
                    if(checkSur[i].isChecked()) checkSur[i].setChecked(false);
                    else checkSur[i].setChecked(true);
                }
            }
        });

    }
    /*初始化界面*/
    private void initDefault(){
        JSONObject jsonTotal = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getJson();
        if (jsonTotal != null &&  ((com.hanyu.hust.testnet.ui.MachineLearning)getActivity()).getButtonId()!= ""  ) {//读取纽扣配置json文件
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.METHOD);
                for(int i = 0; i< ButtonKeys.GeoMehodCs.length; i++) {
                    if(Integer.valueOf(json.getString(ButtonKeys.GeoMehodCs[i])) == 1) checkGeo[i].setChecked(true);
                    else checkGeo[i].setChecked(false);
                }
                for(int i = 0; i< ButtonKeys.SurMehodCs.length; i++) {
                    if(Integer.valueOf(json.getString(ButtonKeys.SurMehodCs[i])) == 1) checkSur[i].setChecked(true);
                    else checkSur[i].setChecked(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tittleView.setText("钮扣配置文件：" + ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId());
        } else {
            tittleView.setText("");
        }
    }
    /*绑定控件*/
    private  void initView(){
        checkGeo[0] =  (CheckBox) getView().findViewById(R.id.checkbox_geo_1);
        checkGeo[1] =  (CheckBox) getView().findViewById(R.id.checkbox_geo_2);
        checkGeo[2] =  (CheckBox) getView().findViewById(R.id.checkbox_geo_3);
        checkGeo[3] =  (CheckBox) getView().findViewById(R.id.checkbox_geo_4);

        checkSur[0] =  (CheckBox) getView().findViewById(R.id.checkbox_surface_1);
        checkSur[1] =  (CheckBox) getView().findViewById(R.id.checkbox_surface_2);
        checkSur[2] =  (CheckBox) getView().findViewById(R.id.checkbox_surface_3);
        checkSur[3] =  (CheckBox) getView().findViewById(R.id.checkbox_surface_4);
        checkSur[4] =  (CheckBox) getView().findViewById(R.id.checkbox_surface_5);
        checkSur[5] =  (CheckBox) getView().findViewById(R.id.checkbox_surface_6);
        checkSur[6] =  (CheckBox) getView().findViewById(R.id.checkbox_surface_7);

        checkSurAll = (CheckBox) getView().findViewById(R.id.checkbox_sur_all);
        checkSurAnti = (CheckBox) getView().findViewById(R.id.checkbox_sur_anti);

        checkGeoAll = (CheckBox) getView().findViewById(R.id.checkbox_geo_all);
        checkGeoAnti = (CheckBox) getView().findViewById(R.id.checkbox_geo_anti);
        checkBoxListener();//添加选择控件监听

    }
    /*点击保存按钮调用，方法内调用saveInfo方法将配置信息写入JSON文件*/
    @Override
    public void onSave() {
        if (((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId() == "") {
            EToast.showToast(getActivity(), "先给钮扣取个名字吧！");
            return;
        }
        if (!firstEnter()) {
            /**
             * 从钮扣选择界面进入，则提示修改
             */
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改学习方法信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).writeStatus("检测方法:修改完成");
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
            ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).writeStatus("检测方法:配置完成");
        }
    }
    /**
     * 保存配置信息到json文件
     */
    private void saveInfo() {
        JSONObject jsonTotal = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            for(int i = 0; i< ButtonKeys.GeoMehodCs.length ; i++) {
                if (checkGeo[i].isChecked()) json.put(ButtonKeys.GeoMehodCs[i],"1"+"");
                else json.put(ButtonKeys.GeoMehodCs[i],"0");
            }

            for(int i = 0; i< ButtonKeys.SurMehodCs.length ; i++) {
                if (checkSur[i].isChecked()) json.put(ButtonKeys.SurMehodCs[i],"1"+"");
                else json.put(ButtonKeys.SurMehodCs[i],"0");
            }
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
        try {
            jsonTotal.put(ButtonKeys.METHOD, json);
            SettingProfile buttonProfile = new SettingProfile(((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId());
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
    /**
     * 判断是否是第一次创建
     * @return
     */
    private boolean firstEnter() {
        JSONObject jsonTotal = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getJson();
        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.METHOD);
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
}
