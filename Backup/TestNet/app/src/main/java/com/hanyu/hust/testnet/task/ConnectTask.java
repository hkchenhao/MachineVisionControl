package com.hanyu.hust.testnet.task;

import android.os.Handler;
import android.os.Message;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.LogUtil;

/**
 * Created by Administrator on 2017/4/13.
 * UDP连接任务，目前新版本已废弃UDP连接功能，此类不需要研究
 */
public class ConnectTask {

    private static final String TAG = ConnectTask.class.getSimpleName();

    udpServerTask mUdpTask;

    TcpServerTask mTask;

    public ConnectTask(final int CammeraId, final Handler mHandler) {

        mUdpTask = new udpServerTask(CammeraId, new UdpState() {
            @Override
            public void fail(Exception e) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        EToast.showToast(AppContext.getAppContext(), "未收到UDP广播");
//                    }
//                });
                LogUtil.d(TAG, "udp blocked");
            }

            @Override
            public void success() {
                LogUtil.d(TAG, "udp success");
            }
        });

        mTask = new TcpServerTask(6019 + CammeraId, mHandler, new TcpState() {
            @Override
            public void fail(Exception e) {
                LogUtil.d(TAG, "tcp blocked");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        EToast.showToast(AppContext.getAppContext(), "相机"+CammeraId+"连接失败");
                    }
                });
                Message msg = Message.obtain();
                msg.arg1 = CammeraId;
                msg.what = Constants.CONNECT_FAIL;
                mHandler.sendMessage(msg);
            }

            @Override
            public void success(String socketAddress) {
                LogUtil.d(TAG, "tcp success");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        EToast.showToast(AppContext.getAppContext(), "相机"+CammeraId+"连接成功");
                    }
                });
                Message msg = Message.obtain();
                msg.arg1 = CammeraId;
                msg.what = Constants.CONNECT_SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void close() {
                LogUtil.d(TAG, "tcp closed");
            }
        });
    }

    public void  start()
    {
        new Thread(mUdpTask).start();
        new Thread(mTask).start();
    }
}
