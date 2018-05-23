package com.hanyu.hust.testnet.task;

/**
 * Created by Administrator on 2017/4/11.
 */

import android.os.Handler;
import android.util.Log;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.net.NetReceiveThread;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.hanyu.hust.testnet.utils.Constants.TIMEOUT;

/**
 * ServerSocket启动线程
 */
public class TcpServerTask implements Runnable{

    private static final String TAG = "TcpServerTask";

    TcpState mListener;

    Handler mHandler;

    private int port;

    public TcpServerTask(int port, Handler handler, TcpState Listener) {
        this.port = port;
        mHandler = handler;
        mListener = Listener;
    }

    @Override
    public void run() {
        Log.i(TAG, "------>>>Socket已经开启");
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            String tag = convertPort2Tag(port);
            if (AppContext.getAppContext().isExist(tag))
                return;

            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(TIMEOUT);
            socket = serverSocket.accept();

            try {
                socket.setSoTimeout(TIMEOUT);
                NetReceiveThread netReceiveThread = new NetReceiveThread(1, socket, mHandler);
                AppContext.getAppContext().addLink(netReceiveThread.getTag(), netReceiveThread);
                netReceiveThread.setThreadName(tag).start();

                if (mListener !=  null)
                {
                    mListener.success(socket.getInetAddress() + "");
                }
            } catch (Exception e) {
                tag = convertPort2Tag(socket.getLocalPort());
                if (AppContext.getAppContext().isExist(tag)) {
                    AppContext.removeLink(tag);
                    return;
                }
            }
        } catch (IOException e) {
            LogUtil.e(TAG, e + "");
            if (mListener !=  null)
            {
                mListener.fail(e);
            }
        }
        finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    static String convertPort2Tag(int port)
    {
        if (port == 6020)
            return "1";
        else if (port == 6020)
            return "2";
        else if (port == 6020)
            return "3";
        else
            return null;
    }
}
