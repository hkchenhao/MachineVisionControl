package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/12.
 */

public class Mt9v032Conf {
    public byte isAgc;         //自动增益控制
    public byte isAec;         //自动曝光控制
    public short agVal;        //设置增益大小
    public short aeVal;       //设置曝光大小


    public Mt9v032Conf() {
        this.isAgc = 1;
        this.isAec = 1;
        this.agVal = 0x0010;
        this.aeVal = 0x01E0;
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[6];
        bytes[0] = this.isAgc;
        bytes[1] = this.isAec;
        bytes[2] = (byte) (this.agVal & 0xFF);
        bytes[3] = (byte) ((this.agVal >> 8) & 0xFF);
        bytes[4] = (byte) (this.aeVal & 0xFF);
        bytes[5] = (byte) ((this.aeVal >> 8) & 0xFF);
        return bytes;
    }


    public Mt9v032Conf toDefault(){
        this.isAgc = 1;
        this.isAec = 1;
        this.agVal = 0x0010;
        this.aeVal = 0x01E0;
        return this;
    }
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nMt9v032 Conf:");
        buffer.append("\n isAgc:" + Integer.toString(0xFF & this.isAgc));
        buffer.append("\n isAec:" + Integer.toString(0xFF & this.isAec));
        buffer.append("\n agVal:" + Integer.toString(0xFFFF & this.agVal));
        buffer.append("\n aeVal:" + Integer.toString(0xFFFF & this.aeVal));
        return buffer.toString();
    }
}
