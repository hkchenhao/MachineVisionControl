package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */

/**
 * 相机传感器配置参数
 */
public class SensorConf {
    public short width_max;
    public short height_max;
    public short width_input;
    public short height_input;
    public short startPixel_width;
    public short startPixel_height;
    public byte sensor_number;
    public byte isColor;
    public byte bitPixel;
    public byte checksum;

    private static final int bytesOffset = 156;
    private static final int bytesLength = 16;

    public SensorConf() {
    }

    public SensorConf(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.width_max = (short) (((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF));
            this.height_max = (short) (((bytes[3] & 0xFF) << 8) | (bytes[2] & 0xFF));
            this.width_input = (short) (((bytes[5] & 0xFF) << 8) | (bytes[4] & 0xFF));
            this.height_input = (short) (((bytes[7] & 0xFF) << 8) | (bytes[6] & 0xFF));
            this.startPixel_width = (short) (((bytes[9] & 0xFF) << 8) | (bytes[8] & 0xFF));
            this.startPixel_height = (short) (((bytes[11] & 0xFF) << 8) | (bytes[10] & 0xFF));
            this.sensor_number = bytes[12];
            this.isColor = bytes[13];
            this.bitPixel = bytes[14];
            this.checksum = bytes[15];
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nSensorConf:");
        buffer.append("\nwidth_max:" + this.width_max);
        buffer.append("\nheight_max:" + this.height_max);
        buffer.append("\nwidth_input:" + this.width_input);
        buffer.append("\nheight_input:" + this.height_input);
        buffer.append("\nstartPixel_width:" + this.startPixel_width);
        buffer.append("\nstartPixel_height:" + this.startPixel_height);
        buffer.append("\nsensor_number:" + Integer.toString(0xFF & this.sensor_number));
        buffer.append("\nisColor:" + Integer.toString(0xFF & this.isColor));
        buffer.append("\nbitPixel:" + Integer.toString(0xFF & this.bitPixel));
        buffer.append("\nchecksum:" + Integer.toString(0xFF & this.checksum));
        return buffer.toString();
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = (byte) (this.width_max & 0xFF);
        bytes[1] = (byte) ((this.width_max >> 8) & 0xFF);
        bytes[2] = (byte) (this.height_max & 0xFF);
        bytes[3] = (byte) ((this.height_max >> 8) & 0xFF);
        bytes[4] = (byte) (this.width_input & 0xFF);
        bytes[5] = (byte) ((this.width_input >> 8) & 0xFF);
        bytes[6] = (byte) (this.height_input & 0xFF);
        bytes[7] = (byte) ((this.height_input >> 8) & 0xFF);
        bytes[8] = (byte) (this.startPixel_width & 0xFF);
        bytes[9] = (byte) ((this.startPixel_width >> 8) & 0xFF);
        bytes[10] = (byte) (this.startPixel_height & 0xFF);
        bytes[11] = (byte) ((this.startPixel_height >> 8) & 0xFF);
        bytes[12] = this.sensor_number;
        bytes[13] = this.isColor;
        bytes[14] = this.bitPixel;
        bytes[15] = this.checksum;
        return bytes;
    }

    public static int getStartPosInframe() {
        return bytesOffset;
    }

    public static int getStopPosInFrame() {
        return (bytesOffset + bytesLength);
    }

}
