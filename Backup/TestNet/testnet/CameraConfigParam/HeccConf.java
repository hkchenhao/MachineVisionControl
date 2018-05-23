package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */

/**
 * 相机CAN总线配置
 */
public class HeccConf {
    public byte baudRate;
    public byte id;
    public byte checksum;

    private static final int bytesOffset = 150;
    private static final int bytesLength = 3;

    public HeccConf() {
    }

    public HeccConf(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.baudRate = bytes[0];
            this.id = bytes[1];
            this.checksum = bytes[2];
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nHeccConf:");
        buffer.append("\nbaudRate:" + Integer.toString(0xFF & this.baudRate));
        buffer.append("\nid:" + Integer.toString(0xFF & this.id));
        buffer.append("\nchecksum:" + Integer.toString(0xFF & this.checksum));
        return buffer.toString();
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = this.baudRate;
        bytes[1] = this.id;
        bytes[2] = this.checksum;
        return bytes;
    }

    public static int getStartPosInframe() {
        return bytesOffset;
    }

    public static int getStopPosInFrame() {
        return (bytesOffset + bytesLength);
    }

}


