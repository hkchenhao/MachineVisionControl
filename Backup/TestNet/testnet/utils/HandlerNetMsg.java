package com.hanyu.hust.testnet.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.hanyu.hust.testnet.entity.BitmapCache;

/**
 * Created by Administrator on 2017/4/12.
 * 处理网络消息
 */

public class HandlerNetMsg {

    private static final String TAG = "HandlerNetMsg";

    private static Bitmap[] sBitmap = new Bitmap[3];

    private static int[][] sImage = new int[3][];

    // 获取视屏流
    public static Bitmap handlerVedio(Message msg) {

        byte[] packageData = (byte[]) msg.obj;
        int[] data = new int[12];

        if (packageData == null || packageData.length <= 12)
            return null;

        for (int i = 0; i < 12; i++) {
            data[i] = packageData[i] & 0xFF;
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8 | data[6] << 16 | data[7] << 24;
        int height = data[8] | data[9] << 8 | data[10] << 16 | data[11] << 24;

        if (len <= 0 || width <= 0 || height <= 0 || (packageData.length != (len+100)))
            return null;

        LogUtil.d(TAG, "len = " + len + ", width = " + width + ", height = " + height);
        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {

            int index = msg.arg1 - 1;

            if (sBitmap[index] == null || sBitmap[index].getWidth() != width
                    || sBitmap[index].getHeight() != height) {
                sBitmap[index] = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            }

            if (sImage[index] == null || sImage[index].length != len)
                sImage[index] = new int[len];

            if ((len == width * height)) {
                int temp;
                for (int i = 0; i < len; i++) {
                    temp = packageData[i + 100] & 0xff;
                    sImage[index][i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
                }
            } else if (len == 3 * width * height) {
                for (int i = 0; i < len / 3; i++) {
                    int r = 0, g = 0, b = 0;
                    r = packageData[100 + i * 3] & 0xff;
                    g = packageData[100 + i * 3 + 1] & 0xff;
                    b = packageData[100 + i * 3 + 2] & 0xff;
                    sImage[index][i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            }
            else
                return null;

            sBitmap[index].setPixels(sImage[index], 0, width, 0, 0, width, height);
                return sBitmap[index];
        }
        else
            return null;
    }

    public static Bitmap handlerResult(Message msg) {
        byte[] packageData = (byte[]) msg.obj;
        int[] data = new int[12];

        if (packageData == null || packageData.length <= 12)
            return null;

        for (int i = 0; i < 12; i++) {
            data[i] = packageData[i] & 0xFF;
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8 | data[6] << 16 | data[7] << 24;
        int height = data[8] | data[9] << 8 | data[10] << 16 | data[11] << 24;
        //(packageData.length != (len+13)
        if (len <= 0 || width <= 0 || height <= 0)
            return null;

        LogUtil.d(TAG, "len = " + len + ", width = " + width + ", height = " + height);
        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {

            int index = msg.arg1 - 1;

            if (sBitmap[index] == null || sBitmap[index].getWidth() != width
                    || sBitmap[index].getHeight() != height) {
                {
                    sBitmap[index] = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                }
            }

            if (sImage[index] == null || sImage[index].length != len)
                sImage[index] = new int[len];

            if ((len == width * height)) {
                int temp;
                for (int i = 0; i < len; i++) {
                    temp = packageData[i + 12] & 0xff;
                    sImage[index][i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
                }
            } else if (len == 3 * width * height) {
                for (int i = 0; i < len / 3; i++) {
                    int r = 0, g = 0, b = 0;
                    r = packageData[12 + i * 3] & 0xff;
                    g = packageData[12 + i * 3 + 1] & 0xff;
                    b = packageData[12 + i * 3 + 2] & 0xff;
                    sImage[index][i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            }
            else
                return null;

            sBitmap[index].setPixels(sImage[index], 0, width, 0, 0, width, height);
            return sBitmap[index];
        }
        else
            return null;
    }

    public static Bitmap handlerResult1(Message msg) {
        byte[] packageData = (byte[]) msg.obj;

        int[] data = new int[12];

        if (packageData == null || packageData.length <= 12)
            return null;

        for (int i = 0; i < 12; i++) {
            data[i] = packageData[i] & 0xFF;
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8 | data[6] << 16 | data[7] << 24;
        int height = data[8] | data[9] << 8 | data[10] << 16 | data[11] << 24;
        //(packageData.length != (len+13)
        if (len <= 0 || width <= 0 || height <= 0)
            return null;

        LogUtil.d(TAG, "len = " + len + ", width = " + width + ", height = " + height);
        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {

            Bitmap bm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            int[] image = new int[len];

            if ((len == width * height)) {
                int temp;
                for (int i = 0; i < len; i++) {
                    temp = packageData[i + 12] & 0xff;
                    image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
                }
            } else if (len == 3 * width * height) {
                for (int i = 0; i < len / 3; i++) {
                    int r = 0, g = 0, b = 0;
                    r = packageData[12 + i * 3] & 0xff;
                    g = packageData[12 + i * 3 + 1] & 0xff;
                    b = packageData[12 + i * 3 + 2] & 0xff;
                    image[i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            }
            else
                return null;

            bm.setPixels(image, 0, width, 0, 0, width, height);
            return bm;
        }
        else
            return null;
    }

    public static BitmapCache handlerResult2(Message msg) {
        byte[] packageData = (byte[]) msg.obj;
        int[] data = new int[12];

        if (packageData == null || packageData.length <= 12)
            return null;

        for (int i = 0; i < 12; i++) {
            data[i] = packageData[i] & 0xFF;
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8 | data[6] << 16 | data[7] << 24;
        int height = data[8] | data[9] << 8 | data[10] << 16 | data[11] << 24;
        //(packageData.length != (len+13)

        if (len <= 0 || width <= 0 || height <= 0 || (packageData.length != (len+13)))
            return null;

        LogUtil.d(TAG, "len = " + len + ", width = " + width + ", height = " + height);
        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {

            if (!BitmapCache.isQualify(len, width, height))
                return null;

            BitmapCache bmc = BitmapCache.obtain();
            int[] image = bmc.getImage();
            Bitmap bm = bmc.getBm();

            if ((len == width * height)) {
                int temp;
                for (int i = 0; i < len; i++) {
                    temp = packageData[i + 12] & 0xff;
                    image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
                }
            } else if (len == 3 * width * height) {
                for (int i = 0; i < len / 3; i++) {
                    int r = 0, g = 0, b = 0;
                    r = packageData[12 + i * 3] & 0xff;
                    g = packageData[12 + i * 3 + 1] & 0xff;
                    b = packageData[12 + i * 3 + 2] & 0xff;
                    image[i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            }
            else
                return null;

            bm.setPixels(image, 0, width, 0, 0, width, height);
            return bmc;
        }
        else
            return null;
    }

    /**
     * 处理温度信息
     * @param msg
     * @return
     */
    public static String handlerState(Message msg) {
        String tempFloat = "." + msg.arg2 + "0";
        String result = "温度:"+ msg.arg1 +
                (tempFloat.length() > 3 ? tempFloat.substring(0, 3) : tempFloat) + "\n";
        return result;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
