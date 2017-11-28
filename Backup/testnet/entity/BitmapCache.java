package com.hanyu.hust.testnet.entity;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.hanyu.hust.testnet.entity.Pools.SimplePool;
import com.hanyu.hust.testnet.utils.LogUtil;

/**
 * Created by Administrator on 2017/4/17.
 * 缓存Bitmap对象池，最大支持 MAX_POOL_SIZE 个缓存对象
 */

public class BitmapCache {

    private static final String TAG = "BitmapCache";

    private static final int MAX_POOL_SIZE = 10;

    private static final int WIDTH = 128;

    private static final int HEIGHT = 128;

    private static final int LEN = 128*128;

    /**
     * 创建10个对象，对象为128*128*3 的Bitmap
     */
    private static final SimplePool<BitmapCache> sPool =
            new SimplePool<BitmapCache>(MAX_POOL_SIZE);

    static {
        sPool.release(new BitmapCache());
        sPool.release(new BitmapCache());
        sPool.release(new BitmapCache());
        sPool.release(new BitmapCache());
        sPool.release(new BitmapCache());
        sPool.release(new BitmapCache());
    }

    private Bitmap bm = Bitmap.createBitmap(WIDTH, HEIGHT, Config.ARGB_8888);

    private int[] image = new int[LEN];

    public Bitmap getBm() {
        return bm;
    }

    public int[] getImage() {
        return image;
    }

    /**
     * 从对象池取对象
     * @return
     */
    public static BitmapCache obtain() {
        BitmapCache data = sPool.acquire();
        if (data != null)
            LogUtil.d(TAG, "acquire");

        if (data == null) {
            LogUtil.e(TAG, "new");
            data = new BitmapCache();
        }
        return data;
    }

    public BitmapCache() {
    }

    /**
     * 释放对象
     */
    public void recycle() {
        sPool.release(this);
    }

    /**
     * 对象是否符合条件
     * @param len
     * @param width
     * @param height
     * @return
     */
    public static boolean isQualify(int len, int width, int height)
    {
        return (width == WIDTH) && (HEIGHT == height) && (len == LEN || len == 3*LEN);
    }
}
