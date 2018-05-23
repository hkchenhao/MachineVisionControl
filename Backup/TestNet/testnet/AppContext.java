package com.hanyu.hust.testnet;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Handler;

import com.hanyu.hust.testnet.can.Can;
import com.hanyu.hust.testnet.entity.ButtonJson;
import com.hanyu.hust.testnet.entity.SettingProfile;
import com.hanyu.hust.testnet.net.NetReceiveThread;
import com.hanyu.hust.testnet.task.TimeTask;
import com.hanyu.hust.testnet.utils.LogUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 全局应用类
 */
public class AppContext extends Application {

    private static final String TAG = "AppContext";

    /**
     * 全局应用的上下文
     */
    static AppContext mAppContext;

    // 监听任务
    public static Thread mPingTask;

    // 服务器任务
    public static Thread mTcpServerTask;

    // 定时任务
    public static TimeTask timeTask;

    /**
     * CAN总线任务
     */
    public static Can can0;

    /**
     * 网络发送单线程
     */
    public static Executor SENDER_EXECUTOR = Executors.newSingleThreadExecutor();

    // 连接信息
    static Map<String, NetReceiveThread> mLinks = new HashMap<String, NetReceiveThread>();

    public Map<String, NetReceiveThread> getmLinks() {
        return mLinks;
    }

    public boolean isLinksEmpty() {
        return mLinks.size() == 0;
    }

    public static AppContext getAppContext() {
        return mAppContext;
    }

    /**
     * 钮扣配置文件类  added by heqian，2017/9
     */
    public static ButtonJson btnCfgDetail = null;   //钮扣配置文件的内容
    public static JSONObject btnCfgJson = null;    // 钮扣配置JSON文件
    public static SettingProfile btnCfgProfile = null; //钮扣配置文件路径
    public static Bitmap btnBitmapF = null;//钮扣正面RGB图像
    public static Bitmap btnBitmapB = null;//钮扣反面RGB图像
    public static Bitmap btnBitmapGeo = null;//钮扣正面灰度图像
    public static Bitmap btnBitmapGeoB = null;//钮扣正面灰度图像

    public static void addLink(String inetAdress, NetReceiveThread thread) {
        if (!mLinks.containsKey(inetAdress)) {
            mLinks.put(inetAdress, thread);
        }
    }

    public static void setHandler(Handler handler) {
        for (String key : mLinks.keySet()) {
            NetReceiveThread netThread = mLinks.get(key);
            if (netThread != null) {
                netThread.setHandler(handler);
            }
        }
    }

    public static void removeLinks() {
        LogUtil.d(TAG, mLinks + "");

        for (String key : mLinks.keySet()) {
            NetReceiveThread netThread = mLinks.get(key);
            if (netThread != null) {
                netThread.close();
            }
        }
    }

    public static void removeLink(String key) {
        LogUtil.d(TAG, mLinks + "");
        NetReceiveThread netThread = mLinks.get(key);
        if (netThread != null) {
            netThread.close();
            mLinks.remove(key);
        }
    }

    public static void clearCount() {
        LogUtil.d(TAG, mLinks + "");
        for (String key : mLinks.keySet()) {
            NetReceiveThread netThread = mLinks.get(key);
            if (netThread != null) {
                netThread.clearCount();
            }
        }
    }

    public boolean isExist(String key) {
        if (mLinks == null) return false;
        return mLinks.containsKey(key);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mAppContext = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
