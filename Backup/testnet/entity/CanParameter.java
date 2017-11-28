package com.hanyu.hust.testnet.entity;

/**
 * Created by xinshangqiu on 2017/9/23.
 */

public class CanParameter {
    public static final int Addr_Mask = 0x200;
    /*终端*/
    public static final int DEST_ADDR = 0x311;
    public static final int Transfer_Addr = 0x331;

    /*SA码，源地址标识符，CAN报文第0字节,此处为精简版地址*/
    public static final short Terminal_Addr = 0x11;
    public static final short SmartCamera1_Addr = 0x21;
    public static final short SmartCamera2_Addr = 0x22;
    public static final short SmartCamera3_Addr = 0x23;
    public static final short MotorCtrlCard_Addr = 0x31;
    public static final short LighrSrcDrv_Addr = 0x41;
    public static final short BtnTrigger1_Addr = 0x51;
    public static final short BtnTrigger2_Addr = 0x52;
    public static final short BtnTrigger3_Addr = 0x53;

    /*PF码，标识帧类型，CAN报文第1字节*/
    public static final short PF_HB = 0x21;
    public static final short PF_HBA = 0x23;
    public static final short PF_PR = 0x31;
    public static final short PF_PRA = 0x33;
    public static final short PF_PRAE = 0x35;
    public static final short PF_PW = 0x41;
    public static final short PF_PWA = 0x43;
    public static final short PF_PWAE = 0x45;

    /*PN码，参数标号*/
    public static final short PN_BRIGHTNESS0 = 0x20;

    public static final int HBA_Timeout = 3;/**心跳超时时间，3ms*/
}
