package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
 * 机器学习界面下方法参数配置碎片
 * Created by LitWill on 2017/11/17.
 * 最后更新于2017/12
 */

public class FragmentAlgParameterSet extends Fragment implements OperationInterface {
    /*界面控件申明*/
    private TextView tittleView;
    private View mView;
    private EditText[] ediGeoParameter = new EditText[8];//几何检测方法参数
    private EditText[] ediSurParameter = new EditText[8];//表面缺陷检测方法参数

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
        mView = inflater.inflate(R.layout.fr_alg_parameter_set, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initDefault();
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /*
  * 初始化界面
  * */
    private  void initView() {
        /*几何检测方法参数*/
        ediGeoParameter[0] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_1);
        ediGeoParameter[1] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_2);
        ediGeoParameter[2] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_3);
        ediGeoParameter[3] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_4);
        ediGeoParameter[4] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_5);
        ediGeoParameter[5] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_6);
        ediGeoParameter[6] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_7);
        ediGeoParameter[7] = (EditText) getActivity().findViewById(R.id.edi_geo_parameter_8);
        /*表面缺陷检测方法参数*/
        ediSurParameter[0] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_1);
        ediSurParameter[1] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_2);
        ediSurParameter[2] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_3);
        ediSurParameter[3] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_4);
        ediSurParameter[4] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_5);
        ediSurParameter[5] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_6);
        ediSurParameter[6] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_7);
        ediSurParameter[7] = (EditText) getActivity().findViewById(R.id.edi_sur_parameter_8);

        for (int i = 0;i<ediSurParameter.length ; i++){
            ediSurParameter[i].setText("0"+"");
        }
        for (int i = 0;i<ediGeoParameter.length ; i++){
            ediGeoParameter[i].setText("0"+"");
        }
    }
    /*
    *初始化参数
    * */
    private void initDefault() {
        JSONObject jsonTotal = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getJson();

        if (jsonTotal != null &&  ((com.hanyu.hust.testnet.ui.MachineLearning)getActivity()).getButtonId()!= ""  ) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.ALGPARA);
                for(int i = 0 ; i<ediGeoParameter.length;i++)
                    ediGeoParameter[i].setText(json.getString(ButtonKeys.GeoMethodPara[i]));
                for(int i = 0 ; i<ediSurParameter.length;i++)
                    ediSurParameter[i].setText(json.getString(ButtonKeys.SurMethodPara[i]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tittleView.setText("钮扣配置文件：" + ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId());
        } else {
            tittleView.setText("");
        }
    }



    /**
     * 判断是否是第一次创建
     *
     * @return
     */
    private boolean firstEnter() {
        JSONObject jsonTotal = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getJson();
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

    @Override
    public void onSave() {
        if (((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId() == "") {
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
                    .setTitle("确定修改方法参数信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).writeStatus("方法参数信息:修改完成");
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
            ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).writeStatus("方法参数:配置完成");
        }
    }

    /**
     * 保存配置信息到json文件
     */
    private void saveInfo() {
        JSONObject jsonTotal = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            /**写入几何参数*/
            for (int i = 0; i< ButtonKeys.GeoMethodPara.length; i++) {
                json.put(ButtonKeys.GeoMethodPara[i], Double.parseDouble(ediGeoParameter[i].getText() + ""));
            }
            /**写入表面参数*/
            for (int i = 0; i< ButtonKeys.SurMethodPara.length; i++) {
                json.put(ButtonKeys.SurMethodPara[i], Double.parseDouble(ediSurParameter[i].getText() + ""));
            }

        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
        try {
            jsonTotal.put(ButtonKeys.ALGPARA, json);
            SettingProfile buttonProfile = new SettingProfile(((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId());
            buttonProfile.write2sd(jsonTotal);
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
    }

    /**
     * 判断字符串能否合法转化成数字
     * @param s
     * @return
     */
    public boolean isUnQualify(String s) {
        Double a;
        try {
            a = Double.parseDouble(s);//判断S是否能转换为double类型
        } catch (Exception e) {
            return true;//异常捕获，方法返回非法输入标志true
        }
        return false;
    }


    /**
     * 判断参数合法性
     * @return
     */
    private boolean verify() {
        for(int i = 0 ; i< ediSurParameter.length; i++)
            if(isUnQualify(ediGeoParameter[i].getText()+ ""))  return  false;
        for(int i = 0 ; i<ediSurParameter.length; i++)
            if(isUnQualify(ediSurParameter[i].getText()+ ""))  return  false;
        return true;
    }

    @Override
    public void onRun() {}
    @Override
    public void onCaptrue() {}

}
