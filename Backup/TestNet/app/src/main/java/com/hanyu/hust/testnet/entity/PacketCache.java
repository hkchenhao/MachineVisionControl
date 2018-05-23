package com.hanyu.hust.testnet.entity;

import android.util.Log;

/**
 * Created by Administrator on 2017/4/17.
 * 网络包对象池 ，暂未使用
 */

public class PacketCache {

    private static final String TAG = "BitmapCache";

    private static final int MAX_POOL_SIZE = 10;

    protected static final int LEN = 128 * 128;

    private static final Pools.SynchronizedPool<PacketCache> sPool =
            new Pools.SynchronizedPool<PacketCache>(MAX_POOL_SIZE);

    private int[] image = new int[128 * 128 + 13];

    public int[] getImage() {
        return image;
    }

    static {
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
        sPool.release(new PacketCache());
    }


    public static PacketCache obtain() {
        PacketCache data = sPool.acquire();
        if (data != null)
            Log.d(TAG, "acquire");

        if (data == null) {
            Log.e(TAG, "new");
            data = new PacketCache();
        }
        return data;
    }

    public PacketCache() {

    }

    public void recycle() {
        sPool.release(this);
    }

    public static boolean isQualify(int len) {
        return (len == (128 * 128 * 3 + 13));
    }
}