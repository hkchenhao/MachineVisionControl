package com.hanyu.hust.testnet.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.can.Can;
import com.hanyu.hust.testnet.can.CanMsg;
import com.hanyu.hust.testnet.entity.DetailInfo;
import com.hanyu.hust.testnet.entity.SysDevPacket.AD9849;
import com.hanyu.hust.testnet.entity.SysDevPacket.Parameters;
import com.hanyu.hust.testnet.net.NetUtils.NetPacket;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * 网络接收线程类
 */
public class NetReceiveThread extends Thread {

    private static final String TAG = "NetReceiveThread";

    volatile boolean stop = false;

    private String tag;    //相机号

    private InputStream mIs; //输入流

    private OutputStream mOs; //输出流

    private Socket mSocket;

    private Handler mHandler; //接收handler

    private NetPacket mRevPacket = new NetPacket();

    private int mCameraID;  //相机号

    private int countQualified, countDisqualified; //合格品计数值、不合格品计数值

    public Thread setThreadName(String name) {
        this.setName(name);
        return this;
    }

    /**
     * 构造函数
     * @param CameraID 目前没用到
     * @param socket
     * @param handler
     * @throws Exception
     */
    public NetReceiveThread(int CameraID, Socket socket, Handler handler) throws Exception {
        this.mSocket = socket;

        mIs = socket.getInputStream();
        mOs = socket.getOutputStream();

        String IP = mSocket.getInetAddress().toString();
        IP = IP.replaceAll("[^0-9.]", "");

        /**
         * IP 写死，可做修改
         */
        if ("115.156.211.1".equals(IP))
            tag = "1";
        else if ("115.156.211.2".equals(IP))
            tag = "2";
        else if ("115.156.211.4".equals(IP))
            tag = "3";
        else
            throw new Exception();

        this.mCameraID = Integer.valueOf(tag);
        this.mHandler = handler;
    }

    /**
     * 返回标签值，“1” 代表1号相机
     * @return
     */
    public String getTag() {
        return tag;
    }

    public InputStream getmIs() {
        return mIs;
    }

    public OutputStream getmOs() {
        return mOs;
    }

    public Socket getmSocket() {
        return mSocket;
    }

    /**
     * 清空计数值
     */
    public void clearCount() {
        countDisqualified = 0;
        countQualified = 0;
    }

    /**
     * 清空资源
     */
    public void close() {
        try {
            stop = true;
            mSocket.close();
            mIs.close();
            mOs.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e + "occur");
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 线程运行内容
     */
    @Override
    public void run() {

        Log.d(TAG, "NetReceiveThread init success");
        while (!stop) {
            long time1 = System.currentTimeMillis();
            mRevPacket.recvDataPack(mCameraID, mIs);

            long time2 = System.currentTimeMillis();
//            LogUtil.i(TAG, "Eclipse time = " + (time2 - time1));

            if (mRevPacket.type != 0xaa)// 如果数据正常，表示网络通畅
            {
                Log.d(TAG, "run: got packet"+String.valueOf(mRevPacket.minid));
                switch (mRevPacket.minid) {
                    case NetUtils.MSG_NET_GET_VIDEO: {
                        Message message = Message.obtain();
                        message.arg1 = Integer.valueOf(tag);
                        message.what = NetUtils.MSG_NET_GET_VIDEO;
                        byte[] rxBuf = mRevPacket.data;
                        message.obj = rxBuf;
                        mHandler.sendMessage(message);
                        break;
                    }
                    case NetUtils.MSG_NET_STATE: {
                        byte[] temp = Arrays.copyOfRange(mRevPacket.data,
                                mRevPacket.data.length - 12, mRevPacket.data.length);
                        switch (CmdHandle.getIntFromArray(Arrays.copyOf(temp, 4))) {
                            case 0x01:
                                int tempInteger = CmdHandle.getIntFromArray(Arrays
                                        .copyOfRange(temp, 4, 8));
                                int tempFloat = CmdHandle.getIntFromArray(Arrays
                                        .copyOfRange(temp, 8, 12));

                                Message message = Message.obtain();
                                message.obj = mCameraID;
                                message.what = NetUtils.MSG_NET_STATE;
                                message.arg1 = tempInteger;
                                message.arg2 = tempFloat;
                                mHandler.sendMessage(message);
                        }
                        break;
                    }
                    case NetUtils.MSG_NET_GET_PARAM:{
                        Message message = Message.obtain();
                        message.arg1 = Integer.valueOf(tag);
                        message.what = NetUtils.MSG_NET_GET_PARAM;
                        byte[] rxBuf = mRevPacket.data;
                        message.obj = rxBuf;
                        mHandler.sendMessage(message);
                        break;
                    }
                    case NetUtils.MSG_NET_GET_JSON: {
                        String str = new String(Arrays.copyOfRange(mRevPacket.data,
                                100, mRevPacket.data.length));
//                        LogUtil.d(TAG, "json is:" + str);

                        JsonParser jParser = new JsonParser();
                        Gson gson = new Gson();

                        Parameters.setInstance(gson.fromJson(jParser.parse(str)
                                .getAsJsonObject().toString(), Parameters.class));
                        Parameters p = Parameters.getInstance();
                        AD9849 ad = p.ad9849;
                        ad.pageContents[0] = ad.vga[0] + ad.vga[1] << 8;
                        ad.pageContents[1] = ad.shp;
                        ad.pageContents[2] = ad.hpl;
                        ad.pageContents[3] = ad.rgpl;
                        ad.pageContents[4] = ad.pxga[0];
                        ad.pageContents[5] = ad.pxga[1];
                        ad.pageContents[6] = ad.pxga[2];
                        ad.pageContents[7] = ad.pxga[3];
                        ad.pageContents[8] = ad.rgdrv;
                        ad.pageContents[9] = ad.shd;
                        ad.pageContents[10] = ad.hnl;
                        ad.pageContents[11] = ad.rgnl;
                        ad.pageContents[12] = ad.hxdrv[0];
                        ad.pageContents[13] = ad.hxdrv[1];
                        ad.pageContents[14] = ad.hxdrv[2];
                        ad.pageContents[15] = ad.hxdrv[3];

                        Message message = Message.obtain();
                        message.arg1 = mCameraID;
                        message.what = NetUtils.MSG_NET_GET_JSON;
                        mHandler.sendMessage(message);
                        break;
                    }
                    case NetUtils.MSG_NET_RESULT: {
                        byte[] rxBuf = mRevPacket.data;
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_NET_RESULT;
                        message.obj = rxBuf;
                        message.arg1 = Integer.valueOf(tag);
                        Bundle bundle = new Bundle();
                        // 不合格
                        if (rxBuf[rxBuf.length - 1] == 0) {
                            ++countDisqualified;
                            bundle.putBoolean("result", false);
                        } else {
                            ++countQualified;
                            bundle.putBoolean("result", true);
                        }
                        bundle.putInt("qualified", countQualified);
                        bundle.putInt("disqualified", countDisqualified);
                        message.setData(bundle);
                        mHandler.sendMessage(message);

                        break;
                    }
                    case NetUtils.MSG_NET_GET_COLORIMAGE: {
                        Message message = Message.obtain();
                        message.arg1 = mCameraID;
                        message.what = NetUtils.MSG_NET_GET_COLORIMAGE;
                        byte[] rxBuf = mRevPacket.data;
                        message.obj = rxBuf;
                        mHandler.sendMessage(message);
                        break;
                    }
                    case NetUtils.MSG_NET_GET_FEATURERESULT: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_NET_GET_FEATURERESULT;
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }

                    // 计数帧
                    case NetUtils.MSG_NET_TOTAL_CNT: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_NET_TOTAL_CNT;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }

                    // 结果帧
                    case NetUtils.MSG_ALG_RESULT: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_ALG_RESULT;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }

                    case NetUtils.MSG_GET_ROI: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_GET_ROI;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }
                    case NetUtils.MSG_NET_GET_IMAGE: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_NET_GET_IMAGE;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }
                    // 图像帧
                    case NetUtils.MSG_NET_ALG_IMAGE:
                    {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_NET_ALG_IMAGE;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }

