package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.OperationInterface;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LitWill on 2017/8/3.
 */

public class FragmentSelfLearning extends Fragment  implements View.OnClickListener, OperationInterface {

    /*控件声明*/
    private View mView;
    private TextView tittleView;
    @Bind(R.id.btn_TEST)
    Button bt_TEST;
    @Bind(R.id.sp_cam_choose)
    Spinner sp_cam;
    //正面正品相关控件
    @Bind(R.id.imv_learning_1)
    ImageView imv1;
    @Bind(R.id.bt_control_learning_1)
    Button button1;
    @Bind(R.id.text_learning_count_1)
    TextView count1;
    @Bind(R.id.text_learning_all_1)
    EditText editText1;

    //正面次品相关控件
    @Bind(R.id.imv_learning_2)
    ImageView imv2;
    @Bind(R.id.bt_control_learning_2)
    Button button2;
    @Bind(R.id.text_learning_count_2)
    TextView count2;
    @Bind(R.id.text_learning_all_2)
    EditText editText2;

    //反面正品相关控件
    @Bind(R.id.imv_learning_3)
    ImageView imv3;
    @Bind(R.id.bt_control_learning_3)
    Button button3;
    @Bind(R.id.text_learning_count_3)
    TextView count3;
    @Bind(R.id.text_learning_all_3)
    EditText editText3;

    //反面次品相关控件
    @Bind(R.id.imv_learning_4)
    ImageView imv4;
    @Bind(R.id.bt_control_learning_4)
    Button button4;
    @Bind(R.id.text_learning_count_4)
    TextView count4;
    @Bind(R.id.text_learning_all_4)
    EditText editText4;
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
    state camState1 = state.NULL;
    state camState2 = state.NULL;
    state camState3 = state.NULL;
    state camState4 = state.NULL;

    /*界面数据*/
    //输入目标检测值 默认值为30
    private byte countInput1 = 30;
    private byte countInput2 = 30;
    private byte countInput3 = 30;
    private byte countInput4 = 30;



    //当前操作相机
    private enum camList{
    }

