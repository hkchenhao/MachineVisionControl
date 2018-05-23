package com.hanyu.hust.testnet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hanyu.hust.testnet.ui.FileManager;
import com.hanyu.hust.testnet.ui.Help;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.StatisticsActivity;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.ButtonSearch;

/**
 * Created by Administrator on 2017/4/14.
 * 导航类
 */

public class NavigatorUtils {

    /**
     * 跳转到选择文件接受者UI
     * @param context
     */
    public static void toFileManager(Context context) {
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, FileManager.class);
        context.startActivity(intent);
    }

    public static void toSystemConfig(Context context) {
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, SystemConfig.class);
        context.startActivity(intent);
    }

    /**
     * 跳转统计分析
     * @param context
     */
    public static void toStatistics(Context context) {
        Log.d("StatisticsActivity", "toStatistics: ");
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, StatisticsActivity.class);
        Log.d("StatisticsActivity", "toStatistics: ");
        ((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_STATSICS_SETTINGS);
        Log.d("StatisticsActivity", "toStatistics: ");
    }

    /**
     * 跳转机器学习
     * @param context
     */
    public static void toMachineLearn(Context context) {
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, MachineLearning.class);
        context.startActivity(intent);
    }

    /**
     * 跳转帮助
     * @param context
     */
    public static void toHelp(Context context) {
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, Help.class);
        context.startActivity(intent);
    }

    /**
     * 跳转选择钮扣
     * @param context
     */
    public static void toButtonSearch(Context context) {
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, ButtonSearch.class);
        ((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_BTN_CFG);
    }

}




