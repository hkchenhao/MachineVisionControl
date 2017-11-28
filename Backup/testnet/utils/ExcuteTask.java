package com.hanyu.hust.testnet.utils;

import com.hanyu.hust.testnet.AppContext;

/**
 * Created by Administrator on 2017/4/13.
 * 执行异步任务类
 */

public class ExcuteTask {

    public static void excuteTask(Runnable task)
    {
        AppContext.getAppContext().SENDER_EXECUTOR.execute(task);
    }

    public static void sleep(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
