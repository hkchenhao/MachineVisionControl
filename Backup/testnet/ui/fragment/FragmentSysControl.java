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
 * Created by Administrator on 2017/6/10.
 * 系统设置界面
 */

public class FragmentSysControl extends Fragment implements OperationInterface {
    String TAG = "bug test";
    private View mView;
    private TextView tittleView;
    EditText[] light = new EditText[6];
    @Bind(R.id.edi_cam1_top)
    EditText edLight1;
    @Bind(R.id.edi_cam1_bottom)
    EditText edLight2;
    @Bind(R.id.edi_cam2_top)
    EditText edLight3;
    @Bind(R.id.edi_cam2_bottom)
    EditText edLight4;
    @Bind(R.id.edi_cam3_top)
    EditText edLight5;
    @Bind(R.id.edi_cam3_bottom)
    EditText edLight6;

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

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDefault();
    }

    /**
     * 解析纽扣Json，初始化默认参数
     */
    private void initDefault() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.LIGHT);
                String[] lit = new String[6];
                for (int i = 0; i < 6; i++) {
                    lit[i] = json.getString(ButtonKeys.litgtSy[i]);
                }
                edLight1.setText(lit[0]);
                edLight2.setText(lit[1]);
                edLight3.setText(lit[2]);
                edLight4.setText(lit[3]);
                edLight5.setText(lit[4]);
                edLight6.setText(lit[5]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tittleView.setText("纽扣配置文件：" + ((MachineLearning) getActivity()).getButtonId());
        } else {
            tittleView.setText("纽扣配置文件：");
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
             * 从纽扣选择界面进入，则提示修改
             */
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改光源控制信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((MachineLearning) getActivity()).writeStatus("光源参数:修改完成");
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
            ((MachineLearning) getActivity()).writeStatus("光源参数:配置完成");
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
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.LIGHT);
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
        String[] lightPra = new String[6];
        if (TextUtils.isEmpty(edLight1.getText()) || TextUtils.isEmpty(edLight2.getText()) ||
                TextUtils.isEmpty(edLight3.getText()) || TextUtils.isEmpty(edLight4.getText()) ||
                TextUtils.isEmpty(edLight5.getText()) || TextUtils.isEmpty(edLight6.getText())) {
            return false;
        } else {
            lightPra[0] = edLight1.getText() + "";
            lightPra[1] = edLight2.getText() + "";
            lightPra[2] = edLight3.getText() + "";
            lightPra[3] = edLight4.getText() + "";
            lightPra[4] = edLight5.getText() + "";
            lightPra[5] = edLight6.getText() + "";
            for (int i = 0; i < 6; i++) {
                if (isUnQualify(lightPra[i]))
                    return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串能否合法转化成数字
     *
     * @param s
     * @return
     */
    public boolean isUnQualify(String s) {
        int a;
        try {
            a = Integer.parseInt(s);
        } catch (Exception e) {
            return true;
        }
        if ((a <= 255) && (a > 0))
            return false;
        else return true;
    }

    /**
     * 保存配置信息到json文件
     */
    private void saveInfo() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            json.put(ButtonKeys.litgtSy[0], Integer.parseInt(edLight1.getText() + ""));
            json.put(ButtonKeys.litgtSy[1], Integer.parseInt(edLight2.getText() + ""));
            json.put(ButtonKeys.litgtSy[2], Integer.parseInt(edLight3.getText() + ""));
            json.put(ButtonKeys.litgtSy[3], Integer.parseInt(edLight4.getText() + ""));
            json.put(ButtonKeys.litgtSy[4], Integer.parseInt(edLight5.getText() + ""));
            json.put(ButtonKeys.litgtSy[5], Integer.parseInt(edLight6.getText() + ""));
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
        try {
            jsonTotal.put(ButtonKeys.LIGHT, json);
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


//package com.hanyu.hust.testnet.ui.fragment;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.content.DialogInterface;
//import android.nfc.Tag;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.hanyu.hust.testnet.R;
//import com.hanyu.hust.testnet.entity.ButtonKeys;
//import com.hanyu.hust.testnet.entity.SettingProfile;
//import com.hanyu.hust.testnet.ui.MachineLearning;
//import com.hanyu.hust.testnet.ui.view.DialogWindow;
//import com.hanyu.hust.testnet.ui.view.EToast;
//import com.hanyu.hust.testnet.utils.OperationInterface;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// * Created by Administrator on 2017/6/10.
// * 系统设置界面
// */
//
//public class FragmentSysControl extends Fragment implements OperationInterface {
//    String TAG = "bug test";
//    private View mView;
//    private TextView tittleView;
//    EditText[] light = new EditText[6];
//    @Bind(R.id.edi_cam1_top)
//    EditText lightTe;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mView = inflater.inflate(R.layout.fr_sys_control, container, false);
//        ButterKnife.bind(this, mView);//绑定framgent
//        return mView;
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        if (activity instanceof MachineLearning) {
//            MachineLearning machineLearning = (MachineLearning) activity;
//            tittleView = (TextView) machineLearning.findViewById(R.id.title_button);
//
//        }
//    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        light[0] = (EditText) mView.findViewById(R.id.edi_cam1_bottom);
//       // light[1] = (EditText) getActivity().findViewById(R.id.edi_cam1_bottom);
//        light[2] = (EditText) getActivity().findViewById(R.id.edi_cam2_top);
//        light[3] = (EditText) getActivity().findViewById(R.id.edi_cam2_bottom);
//        light[4] = (EditText) getActivity().findViewById(R.id.edi_cam3_top);
//        light[5] = (EditText) getActivity().findViewById(R.id.edi_cam3_bottom);
//    }
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initDefault();
//    }
//
//    /**
//     * 解析纽扣Json，初始化默认参数
//     *
//     */
//    private void initDefault() {
//        String TAG = "bug test";
//        Log.d(TAG, "initDefault: 0");
//        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
//        Log.d(TAG, "initDefault: 1");
//        if (jsonTotal != null) {
//            try {
//                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.LIGHT);
//                String[] lit = new String[6];
//                for(int i = 0; i<6 ; i++ ){
//                    Log.d(TAG, "initDefault: 1/"+String.valueOf(i));
//                    lit[i] = json .getString(ButtonKeys.litgtSy[i]);
//                    Log.d(TAG, "initDefault: 1/"+String.valueOf(i));
//                    lightTe.setText( lit[0]);
//                    Log.d(TAG, "initDefault: 1/"+String.valueOf(i));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, "initDefault: 2");
//            tittleView.setText("纽扣配置文件：" + ((MachineLearning) getActivity()).getButtonId());
//        } else {
//            tittleView.setText("纽扣配置文件：");
//        }
//    }
//
//    @Override
//    public void onSave() {
//        String TAG = "bug test";
//        Log.d(TAG, "onSave: 0");
//        if (((MachineLearning) getActivity()).getButtonId() == "") {
//            EToast.showToast(getActivity(), "先给钮扣取个名字吧！");
//            return;
//        }
//        Log.d(TAG, "onSave: 1");
////        if (!verify()) {
////            EToast.showToast(getActivity(), "输入信息存在问题！");
////            return;
////        }
//        Log.d(TAG, "onSave: 2");
//        if (!firstEnter()) {
//            /**
//             * 从纽扣选择界面进入，则提示修改
//             */
//            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
//                    .setTitle("确定修改光源控制信息?")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            saveInfo();
//                            ((MachineLearning) getActivity()).writeStatus("光源参数:修改完成");
//                        }
//                    })
//                    .setNegativeButton("取消", null)
//                    .create();
//            dialogWindow.showWrapContent();
//        } else {
//            /**
//             * 初始化创建
//             */
//            saveInfo();
//            ((MachineLearning) getActivity()).writeStatus("光源参数:配置完成");
//        }
//    }
//
//    /**
//     * 判断是否是第一次创建
//     * @return
//     */
//    private boolean firstEnter() {
//        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
//        if (jsonTotal != null) {
//            try {
//                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.LIGHT);
//                if (json == null)
//                    return true;
//                else
//                    return false;
//            } catch (JSONException e) {
//                e.printStackTrace();
//                return true;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 判断参数合法性
//     * @return
//     */
//    private boolean verify() {
//        Log.d(TAG, "verify: 0");
//        String[] lightPra = new String[6] ;
//        Log.d(TAG, "verify: 1");
//        for(int i = 0;i<6;i++) {
//            Log.d(TAG, "verify: 2"+String.valueOf(i));
//            if (TextUtils.isEmpty(light[i].getText() ) )
//            {
//                Log.d(TAG, "verify: 2 GET"+String.valueOf(i));
//                return false;
//            }
//            else {
//                lightPra[i] = light[i].getText() + "";
//                if(isUnQualify(lightPra[i]))
//                    return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 判断字符串能否合法转化成数字
//     * @param s
//     * @return
//     */
//    public boolean isUnQualify(String s) {
//        int a;
//        try {
//            a = Integer.parseInt(s);
//        } catch (Exception e) {
//            return true;
//        }
//        if ( (a<=255) && (a>0))
//        return false;
//        else return true;
//    }
//
//    /**
//     * 保存配置信息到json文件
//     */
//    private void saveInfo() {
//        String TAG = "bug test";
//        Log.d(TAG, "saveInfo:0 ");
//        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
//        Log.d(TAG, "saveInfo:1 ");
//        JSONObject json = new JSONObject();
//        Log.d(TAG, "saveInfo:2 ");
//        try {
//            for(int i = 0; i<6;i++ ) {
//                Log.d(TAG, "saveInfo:2/"+ light[i].getText() );
//                json.put(ButtonKeys.litgtSy[i], Integer.parseInt(light[i].getText() + ""));
//                Log.d(TAG, "saveInfo:2/"+ light[i].getText() );
//            }
//        } catch (JSONException e) {
//            EToast.showToast(getActivity(), "保存失败！");
//            e.printStackTrace();
//            return;
//        }
//        Log.d(TAG, "saveInfo:3 ");
//        try {
//            jsonTotal.put(ButtonKeys.LIGHT, json);
//            SettingProfile buttonProfile = new SettingProfile(((MachineLearning) getActivity()).getButtonId());
//            buttonProfile.write2sd(jsonTotal);
//        } catch (JSONException e) {
//            EToast.showToast(getActivity(), "保存失败！");
//            e.printStackTrace();
//            return;
//        }
//        Log.d(TAG, "saveInfo:4 ");
//    }
//
//
//
//    @Override
//    public void onRun() {
//    }
//
//    @Override
//    public void onCaptrue() {
//    }
//
//
//
//}
