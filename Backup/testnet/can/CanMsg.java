package com.hanyu.hust.testnet.can;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xinshangqiu on 2017/7/7.
 */

public class CanMsg{
    public int id;
    public int dlc;
    public short data[];

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
