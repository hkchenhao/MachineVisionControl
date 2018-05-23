package com.hanyu.hust.testnet.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.storage.IMountService;
import android.util.Log;

import com.hanyu.hust.testnet.CameraConfigParam.NetConf;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * 工具类
 */
public class MyUtils {

    private static final String TAG = "MyUtils";

    static IMountService iMountService;

    static {
        LogUtil.d(TAG, AppDirectory.getUsbDirectory());

        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, "mount");
            iMountService = IMountService.Stub.asInterface(binder);
        } catch (Exception e) {
            LogUtil.e(TAG, "static error");
            e.printStackTrace();
        }
    }

    // 设备卸载
    public static void unMount() {
        try {
            iMountService.unmountVolume(AppDirectory.getUsbDirectory(), true, true);
        } catch (RemoteException e) {
            LogUtil.e(TAG, "unMount error");
            e.printStackTrace();
        }
    }

    // 设备挂载
    public static void mount() {
        try {
            iMountService.mountVolume(AppDirectory.getUsbDirectory());
        } catch (Exception e) {
            LogUtil.e(TAG, "Mount error");
            e.printStackTrace();
        }
    }

    // 获取进程类名
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void executeInThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static String getsDouble(int data) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(data * 1.0 / 100);
    }


    public static byte[] getBytesFromObject(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        return bo.toByteArray();
    }

    /**
     * 将彩色图转换为灰度图
     *
     * @param img 位图
     * @return 返回转换好的位图
     */
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 网络获取数据，处理显示函数
     *
     * @param msg
     * @return
     */
    public static Bitmap handlerRoi(Message msg) {
        byte[] packageData = (byte[]) msg.obj;
        if (packageData == null || packageData.length <= 8)
            return null;
        int[] data = new int[8];
        for (int i = 0; i < 8; i++) {
            data[i] = packageData[i] & 0xFF;
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8;
        int height = data[6] | data[7] << 8;
        if (len <= 0 || width <= 0 || height <= 0)
            return null;
        Bitmap bm = null;
        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {
            bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int[] image = new int[len];

            if ((len == width * height)) {
                int temp;
                for (int i = 0; i < len; i++) {
                    temp = packageData[i + 8] & 0xff;
                    image[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
                }
            } else if (len == 3 * width * height) {
                for (int i = 0; i < len / 3; i++) {
                    int r = 0, g = 0, b = 0;
                    r = packageData[8 + i * 3] & 0xff;
                    g = packageData[8 + i * 3 + 1] & 0xff;
                    b = packageData[8 + i * 3 + 2] & 0xff;
                    image[i] = (0xFF000000 | r << 16 | g << 8 | b);
                }
            } else
                return null;
            bm.setPixels(image, 0, width, 0, 0, width, height);
            return bm;
        } else
            return null;
    }

    /**
     * 视频流处理
     * @param msg
     * @return 返回bitmap类型数据
     */
    public static Bitmap handlerVideo(Message msg) {
        byte[] packageData = (byte[]) msg.obj;
        int[] data = new int[12];
        if (packageData == null || packageData.length <= data.length) {
            return null;
        } else {
            for (int i = 0; i < data.length; i++) {
                data[i] = packageData[i] & 0xFF;
            }
        }

        int len = data[0] | data[1] << 8 | data[2] << 16 | data[3] << 24;
        int width = data[4] | data[5] << 8 | data[6] << 16 | data[7] << 24;
        int height = data[8] | data[9] << 8 | data[10] << 16 | data[11] << 24;
//
//        Log.d(TAG, "handlerVideo: len=" + len + " width=" + width + " height=" + height);

        if (len <= 0 || width <= 0 || height <= 0) {
            return null;
        }

        if (msg.arg1 == 1 || msg.arg1 == 2 || msg.arg1 == 3) {
            Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            if ((len == width * height)) {
                int[] image = bayer2RGB(packageData, 100, width, height);
                bm.setPixels(image, 0, width, 0, 0, width, height);
                return bm;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 图像缓存
     */
    static byte[] rgb = null;
    static int[] rgb_image = null;

    /**
     * bayer矩阵转RGB图像
     *
     * @param raw
     * @param w
     * @param h
     * @return
     */
    public static int[] bayer2RGB(byte[] raw, final int offset, final int w, final int h) {
        int i;
        int j;
        int positionF;
        int position;

        int raw_line0;
        int raw_line1;
        int raw_line2;
        int raw_line3;

        int rgb_line0;
        int rgb_line1;

        int r0, g0, r1;
        int g1, b0, g2, b1;
        int r2, g3, r3, g4;
        int b2, g5, b3;

        if (rgb == null) {
            rgb = new byte[640 * 480 * 3];
            rgb_image = new int[640 * 480];
        }

        for (j = h - 2; j > 0; j -= 2) {
            raw_line0 = (j - 2) * w;
            raw_line1 = (j - 1) * w;
            raw_line2 = j * w;
            raw_line3 = (j + 1) * w;

            rgb_line0 = (j - 1) * 3 * w;
            rgb_line1 = j * 3 * w;

            for (i = 0; i < (w - 2); i += 2) {
                r0 = raw[offset + raw_line0 + i] & 0xFF;
                g0 = raw[offset + raw_line0 + i + 1] & 0xFF;
                r1 = raw[offset + raw_line0 + i + 2] & 0xFF;

                g1 = raw[offset + raw_line1 + i] & 0xFF;
                b0 = raw[offset + raw_line1 + i + 1] & 0xFF;
                g2 = raw[offset + raw_line1 + i + 2] & 0xFF;
                b1 = raw[offset + raw_line1 + i + 3] & 0xFF;

                r2 = raw[offset + raw_line2 + i] & 0xFF;
                g3 = raw[offset + raw_line2 + i + 1] & 0xFF;
                r3 = raw[offset + raw_line2 + i + 2] & 0xFF;
                g4 = raw[offset + raw_line2 + i + 3] & 0xFF;

                b2 = raw[offset + raw_line3 + i + 1] & 0xFF;
                g5 = raw[offset + raw_line3 + i + 2] & 0xFF;
                b3 = raw[offset + raw_line3 + i + 3] & 0xFF;

                rgb[rgb_line0 + (i + 1) * 3] = (byte) (((r0 + r1 + r2 + r3) / 4) & 0xFF);
                rgb[rgb_line0 + (i + 1) * 3 + 1] = (byte) (((g0 + g1 + g2 + g3) / 4) & 0xFF);
                rgb[rgb_line0 + (i + 1) * 3 + 2] = (byte) (b0 & 0xFF);

                rgb[rgb_line0 + (i + 2) * 3] = (byte) (((r1 + r3) / 2) & 0xFF);
                rgb[rgb_line0 + (i + 2) * 3 + 1] = (byte) (g2 & 0xFF);
                rgb[rgb_line0 + (i + 2) * 3 + 2] = (byte) (((b0 + b1) / 2) & 0xFF);

                rgb[rgb_line1 + (i + 1) * 3] = (byte) (((r2 + r3) / 2) & 0xFF);
                rgb[rgb_line1 + (i + 1) * 3 + 1] = (byte) (g3 & 0xFF);
                rgb[rgb_line1 + (i + 1) * 3 + 2] = (byte) (((b0 + b2) / 2) & 0xFF);

                rgb[rgb_line1 + (i + 2) * 3] = (byte) (r3 & 0xFF);
                rgb[rgb_line1 + (i + 2) * 3 + 1] = (byte) (((g2 + g3 + g4 + g5) / 4) & 0xFF);
                rgb[rgb_line1 + (i + 2) * 3 + 2] = (byte) (((b0 + b1 + b2 + b3) / 4) & 0xFF);
            }
        }

        /**清除第一行，与最后一行*/
        position = (h - 1) * w * 3;
        for (j = 0; j < w; j++) {
            rgb[j * 3 + 0] = 0;
            rgb[j * 3 + 1] = 0;
            rgb[j * 3 + 2] = 0;
            rgb[position + j * 3 + 0] = 0;
            rgb[position + j * 3 + 1] = 0;
            rgb[position + j * 3 + 2] = 0;
        }

        /**清除第一列，与最后一列*/
        position = (2 * w - 1) * 3;
        positionF = w * 3;
        for (i = 1; i < h - 1; i++) {
            rgb[position + 0] = 0;
            rgb[position + 1] = 0;
            rgb[position + 2] = 0;
            rgb[positionF + 0] = 0;
            rgb[positionF + 1] = 0;
            rgb[positionF + 2] = 0;
            position += w * 3;
            positionF += w * 3;
        }

        /**rgb拼凑*/
        for (i = 0; i < w * h; i++) {
            rgb_image[i] = rgb[i * 3] & 0xFF;
            rgb_image[i] = rgb_image[i] << 8;
            rgb_image[i] |= rgb[i * 3 + 1] & 0xFF;
            rgb_image[i] = rgb_image[i] << 8;
            rgb_image[i] |= rgb[i * 3 + 2] & 0xFF;
            rgb_image[i] |= 0xFF000000;
        }

        return rgb_image;
    }

    public static int[] getGrayArray(Bitmap myBitmap) {
        int width = myBitmap.getWidth();
        int height = myBitmap.getHeight();
        // 灰度映射表
        int lCount[] = new int[256];
        // 重置计数为0
        int i, j;
        for (i = 0; i < 256; i++) {
            // 清零
            lCount[i] = 0;
        }
        // 计算各个灰度值的计数 - 参考灰度直方图的绘制代码 (对话框类中)
        for (i = 0; i < height; i++) {
            for (j = 0; j < width; j++) {
                int pixel = myBitmap.getPixel(j, i);
                int red = ((pixel & 0x00FF0000) >> 16);
                int green = ((pixel & 0x0000FF00) >> 8);
                int blue = (pixel & 0x000000FF);
                int grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = Math.min(grey, 250);
                lCount[grey]++;  // 计数加1
            }
        }
        return lCount;
    }

}
