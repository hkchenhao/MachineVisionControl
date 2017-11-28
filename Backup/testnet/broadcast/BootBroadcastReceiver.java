package com.hanyu.hust.testnet.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hanyu.hust.testnet.ui.Splash;

/**
 * 开机启动广播，启动应用程序
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent StartIntent = new Intent(context, Splash.class); // 接收到广播后，跳转到MainActivity
		StartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(StartIntent);
	}
}
