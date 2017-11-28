package com.hanyu.hust.testnet.task;

import android.os.Handler;
import android.os.Message;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.net.NetReceiveThread;
import com.hanyu.hust.testnet.utils.Constants;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.hanyu.hust.testnet.utils.Constants.TIMEOUT;

/**
 * 服务器监听线程，监听来自外部连接
 */
public class TCPServerService extends Thread {

    private static final String TAG = "TCPServerService";

    private boolean mIsServiceDestoryed = false;

    private final Handler mHandler;

    public TCPServerService(Handler handler) {
        mHandler = handler;
    }

    public Thread setThreadName(String name) {
        setName(name);
        return this;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6020);
        } catch (IOException e) {
            LogUtil.e(TAG, "establish tcp server failed, port:6020");
            e.printStackTrace();
            return;
        }

        while (!mIsServiceDestoryed) {
            Socket socket = null;
            try {
                // 接受客户端请求
                socket = serverSocket.accept();

                LogUtil.d(TAG, "client accept, ip" + socket.getInetAddress());
                socket.setSoTimeout(TIMEOUT);
                NetReceiveThread netReceiveThread = new NetReceiveThread(1, socket, mHandler);

                String tag = netReceiveThread.getTag();
                AppContext.getAppContext().addLink(tag, netReceiveThread);
                netReceiveThread.setThreadName("netReceiveThread" + tag).start();

                Message msg = Message.obtain();
                msg.arg1 = Integer.valueOf(tag);
                msg.what = Constants.CONNECT_SUCCESS;
                mHandler.sendMessage(msg);

            } catch (Exception e) {
                LogUtil.e(TAG, "Exception");
                if (socket != null)
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
            }
        }

        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
