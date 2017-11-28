package com.hanyu.hust.testnet.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
import android.util.Log;

import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.LogUtil;
import com.hanyu.hust.testnet.utils.MyUtils;

/**
 * USB 挂载事件监听广播
 */
public class USBReciever extends BroadcastReceiver {
		
	private static final String TAG = "USBReciever";

    private static final String DEV_NAME = "file:///storage/usb1";

	/**
	 * 三种状态
	 * 卸载：UNMOUNT
	 * 识别：CHECK
	 * 挂载：MOUNT
	 */
	private static enum State {
		UNMOUNT, CHECK, MOUNT
	}

	private static State usbState = State.UNMOUNT;
	
	public static boolean USBExist()
	{
		return usbState == State.MOUNT;
	}

	public static boolean USBCheck()
	{
		return usbState == State.CHECK;
	}

	public static boolean USBRemove()
	{
		return usbState == State.UNMOUNT;
	}
	
	private StorageManager mStorageManager;

	/**
	 * 接受广播进行处理
	 * @param context
	 * @param intent
	 */
    @Override
	public void onReceive(Context context, Intent intent) {


		LogUtil.d(TAG, "Component: " + intent.getComponent());

		LogUtil.d(TAG, "Aciton: " +  intent.getAction());
		LogUtil.d(TAG, "Categories: " +  intent.getCategories());

		LogUtil.d(TAG, "Data: " + intent.getData());
		LogUtil.d(TAG, "DataType: " + intent.getType());
		LogUtil.d(TAG, "DataSchema: " + intent.getScheme());

		Log.i("TAG"," Receive SDCard Mount/UnMount!");

		if( intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED) &&
                DEV_NAME.equals(intent.getData() + "") ){
	    	EToast.showToast(context, "挂载U盘成功！");
	    	usbState = State.MOUNT;
	    }
	    else if (intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING ) &&
                DEV_NAME.equals(intent.getData() + ""))
	    {
	    	EToast.showToast(context, "正在识别U盘设备...");	    	    
	    	usbState = State.CHECK;
	    }
	    else if (DEV_NAME.equals(intent.getData() + ""))
	    {  
			new Thread(new Runnable() {
				@Override
				public void run() {
					MyUtils.unMount();
				}
			}).start();
	    	usbState = State.UNMOUNT;
	    	EToast.showToast(context, "卸载U盘成功！");
	    }  
	}
}
