package com.hanyu.hust.testnet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanyu.hust.testnet.R;

import java.util.Date;
import java.util.List;

/**
 * ButtonSearch 类GridView的适配器
 */
public class ImageAdapter extends BaseAdapter implements OnScrollListener{
	/**
	 * 上下文对象的引用
	 */
	private Context context;
	
	private List<String> mList;
	
	/**
	 * GridView对象的应用
	 */
	private GridView mGridView;
	
	/**
	 * Image 加载器
	 */
	private ImageLoader mImageLoader;
	
	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 * 参考http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	private boolean isFirstEnter = true;
	
	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;
	
	/**
	 * 一屏中所有item的个数
	 */
	private int mVisibleItemCount;
	
	
	public ImageAdapter(Context context, GridView gridView, List<String> list){
		this.context = context;
		mGridView = gridView;
		mList = list;
		mImageLoader = new ImageLoader(context);
		mGridView.setOnScrollListener(this);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务  
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			
			Date curDate = new Date(System.currentTimeMillis());  
			showImage(mFirstVisibleItem, mVisibleItemCount);
			Date endDate = new Date(System.currentTimeMillis()); 
			long diff = endDate.getTime() - curDate.getTime(); 

		}else{
			cancelTask();
		}	
	}

	/**
	 * GridView滚动的时候调用的方法，刚开始显示GridView也会调用此方法
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。 
		if(isFirstEnter && visibleItemCount > 0){		
			Date curDate = new Date(System.currentTimeMillis());  
			showImage(mFirstVisibleItem, mVisibleItemCount);
			Date endDate = new Date(System.currentTimeMillis()); 
			long diff = endDate.getTime() - curDate.getTime(); 
			isFirstEnter = false;
		}
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public final class ViewHolder
	{
		public ImageView iv_name;
		public TextView tv_name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String mInfo = mList.get(position);

		ViewHolder holder = null;		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_main_item);
			holder.iv_name = (ImageView) convertView.findViewById(R.id.iv_main_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_name.setText(mInfo);
						
		/*******************************去掉下面这几行试试是什么效果****************************/
		Bitmap bitmap = mImageLoader.showCacheBitmap(mInfo);
		if(bitmap != null){
			holder.iv_name.setImageBitmap(bitmap);
		}else{
			holder.iv_name.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_empty));
		}
		/**********************************************************************************/	
		return convertView;
	}
	
	
	/**
	 * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去sd卡或者手机目录查找
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount){
		Bitmap bitmap = null;
		for(int i=firstVisibleItem; i<firstVisibleItem + visibleItemCount; i++){
			String mInfo = mList.get(i);
			mImageLoader.showCacheBitmap(mInfo);
		}
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTask(){
		mImageLoader.cancelTask();
	}
}
