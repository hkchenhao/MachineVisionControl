package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */


/**
 * AD9849参数配置
 */
public class Ad9849Conf {
    public byte[] vga = new byte[2];
    public byte[] pxga = new byte[4];
    public byte[] hxdrv = new byte[4];
    public byte rgdrv;
    public byte shp;
    public byte shd;
    public byte hp1;
    public byte hn1;
    public byte rgp1;
    public byte rgn1;

    private static final int bytesOffset = 202;
    private static final int bytesLength = 17;

    public Ad9849Conf() {
    }

    public Ad9849Conf(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.vga[0] = bytes[0];
            this.vga[1] = bytes[1];
            this.pxga[0] = bytes[2];
            this.pxga[1] = bytes[3];
            this.pxga[2] = bytes[4];
            this.pxga[3] = bytes[5];
            this.hxdrv[0] = bytes[6];
            this.hxdrv[1] = bytes[7];
            this.hxdrv[2] = bytes[8];
            this.hxdrv[3] = bytes[9];
            this.rgdrv = bytes[10];
            this.shp = bytes[11];
            this.shd = bytes[12];
            this.hp1 = bytes[13];
            this.hn1 = bytes[14];
            this.rgp1 = bytes[15];
            this.rgn1 = bytes[16];
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nAd9849Reg:");
        buffer.append("\nvga:" + Integer.toString(0xFF & this.vga[0]) + " " +
                Integer.toString(0xFF & this.vga[1]));
        buffer.append("\npxga:" + Integer.toString(0xFF & this.pxga[0]) + " " +
                Integer.toString(0xFF & this.pxga[1]) + " " + Integer.toString(0xFF & this.pxga[2])
                + " " + Integer.toString(0xFF & this.pxga[3]));
        buffer.append("\nhxdrv:" + Integer.toString(0xFF & this.hxdrv[0]) + " " +
                Integer.toString(0xFF & this.hxdrv[1]) + " " + Integer.toString(0xFF & this.hxdrv[2])
                + " " + Integer.toString(0xFF & this.hxdrv[3]));
        buffer.append("\nrgdrv:" + Integer.toString(0xFF & this.rgdrv));
        buffer.append("\nshp:" + Integer.toString(0xFF & this.shp));
        buffer.append("\nshd:" + Integer.toString(0xFF & this.shd));
        buffer.append("\nhp1:" + Integer.toString(0xFF & this.hp1));
        buffer.append("\nhn1:" + Integer.toString(0xFF & this.hn1));
        buffer.append("\nrgp1:" + Integer.toString(0xFF & this.rgp1));
        buffer.append("\nrgn1:" + Integer.toString(0xFF & this.rgn1));
        return buffer.toString();
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = this.vga[0];
        bytes[1] = this.vga[1];
        bytes[2] = this.pxga[0];
        bytes[3] = this.pxga[1];
        bytes[4] = this.pxga[2];
        bytes[5] = this.pxga[3];
        bytes[6] = this.hxdrv[0];
        bytes[7] = this.hxdrv[1];
        bytes[8] = this.hxdrv[2];
        bytes[9] = this.hxdrv[3];
        bytes[10] = this.rgdrv;
        bytes[11] = this.shp;
        bytes[12] = this.shd;
        bytes[13] = this.hp1;
        bytes[14] = this.hn1;
        bytes[15] = this.rgp1;
        bytes[16] = this.rgn1;
        return bytes;
    }

    /***
     * 恢复默认参数
     * @return
     */
    public Ad9849Conf toDefault() {
        this.vga[0] = (byte)(0xFF&200);
        this.vga[1] = 0;
        this.pxga[0] = 0;
        this.pxga[1] = 0;
        this.pxga[2] = 0;
        this.pxga[3] = 0;
        this.hxdrv[0] = 0x03;
        this.hxdrv[1] = 0x03;
        this.hxdrv[2] = 0x03;
        this.hxdrv[3] = 0x03;
        this.rgdrv = 0x03;
        this.shp = 0x24;
        this.shd = 0x00;
        this.hp1 = 0x00;
        this.hn1 = 0x20;
        this.rgp1 = 0x00;
        this.rgn1 = 0x3F;
        return this;
    }

    public static int getStartPosInframe() {
        return bytesOffset;
    }

    public static int getStopPosInFrame() {
        return (bytesOffset + bytesLength);
    }

}
