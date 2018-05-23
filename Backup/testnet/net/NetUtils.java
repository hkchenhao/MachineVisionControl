package com.hanyu.hust.testnet.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

public class NetUtils
{
	/**
	 * 设置本地IP地址
	 */

	/**
	 * 三种端口定义
	 */
	public final static int listenBroadCastPort = 6019;
	public final static int sendIpPort = 6018;
	public static String ip = "115.156.211.10";
	public static String ip1 = "";
	
	/**
	 * 指令集常量定义
	 */
	public final static int MSG_NET_GET_VIDEO = 1;
	public final static int MSG_NET_GENERAL = 2;
	public final static int MSG_NET_ISL12026 = 6;
	public final static int MSG_NET_AD9849 = 7;
	public final static int MSG_NET_AT25040 = 8;
	public final static int MSG_NET_TEXTINFO = 9;
	public final static int MSG_NET_LINKINFO = 10;
	public final static int MSG_NET_NORMAL = 11;
	public final static int MSG_NET_FLASH = 12;
	public final static int MSG_NET_STATE = 14;
	public final static int MSG_NET_SEND_IMAGE = 15;
	public final static int MSG_NET_GET_RAW = 16;
	public final static int MSG_NET_UARTHECC = 17;
	public final static int MSG_NET_CONFSAVE = 18;
	public final static int MSG_NET_SETNET = 19;
	public final static int MSG_NET_FACTRESET = 20;
	public final static int MSG_NET_GET_PARAM = 21;
	public final static int MSG_NET_SAVE_VIDEO = 22;
	public final static int MSG_NET_TRIGGER = 23;
	public final static int MSG_NET_DSPTRIG = 24;
	public final static int MSG_NET_HELP_ALG_CMD = 25;
	public final static int MSG_NET_GET_JSON = 26;
	public final static int MSG_NET_SET_JSON = 27;
	public final static int MSG_NET_RESULT = 28;
	public final static int MSG_NET_SEND_BINARY = 29;
	
	// 发送钮扣配置文件的命令
	public final static int MSG_NET_SEND_GENERALINFO = 30;
	public final static int MSG_NET_SEND_FEATUREEXTRAOR = 31;
	public final static int MSG_NET_GET_FEATURERESULT = 32;
	public final static int MSG_NET_ALG_CONFIGURE = 33;

	// 调试命令
	public final static int MSG_NET_ALG_TEST_CONFIGURE = 35;
	public final static int MSG_NET_GET_COLORIMAGE = 36;

	//自学习相关命令 added by heqian, on 2017/8
	public final static int MSG_SELFLEARNING_TOTAL = 150;
	public final static int MSG_SELFLEARNING_COUNT = 151 ;
	public final static int MSG_SELFLEARNING_STATE = 152 ;
	public final static int MSG_SELFLEARLING_PARAMETER = 153;
	public final static int MSG_SELFLEARLING_PARADISPLAY = 154;

	public final static int MSG_TRADITION_PARAMETER = 152 ;


	public final static int MSG_HEART_BEAT	= 180;		/*心跳应答帧*/

	public final static int MSG_ALG_CFG_JSON = 191;
	public final static int MSG_GET_ROI = 192;
	public final static int MSG_DETAIL_INFO = 193;
	public final static int MSG_BTN_CFG_JSON = 194;
	public final static int MSG_IS_SEND_IMAGE = 195;
	public final static int MSG_NET_TOTAL_CNT = 196;
	public final static int MSG_ALG_RESULT = 197;
	public final static int MSG_ALG_INFO = 198;
	public final static int MSG_NET_ALG_IMAGE = 199;
	public final static int MSG_NET_ALGRESULT = 200;

