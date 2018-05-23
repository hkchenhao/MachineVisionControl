package com.hanyu.hust.testnet.CameraConfigParam;

import java.util.Arrays;

/**
 * Created by xinshangqiu on 2017/12/6.
 */

public class CameraConfigParam {
    public NetConf netConf;
    public HeccConf heccConf;
    public UartConf uartConf;
    public SensorConf sensorConf;
    public ModeConf modeConf;
    public VersionRec versionRec;
    public Ad9849Conf ad9849Conf;
    public TriggerConf triggerConf;

    public CameraConfigParam(byte[] bytes) {
        byte[] netConf_bytes = Arrays.copyOfRange(bytes, NetConf.getStartPosInframe(), NetConf.getStopPosInFrame());
        this.netConf = new NetConf(netConf_bytes);

        byte[] heccConf_bytes = Arrays.copyOfRange(bytes, HeccConf.getStartPosInframe(), HeccConf.getStopPosInFrame());
        this.heccConf = new HeccConf(heccConf_bytes);

        byte[] uartConf_bytes = Arrays.copyOfRange(bytes, UartConf.getStartPosInframe(), UartConf.getStopPosInFrame());
        this.uartConf = new UartConf(uartConf_bytes);

        byte[] sensorConf_bytes = Arrays.copyOfRange(bytes, SensorConf.getStartPosInframe(), SensorConf.getStopPosInFrame());
        this.sensorConf = new SensorConf(sensorConf_bytes);

        byte[] modeConf_bytes = Arrays.copyOfRange(bytes, ModeConf.getStartPosInframe(), ModeConf.getStopPosInFrame());
        this.modeConf = new ModeConf(modeConf_bytes);

        byte[] versionRec_bytes = Arrays.copyOfRange(bytes, VersionRec.getStartPosInframe(), VersionRec.getStopPosInFrame());
        this.versionRec = new VersionRec(versionRec_bytes);

        byte[] ad9849Conf_bytes = Arrays.copyOfRange(bytes, Ad9849Conf.getStartPosInframe(), Ad9849Conf.getStopPosInFrame());
        this.ad9849Conf = new Ad9849Conf(ad9849Conf_bytes);

        byte[] triggerConf_bytes = Arrays.copyOfRange(bytes, TriggerConf.getStartPosInframe(), TriggerConf.getStopPosInFrame());
        this.triggerConf = new TriggerConf(triggerConf_bytes);
    }

    public GeneralConf getGeneralInfo() {
        GeneralConf cfg = new GeneralConf();
        cfg.input = this.sensorConf.sensor_number;
        cfg.output = this.modeConf.output_mode;
        cfg.bitType = this.modeConf.bitType;
        cfg.algorithm = this.modeConf.algorithm;
        cfg.expTime = (short) this.modeConf.expoTime;
        cfg.inited = 0;
        cfg.trigger = this.modeConf.trigger;
        cfg.horzStartPix = this.sensorConf.startPixel_width;
        cfg.vertStartPix = this.sensorConf.startPixel_height;
        cfg.inWidth = this.sensorConf.width_input;
        cfg.inHeight = this.sensorConf.height_input;
        cfg.outWidth = this.modeConf.width_crt;
        cfg.outHeight = this.modeConf.height_crt;
        return cfg;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("\nCameraConfigParam:");
        buffer.append(this.netConf.toString()
                + this.heccConf.toString()
                + this.uartConf.toString()
                + this.sensorConf.toString()
                + this.modeConf.toString()
                + this.versionRec.toString()
                + this.ad9849Conf.toString()
                + this.triggerConf.toString());
        return buffer.toString();
    }
}
