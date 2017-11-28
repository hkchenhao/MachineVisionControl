package com.hanyu.hust.testnet.task;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import static com.hanyu.hust.testnet.utils.Constants.TIME_TASK;

/**
 * Created by Administrator on 2017/6/12.
 * 定时任务（5s）保存数据
 */

public class TimeTask {

    private Timer timer;

    private TimerTask task;

    public TimeTask(final Handler handler) {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = TIME_TASK;
                handler.sendMessage(message);
            }
        };
     //modifie by heqian on 2017/9
        timer.schedule(task, 10*60*1000, 10*60*1000);//10分钟的定时任务
    }

    void cancel()
    {
        timer.cancel();
    }
}
