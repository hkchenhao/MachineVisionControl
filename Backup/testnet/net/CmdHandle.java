package com.hanyu.hust.testnet.net;

import android.os.Handler;
import android.util.Log;

import com.hanyu.hust.testnet.AppContext;
import com.hanyu.hust.testnet.utils.ExcuteTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.hanyu.hust.testnet.net.NetUtils.MSG_ALG_CFG_JSON;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_BTN_CFG_JSON;

import static com.hanyu.hust.testnet.net.NetUtils.MSG_GET_ROI;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_HEART_BEAT;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_ALG_CONFIGURE;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_ALG_TEST_CONFIGURE;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_GENERAL;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_GET_COLORIMAGE;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_GET_JSON;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_GET_PARAM;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_GET_VIDEO;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_NORMAL;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_SEND_BINARY;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_SEND_FEATUREEXTRAOR;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_SEND_GENERALINFO;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_SEND_IMAGE;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_SET_JSON;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_NET_STATE;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_SELFLEARNING_TOTAL;
import static com.hanyu.hust.testnet.net.NetUtils.MSG_TRADITION_PARAMETER;

/**
 * 使用外观模式，将上述NetPacket的具体子类进行统一处理，创建命令包，设置数据然后发送。
 * 例如，获取温度可以用cmdHandle.getState(ID, handler)一行代码完成。
 * @author
 */
public class CmdHandle{

	private static final String TAG = "CmdHandle";

	static CmdHandle cmdhandle;

	public static CmdHandle getInstance() {
		if (cmdhandle == null) {
			synchronized(CmdHandle.class) {
				if (cmdhandle ==  null)
					cmdhandle = new CmdHandle();
			}
		}
		return cmdhandle;
	}

	/**
	 * 向相机端发送获取一张图片的命令，在发送前修改接收handler
	 * 
	 * @param handler
	 *            处理网络数据的handler
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void getVideo(String ID, Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_VIDEO);
		send(context, ID, handler);
	}

	/**
	 * 发送选择算法命令
	 * 处理网络数据的handler
	 * @param algorithm
	 * 要选择的算法编号
	 */
	public void normal(String ID, int algorithm) {
		NetPacketContext context = new NetPacketContext(MSG_NET_NORMAL);
		context.setData(new byte[] { (byte) algorithm });
		send(context, ID);
	}

	/**
	 * 发送获取相机温度的命令
	 * 
	 * @param handler
	 *            处理接收到的温度信息
	 */
	public void getState(String ID, Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_NET_STATE);
		send(context, ID, handler);
	}

	public void getParam(String ID, Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_PARAM);
		send(context, ID, handler);
	}
	
	public void getJson(String ID) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_JSON);
		send(context, ID);
	}

	public void setJson(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_SET_JSON);
		context.setData(data);
		send(context, ID);
	}

	// 发送文件（29）
	public void sendBinary(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_SEND_BINARY);
		context.setData(data);
		send(context, ID);
	}

	// 发送钮扣基本信息(30)
	public void sendGeneralInfo(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(
				MSG_NET_SEND_GENERALINFO);
		context.setData(data);
		send(context, ID);
	}

	// 发送钮扣特征提取信息
	public void sendFeatureExtrator(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(
				MSG_NET_SEND_FEATUREEXTRAOR);
		context.setData(data);
		send(context, ID);
	}

	// 发送算法配置信息
	public void sendAlgConfigure(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_ALG_CONFIGURE);
		context.setData(data);
		send(context, ID);
	}
	
	// 发送检测开始
	public void AlgTestConfigure(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_ALG_TEST_CONFIGURE);
		context.setData(data);
		send(context, ID);
	}
	
	public void generalInfo(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GENERAL);
		context.setData(data);
	}

	public void sendImage(String ID, Handler handler, int width, int height, int length,
			byte[] image) {
		NetPacketContext context = new NetPacketContext(MSG_NET_SEND_IMAGE);
		context.setData(getArrayFromInt(width, height, length));
	}

	/**
	 * 发送纽扣配置信息帧
	 * */
	public void sendBtnCfgJson(String ID, byte[] data) {
		NetPacketContext context = new NetPacketContext(MSG_BTN_CFG_JSON);
		context.setData(data);
		send(context, ID);
	}

	/**
	 * 发送算法配置信息帧
	 * */
	public void sendAlgCfgJson(String ID,byte[] data){
		NetPacketContext context = new NetPacketContext(MSG_ALG_CFG_JSON);
		context.setData(data);
		send(context,ID);
	}


	/**
	 * 发送心跳应答
	 * */
	public void sendHeartBeatAck(String ID,byte[] data){
		NetPacketContext context = new NetPacketContext(MSG_HEART_BEAT);
		context.setData(data);
		send(context,ID);
	}

	/**
	 * 向相机端发送获取一张图片的命令，在发送前修改接收handler
	 *
	 * @param handler
	 *            处理网络数据的handler
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void getColorImage(String ID, Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_NET_GET_COLORIMAGE);
		send(context, ID, handler);
	}

	/**
	 * 获取ROi图像
	 * @param ID  相机ID：   1：1号；2：2号 ....
	 * @param handler
     */
	public void getRoiImage(String ID, Handler handler) {
		NetPacketContext context = new NetPacketContext(MSG_GET_ROI);
		send(context, ID, handler);
	}

	/*added by heqian ,on 2017/8/7*/
	/**/
	public  void  sendSelflearningTotal( String ID,byte[] data ) {
		NetPacketContext context = new NetPacketContext(MSG_SELFLEARNING_TOTAL);
		context.setData(data);
		send(context,ID);
	}
	public  void  sendTraditionPrameter( String ID,byte[] data ) {
		NetPacketContext context = new NetPacketContext(MSG_TRADITION_PARAMETER);
		context.setData(data);
		send(context,ID);
	}

	private void send(NetPacketContext context, String CameraId, Handler handler)
	{
		Map<String, NetReceiveThread> mLinks = AppContext.getAppContext().getmLinks();
		NetReceiveThread netThread = mLinks.get(CameraId);
		if (netThread != null)
		{
			OutputStream os = netThread.getmOs();
			netThread.setHandler(handler);
			context.sendPacket(os);
		}
	}

	private void send(final NetPacketContext context, String CameraId)
	{
		Map<String, NetReceiveThread> mLinks = AppContext.getAppContext().getmLinks();
		final NetReceiveThread netThread = mLinks.get(CameraId);
		if (netThread != null)
		{
//			Log.d(TAG, "CmdHandle send for:" + netThread.getTag());
//			OutputStream os = netThread.getmOs();
//			context.sendPacket(os);
			ExcuteTask.excuteTask(new Runnable() {
				@Override
				public void run() {
					OutputStream os = netThread.getmOs();
					context.sendPacket(os);
				}
			});
		}
	}

	/**
	 * 从字节数组中获取整形数据
	 * 
	 * @param data
	 *            大小为4的字节数组
	 * @return 又四个字节组成的整形数
	 */
	public static int getIntFromArray(byte[] data) {
		if (data.length != 4) {
			return 0xFFFF;
		} else {
			return data[0] & 0xff | (data[1] << 8) & 0xff00 | (data[2] << 16)
					& 0xff0000 | data[3] << 24;
		}
	}

	private byte[] getArrayFromInt(int... data) {
		int i = -1;
		byte[] result = new byte[data.length * 4];

		while (++i < result.length) {
			result[i] = (byte) ((data[i / 4] >>> ((3 - i % 4) * 8)) & 0xFF);
		}

		return result;
	}
}
