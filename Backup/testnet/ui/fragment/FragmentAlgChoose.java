package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.AlgSettingUtils;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by heqian on 2017/9/20.
 */

public class FragmentAlgChoose extends Fragment implements OperationInterface {
    private View mView;
    private TextView tittleView;
    @Bind(R.id.sp_alg_geometry)
    Spinner sp_geometry;

    @Bind(R.id.sp_alg_surface)
    Spinner sp_surface;


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
        mView = inflater.inflate(R.layout.fr_alg_choose, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        initData();
        initDefault();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnGeoALGCnList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnSurALGCnList);

        sp_geometry.setAdapter(adapter1);
        sp_surface.setAdapter(adapter2);
    }

    /**
     * 初始化默认数据
     */
    private void initDefault() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.ALG);
                String geometry = json.getString(ButtonKeys.geometryF);
                String surface = json.getString(ButtonKeys.surfaceF);

                sp_geometry.setSelection(      AlgSettingUtils.findIndex(Constants.btnGeoALGEnList,     geometry));
                sp_surface.setSelection(      AlgSettingUtils.findIndex(Constants.btnSurALGEnList,     surface));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tittleView.setText("纽扣配置文件："+((MachineLearning)getActivity()).getButtonId());
        }
        else{
            tittleView.setText("纽扣配置文件：");
        }
    }

    /**
     * 保存配置信息到json文件
     */
    private void saveInfo() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            json.put(ButtonKeys.geometryF, Constants.btnGeoALGEnList[sp_geometry.getSelectedItemPosition()]);
            json.put(ButtonKeys.surfaceF, Constants.btnSurALGEnList[sp_surface.getSelectedItemPosition()]);

        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }

        try {
            jsonTotal.put(ButtonKeys.ALG, json);
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
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.ALG);
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
        if (((MachineLearning) getActivity()).getButtonId() == "") {
            EToast.showToast(getActivity(), "先给钮扣取个名字吧！");
            return;
        }


        if (!firstEnter()) {
            /**
             * 从纽扣选择界面进入，则提示修改
             */
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改算法信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((MachineLearning) getActivity()).writeStatus("算法选择:修改完成");
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
            ((MachineLearning) getActivity()).writeStatus("算法选择:配置完成");
        }
    }



    @Override
    public void onRun() {

    }

    @Override
    public void onCaptrue() {
    }
}
