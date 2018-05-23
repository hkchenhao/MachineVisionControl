package com.hanyu.hust.testnet.task;

import android.os.Handler;

import com.hanyu.hust.testnet.utils.ExcuteTask;

/**
 * Created by Administrator on 2017/4/13.
 * 网络任务的基类
 * 覆写task() 函数得到不同的任务
 */

public abstract class BaseTask extends Thread {

    protected Handler mHandler;

    protected int mInterval;

    protected String mTag;

    private volatile boolean isStop = false;

    public void cancle() {
        isStop = true;
    }

    public BaseTask setThreadName(String name) {
        return this;
    }

    public BaseTask(String tag, Handler handler, int interval) {
        mTag = tag;
        mHandler = handler;
        mInterval = interval;
    }

    public BaseTask(String tag, Handler handler) {
        this(tag, handler, 100);
    }

    abstract public void task();

    @Override
    public void run() {
        while (!isStop) {
            try {
                ExcuteTask.excuteTask(new Runnable() {
                    @Override
                    public void run() {
                        task();
                    }
                });
                Thread.sleep(mInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
