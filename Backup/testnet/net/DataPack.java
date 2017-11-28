package com.hanyu.hust.testnet.net;

import com.hanyu.hust.testnet.net.NetUtils.NetPacket;
import com.hanyu.hust.testnet.utils.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * ）网络包的打包/解析
 * @author MC
 *
 */
public class DataPack
{
//	private static final String TAG = "DataPack";

	static byte[] rxBuf1 = new byte[1000 * 1024];

	static byte[] rxBuf2 = new byte[1000 * 1024];

	static byte[] rxBuf3 = new byte[1000 * 1024];

	final static int magic = 0x695a695a;
	final static int version = 0;
	final static int offset = 28;

	public static int timeoutCount = 0;
	
	private static Lock lock = new ReentrantLock();

	public static synchronized boolean sendDataPack(NetPacket packet, OutputStream os)
	{
		DataOutputStream dos = new DataOutputStream(os);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream tempDos = new DataOutputStream(baos);

		try
		{
			sendLittleInt(tempDos, magic);
			sendLittleInt(tempDos, version);
			sendLittleInt(tempDos, packet.type);
			sendLittleInt(tempDos, packet.block);
			sendLittleInt(tempDos, packet.data == null ? offset
					: packet.data.length + offset);
			sendLittleInt(tempDos, offset);
			sendLittleInt(tempDos, packet.minid);

			if (packet.data != null)
			{
				tempDos.write(packet.data);
				tempDos.flush();
			}

			dos.write(baos.toByteArray());
			dos.flush();
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据输入流is 解析得到NetPacket
	 * @param id
	 * @param is
	 * @return
	 */
	public static NetPacket recvDataPack(int id, InputStream is)
	{
		byte[] rxBuf;

		/**
		 * 为每台相机Socket分配缓存
		 */
		if (id == 1)
			rxBuf = rxBuf1;
		else if (id == 2)
			rxBuf = rxBuf2;
		else if (id == 3)
			rxBuf = rxBuf3;
		else
			rxBuf = rxBuf1;

		NetPacket revPacket = new NetPacket();
		boolean hasMagicRead = false;
		int bufCount = 0;
		int startPos = 0;
		int availableCount = 0;

		try
		{
			bufCount = is.read(rxBuf);
//			LogUtil.d(TAG, "bufCount = " + bufCount);

			/*
			 * 判断帧头标示
			 */
			for (int i = 0; i < bufCount - 1; i++)
			{
				if (rxBuf[i] == 0x5a && rxBuf[i + 1] == 0x69
						&& rxBuf[i + 2] == 0x5a && rxBuf[i + 3] == 0x69)
				{
					startPos = i;
					hasMagicRead = true;
					break;
				}
			}
			
			if (!hasMagicRead)
			{
//				LogUtil.e("ZY", "hasn't read magic");
				return null;
			}
			availableCount = bufCount - startPos;						// 计算有效长度

			/*
			 * 如果有效长度大于offset，则帧头接收完毕
			 */
			if (availableCount >= offset)
			{
				/*
				 * 解析帧头
				 */
				DataInputStream dis = new DataInputStream(
						new ByteArrayInputStream(rxBuf));
				dis.skip(startPos + 16);

				int length = readLittleInt(dis);
				int len = length - offset;

				dis.skip(startPos + 4);

				revPacket.minid = readLittleInt(dis);
				if (availableCount < length)								// 判断整包是否接收完毕
				{
					// 将剩余部分接收完并拼接到一起
					int tempCount = 0;
					int tempPos = 0;
					int restCount = length - availableCount;
					do
					{
						int maxLen = rxBuf.length - (bufCount + tempPos) - 1;
						maxLen = Math.min(maxLen, restCount - tempPos);
						tempCount = is.read(rxBuf, bufCount + tempPos,
								maxLen);
						tempPos += tempCount;
					} while (tempPos < restCount);
				}
				else if (availableCount > length)
				{
//					LogUtil.e(TAG, "availableCount > length : " + availableCount);
				}

				/*
				 * 接收data数组
				 */
				if (len > 0)
				{
					long time = System.currentTimeMillis();
					revPacket.data = new byte[len];
					long time1 = System.currentTimeMillis();
//					LogUtil.d(TAG, "new byte[len] time =  " + (time1-time) + ", len = " + len);

					int count = 0, pos = 0;
					byte[] temp = revPacket.data;
					do
					{
						count = dis.read(temp, pos, len - pos);
						pos += count;
					} while (count > 0 && pos < len);
				}
				return revPacket;
			}
			else
			{
//				LogUtil.d("ZY", "bufCount = " + bufCount);
			}
		} catch (IOException e)
		{
//			LogUtil.e("ZY", "time out");
			timeoutCount++;
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 以小端形式发送一个short数据
	 * 
	 * @param dos
	 *            发送输出流
	 * @param data
	 *            待发送的short数据
	 * @throws IOException
	 */
	private static void sendLittleShort(DataOutputStream dos, short data)
			throws IOException
	{
		dos.writeShort(swapShortToLittleEndian(data));
	}

	/**
	 * 以小端形式接收一个short数据
	 * 
	 * @param dis
	 *            接收输入流
	 * @return 小端形式的short数据
	 * @throws IOException
	 */
	private static short readLittleShort(DataInputStream dis)
			throws IOException
	{
		short data = dis.readShort();
		return swapShortToLittleEndian(data);
	}

	/**
	 * 以小端形式接收一个int数据
	 * 
	 * @param dos
	 *            发送输出流
	 * @param data
	 *            待发送的数据
	 * @throws IOException
	 */
	private static void sendLittleInt(DataOutputStream dos, int data)
			throws IOException
	{
		int ret = swapShortToLittleEndian((short) ((data >> 16) & 0xFFFF))
				| (swapShortToLittleEndian((short) data) << 16);
		dos.writeInt(ret);
	}

	/**
	 * 以小端形式读取一个int数据
	 * 
	 * @param dis
	 *            接收输入流
	 * @return 小端形式的int数
	 * @throws IOException
	 */
	public static int readLittleInt(DataInputStream dis) throws IOException
	{
		int data = dis.readInt();
		int ret = swapShortToLittleEndian((short) (data >> 16)) & 0xFFFF
				| (swapShortToLittleEndian((short) data) << 16);
		return ret;
	}
	/**
	 * 大小端蝶形交换
	 * 
	 * @param data
	 *            待交换的short数据
	 * @return 转换后的short数
	 */
	private static short swapShortToLittleEndian(short data)
	{
		short ret = (short) ((data << 8) | (data >> 8) & 0x00FF);
		ret = (short) (ret & 0xFFFF);
		return ret;
	}
}
