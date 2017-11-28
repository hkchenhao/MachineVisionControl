package com.hanyu.hust.testnet.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.AlgCfgJson;
import com.hanyu.hust.testnet.entity.AlgCfgJsonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.AlgSettingUtils;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View.OnClickListener;

/**
 * 检测任务及门限配置类
 */
public class FragmentAlgSetting extends Fragment implements OnClickListener, OperationInterface {

    Spinner[] mSpinner = new Spinner[9];



    /*右下角的确认和退出按键*/
    Button bt_confirm, bt_cancel;

    /*确认弹框中的内容*/
    private EditText tv_id, tv_manufacturer;
    private AlertDialog alertDialog;

    /*设置文件名、厂家名*/
    String algCfgFileName = null;
    String manufacturerName = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_task, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidget();
        initData();
    }

    private void initWidget() {
        mSpinner[0] = (Spinner) getActivity().findViewById(R.id.sp_camera_sel);
        mSpinner[1] = (Spinner) getActivity().findViewById(R.id.sp_alg0);
        mSpinner[2] = (Spinner) getActivity().findViewById(R.id.sp_alg1);
        mSpinner[3] = (Spinner) getActivity().findViewById(R.id.sp_alg2);
        mSpinner[4] = (Spinner) getActivity().findViewById(R.id.sp_alg3);
        mSpinner[5] = (Spinner) getActivity().findViewById(R.id.sp_alg4);
        mSpinner[6] = (Spinner) getActivity().findViewById(R.id.sp_alg5);
        mSpinner[7] = (Spinner) getActivity().findViewById(R.id.sp_alg6);
        mSpinner[8] = (Spinner) getActivity().findViewById(R.id.sp_alg7);

        ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.cameraList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);
        ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.algCnList);

        mSpinner[0].setAdapter(adapter0);
        mSpinner[1].setAdapter(adapter1);
        mSpinner[2].setAdapter(adapter2);
        mSpinner[3].setAdapter(adapter3);
        mSpinner[4].setAdapter(adapter4);
        mSpinner[5].setAdapter(adapter5);
        mSpinner[6].setAdapter(adapter6);
        mSpinner[7].setAdapter(adapter7);
        mSpinner[8].setAdapter(adapter8);
    }

    private void initData() {
        algCfgFileName = ((MachineLearning) getActivity()).getAlgCfgFileName();
        if (algCfgFileName != null) {
            ((MachineLearning) getActivity()).setTitle("算法配置文件：" + algCfgFileName);
            SettingProfile profile = new SettingProfile(AppDirectory.getAlgDirectory() + algCfgFileName, algCfgFileName);
            if (profile != null) {
                JSONObject jObj = profile.getJsonStr();
                try {
                    String camNum;
                    JSONObject algCfgJObj;
                    String[] arg = new String[AlgCfgJsonKeys.argc];

                    camNum = jObj.getString(AlgCfgJsonKeys.cameraNum);
                    mSpinner[0].setSelection(AlgSettingUtils.findIndex(Constants.cameraList, camNum));

                    algCfgJObj = jObj.getJSONObject(AlgCfgJsonKeys.atomAlg);
                    for(int i=0;i<AlgCfgJsonKeys.argc;i++){
                        arg[i] = AlgSettingUtils.algNameEn2Cn(algCfgJObj.getString(AlgCfgJsonKeys.arg[i]));
                        mSpinner[i+1].setSelection(AlgSettingUtils.findIndex(Constants.algCnList, arg[i]));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ((MachineLearning) getActivity()).setTitle("算法配置文件：");
        }
    }

    /**
     * 保存配置参数到Json文件中
     */
    protected void saveAlgInfo() {
        JSONObject jsonTotal = new JSONObject();
        JSONObject json = new JSONObject();

        String name = ((MachineLearning) getActivity()).getAlgCfgFileName();
        if (name != null) {
            SettingProfile profile = new SettingProfile(AppDirectory.getAlgDirectory() + algCfgFileName, algCfgFileName);
            if (profile != null) {
                jsonTotal = profile.getJsonStr();
            }
        }

        try {
            for(int i=0;i<AlgCfgJsonKeys.argc;i++){
                json.put(AlgCfgJsonKeys.arg[i], Constants.algEnList[mSpinner[i+1].getSelectedItemPosition()]);
            }
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ex_doc);

        try {
            jsonTotal.put(AlgCfgJsonKeys.cameraNum, Constants.cameraList[mSpinner[0].getSelectedItemPosition()]);
            jsonTotal.put(AlgCfgJsonKeys.atomAlg, json);
            SettingProfile profile = new SettingProfile(AppDirectory.getAlgDirectory() + algCfgFileName, algCfgFileName);
            profile.write2sd(jsonTotal);
            profile.saveMyBitmap(bitmap);
            ((MachineLearning) getActivity()).setTitle("算法配置文件：" + algCfgFileName);
            EToast.showToast(getActivity(), "保存成功");
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败");
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void onSave() {
        String name = ((MachineLearning) getActivity()).getAlgCfgFileName();
        if (name != null) {
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改算法配置信息？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveAlgInfo();
                            ((MachineLearning) getActivity()).writeStatus("算法配置文件：" + algCfgFileName);
                            ((MachineLearning) getActivity()).writeStatus("算法配置完成");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialogWindow.showWrapContent();
        } else {
            /**
             * 由外部输入文件名
             * */
            name = getName();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getActivity(), R.layout.dialog_rname, null);
            bt_confirm = (Button) view.findViewById(R.id.bt_rname_dialog_ok);
            bt_cancel = (Button) view.findViewById(R.id.bt_rname_dialog_cancel);
            tv_id = (EditText) view.findViewById(R.id.et_id);
            tv_id.setText(name);
            tv_id.setSelection(name.length());
            tv_manufacturer = (EditText) view.findViewById(R.id.et_manufature);
            bt_confirm.setOnClickListener(this);
            bt_cancel.setOnClickListener(this);
            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onRun() {

    }

    @Override
    public void onCaptrue() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_rname_dialog_ok: {
                algCfgFileName = tv_id.getText().toString().trim();
                manufacturerName = tv_manufacturer.getText().toString().trim();
                if (TextUtils.isEmpty(algCfgFileName)) {
                    EToast.showToast(getActivity(), "名字不能为空");
                    return;
                }

                saveAlgInfo();
                if (((MachineLearning) getActivity()).mFragment != null) {
                    ((MachineLearning) getActivity()).writeStatus("算法配置文件：" + algCfgFileName);
                    ((MachineLearning) getActivity()).writeStatus("算法配置完成");
                }
                alertDialog.dismiss();
                break;
            }
            case R.id.bt_rname_dialog_cancel: {
                alertDialog.dismiss();
                break;
            }
            default: {
                break;
            }
        }

    }

    private String getName() {
        StringBuilder name = new StringBuilder();
        name.append("ALG-");
        return name.toString();
    }

}