    /*重写函数 创建界面*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fr_selflerning, container, false);
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

   /*帧接收处理*/
   /*1.计数 帧头 标识帧（类型）计数
   *2.状态帧
    *
     * */
    Handler mHandler = new Handler() {
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what) {
               case NetUtils.MSG_SELFLEARNING_COUNT:
                   parseSelfLearningCount(msg);
                   break;
               case NetUtils.MSG_SELFLEARNING_STATE:
                   parseSelfLearningState(msg);
                   break;

           }
       }
   };

   /*初始化数据*/
   private void initData(){
       String[] cam_list = {"相机2","相机3"};
       ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, cam_list);
       sp_cam.setAdapter(adapter1);

       countInput1 = 30;
       countInput2 = 30;
       countInput3 = 30;
       countInput4 = 30;
   }
    /*初始化数据*/
    private void initView(){
        editText1.setText(String.valueOf(countInput1));
        editText2.setText(String.valueOf(countInput2));
        editText3.setText(String.valueOf(countInput3));
        editText4.setText(String.valueOf(countInput4));

        tittleView.setText("自学习训练");
    }

    /**
     * 设置按键的监听器
     *
     * @param view 点击的按钮视图
     */
    @OnClick({ R.id.bt_control_learning_1, R.id.bt_control_learning_2,
            R.id.bt_control_learning_3, R.id.bt_control_learning_4,})
    public void onClick(View view) {
        byte[] data = new byte[3];
        if (inputJudge()&& netJudge()) {
            switch (view.getId()) {
                case R.id.bt_control_learning_1:
                    if (camState1 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 0;
                        data[2] = countInput1;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button1.setEnabled(true);
                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button1.setText("暂停");
                        //训练状态设置
                        camState1 = state.LEARNING;
                    } else if (camState1 == state.LEARNING) {
                        //发送网络包
                        data[0] = NET_PAUSE;//暂停训练
                        data[1] = 0;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button1.setEnabled(true);
                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
                        button1.setText("继续");
                        //训练状态设置
                        camState1 = state.PAUSED;
                    } else if (camState1 == state.PAUSED) {
                        //发送网络包
                        data[0] = NET_CONTINUE;//继续训练
                        data[1] = 0;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button1.setEnabled(true);
                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button1.setText("暂停");
                        //训练状态设置
                        camState1 = state.LEARNING;
                    } else if (camState1 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 0;
                        data[2] = countInput1;//第三字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button1.setEnabled(true);
                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button1.setText("暂停");
                        //训练状态设置
                        camState1 = state.LEARNING;
                    }
                    break;

                case R.id.bt_control_learning_2:
                    if (camState2 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 1;
                        data[2] = countInput2;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button2.setEnabled(true);
                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button2.setText("暂停");
                        //训练状态设置
                        camState2 = state.LEARNING;
                    } else if (camState2 == state.LEARNING) {
                        //发送网络包
                        data[0] = NET_PAUSE;//暂停训练
                        data[1] = 1;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button2.setEnabled(true);
                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
                        button2.setText("继续");
                        //训练状态设置
                        camState2 = state.PAUSED;
                    } else if (camState2 == state.PAUSED) {
                        //发送网络包
                        data[0] = NET_CONTINUE;//继续训练
                        data[1] = 1;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button2.setEnabled(true);
                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button2.setText("暂停");
                        //训练状态设置
                        camState2 = state.LEARNING;
                    } else if (camState2 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 1;
                        data[2] = countInput2;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button2.setEnabled(true);
                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button2.setText("暂停");
                        //训练状态设置
                        camState2 = state.LEARNING;
                    }
                    break;

                case R.id.bt_control_learning_3:
                    if (camState3 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 2;
                        data[2] = countInput3;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button3.setEnabled(true);
                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button3.setText("暂停");
                        //训练状态设置
                        camState3 = state.LEARNING;
                    } else if (camState3 == state.LEARNING) {
                        //发送网络包
                        data[0] = NET_PAUSE;//暂停训练
                        data[1] = 2;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button3.setEnabled(true);
                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
                        button3.setText("继续");
                        //训练状态设置
                        camState3 = state.PAUSED;
                    } else if (camState3 == state.PAUSED) {
                        //发送网络包
                        data[0] = NET_CONTINUE;//继续训练
                        data[1] = 2;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button3.setEnabled(true);
                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button3.setText("暂停");
                        //训练状态设置
                        camState3 = state.LEARNING;
                    } else if (camState3 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 2;
                        data[2] = countInput3;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button3.setEnabled(true);
                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button3.setText("暂停");
                        //训练状态设置
                        camState3 = state.LEARNING;
                    }
                    break;

                case R.id.bt_control_learning_4:
                    if (camState4 == state.NULL) {
                        //发送网络包
                        data[0] = NET_START;//开始训练
                        data[1] = 3;
                        data[2] = countInput4;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button4.setEnabled(true);
                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button4.setText("暂停");
                        //训练状态设置
                        camState4 = state.LEARNING;
                    } else if (camState4 == state.LEARNING) {
                        //发送网络包
                        data[0] = NET_PAUSE;//暂停训练
                        data[1] = 3;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button4.setEnabled(true);
                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_start));
                        button4.setText("继续");
                        //训练状态设置
                        camState4 = state.PAUSED;
                    } else if (camState4 == state.PAUSED) {
                        //发送网络包
                        data[0] = NET_CONTINUE;//继续训练
                        data[1] = 3;
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button4.setEnabled(true);
                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button4.setText("暂停");
                        //训练状态设置
                        camState4 = state.LEARNING;
                    } else if (camState4 == state.LEARNED) {
                        //发送网络包
                        data[0] = NET_RESTART;//重新开始训练
                        data[1] = 3;
                        data[2] = countInput4;//第二字节为目标训练数量
                        CmdHandle.getInstance().sendSelflearningTotal(getcamID(), data);
                        //按键设置
                        buttonset(false);
                        button4.setEnabled(true);
                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pause));
                        button4.setText("暂停");
                        //训练状态设置
                        camState4 = state.LEARNING;
                    }
                    break;
                case R.id.btn_TEST:
                    test();
                    break;
                default:
                    break;
            }
        }
    }

    /*
    发送函数

    */


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
                count1.setText(String.valueOf(count));
                break;
            case 1:
                count2.setText(String.valueOf(count));
                break;
            case 2:
                count3.setText(String.valueOf(count));
                break;
            case 3:
                count4.setText(String.valueOf(count));
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
        String str = "";
        switch (type){
            case 0:
                str = "正面正品";
                break;
            case 1:
                str = "正面次品";
                break;
            case 2:
                str = "反面正品";
                break;
            case 3:
                str = "反面次品";
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                break;
        }
        switch (state){
            case 0 :
                ((MachineLearning) getActivity()).writeStatus(str + "正在进行自学习");
                break;
            case 1 :
                ((MachineLearning) getActivity()).writeStatus(str + "自学习完成");
                buttonset(true);
                switch (type){
                    case 0:
                        camState1 = FragmentSelfLearning.state.LEARNED;
                        button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button1.setText("开始");//BUG
                        break;
                    case 1:
                        camState2 = FragmentSelfLearning.state.LEARNED;
                        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button2.setText("开始");
                        break;
                    case 2:
                        camState3 = FragmentSelfLearning.state.LEARNED;
                        button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button3.setText("开始");
                        break;
                    case 3:
                        camState4 = FragmentSelfLearning.state.LEARNED;
                        button4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_restart));
                        button4.setText("开始");
                        break;
                    default:
                        break;
                }
                break;
            default:
                EToast.showToast(AppContext.getAppContext(), "传输错误");
                break;
        }
    }

    /*根据spinner获取相机ID*/
    private String getcamID(){
        String str = "";
        switch (sp_cam.getSelectedItemPosition()){
            case 0:
                str = "2";
                break;
            case 1:
                str = "3";
                break;
            default:
                break;
        }
        return str;

    }
    private void buttonset(boolean ins){
        button1.setEnabled(ins);
        button2.setEnabled(ins);
        button3.setEnabled(ins);
        button4.setEnabled(ins);
    }

        private boolean inputJudge( ){
            Editable[] s = new Editable[4];
            s[0] = editText1.getText();
            s[1] = editText2.getText();
            s[2] = editText3.getText();
            s[3] = editText4.getText();
            boolean flag = true;
            for (int i =0;i<4 ;i++) {
                if (TextUtils.isEmpty(s[i].toString())) {
                    EToast.showToast(getActivity(), "第" + String.valueOf(i) + "训练项目输入为空");
                    return false;
                }
            }

            for (int i =0;i<4 ;i++) {
                byte countTem = 30;
                boolean flagTem = true;
                if (((Integer.valueOf(s[i].toString())) < 100) && (Integer.valueOf(s[i].toString()) > 5)) {
                     countTem  = Byte.valueOf(s[i].toString()) ;
                }
                else flagTem = false;
                switch (i) {
                    case 0 :
                        if (flagTem == true) countInput1 = countTem;
                        else {
                            editText1.setText("30");
                            countInput1 = 30;
                            EToast.showToast(getActivity(), "训练数量请输入5-100");
                            flag = false;
                        }
                        break;
                    case 1 :
                        if (flagTem == true) countInput2 = countTem;
                        else {
                            editText2.setText("30");
                            countInput2 = 30;
                            EToast.showToast(getActivity(), "训练数量请输入5-100");
                            flag = false;
                        }
                        break;
                    case 2 :
                        if (flagTem == true) countInput3 = countTem;
                        else {
                            editText3.setText("30");
                            countInput3 = 30;
                            EToast.showToast(getActivity(), "训练数量请输入5-100");
                            flag = false;
                        }
                        break;
                    case 3 :
                        if (flagTem == true) countInput4 = countTem;
                        else {
                            editText4.setText("30");
                            countInput4 = 30;
                            EToast.showToast(getActivity(), "训练数量请输入5-100");
                            flag = false;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (flag ) return true;
            else return false;
    }

    private boolean netJudge() {
        if (AppContext.getAppContext().isExist(getcamID())) {
            return  true;
        } else {
            EToast.showToast(getActivity(), "相机" + getcamID() + "网络未连接");
            return false;
        }
    }

//    private boolean inputJudge( EditText editText  ){
//        Editable s = editText.getText();
//        if(TextUtils.isEmpty(s.toString())) {
//            EToast.showToast(getActivity(),"输入为空");
//            return false;}
//        if(((Integer.valueOf(s.toString()) ) < 100) && (Integer.valueOf(s.toString()) > 5 )) {
//            if(editText == editText1) countInput1 = Byte.valueOf(s.toString()) ;
//            else if(editText == editText2) countInput2 = Byte.valueOf(s.toString()) ;
//            else if(editText == editText3) countInput3 = Byte.valueOf(s.toString()) ;
//            else if(editText == editText4) countInput4 = Byte.valueOf(s.toString()) ;
//            return true;
//        }
//        else{
//            editText.setText("30");
//            if(editText == editText1) countInput1 = 30 ;
//            else if(editText == editText2) countInput2 = 30 ;
//            else if(editText == editText3) countInput3 = 30 ;
//            else if(editText == editText4) countInput4 = 30 ;
//            EToast.showToast(getActivity(),"训练数量请输入5-100");
//            return false;
//        }
//    }

    private  void test(){
        imv1.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_right));
    }
    private void rest(){
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
