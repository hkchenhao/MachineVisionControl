package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.LogUtil;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  Created by HanYu on 2017/4
 * 机器学习界面下背面信息配置碎片
 */

public class FragmentButtoInfoBack extends Fragment implements OperationInterface {
    /*界面资源（同正面信息碎片）*/
    private static final String TAG = "FragmentButtoInfoBack";
    @Bind(R.id.sp_material)
    Spinner sp_material;
    @Bind(R.id.sp_shape)
    Spinner sp_shape;
    @Bind(R.id.sp_hole_num)
    Spinner sp_hole_num;

    @Bind(R.id.sp_light)
    Spinner sp_light;

    @Bind(R.id.sp_reflect)
    Spinner sp_reflect;

    @Bind(R.id.sp_word)
    Spinner sp_word;

    @Bind(R.id.sp_color)
    Spinner sp_color;

    @Bind(R.id.et_size)
    EditText et_size;

    @Bind(R.id.iv_roi)
    ImageView iv_roi;

    private View mView;
    private TextView tittleView;


    private ProgressDialog mProgressDialog;

    /**
     * 异步事件处理器
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetUtils.MSG_GET_ROI: {
                    handlerRoi(msg);
                }
                break;
            }
        }
    };

    /**
     * 网络传输ROI图像显示
     * @param msg
     * @return
     */
    private void handlerRoi(Message msg) {
        byte[] packageData = (byte[]) msg.obj;
        if (packageData == null || packageData.length <= 8)
            return;

        int[] data = new int[8];
        for (int i = 0; i < 8; i++) {
            data[i] = packageData[i] & 0xFF;
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8;
        int height = data[6] | data[7] << 8;
        if (len <= 0 || width <= 0 || height <= 0)
            return;

        LogUtil.d(TAG, "len = " + len + ", width = " + width + ", height = " + height);
        Bitmap bm = null;
        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {
            bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int[] image = new int[len];

            if ((len == width * height)) {
                int temp;
                for (int i = 0; i < len; i++) {
                    temp = packageData[i + 8] & 0xff;
                    image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
                }
            } else if (len == 3 * width * height) {
                for (int i = 0; i < len / 3; i++) {
                    int r = 0, g = 0, b = 0;
                    r = packageData[8 + i * 3] & 0xff;
                    g = packageData[8 + i * 3 + 1] & 0xff;
                    b = packageData[8 + i * 3 + 2] & 0xff;
                    image[i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            } else
                return;
            bm.setPixels(image, 0, width, 0, 0, width, height);
            if (msg.arg1 == 1) {
                iv_roi.setImageBitmap(bm);//在碎片中显示纽扣背面图像
            //    mProgressDialog.dismiss();
            }
        } else
            return;

    }
    /*
    * 监听ROI图像按钮
    * */
    @OnClick({R.id.iv_roi})
    public void getRoi(View view) {
        CmdHandle.getInstance().getRoiImage("1", mHandler);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("等待获取图像....");
        mProgressDialog.show();
    }

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
        mView = inflater.inflate(R.layout.fr_button_info_back, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initDefault();
    }

    /*
    * 初始化数据
    * */
    private void initData() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.btnShapeTypeCnList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.btnHoleNumCnList);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.btnLightTypeCnList);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.btnColorTypeCnList);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.btnPatternTypeCnList);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.btnMaterialTypeCnList);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.btnReflectTypeCnList);

        sp_shape.setAdapter(adapter1);
        sp_hole_num.setAdapter(adapter2);
        sp_hole_num.setSelection(1);
        sp_light.setAdapter(adapter3);
        sp_color.setAdapter(adapter4);
        sp_word.setAdapter(adapter5);
        sp_material.setAdapter(adapter6);
        sp_reflect.setAdapter(adapter7);
    }
    /*
    * 读取纽扣配置文件初始化界面
    * */
    private void initDefault() {
        JSONObject jsonTotal = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getJson();
        Log.d(TAG, "initDefault ");

        if (jsonTotal != null &&  ((com.hanyu.hust.testnet.ui.MachineLearning)getActivity()).getButtonId()!= ""  ) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.FRONT);
                String holeNum = json.getString(ButtonKeys.holeNumF);
                String size = json.getString(ButtonKeys.sizeF);
                String material = json.getString(ButtonKeys.materialF);
                String light = json.getString(ButtonKeys.lightF);
                String reflect = json.getString(ButtonKeys.reflectF);

                sp_hole_num.setSelection(findIndex(Constants.btnHoleNumEnList, holeNum));
                sp_material.setSelection(findIndex(Constants.btnMaterialTypeEnList, material));
                et_size.setText(size);
                sp_light.setSelection(findIndex(Constants.btnLightTypeEnList, light));
                sp_reflect.setSelection(findIndex(Constants.btnReflectTypeEnList, reflect));


                sp_hole_num.setEnabled(false);
                sp_material.setEnabled(false);
                et_size.setEnabled(false);
                sp_light.setEnabled(false);
                sp_reflect.setEnabled(false);

            } catch (JSONException e) {
                Log.d(TAG, e + "");
                e.printStackTrace();
            }
            tittleView.setText("钮扣配置文件：" + ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId());
        } else {
            tittleView.setText("");
        }
    }
    /*
    * @return
    *
    * */
    private int findIndex(String[] arrays, String val) {
        int index = 0;
        for (String array : arrays) {
            if (array.equals(val))
                return index;
            index++;
        }
        if (index == arrays.length)
            return 0;
        return index;
    }

    @Override
    public void onSave() {
        if (TextUtils.isEmpty(((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId())) {
            EToast.showToast(getActivity(), "为钮扣取个名字吧");
            return;
        }
        ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).writeStatus("背面信息:配置完成");

        Bitmap bitmap = ((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getmBitmapB();
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.sample3);
        SettingProfile bp = new SettingProfile(((com.hanyu.hust.testnet.ui.MachineLearning) getActivity()).getButtonId());
        bp.saveMyBitmapBack(bitmap);
        EToast.showToast(getActivity(), "保存成功");
    }

    @Override
    public void onRun() {

    }

    @Override
    public void onCaptrue() {

    }
}
