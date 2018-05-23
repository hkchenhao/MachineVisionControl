package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */

/**
 * 相机模式配置参数
 */
public class ModeConf {
    public int expoTime;
    public byte trigger;
    public byte algorithm;
    public short width_crt;
    public short height_crt;
    public byte output_mode;
    public byte bitType;
    public byte checksum;

    private static final int bytesOffset = 172;
    private static final int bytesLength = 16;

    public ModeConf() {
    }

    public ModeConf(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.expoTime = ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
            this.trigger = bytes[4];
            this.algorithm = bytes[5];
            this.width_crt = (short) (((bytes[7] & 0xFF) << 8) | (bytes[6] & 0xFF));
            this.height_crt = (short) (((bytes[9] & 0xFF) << 8) | (bytes[8] & 0xFF));
            this.output_mode = bytes[10];
            this.bitType = bytes[11];
            this.checksum = bytes[12];
        }
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nModeConf:");
        buffer.append("\nexpoTime:" + Integer.toString(this.expoTime));
        buffer.append("\ntrigger:" + Integer.toString(0xFF & this.trigger));
        buffer.append("\nalgorithm:" + Integer.toString(0xFF & this.algorithm));
        buffer.append("\nwidth_crt:" + Integer.toString(this.width_crt));
        buffer.append("\nheight_crt:" + Integer.toString(this.height_crt));
        buffer.append("\noutput_mode:" + Integer.toString(0xFF & this.output_mode));
        buffer.append("\nbitType:" + Integer.toString(0xFF & this.bitType));
        buffer.append("\nchecksum:" + Integer.toString(0xFF & this.checksum));
        return buffer.toString();
    }


    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = (byte) ((this.expoTime) & 0xFF);
        bytes[1] = (byte) ((this.expoTime >> 8) & 0xFF);
        bytes[2] = (byte) ((this.expoTime >> 16) & 0xFF);
        bytes[3] = (byte) ((this.expoTime >> 24) & 0xFF);
        bytes[4] = this.trigger;
        bytes[5] = this.algorithm;
        bytes[6] = (byte) ((this.width_crt) & 0xFF);
        bytes[7] = (byte) ((this.width_crt >> 8) & 0xFF);
        bytes[8] = (byte) ((this.height_crt) & 0xFF);
        bytes[9] = (byte) ((this.height_crt >> 8) & 0xFF);
        bytes[10] = this.output_mode;
        bytes[11] = this.bitType;
        bytes[12] = this.checksum;
        return bytes;
    }

    public static int getStartPosInframe() {
        return bytesOffset;
    }

    public static int getStopPosInFrame() {
        return (bytesOffset + bytesLength);
    }

}

