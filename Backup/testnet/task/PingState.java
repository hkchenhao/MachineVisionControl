package com.hanyu.hust.testnet.task;

/**
 * Created by Administrator on 2017/4/11.
 * 监听网络状态的接口
 */

public interface PingState {
    void fail(String socketAddress, String tag);

    void success();
}