                    // 钮扣信息帧（详细版）
                    case NetUtils.MSG_DETAIL_INFO: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_DETAIL_INFO;
                        message.arg1 = Integer.valueOf(tag);
                        String str = new String(Arrays.copyOfRange(mRevPacket.data,
                                0, mRevPacket.data.length));
                        try {
//                            Log.d(TAG, "str:" + str);
                            JsonParser jParser = new JsonParser();
                            Gson gson = new Gson();
                            DetailInfo simpleInfo = gson.fromJson(jParser.parse(str)
                                    .getAsJsonObject().toString(), DetailInfo.class);
                            message.obj = simpleInfo;
                            mHandler.sendMessage(message);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    // 钮扣信息帧+计数帧
                        //// TODO: 2018/1/31  
                    case NetUtils.MSG_NET_DETECT_INFO: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_NET_DETECT_INFO;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                    }
                    /*心跳应答*/
                    case NetUtils.MSG_HEART_BEAT:{
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_HEART_BEAT;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }

                    /*自学习计数 added heqian ,on 2017/8 */

                    case NetUtils.MSG_SELFLEARNING_COUNT: {
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_SELFLEARNING_COUNT;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }
                    /*自学习状态帧 */
                    case NetUtils.MSG_SELFLEARNING_STATE:{
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_SELFLEARNING_STATE;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }
                    /*自学习参数保存帧*/
                    case NetUtils.MSG_SELFLEARLING_PARAMETER:{
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_SELFLEARLING_PARAMETER;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }
                    /**自学习参数显示帧*/
                    case NetUtils.MSG_SELFLEARLING_PARADISPLAY:{
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_SELFLEARLING_PARADISPLAY;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }


                    /*钮扣配置，接收处理*/
                    case NetUtils.MSG_BTN_CFG_JSON: {
                        mHandler.sendEmptyMessage(NetUtils.MSG_BTN_CFG_JSON);
                        break;
                    }
                    /*算法配置，接收处理*/
                    case NetUtils.MSG_ALG_CFG_JSON:{
                        mHandler.sendEmptyMessage(NetUtils.MSG_ALG_CFG_JSON);
                        break;
                    }
                    /**NET 2 CAN*/
                    case NetUtils.MSG_NET_TO_CAN:{
                        CanMsg net2can_msg = Can.netMsg2CanMsg(mRevPacket.data);
                        if (net2can_msg != null){
                            int mask = Integer.valueOf(tag);
                            net2can_msg.id |= mask;
                            net2can_msg.data[0] |= mask;
                            net2can_msg.data[1] |= mask;
                            AppContext.can0.write(net2can_msg);
                        }
                        break;
                    }
                    case NetUtils.MSG_NET_CMD:{
                        Message message = Message.obtain();
                        message.what = NetUtils.MSG_NET_CMD;
                        message.arg1 = Integer.valueOf(tag);
                        message.obj = mRevPacket.data;
                        mHandler.sendMessage(message);
                        break;
                    }
                    default: {
                        LogUtil.e(TAG, "default: minid=" + mRevPacket.minid);
                        break;
                    }
                }
            } else // 接收的数据不正常，表示网络故障 Close
            {
                LogUtil.e(TAG, "packet == null");
            }
        } // while
        LogUtil.d(TAG, "NetReceiveThread exit with interrupted");
    }
}