	/*
	 * 获取本地IP
	 */
	static
	{
		Enumeration<NetworkInterface> en = null;
		try
		{
			en = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e)
		{
			e.printStackTrace();
		}
		while (en.hasMoreElements())
		{
			NetworkInterface intf = en.nextElement();
			if (intf.getName().toLowerCase().equals("eth0"))
			{
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						String ipaddress = inetAddress.getHostAddress();
						if (!ipaddress.contains("::"))
						{// ipV6的地址
							ip1 = ipaddress;
						}
					}
				}
			}
			else
			{
				continue;
			}
		}
	}

	public static void setIp()
	{
		setIp(ip);
	}

	/**
	 * 设置本地Ip
	 * @param ip
	 */
	public static void setIp(String ip)
	{
		Runtime r = Runtime.getRuntime();
		try
		{
			Process proc = r.exec("su");
			DataOutputStream dos = new DataOutputStream(proc.getOutputStream());
			dos.writeBytes("ifconfig eth0 " + ip + "\n");
			dos.writeBytes("exit\n");
			dos.flush();
			NetUtils.ip = ip;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 网络数据包
	 */
	public static class NetPacket
	{
		public int type;
		public int block;
		public int minid;
		public byte[] data;

		public void setData(byte[] data)
		{
			this.data = data;
		}
		public void send(OutputStream os)
		{
			DataPack.sendDataPack(this, os);
			try
			{
				Thread.sleep(60);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}		// 线程延时30ms,降低发送频率
		}
		
		public void recvDataPack(int id, InputStream is)
		{
			NetPacket packet = DataPack.recvDataPack(id, is);
			if (packet == null)
			{
				type = 0xaa;
			}
			else
			{
				type = packet.type;
				block = packet.block;
				minid = packet.minid;
				if (packet.data != null)
				{
					data = packet.data;
				}
			}
		}
	}

	public static class GetVideo extends NetPacket
	{
		public GetVideo()
		{
			minid = MSG_NET_GET_VIDEO;
			type = 1;
			block = 5000;
		}
	}

	public static class Normal extends NetPacket
	{
		public Normal()
		{
			minid = MSG_NET_NORMAL;
			type = 0;
			block = 0;
		}
	}

	public static class State extends NetPacket
	{
		public State()
		{
			minid = MSG_NET_STATE;
			type = 1;
			block = 50000;
			data = new byte[]
			{ 0x01, 0, 0, 0 };							// 设置子命令
		}
	}

	public static class SendImage extends NetPacket
	{
		public SendImage()
		{
			minid = MSG_NET_SEND_IMAGE;
			type = 2;
			block = 0;				// 可设为任意值
		}
	}

	public static class GeneralInfo extends NetPacket
	{
		public GeneralInfo()
		{
			minid = MSG_NET_GENERAL;
			type = 0;
			block = 0;
		}
	}

	public static class GetParam extends NetPacket
	{
		public GetParam()
		{
			minid = MSG_NET_GET_PARAM;
			type = 1;
			block = 1000;
		}
	}

	public static class GetJson extends NetPacket
	{
		public GetJson()
		{
			minid = MSG_NET_GET_JSON;
			type = 1;
			block = 1000;
		}
	}

	public static class SetJson extends NetPacket
	{
		public SetJson()
		{
			minid = MSG_NET_SET_JSON;
			type = 0;
			block = 0;
		}
	}
	
	public static class SendBinary extends NetPacket
	{
		public SendBinary()
		{
			minid = MSG_NET_SEND_BINARY;
			type = 0;
			block = 0;
		}
	}
	
	public static class SendGeneralInfo extends NetPacket
	{
		public SendGeneralInfo()
		{
			minid = MSG_NET_SEND_GENERALINFO;
			type = 0;
			block = 0;
		}
	}
	
	public static class SendFeatureExtrator extends NetPacket
	{
		public SendFeatureExtrator()
		{
			minid = MSG_NET_SEND_FEATUREEXTRAOR;
			type = 0;
			block = 0;
		}
	}
	
	public static class SendAlgConfigure extends NetPacket
	{
		public SendAlgConfigure()
		{
			minid = MSG_NET_ALG_CONFIGURE;
			type = 0;
			block = 0;
		}
	}
	
	public static class AlgTestConfigure extends NetPacket
	{
		public AlgTestConfigure()
		{
			minid = MSG_NET_ALG_TEST_CONFIGURE;
			type = 0;
			block = 0;
		}
	}
	public static class GetColorImage extends NetPacket
	{
		public GetColorImage()
		{
			minid = MSG_NET_GET_COLORIMAGE;
			type = 1;
			block = 5000;
		}
	}


	/**
	 * 向相机发送纽扣配置信息类
	 * */
	public static class SendConfigJson extends NetPacket
	{
		public SendConfigJson()
		{
			minid = MSG_BTN_CFG_JSON;
			type = 1;
			block = 5000;
		}
	}

	/**
	 * 向相机发送算法配置信息类
	 * */
	public static class SendAlgCfgJson extends NetPacket{
		public SendAlgCfgJson(){
			minid = MSG_ALG_CFG_JSON;
			type = 1;
			block = 5000;
		}
	}

	/*自学习相关类  added by heqian , on 2017/8 /*/
	//发送计数值
	public static class  sendSelfLearningTotal extends  NetPacket
	{
		public sendSelfLearningTotal()
		{
			minid = MSG_SELFLEARNING_TOTAL;
			type = 0;
			block = 0;
		}
	}
	public static class  sendTraditionPrameter extends  NetPacket
	{
		public sendTraditionPrameter()
		{
			minid = MSG_SELFLEARNING_TOTAL;
			type = 0;
			block = 0;
		}
	}

	/**
     * 向相机发送心跳应答帧
     * */
    public static class SendHeartBeatAck extends NetPacket{
        public SendHeartBeatAck(){
            minid = MSG_HEART_BEAT;
            type = 0;
            block = 5000;
        }
    }


	public static class GetRoiImage extends NetPacket
	{
		public GetRoiImage()
		{
			minid = MSG_GET_ROI;
			type = 1;
			block = 5000;
		}
	}

	/*
	*add by heqian on 2017/10/25
	* 网络协议定义
	* */
	//
	public  class netStruct_generalInfo implements Serializable {
		/* input camera type	CAMERA_Type			*/
		byte		input;
		/* output display way 0:lcd; 1:net; 2:crt	*/
		byte		output;
		/* fpga 获取图像数据位数 0：8bit; 1:16bits	*/
		byte		bitType;
		/* 所使用的算法								*/
		byte		algorithm;
		/* fpga 控制曝光时间 0-1720					*/
		short		expTime;

		byte		inited;
		/* 触发模式选择 0->auto, 1->dsp, 2->outside	*/
		byte		trigger;
		/* ccdc 获取图像数据横向起始位置			*/
		short		horzStartPix;
		/* ccdc 获取图像数据纵向起始位置			*/
		short		vertStartPix;
		/* ccdc 获取图像数据实际宽度				*/
		short		inWidth;
		/* ccdc 获取图像数据实际高度				*/
		short		inHeight;

		short		outWidth;
		short		outHeight;
	}

}
