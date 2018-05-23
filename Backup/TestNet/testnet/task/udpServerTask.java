package com.hanyu.hust.testnet.task;

import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.net.UdpServerSocket;
import com.hanyu.hust.testnet.utils.LogUtil;

import static com.hanyu.hust.testnet.utils.Constants.TIMEOUT;

/**
 * Created by Administrator on 2017/4/11.
 * UDP服务器类，已废弃，不需要看
 */

public class udpServerTask implements Runnable {

    private static final String TAG = "udpServerTask";

    private UdpState mlistener;

    private State connectState = State.Init;

    public static enum State {
        Init, udpFail, udpSuccess
    }

    public udpServerTask(int cameraId, UdpState listener) {
        mCameraId = cameraId;
        mlistener = listener;
    }

    private int mCameraId;

    @Override
    public void run() {

        NetUtils.setIp();

        UdpServerSocket udpSocket = null;

        try {
            udpSocket = new UdpServerSocket(NetUtils.listenBroadCastPort);
            udpSocket.setSoTimeout(TIMEOUT);

            while (connectState == State.Init) {
                String name = udpSocket.receive();
                if (name.startsWith("Get Server IP")) {
                    LogUtil.d(TAG, "get qualified string, send 3th times message");

                    int times = 0;
                    while (times++ < 5) {
                        udpSocket.response(NetUtils.ip + "\0", NetUtils.sendIpPort);
                        Thread.sleep(200);
                    }
                    connectState = State.udpSuccess;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "udpSocket = " + udpSocket + "blocked");
            LogUtil.e(TAG, "error:" + e);
            if (mlistener != null) {
                mlistener.fail(e);
            }
        } finally {
            if (udpSocket != null) {
                LogUtil.e(TAG, "finally udpSocket closed");
                udpSocket.close();
            }
            else
            {
                LogUtil.e(TAG, "udpSocket empty");
            }
        }
    }

}
