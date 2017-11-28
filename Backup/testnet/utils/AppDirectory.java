package com.hanyu.hust.testnet.utils;

import android.os.Environment;
import android.util.Log;

/**
 * 文件路径管理类
 */
public class AppDirectory
{
	/**
	 * 应用路径
	 */
	private static final String appDirectory = Environment
			.getExternalStorageDirectory() + "/MachineVision/";

	// USB挂载路径
	private static final String UsbDirectory = Environment
			.getExternalStorageDirectory().getParent() + "/usb1";

	// 算法路径
	private static final String AlgDirectory = appDirectory + "Alg/";

	//算子路径
	private static final String OptDirectory = appDirectory + "Opt";

	// 机器学习路径
	private static final String LeaningDirectory = appDirectory + "Learning/";

	// 机器配置文件路径
	private static final String SystemConfigDirectory = appDirectory + "SystemConfig/";
	/**
	 * 获取App的目录路径
	 *
	 * @return app根目录
	 */
	public static String getAppDirectory()
	{
		return appDirectory;
	}
	
	public static String getUsbDirectory()
	{
		return UsbDirectory;
	}
	
	public static String getAlgDirectory()
	{
		return AlgDirectory;
	}

	public static String getOptDirectory() {
		return OptDirectory;
	}

	public static String getLeaningDirectory() {
		return LeaningDirectory;
	}

	public static String getSystemConfigDirectory(){ return  SystemConfigDirectory; }
}
