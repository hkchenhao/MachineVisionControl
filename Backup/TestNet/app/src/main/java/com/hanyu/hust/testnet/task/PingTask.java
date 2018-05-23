package com.hanyu.hust.testnet.task;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.net.NetReceiveThread;
import com.hanyu.hust.testnet.net.NetUtils;
import com.hanyu.hust.testnet.net.PingUtils;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.util.Map;

/**
 * Created by Administrator on 2017/4/11.
 * 监听设备的网路连接稳定性的任务
 */


public class PingTask implements Runnable {
    private static final String TAG = "PingTask";

    private PingState mListener;

    public PingTask(PingState listener) {
        mListener = listener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map<String, NetReceiveThread> links = AppContext.getAppContext().getmLinks();
                for (String key : links.keySet()) {
                    NetReceiveThread netThread = links.get(key);
                    if (netThread != null) {
                        String IP = netThread.getmSocket().getInetAddress() + "";
                        IP = IP.replaceAll("[^0-9.]", "");

                        // 物理连接是否失效
                        boolean result = PingUtils.ping(IP);
                        if (!result) {
                            NetUtils.setIp();
                            if (mListener != null) {
                                // 回调
                                mListener.fail(netThread.getmSocket().getInetAddress() + "", key);
                            }
                        }

                        // 心跳包，逻辑连接是否失效
                        try {
                            netThread.getmSocket().sendUrgentData(0xFF);
                        } catch (Exception ex) {
                            if (mListener != null && AppContext.getAppContext().isExist(key)) {
                                // 回调
                                mListener.fail(netThread.getmSocket().getInetAddress() + "", key);
                            }
                        }
                        LogUtil.d(TAG, "result : " + result + "\n" + "ip:" + IP);
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (AppContext.getAppContext().isLinksEmpty())
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }
}
