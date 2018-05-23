package com.hanyu.hust.testnet.CameraConfigParam;

/**
 * Created by xinshangqiu on 2017/12/8.
 */

/**
 * 相机网络配置，偏移120字节，大小26字节
 */
public class NetConf {
    public short port;
    public byte work_mode;
    public byte[] ip_address = new byte[4];
    public byte[] remote_ip = new byte[4];
    public byte[] mac_address = new byte[6];
    public byte[] gateway = new byte[4];
    public byte[] ip_mask = new byte[4];
    public byte[] dns = new byte[4];
    public byte checksum;

    private static final int bytesOffset = 120;
    private static final int bytesLength = 30;

    public NetConf() {
    }

    public NetConf(byte[] bytes) {
        if (bytes.length == bytesLength) {
            this.port = (short) (((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF));
            this.work_mode = bytes[2];
            this.ip_address[0] = bytes[3];
            this.ip_address[1] = bytes[4];
            this.ip_address[2] = bytes[5];
            this.ip_address[3] = bytes[6];
            this.remote_ip[0] = bytes[7];
            this.remote_ip[1] = bytes[8];
            this.remote_ip[2] = bytes[9];
            this.remote_ip[3] = bytes[10];
            this.mac_address[0] = bytes[11];
            this.mac_address[1] = bytes[12];
            this.mac_address[2] = bytes[13];
            this.mac_address[3] = bytes[14];
            this.mac_address[4] = bytes[15];
            this.mac_address[5] = bytes[16];
            this.gateway[0] = bytes[17];
            this.gateway[1] = bytes[18];
            this.gateway[2] = bytes[19];
            this.gateway[3] = bytes[20];
            this.ip_mask[0] = bytes[21];
            this.ip_mask[1] = bytes[22];
            this.ip_mask[2] = bytes[23];
            this.ip_mask[3] = bytes[24];
            this.dns[0] = bytes[25];
            this.dns[1] = bytes[26];
            this.dns[2] = bytes[27];
            this.dns[3] = bytes[28];
            this.checksum = bytes[29];
        }
    }


    public NetConf toDeault(){
        this.port = 6020;
        this.work_mode = 0;
        this.ip_address[0] = (byte)(0xFF&115);
        this.ip_address[1] = (byte)(0xFF&156);
        this.ip_address[2] = (byte)(0xFF&211);
        this.ip_address[3] = (byte)(0xFF&4);
        this.remote_ip[0] = (byte)(0xFF&115);
        this.remote_ip[1] = (byte)(0xFF&156);
        this.remote_ip[2] = (byte)(0xFF&211);
        this.remote_ip[3] = (byte)(0xFF&10);
        this.mac_address[0] = (byte)(0xFF&0x00);
        this.mac_address[1] = (byte)(0xFF&0x1e);
        this.mac_address[2] = (byte)(0xFF&0x8c);
        this.mac_address[3] = (byte)(0xFF&0x80);
        this.mac_address[4] = (byte)(0xFF&0x00);
        this.mac_address[5] = (byte)(0xFF&0x05);
        return this;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nNetConf:");
        buffer.append("\nport:" + Integer.toString(this.port));
        buffer.append("\nwork_mode:" + Integer.toString(0xFF & this.work_mode));

        buffer.append("\nip_address");
        for (int i = 0; i < this.ip_address.length; i++) {
            buffer.append(":" + Integer.toString(0xFF & this.ip_address[i]));
        }

        buffer.append("\nremote_ip");
        for (int i = 0; i < this.remote_ip.length; i++) {
            buffer.append(":" + Integer.toString(0xFF & this.remote_ip[i]));
        }

        buffer.append("\nmac_address");
        for (int i = 0; i < this.mac_address.length; i++) {
            buffer.append(":" + Integer.toHexString(0xFF & this.mac_address[i]));
        }

        buffer.append("\ngateway");
        for (int i = 0; i < this.gateway.length; i++) {
            buffer.append(":" + Integer.toString(0xFF & this.gateway[i]));
        }

        buffer.append("\nip_mask");
        for (int i = 0; i < this.ip_mask.length; i++) {
            buffer.append(":" + Integer.toString(0xFF & this.ip_mask[i]));
        }

        buffer.append("\ndns");
        for (int i = 0; i < this.dns.length; i++) {
            buffer.append(":" + Integer.toString(0xFF & this.dns[i]));
        }

        buffer.append("\nchecksum:" + Integer.toString(0xFF & this.checksum));
        return buffer.toString();
    }


    public byte[] getBytes() {
        byte[] bytes = new byte[bytesLength];
        bytes[0] = (byte) (this.port & 0xFF);
        bytes[1] = (byte) ((this.port >> 8) & 0xFF);
        bytes[2] = this.work_mode;
        bytes[3] = this.ip_address[0];
        bytes[4] = this.ip_address[1];
        bytes[5] = this.ip_address[2];
        bytes[6] = this.ip_address[3];
        bytes[7] = this.remote_ip[0];
        bytes[8] = this.remote_ip[1];
        bytes[9] = this.remote_ip[2];
        bytes[10] = this.remote_ip[3];
        bytes[11] = this.mac_address[0];
        bytes[12] = this.mac_address[1];
        bytes[13] = this.mac_address[2];
        bytes[14] = this.mac_address[3];
        bytes[15] = this.mac_address[4];
        bytes[16] = this.mac_address[5];
        bytes[17] = this.gateway[0];
        bytes[18] = this.gateway[1];
        bytes[19] = this.gateway[2];
        bytes[20] = this.gateway[3];
        bytes[21] = this.ip_mask[0];
        bytes[22] = this.ip_mask[1];
        bytes[23] = this.ip_mask[2];
        bytes[24] = this.ip_mask[3];
        bytes[25] = this.dns[0];
        bytes[26] = this.dns[1];
        bytes[27] = this.dns[2];
        bytes[28] = this.dns[3];
        bytes[29] = (byte) (0x00);
        return bytes;
    }


    public static int getStartPosInframe() {
        return bytesOffset;
    }

    public static int getStopPosInFrame() {
        return (bytesOffset + bytesLength);
    }

}




