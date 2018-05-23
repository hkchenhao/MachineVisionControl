package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.HomeAcitivity;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.ui.view.ProgressBox;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.ButterKnife;

/**
 * Created by HeQian on 2017/9
 * 几何缺陷检测方法参数学习界面
 */

public class FragmentGeometryLearning extends Fragment implements View.OnClickListener, OperationInterface {
    /*界面常量，用于通用控制帧  */
    private final byte page_addr = 0x03;
    private final byte fr_addr = 0x31;
    private final byte Pn_DCN = 0x01;
    private final int CMD_Start_Learning = 1;
    /**
     * UI组件
     */
    private View mView;
    private Spinner spCamera; //相机选择

    private TextView textCount; //学习计数值
    private EditText editText; //需学习目标数
    private Button btControl; //检测控制按钮
    private Button btStartAll; //开始学习按钮

    private TextView[] textPara = new TextView[9];

    private Byte countInput = 0;

    /*常量*/
    //网络命令常量
    final byte NET_START = 0;
    final byte NET_PAUSE = 1;
    final byte NET_CONTINUE = 2;
    final byte NET_RESTART = 3;
    final byte NET_CANSEL = 5;

    //当前学习状态
    private enum state {
        NULL, LEARNING, PAUSED, LEARNED
    }

    final String[] cam_list = {"相机1"};
    state learningState = state.NULL;

    /*重写函数 创建界面*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MachineLearning) {
            MachineLearning machineLearning = (MachineLearning) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_geometrylearning, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }


    @Override
    public void onStart() {
        AppContext.setHandler(mHandler);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
        super.onStart();
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    /*初始化数据*/
    private void initData() {
        countInput = 30;
    }

