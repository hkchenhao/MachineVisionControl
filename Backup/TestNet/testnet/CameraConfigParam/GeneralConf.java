package com.hanyu.hust.testnet.CameraConfigParam;

import com.hanyu.hust.testnet.entity.SysDevPacket;

/**
 * Created by xinshangqiu on 2017/12/11.
 */

public class GeneralConf {
    public byte input;
    public byte output;
    public byte bitType;
    public byte algorithm;
    public short expTime;
    public byte inited;
    public byte trigger;
    public short horzStartPix;
    public short vertStartPix;
    public short inWidth;
    public short inHeight;
    public short outWidth;
    public short outHeight;

    public GeneralConf() {
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[20];
        bytes[0] = this.input;
        bytes[1] = this.output;
        bytes[2] = this.bitType;
        bytes[3] = this.algorithm;
        bytes[4] = (byte) (this.expTime & 0xFF);
        bytes[5] = (byte) ((this.expTime >> 8) & 0xFF);
        bytes[6] = this.inited;
        bytes[7] = this.trigger;
        bytes[8] = (byte) (this.horzStartPix & 0xFF);
        bytes[9] = (byte) ((this.horzStartPix >> 8) & 0xFF);
        bytes[10] = (byte) (this.vertStartPix & 0xFF);
        bytes[11] = (byte) ((this.vertStartPix >> 8) & 0xFF);
        bytes[12] = (byte) (this.inWidth & 0xFF);
        bytes[13] = (byte) ((this.inWidth >> 8) & 0xFF);
        bytes[14] = (byte) (this.inHeight & 0xFF);
        bytes[15] = (byte) ((this.inHeight >> 8) & 0xFF);
        bytes[16] = (byte) (this.outWidth & 0xFF);
        bytes[17] = (byte) ((this.outWidth >> 8) & 0xFF);
        bytes[18] = (byte) (this.outHeight & 0xFF);
        bytes[19] = (byte) ((this.outHeight >> 8) & 0xFF);
        return bytes;
    }

    public GeneralConf toDefault(){
        this.output = 1;
        this.algorithm = 0;
        this.expTime = 860;
        this.bitType = 0x10;
        this.horzStartPix = 0;
        this.vertStartPix = 0;
        this.inWidth = 640;
        this.inHeight = 480;
        this.outWidth = 720;
        this.outHeight = 576;
        return this;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nGeneral Info:");
        buffer.append("\n input:" + Integer.toString(0xFF & this.input));
        buffer.append("\n output:" + Integer.toString(0xFF & this.output));
        buffer.append("\n bitType:" + Integer.toString(0xFF & this.bitType));
        buffer.append("\n algorithm:" + Integer.toString(0xFF & this.algorithm));
        buffer.append("\n expTime:" + Integer.toString(0xFFFF & this.expTime));
        buffer.append("\n inited:" + Integer.toString(0xFF & this.inited));
        buffer.append("\n trigger:" + Integer.toString(0xFF & this.trigger));
        buffer.append("\n horzStartPix:" + Integer.toString(0xFFFF & this.horzStartPix));
        buffer.append("\n vertStartPix:" + Integer.toString(0xFFFF & this.vertStartPix));
        buffer.append("\n inWidth:" + Integer.toString(0xFFFF & this.inWidth));
        buffer.append("\n inHeight:" + Integer.toString(0xFFFF & this.inHeight));
        buffer.append("\n outWidth:" + Integer.toString(0xFFFF & this.outWidth));
        buffer.append("\n outHeight:" + Integer.toString(0xFFFF & this.outHeight));

        return buffer.toString();
    }
}
