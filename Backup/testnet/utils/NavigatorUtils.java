package com.hanyu.hust.testnet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hanyu.hust.testnet.ui.FileManager;
import com.hanyu.hust.testnet.ui.Help;
import com.hanyu.hust.testnet.ui.MachineLearning;
import com.hanyu.hust.testnet.ui.StatisticsActivity;
import com.hanyu.hust.testnet.ui.SystemConfig;
import com.hanyu.hust.testnet.ui.view.AlgSearch;
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
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, StatisticsActivity.class);
        ((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_STATSICS_SETTINGS);
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
     * 跳转选择纽扣
     * @param context
     */
    public static void toButtonSearch(Context context) {
        if (context == null) {
            throw new RuntimeException("Context not be null!!!");
        }
        Intent intent = new Intent(context, ButtonSearch.class);
        ((Activity)context).startActivityForResult(intent, Constants.REQUEST_CODE_BTN_CFG);
    }

    /**
     * @breif :跳转选择算法
     * @param ：context
     * */
    public static void toAlgSearch(Context context){
        if(context == null){
            throw new RuntimeException("Context not be null!");
        }else{
            Intent intent = new Intent(context, AlgSearch.class);
            ((Activity)context).startActivityForResult(intent,Constants.REQUEST_CODE_ALG_CFG);
        }

        /*添加activity启动*/
    }
    /**
     * @breif :跳转选择算子
     * @param ：context
     * */
    public static void toOptSearch(Context context){
        if(context == null){
            throw new RuntimeException("Context not be null!");
        }
        else{
            Toast.makeText(context,"进入算子选择界面", Toast.LENGTH_SHORT).show();
        }

        /*添加activity启动*/

    }


}