    /*初始化界面*/
    private void initView() {
        textCount = (TextView) getView().findViewById(R.id.text_learning_count);
        editText = (EditText) getView().findViewById(R.id.text_learning_all);
        spCamera = (Spinner) getView().findViewById(R.id.sp_cam_choose);
        textPara[0] = (TextView) getView().findViewById(R.id.tx_pra1);
        textPara[1] = (TextView) getView().findViewById(R.id.tx_pra2);
        textPara[2] = (TextView) getView().findViewById(R.id.tx_pra3);
        textPara[3] = (TextView) getView().findViewById(R.id.tx_pra4);
        textPara[4] = (TextView) getView().findViewById(R.id.tx_pra5);
        textPara[5] = (TextView) getView().findViewById(R.id.tx_pra6);
        textPara[6] = (TextView) getView().findViewById(R.id.tx_pra7);
        textPara[7] = (TextView) getView().findViewById(R.id.tx_pra8);
        textPara[8] = (TextView) getView().findViewById(R.id.tx_pra9);
        btControl = (Button) getView().findViewById(R.id.bt_control_learning);
        btStartAll = (Button) getView().findViewById(R.id.btn_start_geo_learning);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, cam_list);
        spCamera.setAdapter(adapter1);
        editText.setText(String.valueOf(countInput));
        btControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_control_learning:
                        learningControl();
                        break;
                    default:
                        break;
                }
            }
        });

        btStartAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**发送启动测试命令*/
                if (netJudge()) {
                    byte[] net_tx_data = NetUtils.createNetCmdData(page_addr, fr_addr, Pn_DCN, CMD_Start_Learning);
                    if (net_tx_data != null) {
                        CmdHandle.getInstance().sendCmdInfo(getcamID(), net_tx_data);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONObject btnCfgJson = ((MachineLearning) getActivity()).getJson();
                    String btnCfgJsonName = ((MachineLearning) getActivity()).getButtonId();
                    if (btnCfgJson != null) {
                        EToast.showToast(getActivity(), "向相机" + getcamID() + "发送钮扣配置文件:" + btnCfgJsonName);
                        CmdHandle.getInstance().sendBtnCfgJson(getcamID(), btnCfgJson.toString().getBytes());
                        btControl.setEnabled(true);
                    } else {
                        EToast.showToast(getActivity(), "请选择钮扣配置文件！！！");
                    }
                }
            }
        });
    }

    /*帧接收处理*/
   /*1.计数 帧头 标识帧（类型）计数
   *2.状态帧
    *
     * */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case NetUtils.MSG_SELFLEARNING_COUNT:
                    parseSelfLearningCount(msg);
                    break;
                case NetUtils.MSG_SELFLEARNING_STATE:
                    parseSelfLearningState(msg);
                    break;
                case NetUtils.MSG_SELFLEARLING_PARAMETER:
                    saveParameter(parseParameter(msg));
                    break;
                case NetUtils.MSG_SELFLEARLING_PARADISPLAY:
                    showParameter(parseParameter(msg));
                    break;
                default:
                    break;
            }
        }
    };

    /*自学习终端控制函数*/
    private void learningControl() {
        byte[] data = new byte[3];
        if (learningState == state.NULL) {
            //发送网络包
            if (inputJudge() && netJudge() && fileJudge()) {
                data[0] = NET_START;//开始训练
                data[1] = 0;
                data[2] = countInput;//第二字节为目标训练数量
                CmdHandle.getInstance().sendSelflearningTotal("1", data);
                //按键设置
                btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cansel));
                btControl.setText("取消");
                //训练状态设置
                editText.setEnabled(false);
                learningState = state.LEARNING;
            } else return;
        } else if (learningState == state.LEARNING) {
            //发送网络包
            data[0] = NET_CANSEL;//取消训练
            data[1] = 0;
            CmdHandle.getInstance().sendSelflearningTotal("1", data);
            //按键设置
            btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
            btControl.setText("开始");
            //训练状态设置
            textCount.setText("");
            editText.setEnabled(true);
            learningState = state.NULL;
        } else if (learningState == state.LEARNED) {
            if (inputJudge() && netJudge()) {
                data[0] = NET_RESTART;//开始训练
                data[1] = 0;
                data[2] = countInput;//第二字节为目标训练数量
                CmdHandle.getInstance().sendSelflearningTotal("1", data);
                //按键设置
                btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_cansel));
                btControl.setText("取消");
                //计数值归零
                textCount.setText("");
                //训练状态设置
                editText.setEnabled(false);
                learningState = state.LEARNING;
            } else return;
        }
    }


    /**
     * 解析网络包计数值
     *
     * @param msg 由一个字节的type和一个字节的计数值组成
     */
    private void parseSelfLearningCount(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null)
            return;
        byte type = data[0];
        byte count = data[1];
        switch (type) {
            case 0:
                textCount.setText(String.valueOf(count));
                break;
            default:
                break;
        }
    }

    /**
     * 解析DSP网络包状态信息
     *
     * @param msg 由一个字节的type与一个字节的状态节组成
     */
    private void parseSelfLearningState(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null)
            return;
        int type = data[0];
        int state = data[1];
        switch (type) {
            case 4:
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                return;
        }
        switch (state) {
            case 0:
                ((MachineLearning) getActivity()).writeStatus("正在进行几何参数自学习");
                editText.setEnabled(false);
                break;
            case 1:
                ((MachineLearning) getActivity()).writeStatus("已完成几何参数自学习");
                btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                btControl.setText("重新开始");
                editText.setEnabled(true);
                break;
            case 255:
                reset();
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                break;
        }
    }


    /**
     * 解析网络包参数
     *
     * @param msg 一个字节的参数数量+未定长度的一组4字节参数值
     */
    private float[] parseParameter(Message msg) {
        Log.d("nettest", "parseParameter: 0");
        byte[] data = (byte[]) msg.obj;
        byte count = data[0]; //获取参数数量

        if ((data == null) || (count == 0))
            return null;

        float[] parameter = new float[count];
        for (int i = 0; i < count; i++) {
            byte[] tem = new byte[4];
            float parameterIn;
            System.arraycopy(data, 4 * i + 2, tem, 0, 3);
            parameterIn = (float) (byteArrayToInt(tem) * 1.0);//转换为浮点数
            parameter[i] = parameterIn / (float) 100.0;//除以100
            Log.d("nettest", "parseParameter count" + String.valueOf(i) + ":" + String.valueOf(parameterIn) + String.valueOf(byteArrayToInt(tem)) + String.valueOf(parameter[i]));
        }

        return parameter;

    }

    /**
     * 显示网络包参数
     */
    private void showParameter(float para[]) {
        int count = para.length;
        if (count > 9) count = 9;//至多显示9个参数
        for (int i = 0; i < count; i++) {
            textPara[i].setText(String.valueOf(para[i]));
        }
    }

    /**
     * 保存网络包参数
     */
    private void saveParameter(float para[]) {
        int count = para.length;

        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            json.put("pranum", count);
            for (int i = 0; i < count; i++) {
                json.put("para" + String.valueOf(i), para[i]);
            }
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }
        try {
            jsonTotal.put(ButtonKeys.GEOMETRY_ALGPARA, json);
            SettingProfile buttonProfile = new SettingProfile(((MachineLearning) getActivity()).getButtonId());
            buttonProfile.write2sd(jsonTotal);
        } catch (JSONException e) {
            EToast.showToast(getActivity(), "保存失败！");
            e.printStackTrace();
            return;
        }

    }


    //byte 数组与 int 的相互转换
    private int byteArrayToInt(byte[] b) {

        return (getUnsigned(b[0])) | (getUnsigned(b[1]) << 8)
                | (getUnsigned(b[2]) << 16) | (getUnsigned(b[3]) << 24);
    }

    /**
     * 返回无符号数
     * @param data
     * @return
     */
    private int getUnsigned(byte data) {
        return data & 0x0FF;
    }

    /*
    *学习数量判断 输入数量需大于5小于100
    * */
    private boolean inputJudge() {
        Editable s = editText.getText();
        if (TextUtils.isEmpty(s.toString())) {
            EToast.showToast(getActivity(), "输入为空");
            return false;
        }
        if (((Integer.valueOf(s.toString())) < 100) && (Integer.valueOf(s.toString()) > 5)) {
            countInput = Byte.valueOf(s.toString());
            return true;
        } else {
            editText.setText("30");
            countInput = 30;
            EToast.showToast(getActivity(), "训练数量请输入5-100");
            return false;
        }
    }

    /*
    *网络连接状态判断
    * */
    private boolean netJudge() {
        if (AppContext.getAppContext().isExist(getcamID())) {
            return true;
        } else {
            EToast.showToast(getActivity(), "相机" + getcamID() + "网络未连接");
            return false;
        }
    }

    /*
    *配置文件判断
    * */
    private boolean fileJudge() {
        if (AppContext.btnCfgProfile != null) {
            EToast.showToast(getActivity(), "钮扣文件：" + (AppContext.btnCfgProfile.getID()));
            return true;
        } else {
            EToast.showToast(getActivity(), "请返回主界面选择钮扣配置文件");
            return false;
        }
    }

    /**
     * 复位函数
     * */
    private void reset() {
        countInput = 30;

        textCount.setText("");
        for (int i = 0; i < textPara.length; i++)
            textPara[i].setText("");
        btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
        btControl.setText("开始");
        editText.setText(String.valueOf(countInput));
        editText.setEnabled(true);

        EToast.showToast(getActivity(), "数据重置完成");
    }

    private String getcamID() {
        return "1";
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSave() {

    }

    @Override
    public void onRun() {

    }

    @Override
    public void onCaptrue() {

    }
}
