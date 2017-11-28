package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
 * 尺寸测量配置
 */
public class FragmentTaskSize extends Fragment implements OperationInterface {

    @Bind(R.id.et_outDia)
    EditText et_outDia;

    @Bind(R.id.et_holeDia)
    EditText et_holeDia;

    @Bind(R.id.et_holeDist)
    EditText et_holeDist;

    @Bind(R.id.et_outDiaDevUp)
    EditText et_outDiaDevUp;

    @Bind(R.id.et_outDiaDevDown)
    EditText et_outDiaDevDown;

    @Bind(R.id.et_holeDistDevUp)
    EditText et_holeDistDevUp;

    @Bind(R.id.et_holeDistDevDown)
    EditText et_holeDistDevDown;

    @Bind(R.id.et_holeDiaDevUp)
    EditText et_holeDiaDevUp;

    @Bind(R.id.et_holeDiaDevDown)
    EditText et_holeDiaDevDown;


    private TextView tittleView;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_task_size, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDefault();
    }

    /**
     * 初始化默认参数
     *
     */
    private void initDefault() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();

        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.TASK_SIZE);

                String outDia = json.getString("outDia");
                String holeDist = json.getString("holeDist");
                String holeDia = json.getString("holeDia");

                String outDiaDevUp = json.getString("outDiaDevUp");
                String outDiaDevDown = json.getString("outDiaDevDown");

                String holeDistDevUp = json.getString("holeDistDevUp");
                String holeDistDevDown = json.getString("holeDistDevDown");

                String holeDiaDevUp = json.getString("holeDiaDevUp");
                String holeDiaDevDown = json.getString("holeDiaDevDown");

                et_outDia.setText(outDia);
                et_holeDist.setText(holeDist);
                et_holeDia.setText(holeDia);

                et_outDiaDevUp.setText(outDiaDevUp);
                et_outDiaDevDown.setText(outDiaDevDown);

                et_holeDistDevUp.setText(holeDistDevUp);
                et_holeDiaDevDown.setText(holeDiaDevDown);
                et_holeDistDevDown.setText(holeDistDevDown);
                et_holeDiaDevUp.setText(holeDiaDevUp);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            tittleView.setText("纽扣配置文件：" + ((MachineLearning) getActivity()).getButtonId());
        } else {
            tittleView.setText("纽扣配置文件：");
        }
    }



    /**
     * 保存尺寸测量信息到Json文件
     */
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
             * 从纽扣选择界面进入，则提示修改
             */
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改尺寸测量信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((MachineLearning) getActivity()).writeStatus("尺寸检测:修改完成");
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
            ((MachineLearning) getActivity()).writeStatus("尺寸检测:配置完成");
        }
    }

    /**
     * 判断是否是第一次创建
     * @return
     */
    private boolean firstEnter() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.TASK_SIZE);
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
     * 保存配置信息到json文件
     */
    private void saveInfo() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            json.put("outDia", Double.parseDouble(et_outDia.getText() + ""));
            json.put("holeDist", Double.parseDouble(et_holeDist.getText() + ""));
            json.put("holeDia", Double.parseDouble(et_holeDia.getText() + ""));

            json.put("outDiaDevUp", Double.parseDouble(et_outDiaDevUp.getText() + ""));
            json.put("outDiaDevDown", Double.parseDouble(et_outDiaDevDown.getText() + ""));

            json.put("holeDistDevUp", Double.parseDouble(et_holeDistDevUp.getText() + ""));
            json.put("holeDistDevDown", Double.parseDouble(et_holeDistDevDown.getText() + ""));

            json.put("holeDiaDevUp", Double.parseDouble(et_holeDiaDevUp.getText() + ""));
            json.put("holeDiaDevDown", Double.parseDouble(et_holeDiaDevDown.getText() + ""));
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }

        try {
            jsonTotal.put(ButtonKeys.TASK_SIZE, json);
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

    /**
     * 判断参数合法性
     * @return
     */
    private boolean verify() {

        if (TextUtils.isEmpty(et_outDia.getText()) ||
                TextUtils.isEmpty(et_holeDia.getText()) ||
                TextUtils.isEmpty(et_holeDist.getText()))
            return false;

        String outDia = et_outDia.getText() + "";
        String holeDist = et_holeDist.getText() + "";
        String holeDia = et_holeDia.getText() +"";

        String outDiaDevUp = et_outDiaDevUp.getText()+"";
        String outDiaDevDown = et_outDiaDevDown.getText()+"";

        String holeDistDevUp = et_holeDistDevUp.getText()+"";
        String holeDistDevDown = et_holeDistDevDown.getText()+"";

        String holeDiaDevUp = et_holeDiaDevUp.getText()+"";
        String holeDiaDevDown = et_holeDiaDevUp.getText()+"";

        if(isUnQualify(outDia) || isUnQualify(holeDist)|| isUnQualify(holeDia)
                ||isUnQualify(outDiaDevUp)||isUnQualify(outDiaDevDown)||isUnQualify(holeDistDevUp)
                ||isUnQualify(holeDistDevDown)||isUnQualify(holeDiaDevUp)||isUnQualify(holeDiaDevDown))
            return false;
        return true;
    }

    /**
     * 判断字符串能否合法转化成数字
     * @param s
     * @return
     */
    public boolean isUnQualify(String s) {
        Double a;
        try {
            a = Double.parseDouble(s);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

}
