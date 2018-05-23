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
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.entity.ButtonKeys;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.ui.view.ProgressBox;
import com.hanyu.hust.testnet.utils.OperationInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.ButterKnife;

/**
 * Created by LitWill on 2017/8/18.
 */

public class FragmentGeometryLearning extends Fragment implements View.OnClickListener, OperationInterface {

    private View mView;
    private TextView tittleView ;

    private TextView textCount;
    private EditText editText;
    private Button btControl;

    private TextView[] textPara = new TextView[9];

    private Byte countInput = 0 ;

    /*常量*/
    //网络命令常量
    final byte NET_START = 0 ;
    final byte NET_PAUSE = 1 ;
    final byte NET_CONTINUE = 2 ;
    final byte NET_RESTART = 3 ;
    //当前学习状态
    private enum state{
        NULL,LEARNING,PAUSED,LEARNED
    }
    state learningState = state.NULL;

    /*重写函数 创建界面*/

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
        mView = inflater.inflate(R.layout.fr_geometrylearning, container, false);
        ButterKnife.bind(this, mView);//绑定framgent
        return mView;
    }



    @Override
    public void onStart(){
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
    private void initData(){
        countInput = 30;
    }
    /*初始化数据*/
    private void initView(){
        textCount = (TextView) getView().findViewById(R.id.text_learning_count);
        editText = (EditText) getView().findViewById(R.id.text_learning_all);
        textPara[0] = (TextView)  getView().findViewById(R.id.tx_pra1);
        textPara[1] = (TextView)  getView().findViewById(R.id.tx_pra2);
        textPara[2] = (TextView)  getView().findViewById(R.id.tx_pra3);
        textPara[3] = (TextView)  getView().findViewById(R.id.tx_pra4);
        textPara[4] = (TextView)  getView().findViewById(R.id.tx_pra5);
        textPara[5] = (TextView)  getView().findViewById(R.id.tx_pra6);
        textPara[6] = (TextView)  getView().findViewById(R.id.tx_pra7);
        textPara[7] = (TextView)  getView().findViewById(R.id.tx_pra8);
        textPara[8] = (TextView)  getView().findViewById(R.id.tx_pra9);
        btControl =  (Button) getView().findViewById(R.id.bt_control_learning);

        editText.setText(String.valueOf(countInput));
        tittleView.setText("自学习训练");
        btControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                learningControl();
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


    private void learningControl(){
        byte[] data = new byte[3];
        if (learningState == state.NULL) {
            //发送网络包
            if(inputJudge() && netJudge() && fileJudge()){
                data[0] = NET_START;//开始训练
                data[1] = 0;
                data[2] = countInput;//第二字节为目标训练数量
                CmdHandle.getInstance().sendSelflearningTotal("1", data);
                //按键设置
                btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                btControl.setText("暂停");
                //训练状态设置
                editText.setEnabled(false);
                learningState = state.LEARNING;
            }
            else return;
        }
        else if (learningState  == state.LEARNING){
            //发送网络包
            data[0] = NET_PAUSE;//暂停训练
            data[1] = 0;
            CmdHandle.getInstance().sendSelflearningTotal("1", data);
            //按键设置
            btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
            btControl.setText("继续");
            //训练状态设置
            learningState = state.PAUSED;
        }
        else if (learningState == state.PAUSED){
            //发送网络包
            data[0] = NET_CONTINUE;//继续训练
            data[1] = 0;
            CmdHandle.getInstance().sendSelflearningTotal("1", data);
            //按键设置
            btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
            btControl.setText("暂停");
            //训练状态设置
            learningState = state.LEARNING;
        }
        else if (learningState == state.LEARNED){
            if(inputJudge() && netJudge()){
                data[0] = NET_RESTART;//开始训练
                data[1] = 0;
                data[2] = countInput;//第二字节为目标训练数量
                CmdHandle.getInstance().sendSelflearningTotal("1", data);
                //按键设置
                btControl.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                btControl.setText("暂停");
                //训练状态设置
                editText.setEnabled(false);
                learningState = state.LEARNING;
            }
            else return;
        }
    }


    /**
     * 解析网络包计数值
     *
     * @param msg
     */
    private void parseSelfLearningCount(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null )
            return;
        byte type = data[0] ;
        byte count = data[1];
        switch (type){
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
     * @param msg
     */
    private void parseSelfLearningState(Message msg) {
        byte[] data = (byte[]) msg.obj;
        if (data == null)
            return;
        int type = data[0];
        int state = data[1];
        switch (type){
            case 4 :
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                return;
        }
        switch (state){
            case 0 :
                ((MachineLearning) getActivity()).writeStatus( "正在进行几何参数自学习");
                editText.setEnabled(false);
                break;
            case 1 :
                ((MachineLearning) getActivity()).writeStatus( "已完成几何参数自学习");
                editText.setEnabled(true);
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                break;
        }
    }

    /**
     * 解析网络包参数
     *
     * @param msg
     */
    private float[] parseParameter(Message msg){
        Log.d("nettest","parseParameter: 0");
        byte[] data = (byte[]) msg.obj;
        byte count = data[0] ;

        if ((data == null) || (count == 0) )
            return null;

        float[] parameter = new float[count];
        for(int i=0 ; i<count ; i++ ){
            byte[] tem = new byte[4];
            float parameterIn ;

            System.arraycopy(data,4*i+2,tem,0,3);
            parameterIn = (float) (byteArrayToInt(tem) * 1.0);
            parameter[i] =  parameterIn /(float)100.0;
            Log.d("nettest", "parseParameter count"+ String.valueOf(i)+":"+ String.valueOf(parameterIn) +String.valueOf(byteArrayToInt(tem)) + String.valueOf(parameter[i]) );
        }

        return parameter;

    }
    /**
    * 显示网络包参数
    */
    private void showParameter(float para[]){
        int count = para.length;
        if (count > 9) count = 9;//至多显示9个参数
        for (int i = 0 ; i<count ; i++){
            textPara[i].setText(String.valueOf(para[i]));
        }
    }

    /**
     * 保存网络包参数
     */
    private void saveParameter(float para[]){
        int count = para.length;

        JSONObject jsonTotal = ((MachineLearning) getActivity()).getJson();
        JSONObject json = new JSONObject();
        try {
            json.put("pranum", count);
            for(int i = 0 ; i < count ; i++){
                json.put("para"+String.valueOf(i),para[i]);
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

        return  (getUnsigned(b[0])) | (getUnsigned(b[1]) << 8)
                | (getUnsigned(b[2]) << 16) | (getUnsigned(b[3]) << 24);
    }

    /**
     * 返回无符号数
     *
     * @param data
     * @return
     */
    private int getUnsigned(byte data) {
        return data & 0x0FF;
    }

    private boolean inputJudge(){
        Editable s = editText.getText();
        if(TextUtils.isEmpty(s.toString())) {
            EToast.showToast(getActivity(),"输入为空");
            return false;}
        if(((Integer.valueOf(s.toString()) ) < 100) && (Integer.valueOf(s.toString()) > 5 )) {
            countInput = Byte.valueOf(s.toString()) ;
            return true;
        }
        else{
            editText.setText("30");
            countInput = 30;
            EToast.showToast(getActivity(),"训练数量请输入5-100");
            return false;
        }
    }

    private boolean netJudge() {
        if (AppContext.getAppContext().isExist(getcamID())) {
            return  true;
        } else {
            EToast.showToast(getActivity(), "相机" + getcamID() + "网络未连接");
            return false;
        }
    }

    private boolean fileJudge(){
        if(AppContext.btnCfgProfile != null) {
            EToast.showToast(getActivity(), "纽扣文件：" + (AppContext.btnCfgProfile.getID()));
            return true;
        }
        else {
            EToast.showToast(getActivity(),"请返回主界面选择纽扣配置文件");
            return false;
        }
        }

    private String getcamID(){
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
