package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.hanyu.hust.testnet.ui.view.ProgressBox;
import com.hanyu.hust.testnet.utils.AlgSettingUtils;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.LogUtil;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 正面信息配置类
 */
public class FragmentButtoInfoFront extends Fragment implements OnClickListener, OperationInterface {

    private static final String TAG = "FragmentButtoInfoFront";

    private View mView;
    private TextView tittleView;


    @Bind(R.id.sp_material)
    Spinner sp_material;

    @Bind(R.id.sp_shape)
    Spinner sp_shape;

    @Bind(R.id.sp_hole_num)
    Spinner sp_hole_num;

    @Bind(R.id.sp_light)
    Spinner sp_light;

    @Bind(R.id.sp_word)
    Spinner sp_word;

    @Bind(R.id.sp_color)
    Spinner sp_color;

    Button bt_confirm, bt_cancel;

    @Bind(R.id.et_size)
    EditText et_size;

    @Bind(R.id.iv_roi)
    ImageView iv_roi;

    private EditText tv_id, tv_manufature;

    private String buttonId, manufature;

    private AlertDialog alertDialog;

    private ProgressDialog mProgressDialog;

    /**
     * 异步事件处理器
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.d(TAG, "handleMessage");
            switch (msg.what) {
                case NetUtils.MSG_GET_ROI: // 定时保存数据
                {
                    handlerRoi(msg);
                }
                break;
            }
        }
    };

    // 网络获取数据，处理显示函数
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
            if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {
                iv_roi.setImageBitmap(bm);
//                if (mProgressDialog != null)
//                    mProgressDialog.dismiss();
                EToast.showToast(getActivity(), "图像获取成功");
                ((MachineLearning) getActivity()).setmBitmapF(bm);
            }
        } else
            return;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_button_info_front, container, false);
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
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnShapeTypeCnList);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnHoleNumCnList);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnLightTypeCnList);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnColorTypeCnList);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnPatternTypeCnList);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(getActivity(),R.layout.layout_spinner, Constants.btnMaterialTypeCnList);

        sp_shape.setAdapter(adapter1);
        sp_hole_num.setAdapter(adapter2);
        sp_light.setAdapter(adapter3);
        sp_color.setAdapter(adapter4);
        sp_word.setAdapter(adapter5);
        sp_material.setAdapter(adapter6);
    }

    /**
     * 初始化默认数据
     */
    private void initDefault() {
        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        Log.d(TAG, "initDefault ");

        if (jsonTotal != null) {
            try {
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.FRONT);
                String holeNum = json.getString(ButtonKeys.holeNumF);
                String size = json.getString(ButtonKeys.sizeF);
                String material = json.getString(ButtonKeys.materialF);
                String light = json.getString(ButtonKeys.lightF);
                String pattern = json.getString(ButtonKeys.patternF);
                String color = json.getString(ButtonKeys.colorF);
                String shape = json.getString(ButtonKeys.shapeF);

                sp_shape.setSelection(      AlgSettingUtils.findIndex(Constants.btnShapeTypeEnList,     shape));
                sp_color.setSelection(      AlgSettingUtils.findIndex(Constants.btnColorTypeEnList,     color));
                sp_word.setSelection(       AlgSettingUtils.findIndex(Constants.btnPatternTypeEnList,   pattern));
                sp_hole_num.setSelection(   AlgSettingUtils.findIndex(Constants.btnHoleNumEnList,       holeNum));
                sp_material.setSelection(   AlgSettingUtils.findIndex(Constants.btnMaterialTypeEnList,  material));
                et_size.setText(size);
                sp_light.setSelection(      AlgSettingUtils.findIndex(Constants.btnLightTypeEnList,     light));

            } catch (JSONException e) {
                Log.d(TAG, e + "");
                e.printStackTrace();
            }
            iv_roi.setImageBitmap(((MachineLearning) getActivity()).getmBitmapF());
            tittleView.setText("纽扣配置文件："+((MachineLearning)getActivity()).getButtonId());
        }
        else{
            tittleView.setText("纽扣配置文件：");
        }
    }

    /**
     * 绑定监听事件，获取ROi图像
     *
     * @param view
     */
    @OnClick({R.id.iv_roi})
    public void getRoi(View view) {
        String tag = null;

        if (AppContext.getAppContext().isExist("2"))
            tag = "2";
        else if(AppContext.getAppContext().isExist("3"))
            tag = "3";
        else if(AppContext.getAppContext().isExist("1"))
            tag = "1";
        else {
            tag = null;
        }

        if (tag != null ){
            CmdHandle.getInstance().getRoiImage(tag, mHandler);
            EToast.showToast(getActivity(), "相机"+ tag +"请放入纽扣");
//            mProgressDialog = ProgressBox.showCancleable(getActivity(), "等待获取图像......"); // 进程弹窗
//            mProgressDialog.show();
        }
        else {
            EToast.showToast(getActivity(), "相机网络未连接");
        }
    }



    /**
     * 创建配置文件对话框
     */
    @Override
    public void onSave() {
        if (!firstEnter()) {
            DialogWindow dialogWindow = new DialogWindow.Builder(getActivity())
                    .setTitle("确定修改正面配置信息?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveInfo();
                            ((MachineLearning) getActivity()).writeStatus("正面信息:修改完成");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialogWindow.showWrapContent();
        } else {
            String name = getName();
            if (TextUtils.isEmpty(name))
                return;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getActivity(), R.layout.dialog_rname, null);
            bt_confirm = (Button) view.findViewById(R.id.bt_rname_dialog_ok);
            bt_cancel = (Button) view.findViewById(R.id.bt_rname_dialog_cancel);
            tv_id = (EditText) view.findViewById(R.id.et_id);
            tv_id.setText(name);
            tv_id.setSelection(name.length());
            tv_manufature = (EditText) view.findViewById(R.id.et_manufature);
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

    /**
     * 绑定控件监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_rname_dialog_ok:
                buttonId = tv_id.getText().toString().trim();
                manufature = tv_manufature.getText().toString().trim();

                if (TextUtils.isEmpty(buttonId) || TextUtils.isEmpty(manufature)) {
                    EToast.showToast(getActivity(), "输入不能为空");
                    return;
                }

                ((MachineLearning) getActivity()).setButtonId(buttonId);

                ((MachineLearning) getActivity()).setTitle("钮扣配置文件:" + buttonId);

                saveInfo();
                if (((MachineLearning) getActivity()).mFragment != null) {
                    ((MachineLearning) getActivity()).writeStatus("钮扣:" + buttonId);
                    ((MachineLearning) getActivity()).writeStatus("钮扣信息:正面配置完成");
                }
                alertDialog.dismiss();
                break;
            case R.id.bt_rname_dialog_cancel:
                alertDialog.dismiss();
            default:
                break;
        }
    }


    /**
     * 保存信息
     */
    private void saveInfo() {
        if (TextUtils.isEmpty(((MachineLearning) getActivity()).getButtonId())) {
            EToast.showToast(getActivity(), "为钮扣取个名字吧");
            return;
        }

        final JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            json.put(ButtonKeys.materialF,  Constants.btnMaterialTypeEnList[sp_material.getSelectedItemPosition()]);
            json.put(ButtonKeys.sizeF, et_size.getText());
            json.put(ButtonKeys.shapeF,     Constants.btnShapeTypeEnList[sp_shape.getSelectedItemPosition()]);
            json.put(ButtonKeys.holeNumF,   Constants.btnHoleNumEnList[sp_hole_num.getSelectedItemPosition()]);
            json.put(ButtonKeys.lightF,     Constants.btnLightTypeEnList[sp_light.getSelectedItemPosition()]);
            json.put(ButtonKeys.patternF,   Constants.btnPatternTypeEnList[sp_word.getSelectedItemPosition()]);
            json.put(ButtonKeys.colorF,     Constants.btnColorTypeEnList[sp_color.getSelectedItemPosition()]);
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }

        Bitmap bitmap = ((MachineLearning) getActivity()).getmBitmapF();
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.sample3);

        try {
            jsonTotal.put(ButtonKeys.NAME,((MachineLearning) getActivity()).getButtonId());
            jsonTotal.put(ButtonKeys.FRONT, json);
            SettingProfile bp = new SettingProfile(((MachineLearning) getActivity()).getButtonId());
            bp.write2sd(jsonTotal);
            bp.saveMyBitmap(bitmap);
            EToast.showToast(getActivity(), "保存成功");
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
    }

    /**
     * 判断有效性
     * -1 ：输入不合法；
     * 0: 数据不合理；
     * 正常返回取整数据，例如11.4->11
     */
    public int getIntSize(String s) {
        Double a;
        try {
            a = Double.parseDouble(s);
        } catch (Exception e) {
            return -1;
        }

        int result = (int) Math.round(a);
        if (result < 0)
            return 0;
        else
            return result;
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
                JSONObject json = jsonTotal.getJSONObject(ButtonKeys.FRONT);
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
     * 根据配置项目生成文件名
     *
     * @return
     */
    private String getName() {
        StringBuilder name = new StringBuilder();
        switch (sp_material.getSelectedItemPosition()) {
            case 0:
                name.append("S");
                break;
            case 1:
                name.append("G");
                break;
            case 2:
                name.append("B");
                break;
            case 3:
                name.append("J");
                break;
            case 4:
                name.append("Y");
                break;
            default:
                break;
        }

        if (TextUtils.isEmpty(et_size.getText().toString().trim())) {
            EToast.showToast(getActivity(), "请先输入尺寸信息");
            return null;
        }
        int size = getIntSize(et_size.getText().toString().trim());
        if (size <= 0 || size > 50) {
            EToast.showToast(getActivity(), "尺寸设置不合理");
            return null;
        }
        if (size < 10)
            name.append("0" + size);
        else
            name.append("" + size);

        switch (sp_shape.getSelectedItemPosition()) {
            case 0:
                name.append("0");
                break;
            case 1:
                name.append("2");
                break;
            case 2:
                name.append("3");
                break;
            case 3:
                name.append("4");
                break;
            case 4:
                name.append("5");
                break;
            case 5:
                name.append("6");
                break;
            default:
                name.append("9");
                break;
        }

        switch (sp_hole_num.getSelectedItemPosition()) {
            case 0:
                name.append("4");
                break;
            case 1:
                name.append("2");
                break;
            default:
                name.append("*");
                break;
        }

        switch (sp_word.getSelectedItemPosition()) {
            case 0:
                name.append("0");
                break;
            case 1:
                name.append("1");
                break;
            case 2:
                name.append("2");
                break;
            case 3:
                name.append("3");
                break;
            case 4:
                name.append("4");
                break;
            default:
                break;
        }

        switch (sp_color.getSelectedItemPosition()) {
            case 0:
                name.append("0");
                break;
            case 1:
                name.append("1");
                break;
            case 2:
                name.append("2");
                break;
            case 3:
                name.append("3");
                break;
            case 4:
                name.append("4");
                break;
            case 5:
                name.append("5");
                break;
            case 6:
                name.append("6");
                break;
            case 7:
                name.append("7");
                break;
            default:
                name.append("9");
                break;
        }
        name.append("-");
        return name.toString();
    }

}
