package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */

/**
 * 相机串口配置，偏移126字节，大小3字节
 */

public class UartConf {
    public byte baudRate;
    public byte work_mode;
    public byte checksum;

    private static final int bytesOffset = 153;
    private static final int bytesLength = 3;

    public UartConf() {
    }

    public UartConf(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.baudRate = bytes[0];
            this.work_mode = bytes[1];
            this.checksum = bytes[2];
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nUartConf:");
        buffer.append("\nbaudRate:" + Integer.toString(0xFF & this.baudRate));
        buffer.append("\nwork:" + Integer.toString(0xFF & this.work_mode));
        buffer.append("\nchecksum:" + Integer.toString(0xFF & this.checksum));
        return buffer.toString();
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = this.baudRate;
        bytes[1] = this.baudRate;
        bytes[2] = this.baudRate;
        return bytes;
    }

    public static int getStartPosInframe() {
        return bytesOffset;
    }

    public static int getStopPosInFrame() {
        return (bytesOffset + bytesLength);
    }

}
