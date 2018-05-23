package com.hanyu.hust.testnet.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.net.CmdHandle;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.ui.view.ProgressBox;
import com.hanyu.hust.testnet.ui.view.SeekBarEditLayout;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.ExcuteTask;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by LitWill on 2017/8/4.
 * 暂未使用
 */

public class FragmentTradition extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "FragmentTradition" ;
    /*
    *控件声明
    * */
    private View mView;
    //标题
    private TextView tittleView;
    //相机选择
    private Spinner sp_cam_choose;
    //滑动控件
    Button bt_TEST;
    SeekBarEditLayout se_1;
    SeekBarEditLayout se_2;
    SeekBarEditLayout se_3;
    SeekBarEditLayout se_4;
    SeekBarEditLayout se_5;
    SeekBarEditLayout se_6;
    //图片控件
    ImageView iv_1;
    ImageView iv_2;
    ImageView iv_3;
    //网络状态提示窗
    private ProgressDialog mProgressDialog;
    /*变量资源*/
    private int picChoose = 0;
    private static ByteBuffer buffer = ByteBuffer.allocate(8);//long转byte数组缓存
    /**
     * 异步事件处理器
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetUtils.MSG_GET_ROI: // 定时保存数据
                {
                    handlerRoi(msg , picChoose);
                }
                break;
            }
        }
    };


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
        mView = inflater.inflate(R.layout.fr_tradition, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        initData();
        initView();

    }

    @Override
    public void onStart(){
        // 将handler 切换回主界面
        AppContext.setHandler(mHandler);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
        super.onStart();
    }

    private void initData(){
        sp_cam_choose = (Spinner) getView().findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner, Constants.camList);
        sp_cam_choose.setAdapter(adapter);
    }

    private void  initView(){
        tittleView.setText("传统方法");

        bt_TEST = (Button) getView().findViewById(R.id.btn_TEST);
        se_1 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra_st1);
        se_2 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra_st2);
        se_3 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra_st3);
        se_4 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra_st4);
        se_5 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra_st5);
        se_6 = (SeekBarEditLayout) getView().findViewById(R.id.se_pra_st6);

        iv_1 = (ImageView) getView().findViewById(R.id.iv_1);
        iv_2 = (ImageView) getView().findViewById(R.id.iv_2);
        iv_3 = (ImageView) getView().findViewById(R.id.iv_3);



        bt_TEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

//        ArrayAdapter<String> spinnerAadapter = new ArrayAdapter<String>(this,
//                R.layout.layout_spinner, getDataSource());
//        spinnerAadapter.setDropDownViewResource(R.layout.layout_spinner);


        iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imvFunc(1);
            }
        });

        iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imvFunc(2);
            }
        });

        iv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imvFunc(3);
            }
        });


    }





    // 网络获取数据，处理显示函数
    private void handlerRoi(Message msg , int picChoose) {
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
            switch (picChoose){
                case 1:
                    iv_1.setImageBitmap(bm);
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    break;
                case 2 :
                    iv_2.setImageBitmap(bm);
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    break;
                case 3 :
                    iv_3.setImageBitmap(bm);
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    break;
            }
        } else
            return;
    }

    /*根据spinner获取相机ID*/
    private String getcamID(){
        String str = "1";
        switch (sp_cam_choose.getSelectedItemPosition()){
            case 0:
                str = "1";
                break;
            case 1:
                str = "2";
                break;
            case 2:
                str = "3";
                break;
            default:
                break;
        }
        return str;
    }


    /*获取图像*/
    private void getPic(){
        if (AppContext.getAppContext().isExist( getcamID() )) {
            CmdHandle.getInstance().getRoiImage(getcamID(), mHandler);
//            mProgressDialog = ProgressBox.showCancleable(getActivity(), "等待获取图像......"); // 进程弹窗
//            mProgressDialog.show();
        } else {
            EToast.showToast(getActivity(), "相机"+ getcamID() +"网络未连接");
        }
    }
    /*发送参数*/
    private void sendPra(String camId ,int picChoose ){
        long[] pra = new long[6];//参数数组
        byte[] data = new byte[50];//一字节选择图片 一字节预留 48字节参数数据（8X6）

        pra[0] = se_1.getValue();
        pra[1] = se_2.getValue();
        pra[2] = se_3.getValue();
        pra[3] = se_4.getValue();
        pra[4] = se_5.getValue();
        pra[5] = se_6.getValue();

        data[0] = (byte) picChoose ;//选择图片
        data[1] = 0;//预留字节

        for (int i=0 ; i<6; i++){
            System.arraycopy(longToBytes(pra[i]) , 0 ,data , i*8+2 , 8);//将pra[i]的八字节数组填充入data
        }
        CmdHandle.getInstance().sendTraditionPrameter(getcamID(), data);

    }
    /*/byte 数组与 long 的相互转换 高字节在前 /*/
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    private void imvFunc(int imv)
    {
        picChoose = imv;
        sendPra(getcamID() , picChoose);//发送参数
        ExcuteTask.sleep(100);
        getPic();
    }

    public List<String> getDataSource() {
        List<String> spinnerList = new ArrayList<String>();
        spinnerList.add("相机1");
        spinnerList.add("相机2");
        spinnerList.add("相机3");
        return spinnerList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
