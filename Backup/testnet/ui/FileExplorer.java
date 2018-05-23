package com.hanyu.hust.testnet.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.hanyu.hust.testnet.R;
import com.hanyu.hust.testnet.broadcast.USBReciever;
import com.hanyu.hust.testnet.ui.view.EToast;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.FileUtil;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileExplorer extends ListActivity
{
	private static final String TAG = "FileExplorer";

	public static final int DELETE_SUCCESS = 1; // 从SD卡导出文件

	public static final int EXPORT_MANUAL = 2; // 从SD卡导出文件
		
	public static final int INPORT_MANUAL = 4; // 从U盘手动导入
	
	public static final int REMOVE_USB = 5;    // 弹出USB设备

	private ProgressDialog pd;
	
	private Button btExit;
	
	private List<Map<String, Object>> mData;
	
	private final String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
	
	private String mDir;
	
	MyAdapter adapter;
	
	private final Context mContext = this;

	/**
	 * 异步事件接受器，在主线程中处理
	 */
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DELETE_SUCCESS:
				EToast.showToast(FileExplorer.this, "删除成功");
				refreshListView();
				break;
				
			case EXPORT_MANUAL:
				EToast.showToast(FileExplorer.this, "手动导出完成");				
				break;
				
			case INPORT_MANUAL:
				EToast.showToast(FileExplorer.this, "手动导入完成");				
				break;
						
			case -1:
				EToast.showToast(FileExplorer.this, "取消任务");
				break;
				
			case -2:
				EToast.showToast(FileExplorer.this, "复制出错");
				break;
			default:
				break;
			}
		};
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mDir = getIntent().getStringExtra(FileManager.PATH_KEY);
		if (mDir == null)
			mDir = ROOT_DIRECTORY;

		adapter = new MyAdapter(this);
		mData = getData();

		setListAdapter(adapter);
		/*取消屏幕休眠，add by wuxin 170714*/
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_file_ex);
		
		btExit = (Button) findViewById(R.id.bt_exit);
		btExit.setOnClickListener(new View.OnClickListener() {
	
			public void onClick(View v) {
				finish();				/*结束当前activity,回到前一次的activity中*/
			}
		});
		
		registerForContextMenu(getListView());			// 注册上下文菜单		
 	}

	/**
	 * 读取当前路劲，得到List型数据
	 * @return
	 */
	private List<Map<String, Object>> getData()
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		
		File f = new File(mDir);	
		if (!f.exists()) {
			EToast.makeText(FileExplorer.this, "U盘设备无法识别",
					Toast.LENGTH_SHORT).show();
			return list;
		}
		
		File[] files = f.listFiles();
		if (!mDir.equals(ROOT_DIRECTORY))
		{
			map = new HashMap<String, Object>();
			map.put("title", "..");
			map.put("info", f.getParent());
			map.put("img", R.mipmap.ex_folder);
			list.add(map);
		}
		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				map = new HashMap<String, Object>();
				map.put("title", files[i].getName());
				map.put("info", files[i].getPath());
				if (files[i].isDirectory())
					map.put("img", R.mipmap.ex_folder);
				else
					map.put("img", R.mipmap.ex_doc);
				list.add(map);
			}

		}
		return list;
	}

	/**
	 * 刷新列表数据
	 */
	private void refreshListView()
	{
		mData = getData();
		adapter.notifyDataSetChanged();
	}
		
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		if ((Integer) mData.get(position).get("img") == R.mipmap.ex_folder)		// Object对象只能转为Integer
		{
			mDir = (String) mData.get(position).get("info");
			refreshListView();
		}
		else
		{
            /*通过后缀名，判断文件类型*/
			String fileName = (String)mData.get(position).get("info");
			if(fileName.endsWith(".ini")){
				Toast.makeText(this,"这是json文件",Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(this,"这是jpg图像",Toast.LENGTH_SHORT).show();
			}
//			finishWithResult((String) mData.get(position).get("info"), (String) mData.get(position).get("title")) ;
		}
	}

	/**
	 * 适配器
	 */
	public class MyAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;

		public MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount()
		{
			return mData.size();
		}

		public Object getItem(int arg0)
		{
			return null;
		}

		public long getItemId(int arg0)
		{
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listview_item_file, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);		/*图标*/
				holder.title = (TextView) convertView.findViewById(R.id.title);		/*文件名*/
				holder.info = (TextView) convertView.findViewById(R.id.info);		/*具体路径*/
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
			holder.title.setText((String) mData.get(position).get("title"));
			holder.info.setText((String) mData.get(position).get("info"));
			return convertView;
		}
	}

	/**
	 * 视图容器
	 */
	public final class ViewHolder
	{
		public ImageView img;
		public TextView title;
		public TextView info;
	}

	/**
	 * 创建菜单项
	 * @param menu
	 * @param v
	 * @param menuInfo
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		int menuOrder = Menu.FIRST;
		menu.setHeaderTitle("文件操作");
		menu.add(0, 1, menuOrder++, "新建文件夹");
		if (((AdapterContextMenuInfo) menuInfo).position == 0
				&& mDir != ROOT_DIRECTORY)
		{
			return;
		}
		menu.add(0, 2, menuOrder++, "重命名");
		menu.add(0, 3, menuOrder, "删除");
		menu.add(0, 4, menuOrder, "导出文件(夹)");
		menu.add(0, 5, menuOrder, "导入文件夹");
	}

	/**
	 * 长按菜单
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		final File file = new File((String) mData.get(info.position)
				.get("info"));
		switch (item.getItemId())
		{
		case 1:
			/*
			 * 新建文件夹
			 */
			File newFile = new File(mDir, "新建文件夹");
			newFile.mkdir();
			refreshListView();
			break;

		case 2:
			/*
			 * 重命名
			 */
			LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext)
					.inflate(R.layout.fmr_rename, null);
			final EditText editText = (EditText) layout
					.findViewById(R.id.filemanager_rename);
			editText.setHint(file.getName());
			new AlertDialog.Builder(mContext).setTitle("文件重命名").setView(layout)
					.setPositiveButton("确定", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							File newFile = new File(file.getParentFile(),
									editText.getText().toString());
							file.renameTo(newFile);
							refreshListView();
						}
					}).setNegativeButton("取消", null).show();
			break;

		case 3:
			/*
			 * 刪除文件
			 */
			new AlertDialog.Builder(mContext).setTitle("是否删除文件？")
					.setPositiveButton("确定", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{							
							new Thread(new Runnable() {								
								@Override
								public void run() {
									long tic = System.currentTimeMillis();
									deleteDirectory(file);									
									long toc = System.currentTimeMillis();
									LogUtil.d(TAG, "toc-tic = " + (toc-tic));
								}
							}).start();
							EToast.showToast(mContext, "删除文件中...");
						}
					}).setNegativeButton("取消", null).show();
			break;
		case 4:
			/*
			 * 导出
			 */
			if (USBReciever.USBExist())
			{
				pd = new ProgressDialog(FileExplorer.this);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setTitle("文件正在导出....");
				pd.setCancelable(false);
				pd.show();
				
				new Thread(new Runnable() {								
					@Override
					public void run() {
						if (file.isDirectory())
						{
							long tic = System.currentTimeMillis();
							FileUtil.copyFolderWithSelf(mHandler,
									file.getAbsolutePath(),
									AppDirectory.getUsbDirectory(), pd, EXPORT_MANUAL);
							long toc = System.currentTimeMillis();
							LogUtil.d(TAG, "toc-tic = " + (toc-tic));
						}
						else {
							long tic = System.currentTimeMillis();
							FileUtil.CopySingleFileTo(mHandler, file.getAbsolutePath(),
									AppDirectory.getUsbDirectory(), pd, EXPORT_MANUAL);
							long toc = System.currentTimeMillis();
							LogUtil.d(TAG, "toc-tic = " + (toc-tic));
						}
					}					
				}).start();
			}
			else if (USBReciever.USBCheck())
			{
				EToast.showToast(FileExplorer.this, "USB 设备正在识别,请稍后");
			}
			else {
				EToast.showToast(FileExplorer.this, "USB 设备无法识别或者未插上,请重试");						
			}
			break;	
		case 5:
			/*
			 * 导入
			 */
			if (USBReciever.USBExist())
			{
				if(!file.isDirectory())
				{
					EToast.showToast(FileExplorer.this, "文件格式不对");
					break;					
				}
				pd = new ProgressDialog(FileExplorer.this);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setTitle("文件正在导入....");
				pd.setCancelable(false);
				pd.show();
				
				new Thread(new Runnable() {								
					@Override
					public void run() {
						long tic = System.currentTimeMillis();
						FileUtil.copyFolderWithSelf(mHandler,
								file.getAbsolutePath(),
								AppDirectory.getLeaningDirectory(), pd, INPORT_MANUAL);
						long toc = System.currentTimeMillis();
						LogUtil.d(TAG, "toc-tic = " + (toc-tic));
					}
				}).start();
			}
			else if (USBReciever.USBCheck())
			{
				EToast.showToast(FileExplorer.this, "USB 设备正在识别,请稍后");
			}
			else {
				EToast.showToast(FileExplorer.this, "USB 设备无法识别或者未插上,请重试");						
			}			
			break;	

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 删除文件夹或者文件
	 * @param file
	 */
	public static void deleteDirectory(File file)
	{
		if (file.isFile())
		{
			file.delete();
		}
		else if (file.isDirectory())
		{
			File[] files = file.listFiles();
			if (files == null || files.length == 0)
			{
				file.delete();
			}
			else
			{
				for (File subFile : files)
				{
					deleteDirectory(subFile);
				}
			}
		}
		file.delete();
	}
}
