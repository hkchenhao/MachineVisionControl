package com.hanyu.hust.testnet.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.broadcast.USBReciever;
import com.hanyu.hust.testnet.ui.view.DialogWindow;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.FileUtil;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.io.File;

/**
 * 文件管理界面
 */
public class FileManager extends BasePannelActivity {

	private static final String TAG = "FileManager";
	
	public static final int EXPORT_AUTO = 1; // 自动导入

	public static final int EXPORT_MANUAL = 2; // 从SD卡导出文件

	public static final int INPORT_AUTO = 3; // 从U盘拷贝文件

	public static final int INPORT_MANUAL = 4; // 从U盘手动导入

	public static final String PATH_KEY = "firstDir";

	private ProgressDialog pd;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXPORT_AUTO:
				EToast.showToast(getApplicationContext(), "自动导出完成");
				break;
			case INPORT_AUTO:
				EToast.showToast(getApplicationContext(), "自动导入完成");
				break;
			case -1:
				EToast.showToast(getApplicationContext(), "取消任务");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void init_widget() {
		super.init_widget();
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				Intent intent = new Intent(FileManager.this, FileExplorer.class);
				switch (arg2) {
				case 0:
					if (USBReciever.USBExist())
					{
						showProgress("文件正在导出....");
						new Thread()
						{
							@Override
							public void run() {
								long tic = System.currentTimeMillis();
								FileUtil.copyFolderWithSelf(mHandler,
										AppDirectory.getLeaningDirectory(),
										AppDirectory.getUsbDirectory(), pd, EXPORT_AUTO);
								long toc = System.currentTimeMillis();
								LogUtil.d(TAG, "toc-tic = " + (toc-tic));
							}
						}.start();
					}
					else if (USBReciever.USBCheck())
					{
						EToast.showToast(FileManager.this, "USB 设备正在识别,请稍后");
					}
					else {
						EToast.showToast(FileManager.this, "USB 设备无法识别或者未插上，请重试");
					}
					break;
				case 1:
					intent.putExtra(PATH_KEY, AppDirectory.getAppDirectory());	/*路径传入intent*/
					startActivityForResult(intent, EXPORT_MANUAL);
					break;
				case 2:
					if (USBReciever.USBExist())
					{
						showProgress("文件正在导入....");
						new Thread()
						{
							@Override
							public void run() {
								long tic = System.currentTimeMillis();
								FileUtil.copyFolderWithSelf(mHandler,AppDirectory.getUsbDirectory() + "/Learning/",
										AppDirectory.getAppDirectory(), pd, INPORT_AUTO);
								long toc = System.currentTimeMillis();
								LogUtil.d(TAG, "toc-tic = " + (toc-tic));
							}
						}.start();
					}
					else if (USBReciever.USBCheck())
					{
						EToast.showToast(FileManager.this, "USB 设备正在识别,请稍后");
					}
					else {
						EToast.showToast(FileManager.this, "USB 设备无法识别或者未插上,请重试");
					}
					break;
				case 3:
					if (USBReciever.USBExist())
					{
						intent.putExtra(PATH_KEY, AppDirectory.getUsbDirectory());
						startActivityForResult(intent, INPORT_AUTO);
					}
					else if (USBReciever.USBCheck())
					{
						EToast.showToast(FileManager.this, "USB 设备正在识别,请稍后");
					}
					else {
						EToast.showToast(FileManager.this, "USB 设备无法识别或者未插上，请重试");
					}
					break;
				case 4:
					// 显示容量大小
					ROMandSDcardInfo romSDcardInfo = new ROMandSDcardInfo(
							FileManager.this);
					LayoutInflater inflater = (LayoutInflater) FileManager.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
					final View view = inflater.inflate(R.layout.layout_rom_size,
							null);
					TextView textView = (TextView) view.findViewById(R.id.romInfo);
					textView.setText("内存总量：" + romSDcardInfo.getRomTotalSize()
							+ "\n" + "内存剩余："
							+ romSDcardInfo.getRomAvailableSize() + "\n"
							+ "SD卡总量：" + romSDcardInfo.getSDTotalSize() + "\n"
							+ "SD卡剩余：" + romSDcardInfo.getSDAvailableSize());
					textView.setVisibility(View.VISIBLE);
					DialogWindow dialogWindow = new DialogWindow.Builder(
							FileManager.this).setTitle("内存总量信息").setView(view)
							.setPositiveButton("确定", null)
							.setNegativeButton("取消", null).create();
					dialogWindow.show();
					break;
				case 5:
					Intent intent1 = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("ShutDown", "y");
					intent1.putExtras(bundle);
					setResult(RESULT_OK, intent1);
					finish();
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*取消屏幕休眠，add by wuxin 170714*/
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_option);
		wholeMenu = new MenuWithSubMenu(R.array.option_file_manager, 0);
		init_widget();
	}

	/**
	 * 显示对话框
	 */
	private void showProgress(String title)
	{
		pd = new ProgressDialog(FileManager.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCancelable(false);
		pd.setTitle(title);
		pd.show();
	}

	/**
	 * 销毁对话框
	 */
	private void hideProgress()
	{
		if (pd != null) {
			pd.hide();
			pd = null;
		}
	}

	public static class ROMandSDcardInfo {
		Context context;

		ROMandSDcardInfo(Context _context) {
			context = _context;
		}

		/**
		 * 获得SD卡总大小
		 * 
		 * @return
		 */
		public String getSDTotalSize() {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return Formatter.formatFileSize(context, blockSize * totalBlocks);
		}

		/**
		 * 获得sd卡剩余容量，即可用大小
		 * 
		 * @return
		 */
		public String getSDAvailableSize() {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return Formatter.formatFileSize(context, blockSize
					* availableBlocks);
		}

		/**
		 * 获得机身内存总大小
		 * @return
		 */
		public String getRomTotalSize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return Formatter.formatFileSize(context, blockSize * totalBlocks);
		}

		/**
		 * 获得机身可用内存
		 * @return
		 */
		public String getRomAvailableSize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return Formatter.formatFileSize(context, blockSize
					* availableBlocks);
		}
	}
}
