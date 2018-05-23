package com.hanyu.hust.testnet.net;

import com.hanyu.hust.testnet.net.NetUtils.*;
//import com.hanyu.hust.testnet.net.NetUtils.AlgTestConfigure;
//import com.hanyu.hust.testnet.net.NetUtils.GeneralInfo;
//import com.hanyu.hust.testnet.net.NetUtils.GetColorImage;
//import com.hanyu.hust.testnet.net.NetUtils.GetJson;
//import com.hanyu.hust.testnet.net.NetUtils.GetParam;
//import com.hanyu.hust.testnet.net.NetUtils.GetVideo;
//import com.hanyu.hust.testnet.net.NetUtils.NetPacket;
//import com.hanyu.hust.testnet.net.NetUtils.Normal;
//import com.hanyu.hust.testnet.net.NetUtils.SendAlgConfigure;
//import com.hanyu.hust.testnet.net.NetUtils.SendBinary;
//import com.hanyu.hust.testnet.net.NetUtils.SendFeatureExtrator;
//import com.hanyu.hust.testnet.net.NetUtils.SendGeneralInfo;
//import com.hanyu.hust.testnet.net.NetUtils.SetJson;
//import com.hanyu.hust.testnet.net.NetUtils.State;
//import com.hanyu.hust.testnet.net.NetUtils.SendConfigJson;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.io.OutputStream;


/**
 * 使用工厂方法模式根据需要创建相应的网络包。
 *
 * @author MC
 */
public class NetPacketContext {
    private static final String TAG = NetPacketContext.class.getSimpleName();

    NetPacket packet;

    public NetPacketContext(){}
    /**
     * 采用策略模式生成算法
     *
     * @param type 根据类型判断需要生成的包
     */
    public NetPacketContext(int type) {
        switch (type) {
            case NetUtils.MSG_NET_GET_VIDEO:
                packet = new GetVideo();
                break;
            case NetUtils.MSG_NET_NORMAL:
                packet = new Normal();
                break;
            case NetUtils.MSG_NET_STATE:
                packet = new State();
                break;
            case NetUtils.MSG_NET_GET_JSON:
                packet = new GetJson();
                break;
            case NetUtils.MSG_NET_SET_JSON:
                packet = new SetJson();
                break;
            case NetUtils.MSG_NET_SEND_BINARY:
                packet = new SendBinary();
                break;
            case NetUtils.MSG_NET_SEND_GENERALINFO:
                packet = new SendGeneralInfo();
                break;
            case NetUtils.MSG_NET_SEND_FEATUREEXTRAOR:
                packet = new SendFeatureExtrator();
                break;
            case NetUtils.MSG_NET_ALG_CONFIGURE:
                packet = new SendAlgConfigure();
                break;
            case NetUtils.MSG_NET_ALG_TEST_CONFIGURE:
                packet = new AlgTestConfigure();
                break;
            case NetUtils.MSG_NET_GET_COLORIMAGE:
                packet = new GetColorImage();
                break;

            /**心跳应答包*/
            case NetUtils.MSG_HEART_BEAT: {
                packet = new SendHeartBeatAck();
                break;
            }
            /**钮扣配置*/
            case NetUtils.MSG_BTN_CFG_JSON: {
                packet = new SendConfigJson();
                break;
            }
            /**算法配置*/
            case NetUtils.MSG_ALG_CFG_JSON: {
                packet = new SendAlgCfgJson();
                break;
            }

            case NetUtils.MSG_GET_ROI:
                packet = new GetRoiImage();
                break;

            case NetUtils.MSG_NET_GET_IMAGE:
                packet = new GetImage();
                break;
            /**工作模式控制帧 added by heqian, on 2107/11*/
            case NetUtils.MSG_NET_MODULE:
                packet = new sendMouleInfo();
                break;
            /**自学习相关 added by heqian, on 2107/8*/
            case NetUtils.MSG_SELFLEARNING_TOTAL:
                packet = new sendSelfLearningTotal();
                break;

            case NetUtils.MSG_TRADITION_PARAMETER:
                packet = new sendTraditionPrameter();
                break;
            case NetUtils.MSG_NET_CMD: {
                packet = new SendCmdInfo();
                break;
            }

            case NetUtils.MSG_NET_GET_PARAM:
                packet = new GetParam();
                break;
            default: {
                LogUtil.e(TAG, "netPacketContext: netPacket error");
                break;
            }
        }
    }

    /**
     * 发送数据包
     *
     * @param os socket输出流
     */
    public void sendPacket(OutputStream os) {
        packet.send(os);
    }

    /**
     * 设置数据包的数据区
     *
     * @param data 数据包的数据区
     */
    public void setData(byte[] data) {
        packet.setData(data);
    }
}
