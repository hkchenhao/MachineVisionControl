package com.hanyu.hust.testnet.task;

import android.os.Handler;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.net.CmdHandle;

/**
 * Created by Administrator on 2017/4/13.
 * 获取视频流任务
 */

public class VedioTask extends BaseTask
{
    public VedioTask(String tag, Handler handler)
    {
        super(tag, handler);
    }

    public VedioTask(String tag, Handler handler, int interval)
    {
        super(tag, handler, interval);
    }

    @Override
    public BaseTask setThreadName(String name) {
        setName(name);
        return this;
    }

    @Override
    public void task() {
        if (AppContext.getAppContext().isExist(mTag))
            CmdHandle.getInstance().getVideo(mTag, mHandler);
        else
            cancle();
    }
}
