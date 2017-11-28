package com.hanyu.hust.testnet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.hanyu.hust.testnet.entity.SettingProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hanyu
 * 图片加载器
 */
public class ImageLoader {	
	/**
	 * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	
	/**
	 * 下载Image的线程池
	 */
	private ExecutorService mImageThreadPool = null;
		
	public ImageLoader(Context context){
		//获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
		int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        int mCacheSize = maxMemory / 8;
        //给LruCache分配1/8 4M
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){

			//必须重写此方法，来测量Bitmap的大小
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
			
		};
	}
	
	
	/**
	 * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
	 * @return
	 */
	public ExecutorService getThreadPool(){
		if(mImageThreadPool == null){
			synchronized(ExecutorService.class){
				if(mImageThreadPool == null){
					//为了下载图片更加的流畅，我们用了2个线程来下载图片
					mImageThreadPool = Executors.newFixedThreadPool(2);
				}
			}
		}
		return mImageThreadPool;		
	}
	
	/**
	 * 添加Bitmap到内存缓存
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromMemCache(key) == null && bitmap != null) {  
	        mMemoryCache.put(key, bitmap);  
	    }  
	}  
	 
	/**
	 * 从内存缓存中获取一个Bitmap
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {  
	    return mMemoryCache.get(key);  
	} 
	
	/**
	 * 获取Bitmap, 内存中没有就去手机或者sd卡中获取，这一步在getView中会调用，比较关键的一步
	 * @param url
	 * @return
	 */
	public Bitmap showCacheBitmap(String mInfo){
		String ID = mInfo;
		if(getBitmapFromMemCache(ID) != null){
			return getBitmapFromMemCache(ID);
		}
		else if(SettingProfile.isFileExists(ID))
		{
			//从SD卡获取手机里面获取Bitmap
			Bitmap bitmap = SettingProfile.getBitmap(ID);
			//将Bitmap 加入内存缓存
			addBitmapToMemoryCache(ID, bitmap);
			return bitmap;
		}
		return null;
	}
	
	/**
	 * 取消正在下载的任务
	 */
	public synchronized void cancelTask() {
		if(mImageThreadPool != null){
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}
		
	/**
	 * 异步下载图片的回调接口
	 * @author len
	 *
	 */
	public interface onImageLoaderListener{
		void onImageLoader(Bitmap bitmap, String url);
	}
	
}
