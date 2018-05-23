package com.hanyu.hust.testnet.entity;

import android.annotation.SuppressLint;

import com.hanyu.hust.testnet.utils.AppDirectory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by LitWill on 2017/10/19.
 * 系统控制参数类
 * 功能：实现系统控制文件管理、
 */


public class SettingSystemConfigFile {
    // 默认路径
    private static String defaultPath = AppDirectory.getSystemConfigDirectory();

    //配置文件名
    private String fileName;

    // 纽扣文件夹路径
    private String filePath;

    /**
     * 构造函数
     * @param fileName
     */
    public SettingSystemConfigFile(String fileName){
        this.fileName = fileName;
        filePath = defaultPath;
    }

    public SettingSystemConfigFile(String fileName , String filePath){
        this.fileName = fileName;
        filePath = filePath;
    }
    public SettingSystemConfigFile(File file) {
        fileName = file.getName();
        filePath = file.getAbsolutePath();
    }

    public File getFile() {
        return new File(filePath);
    }

    private String getSystemConfigFilePath() {
        return filePath ;
    }

    /**
     * 保存配置文件到sd卡*
     * @param json
     */
    @SuppressLint("SimpleDateFormat")
    public void write2sd(JSONObject json) {
        try {
            File file = new File(getSystemConfigFilePath());
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

}
