package com.hanyu.hust.testnet.can;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hanyu.hust.testnet.entity.CanParameter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xinshangqiu on 2017/7/6.
 */

public class CanBusThread extends Thread {
    private Process can_process;
    private DataOutputStream output_stream;
    private InputStream input_stream;
    private InputStream err_stream;
    boolean can_bus_init_ok = false;
    private Handler rx_handler;
    private Timer timer;
    private TimerTask timerTask;

    public String TAG = new String("can thread");

    /*消息缓冲队列*/
    List<String> readList = new ArrayList<>();
    List<String> writeList = new ArrayList<>();
    List<CanMsg> can_rx_msg_list = new ArrayList<>();

    /*连接超时信息*/
    static int smart_camera1_timeout = 0;
    static int smart_camera2_timeout = 0;
    static int smart_camera3_timeout = 0;
    static int motor_ctrl_card_timeout = 0;
    static int light_src_drv_timeout = 0;
    static int btn_trigger1_timeout = 0;
    static int btn_trigger2_timeout = 0;
    static int btn_trigger3_timeout = 0;

    /**
     * 构造函数
     */
    public CanBusThread() {
        setName("CanBusThread");
        rx_handler = defaultHandler;
        timerTaskInit();
    }

    /**
     * 向Can总线写入报文
     */
    public void write(CanMsg msg) {
        StringBuffer tx_msg_buffer = new StringBuffer("cansend can0 -i");
        tx_msg_buffer.append(" " + msg.id);
        for (int i = 0; i < msg.dlc; i++) {
            tx_msg_buffer.append(" " + msg.data[i]);
        }
        tx_msg_buffer.append("\n");
        try {
            output_stream.writeBytes(tx_msg_buffer.toString());
            output_stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Can总线
     */
    public void close() {

        try {
            output_stream.writeBytes("canconfig can0 stop\n");
            output_stream.flush();
            output_stream.writeBytes("exit\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setHandler(Handler handler) {
        rx_handler = handler;
    }

    public void setDefaultHandler() {
        rx_handler = defaultHandler;
    }

    /**
     * 异步任务处理
     */
    private Handler defaultHandler = new Handler() {
        public void handleMessage(Message msg) {
//            List<CanMsg> tmp_msg_list = (List) msg.obj;
//            tmp_msg_list.clear();
        }
    };

    /**
     * CAN是否连接判断
     */
    public static boolean isValid(short dest_id) {
        boolean ret = true;
        switch (dest_id) {
            case CanParameter.SmartCamera1_Addr: {
                if (smart_camera1_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            case CanParameter.SmartCamera2_Addr: {
                if (smart_camera2_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            case CanParameter.SmartCamera3_Addr: {
                if (smart_camera3_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            case CanParameter.MotorCtrlCard_Addr: {
                if (motor_ctrl_card_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            case CanParameter.LighrSrcDrv_Addr: {
                if (light_src_drv_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            case CanParameter.BtnTrigger1_Addr: {
                if (btn_trigger1_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            case CanParameter.BtnTrigger2_Addr: {
                if (btn_trigger2_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            case CanParameter.BtnTrigger3_Addr: {
                if (btn_trigger3_timeout > CanParameter.HBA_Timeout)
                    ret = false;
                break;
            }
            default: {
                ret = false;
                break;
            }
        }
        return ret;
    }

    /**
     * CAN接收线程
     */
    @Override
    public void run() {
        try {
            /*生成输出流*/
            can_process = Runtime.getRuntime().exec("su");
            output_stream = new DataOutputStream(can_process.getOutputStream());
            output_stream.writeBytes("canconfig can0 bitrate 1000000 ctrlmode triple-sampling on\n");
            output_stream.flush();
            output_stream.writeBytes("canconfig can0 start\n");
            output_stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                /*生成输入流*/
                can_bus_init_ok = true;
                can_process = Runtime.getRuntime().exec("candump can0 --filter=0x311:0x3FF\n");
                err_stream = can_process.getErrorStream();
                Thread t = new Thread(new InputStreamRunnable(err_stream, "error stream"));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (can_bus_init_ok) {
                input_stream = can_process.getInputStream();
                break;
            } else {
                try {
                    err_stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int cnt;
        byte[] buffer = new byte[2048];

        while (true) {
            try {
                cnt = input_stream.read(buffer);
                if (cnt > 0) {
                    synchronized (writeList) {
                        writeList.add(new String(buffer, 0, cnt));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 定时线程，完成双缓冲列表的切换，和异步handler的调用
     */
    private void timerTaskInit() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (can_bus_init_ok) {
                    synchronized (writeList) {
                        List<String> tmp = readList;
                        readList = writeList;
                        writeList = tmp;
                        writeList.clear();
                    }

                    /*超时累加*/
                    smart_camera1_timeout++;
                    smart_camera2_timeout++;
                    smart_camera3_timeout++;
                    motor_ctrl_card_timeout++;
                    light_src_drv_timeout++;
                    btn_trigger1_timeout++;
                    btn_trigger2_timeout++;
                    btn_trigger3_timeout++;

                    /**字符串拆解成can msg*/
                    can_rx_msg_list.clear();                            /**清除can msg接收列表*/
                    for (int index = 0; index < readList.size(); index++) {
                        String msg = readList.get(index);
                        byte tmp_bytes[] = msg.getBytes();
                        if (tmp_bytes[0] == '<') {                      /**字符串起始位置*/
                            CanMsg tmp_msg = new CanMsg();
                            tmp_msg.id = Integer.parseInt(new String(tmp_bytes, 3, 3), 16);
                            tmp_msg.dlc = (int) tmp_bytes[9] - '0';
                            for (int i = 0; i < tmp_msg.dlc; i++) {
                                tmp_msg.data[i] = (short) ((ascToNumber(tmp_bytes[12 + 3 * i]) * 16 + ascToNumber(tmp_bytes[12 + 3 * i + 1])));
                            }
                            can_rx_msg_list.add(tmp_msg);

                            if (tmp_msg.data[2] == CanParameter.PF_HB) {/**当前为心跳帧，自动应答*/
                                switch (tmp_msg.data[1]) {              /**判断消息帧的发送方*/
                                    /*超时清空*/
                                    case CanParameter.SmartCamera1_Addr: {
                                        smart_camera1_timeout = 0;
                                        break;
                                    }
                                    case CanParameter.SmartCamera2_Addr: {
                                        smart_camera2_timeout = 0;
                                        break;
                                    }
                                    case CanParameter.SmartCamera3_Addr: {
                                        smart_camera3_timeout = 0;
                                        break;
                                    }
                                    case CanParameter.MotorCtrlCard_Addr: {
                                        motor_ctrl_card_timeout = 0;
                                        break;
                                    }
                                    case CanParameter.LighrSrcDrv_Addr: {
                                        light_src_drv_timeout = 0;
                                        break;
                                    }
                                    case CanParameter.BtnTrigger1_Addr: {
                                        btn_trigger1_timeout = 0;
                                        break;
                                    }
                                    case CanParameter.BtnTrigger2_Addr: {
                                        btn_trigger2_timeout = 0;
                                        break;
                                    }
                                    case CanParameter.BtnTrigger3_Addr: {
                                        btn_trigger3_timeout = 0;
                                        break;
                                    }
                                    default: {
                                        break;
                                    }
                                }
                                write(createHeartBeatAckMsg(CanParameter.Addr_Mask | tmp_msg.data[1]));
                            }
                        }
                    }
                    readList.clear();

                    /**这里添加handler相关代码*/
                    Message handler_msg = rx_handler.obtainMessage();
                    if (can_rx_msg_list.isEmpty()) {
                        handler_msg.what = 0;
                    } else {
                        handler_msg.what = 1;
                    }
                    handler_msg.obj = can_rx_msg_list;
                    rx_handler.sendMessage(handler_msg);
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }


    /**
     * 读取InputStream的线程
     */
    class InputStreamRunnable implements Runnable {
        BufferedReader bReader = null;
        String type = null;

        public InputStreamRunnable(InputStream is, String _type) {
            try {
                bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), "UTF-8"));
                type = _type;
            } catch (Exception ex) {
            }
        }

        public void run() {
            String line;
            try {
                while ((line = bReader.readLine()) != null) {
                    if (line.equals("read: Network is down")) {
                        can_bus_init_ok = false;
                        Log.d(TAG, "can error " + line);
                    }
                }
                bReader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    /**
     * 将字符转换成数字，例如：'9'->9,
     */
    private short ascToNumber(byte b) {
        return (short) (b >= '0' && b <= '9' ? b - 0x30
                : b <= 'F' && b >= 'A' ? b - 0x37 : b - 0x57);
    }

    public static CanMsg createHeartBeatAckMsg(int dest_id) {
        CanMsg msg = new CanMsg();
        msg.id = CanParameter.Transfer_Addr;
        msg.data[0] = (short) (dest_id & 0xFF);
        msg.data[1] = CanParameter.Terminal_Addr;
        msg.data[2] = CanParameter.PF_HBA;
        msg.dlc = 3;
        return msg;
    }

    public static CanMsg createParamReadMsg(int dest_id, short PN_CODE) {
        CanMsg msg = new CanMsg();
        msg.id = CanParameter.Transfer_Addr;
        msg.data[0] = (short) (dest_id & 0xFF);
        msg.data[1] = CanParameter.Terminal_Addr;
        msg.data[2] = CanParameter.PF_PR;
        msg.data[3] = PN_CODE;
        msg.dlc = 4;
        return msg;
    }

    public static CanMsg createParamWriteMsg(int dest_id, short PN_CODE, int param) {
        CanMsg msg = new CanMsg();
        msg.id = CanParameter.Transfer_Addr;
        msg.data[0] = (short) (dest_id & 0xFF);
        msg.data[1] = CanParameter.Terminal_Addr;
        msg.data[2] = CanParameter.PF_PW;
        msg.data[3] = PN_CODE;
        msg.data[4] = (short) (param & 0xFF);
        msg.data[5] = (short) ((param >> 8) & 0xFF);
        msg.data[6] = (short) ((param >> 16) & 0xFF);
        msg.data[7] = (short) ((param >> 24) & 0xFF);
        msg.dlc = 8;
        return msg;
    }
}
