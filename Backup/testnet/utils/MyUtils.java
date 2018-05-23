package com.hanyu.hust.testnet.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.storage.IMountService;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 工具类
 */
public class MyUtils {

	private static final String TAG = "MyUtils";

	static IMountService iMountService;

	static {
		LogUtil.d(TAG, AppDirectory.getUsbDirectory());

		try {
			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null, "mount");
			iMountService = IMountService.Stub.asInterface(binder);
		} catch (Exception e) {
			LogUtil.e(TAG, "static error");
			e.printStackTrace();
		}
	}

	// 设备卸载
	public static void unMount() {
		try {
			iMountService.unmountVolume(AppDirectory.getUsbDirectory(), true, true);
		} catch (RemoteException e) {
			LogUtil.e(TAG, "unMount error");
			e.printStackTrace();
		}
	}

	// 设备挂载
	public static void mount(){  
        try {  
            iMountService.mountVolume(AppDirectory.getUsbDirectory());
        } catch (Exception e) {
			LogUtil.e(TAG, "Mount error");
            e.printStackTrace();  
        }  
    }

	// 获取进程类名
	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == pid) {
				return procInfo.processName;
			}
		}
		return null;
	}

	public static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void executeInThread(Runnable runnable) {
		new Thread(runnable).start();
	}

	public static String getsDouble(int data) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(data*1.0/100);
	}


	public static byte[] getBytesFromObject(Serializable obj) throws Exception {
		if (obj == null) {
			return null;
		}
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(obj);
		return bo.toByteArray();
	}

}
