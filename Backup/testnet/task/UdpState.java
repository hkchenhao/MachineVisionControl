package com.hanyu.hust.testnet.task;

/**
 * Created by Administrator on 2017/4/11.
 */

public interface UdpState {

    /**
     * handler the request which matches the uri
     */
    void fail(Exception e);

    /**
     * releas some resource when finish the handler
     */
    void success();
}
