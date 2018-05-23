package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */

public class TriggerConf {
    public int trigDelay;
    public int partDelay;
    public int velocity;
    public int departWide;
    public int expLead;
    public byte checksum;

    private static final int bytesOffset = 220;
    private static final int bytesLength = 24;

    public TriggerConf() {
    }

    public TriggerConf(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.trigDelay = ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
            this.partDelay = ((bytes[7] & 0xFF) << 24) | ((bytes[6] & 0xFF) << 16) | ((bytes[5] & 0xFF) << 8) | (bytes[4] & 0xFF);
            this.velocity = ((bytes[11] & 0xFF) << 24) | ((bytes[10] & 0xFF) << 16) | ((bytes[9] & 0xFF) << 8) | (bytes[8] & 0xFF);
            this.departWide = ((bytes[15] & 0xFF) << 24) | ((bytes[14] & 0xFF) << 16) | ((bytes[13] & 0xFF) << 8) | (bytes[12] & 0xFF);
            this.expLead = ((bytes[19] & 0xFF) << 24) | ((bytes[18] & 0xFF) << 16) | ((bytes[17] & 0xFF) << 8) | (bytes[16] & 0xFF);
            this.checksum = bytes[20];
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nTriggerConf:");
        buffer.append("\ntrigDelay:" + this.trigDelay);
        buffer.append("\npartDelay:" + this.partDelay);
        buffer.append("\nvelocity:" + this.velocity);
        buffer.append("\ndepartWide:" + this.departWide);
        buffer.append("\nexpLead:" + this.expLead);
        buffer.append("\nchecksum:" + Integer.toString(0xFF & this.checksum));
        return buffer.toString();
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = (byte) (this.trigDelay & 0xFF);
        bytes[1] = (byte) ((this.trigDelay >> 8) & 0xFF);
        bytes[2] = (byte) ((this.trigDelay >> 16) & 0xFF);
        bytes[3] = (byte) ((this.trigDelay >> 24) & 0xFF);
        bytes[4] = (byte) (this.partDelay & 0xFF);
        bytes[5] = (byte) ((this.partDelay >> 8) & 0xFF);
        bytes[6] = (byte) ((this.partDelay >> 16) & 0xFF);
        bytes[7] = (byte) ((this.partDelay >> 24) & 0xFF);
        bytes[8] = (byte) (this.velocity & 0xFF);
        bytes[9] = (byte) ((this.velocity >> 8) & 0xFF);
        bytes[10] = (byte) ((this.velocity >> 16) & 0xFF);
        bytes[11] = (byte) ((this.velocity >> 24) & 0xFF);
        bytes[12] = (byte) (this.departWide & 0xFF);
        bytes[13] = (byte) ((this.departWide >> 8) & 0xFF);
        bytes[14] = (byte) ((this.departWide >> 16) & 0xFF);
        bytes[15] = (byte) ((this.departWide >> 24) & 0xFF);
        bytes[16] = (byte) (this.expLead & 0xFF);
        bytes[17] = (byte) ((this.expLead >> 8) & 0xFF);
        bytes[18] = (byte) ((this.expLead >> 16) & 0xFF);
        bytes[19] = (byte) ((this.expLead >> 24) & 0xFF);
        bytes[20] = this.checksum;
        bytes[21] = (byte) (0x00);
        bytes[22] = (byte) (0x00);
        bytes[23] = (byte) (0x00);
        return bytes;
    }

    public TriggerConf toDefault(){
        this.trigDelay = 0;
        this.partDelay = 0;
        this.velocity = 0;
        this.departWide = 0;
        this.expLead = 0;
        return this;
    }

    public static int getStartPosInframe() {
        return bytesOffset;
    }

    public static int getStopPosInFrame() {
        return (bytesOffset + bytesLength);
    }
}
