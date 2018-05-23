package com.hanyu.hust.testnet.entity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.hanyu.hust.testnet.ui.FileExplorer;
import com.hanyu.hust.testnet.utils.AppDirectory;
import com.hanyu.hust.testnet.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * 钮扣配置文件操作类
 * 功能：实现图片保存；Json文件保存；获取路径等
 * @author hanyu
 */
public class SettingProfile {

    // 默认路径
    private static String defaultPath = AppDirectory.getLeaningDirectory();

    // 纽扣ID
    private String id;

    // 纽扣文件夹路径
    private String filePath;

    /**
     * 构造函数
     * @param id
     */
    public SettingProfile(String id) {
        this.id = id;
        filePath = defaultPath + id;
    }

    public SettingProfile(String filepath, String id) {
        this.filePath = filepath;
        this.id = id;
    }

    public SettingProfile(File file) {
        id = file.getName();
        filePath = file.getAbsolutePath();
    }

    public File getFile() {
        return new File(filePath);
    }

    private String getImgPath() {
        return filePath + "/" + id + ".jpg";
    }

    private String getIniFilePath() {
        return filePath + "/" + id + ".ini";
    }
    private String getStaticsFilePath(){
        return filePath + "/" + id + "STA"+".ini";
    }
    /**
     * 保存配置文件到sd卡*
     * @param json
     */
    @SuppressLint("SimpleDateFormat")
    public void write2sd(JSONObject json) {
        try {
            File file = new File(getIniFilePath());
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date(System.currentTimeMillis());
            json.put("time", myFmt.format(now).toString());
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存纽扣统计信息文件到sd卡*
     * @param json
     */
    @SuppressLint("SimpleDateFormat")
    public void writeStatics(JSONObject json) {
        try {
            File file = new File(getStaticsFilePath());
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date(System.currentTimeMillis());
            json.put("time", myFmt.format(now).toString());
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存纽扣统计信息文件到sd卡*
     * @param json
     */
    @SuppressLint("SimpleDateFormat")
    public void writeSystemConfig(JSONObject json) {
        try {
            File file = new File(getStaticsFilePath());
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date(System.currentTimeMillis());
            json.put("time", myFmt.format(now).toString());
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片到sd卡上
     *
     * @param mBitmap
     */
    public void saveMyBitmap(Bitmap mBitmap) {
        File f = new File(getImgPath());
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveInfo(String ID, JSONObject json, Bitmap mBitmap) {
        SettingProfile buttonProfile = new SettingProfile(ID);
        buttonProfile.write2sd(json);
        buttonProfile.saveMyBitmap(mBitmap);
    }

    public String getID() {
        return id;
    }

    /**
     * 从sd卡上获取配置文件，返回json对象
     *
     * @return
     */
    public JSONObject getJsonStr() {
        JSONObject json = null;
        try {
            String str = null;
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(new File(getIniFilePath())));
            while (!TextUtils.isEmpty(str = reader.readLine())) {
                strBuf.append(str);
            }
            json = new JSONObject(strBuf.toString());
        } catch (Exception e) {
        }
        return json;
    }

    /**
     * 从sd卡上获取配置文件，返回json对象
     *
     * @return
     */
    public  JSONObject getStatics() {
        JSONObject json = null;
        try {
            String str = null;
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(new File(getStaticsFilePath())));
            while (!TextUtils.isEmpty(str = reader.readLine())) {
                strBuf.append(str);
            }
            json = new JSONObject(strBuf.toString());
        } catch (Exception e) {
            return json;
        }
        return json;
    }

    /**
     * 从sd卡上获取图片文件
     *
     * @return
     */
    public static Bitmap getBitmap(String ID) {
        Bitmap bitmap = null;
        try {

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = calculateInSampleSize(options, 200, 200);

            options.inJustDecodeBounds = false;

            if(new File(defaultPath + ID).exists()){
                InputStream inputStream;
                inputStream = new FileInputStream(new File(defaultPath + ID + "/" + ID + ".jpg"));
                byte[] data = getBytes(inputStream);
                BitmapFactory.decodeByteArray(data, 0, data.length, options);
                options.inSampleSize = calculateInSampleSize(options, 100, 100);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            }else if(new File(AppDirectory.getAlgDirectory()+ID).exists()){
                InputStream inputStream;
                inputStream = new FileInputStream(new File(AppDirectory.getAlgDirectory() + ID + "/" + ID + ".jpg"));
                byte[] data = getBytes(inputStream);
                BitmapFactory.decodeByteArray(data, 0, data.length, options);
                options.inSampleSize = calculateInSampleSize(options, 100, 100);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            }
            else if(new File(AppDirectory.getOptDirectory()+ID).exists()){
                InputStream inputStream;
                inputStream = new FileInputStream(new File(AppDirectory.getOptDirectory() + ID + "/" + ID + ".jpg"));
                byte[] data = getBytes(inputStream);
                BitmapFactory.decodeByteArray(data, 0, data.length, options);
                options.inSampleSize = calculateInSampleSize(options, 100, 100);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        //把所有的变量收集到一起，然后一次性把数据发送出去
        byte[] buffer = new byte[1024]; // 用数据装
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();

        return outstream.toByteArray();
    }

    /**
     * 根据要求将图片保存成指定大小，求得指定参数options
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
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


    /**
     * 从sd卡上获取图片文件
     *
     * @return
     */
    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new FileInputStream(new File(getImgPath()));
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExists(String fileName) {
        if(new File(defaultPath + fileName).exists()){
            return true;
        }else if(new File(AppDirectory.getAlgDirectory() + fileName).exists()){
            return true;
        }
        else if(new File(AppDirectory.getOptDirectory() + fileName).exists()){
            return true;
        }
        else{
            return false;
        }
    }


    /**
     * id查询
     *
     * @param _id
     * @return
     */
    public boolean hasName(String _id) {
        return id.contains(_id);
    }

    /**
     * 条件查询
     * @param json
     * @param keys
     * @param values
     * @return
     * @throws JSONException
     */
    public boolean checkCondition(String[] keys, String[] values) throws JSONException {
        JSONObject jsonTotal = getJsonStr();
        if (jsonTotal == null)
            return false;

        JSONObject json = jsonTotal.getJSONObject("infoFront");
        for (int i = 0; i < values.length; i++) {
            if ("sizeF".equals(keys[i])) {
                int size = Integer.parseInt(json.getString(keys[i]));
                int low = 0, high = 0;
                switch (values[i]) {
                    case "0":
                        low = 0;
                        high = 10;
                        break;
                    case "1":
                        low = 10;
                        high = 20;
                        break;

                    case "2":
                        low = 20;
                        high = 30;
                        break;
                    case "3":
                        low = 30;
                        high = 40;
                        break;
                    default:
                        break;
                }
                if (!((size >= low) && (size <= high)))
                    return false;
            } else if (json.has(keys[i])) {
                if (!json.getString(keys[i]).equals(values[i].toString())) {
                    return false;
                }
            } else {
                LogUtil.e("ButtonProfile", "含有非法的键值对");
            }
        }
        return true;
    }

    /**
     * 判断文件保存时间是否符合条件
     * @param time1
     * @param time2
     * @return
     * @throws JSONException
     */
    public boolean checkDate(String time1, String time2) throws JSONException {
        JSONObject json = getJsonStr();
        if (json == null)
            return false;
        if (json.has("time")) {
            String date = json.getString("time");
            if ((time1.compareTo(time2) <= 0) && (date.compareTo(time1) >= 0) && (date.compareTo(time2) <= 0))
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * 删除文件
     */
    public void Delete() {
        FileExplorer.deleteDirectory(new File(filePath + "/"));
    }
}
