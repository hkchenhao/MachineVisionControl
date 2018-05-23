package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */

/**
 * 相机版本参数配置
 */
public class VersionRec {
    public byte[] id = new byte[4];
    public short version;
    public byte[] write_time = new byte[6];
    public byte checksum;

    private static final int bytesOffset = 188;
    private static final int bytesLength = 14;

    public VersionRec() {
    }

    public VersionRec(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.id[0] = bytes[0];
            this.id[1] = bytes[1];
            this.id[2] = bytes[2];
            this.id[3] = bytes[3];
            this.version = (short)(((bytes[5] & 0xFF) << 8) | (bytes[4] & 0xFF));
            this.write_time[0] = bytes[6];
            this.write_time[1] = bytes[7];
            this.write_time[2] = bytes[8];
            this.write_time[3] = bytes[9];
            this.write_time[4] = bytes[10];
            this.write_time[5] = bytes[11];
            this.checksum = bytes[12];
        }
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nVersionRec:");
        buffer.append("\nid:" + this.id[0] + "." +
                this.id[1] + "." + this.id[2] + "." + this.id[3]);
        buffer.append("\nversion:" + this.version);
        buffer.append("\nwrite_time:" + this.write_time[0] + "." +
                this.write_time[1] + "." + this.write_time[2] + "." + this.write_time[3]);
        buffer.append("\nchecksum:" + this.checksum);
        return buffer.toString();
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = (this.id[0]);
        bytes[1] = (this.id[1]);
        bytes[2] = (this.id[2]);
        bytes[3] = (this.id[3]);
        bytes[4] = (byte) ((this.version) & 0xFF);
        bytes[5] = (byte) ((this.version >> 8) & 0xFF);
        bytes[6] = this.write_time[0];
        bytes[7] = this.write_time[1];
        bytes[8] = this.write_time[2];
        bytes[9] = this.write_time[3];
        bytes[10] = this.write_time[4];
        bytes[11] = this.write_time[5];
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
