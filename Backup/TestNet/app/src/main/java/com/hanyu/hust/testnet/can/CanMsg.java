package com.hanyu.hust.testnet.can;

/**
 * Created by xinshangqiu on 2017/7/7.
 * can消息包装类
 */

public class CanMsg {
    public int id; //地址
    public int dlc; //data数组长度
    public short data[]; //正文消息

    public CanMsg() {
        id = 0;
        dlc = 0;
        data = new short[8];
    }

    @Override
    public String toString() {
       StringBuffer can_msg_buffer = new StringBuffer(this.id + " " + this.dlc);
        for (int i=0;i<this.dlc;i++){
            can_msg_buffer.append(" " + this.data[i]);
       }
        return  can_msg_buffer.toString();
    }
}
