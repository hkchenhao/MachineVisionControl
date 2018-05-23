package com.hanyu.hust.testnet.can;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
 * Created by Wuxin on 2017/11/22.
 * can消息功能集合类
 */

public class Can {
    public static final String TAG = "can";
    private final boolean enabled = false;
    static boolean existed = false;

    boolean inited_ok = false;
    boolean bus_active = false;
    int rx_timeout = 0;

    private Process can_process;//can进程
    private DataOutputStream output_stream;//输出数据流
    private InputStream input_stream;//输入数据流
    private InputStream err_stream;

    private Handler rx_handler;//接收handler
    private Handler rx_default_handler;
    private Handler rx_timeout_handler;

    private Timer timer;  //任务定时器
    private TimerTask timerTask;

    private Thread receive_thread;

    private static final int Tick_Time = 500;
    private static final int TimeOut = 1000;
    /**
     * 消息缓冲队列
     */
    List<String> readList = new ArrayList<>();
    List<String> writeList = new ArrayList<>();
    List<CanMsg> rx_msg_list = new ArrayList<>();
    public Map<Integer, CanMsg> waitAckMapList = new HashMap<>();

    /**
     * 连接超时时间记录
     */
    static int smart_camera1_timeout = 0;
    static int smart_camera2_timeout = 0;
    static int smart_camera3_timeout = 0;
    static int motor_ctrl_card_timeout = 0;
    static int light_src_drv_timeout = 0;
    static int btn_trigger1_timeout = 0;
    static int btn_trigger2_timeout = 0;
    static int btn_trigger3_timeout = 0;

    public Can() {
        if (this.existed) {
            /**已被初始化*/
        } else {
            this.existed = true;
            this.start();
            this.receiveThreadInit();
            this.timerTaskInit();

            /**实例化接收超时中断处理*/
            rx_timeout_handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    close();
                    start();
                    receiveThreadInit();
                }
            };

            /**实例化缺省接收中断处理*/
            rx_default_handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                }
            };

            rx_handler = rx_default_handler;

            Log.d(TAG, "inited ok");
        }
    }

    /**
     * 启动can总线
     * 1.打开输出流；
     * 2.打开输入流；
     * 3.并检测输入流是否打开成功；
     * 4.初始化接收线程
     */
    public void start() {
        if (enabled) {
            inited_ok = false;
            bus_active = false;
            rx_timeout = 0;

            /**初始化IO流*/
            try {
                /**生成输出流*/
                can_process = Runtime.getRuntime().exec("su");
                output_stream = new DataOutputStream(can_process.getOutputStream());
                output_stream.writeBytes("canconfig can0 bitrate 100000 ctrlmode triple-sampling on\n");
                output_stream.flush();
                output_stream.writeBytes("canconfig can0 start\n");
                output_stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    /**生成输入流*/
                    inited_ok = true;
                    can_process = Runtime.getRuntime().exec("candump can0 --filter=0x211:0x3FF\n");
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

                if (inited_ok) {
                    input_stream = can_process.getInputStream();
                    Log.d(TAG, "input stream inited ok");
                    break;
                } else {
                    try {
                        err_stream.close();
                        can_process.destroy();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 初始化Can的接收线程
     */
    private void receiveThreadInit() {
        /**初始化接收线程*/
        receive_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt;
                byte[] buffer = new byte[2048];

                while (inited_ok) {
                    try {
                        cnt = input_stream.read(buffer);
                        if (cnt > 0) {
                            bus_active = true;
                            synchronized (writeList) {
                                writeList.add(new String(buffer, 0, cnt));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "receive thread closed");
            }
        });
        receive_thread.start();
        Log.d(TAG, "receive thread inited ok");
    }

    /**
     * 定时器任务初始化
     * 1.监测接收线程是否处于活动状态，发送接收超时中断
     * 2.定时处理接收到的can数据，发送接收处理中断
     */
    private void timerTaskInit() {
        /**初始化定时器任务*/
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (inited_ok) {
                    if (bus_active) {
                        bus_active = false;
                        rx_timeout = 0;
                    } else {
                        rx_timeout = rx_timeout + Tick_Time;
                        if (rx_timeout > TimeOut) {
                            /**接收超时异常处理*/
                            Log.d(TAG, "receive time out error");
                            Message handler_msg = rx_timeout_handler.obtainMessage();
                            handler_msg.what = 1;
                            rx_timeout_handler.sendMessage(handler_msg);
                            return;
                        }
                    }

                    /**超时累加*/
                    smart_camera1_timeout += Tick_Time;
                    smart_camera2_timeout += Tick_Time;
                    smart_camera3_timeout += Tick_Time;
                    motor_ctrl_card_timeout += Tick_Time;
                    light_src_drv_timeout += Tick_Time;
                    btn_trigger1_timeout += Tick_Time;
                    btn_trigger2_timeout += Tick_Time;
                    btn_trigger3_timeout += Tick_Time;

                    synchronized (writeList) {
                        List<String> tmp = readList;
                        readList = writeList;
                        writeList = tmp;
                        writeList.clear();
                    }
                    /**添加字符串解析成can msg的代码*/
                    rx_msg_list.clear();
                    for (int index = 0; index < readList.size(); index++) {
                        String msg = readList.get(index);
                        byte tmp_bytes[] = msg.getBytes();
                        if (tmp_bytes[0] == '<') {                      /**字符串起始位置*/
                            CanMsg tmp_msg = new CanMsg();
                            tmp_msg.id = Integer.parseInt(new String(tmp_bytes, 3, 3), 16);
                            tmp_msg.dlc = (int) tmp_bytes[9] - '0';
                            for (int i = 0; i < tmp_msg.dlc; i++) {
                                tmp_msg.data[i] = (short) ((asc2Number(tmp_bytes[12 + 3 * i]) * 16 + asc2Number(tmp_bytes[12 + 3 * i + 1])));
                            }
                            rx_msg_list.add(tmp_msg);
                            if (tmp_msg.data[2] == CanParameter.PF_HB) {/**当前为心跳帧，自动应答*/
                                switch (tmp_msg.data[1]) {              /**判断消息帧的发送方*/
                                    /**超时清空*/
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
                                write(createHeartBeatAckMsg(tmp_msg.data[1]));
                            } else if (tmp_msg.data[2] == CanParameter.PF_PWA) {
                                /**收到“写应答”，从列表中移除对应的帧*/
                                Integer tmp_msg_key = new Integer(getCanMsgKey(tmp_msg));
                                synchronized (waitAckMapList) {
                                    if (waitAckMapList.containsKey(tmp_msg_key)) {
                                        waitAckMapList.remove(tmp_msg_key);
                                    }
                                }
                            }
                        }
                    }
                    readList.clear();

                    /**检查waitAckMapList，对list中的msg进行超时重发处理*/
                    synchronized (waitAckMapList) {
                        for (Integer key : waitAckMapList.keySet()) {
                            CanMsg tmp_tx_msg = waitAckMapList.get(key);
                            write(tmp_tx_msg);
//                            Log.d(TAG, "run: " + tmp_tx_msg.toString());
                        }
                    }

                    /**发送定时异步处理handler给对应的线程*/
                    if (rx_msg_list.size() > 0) {
                        Message handler_msg = rx_handler.obtainMessage();
                        handler_msg.obj = rx_msg_list;
                        rx_handler.sendMessage(handler_msg);
                    }
                }
            }
        };
        timer.schedule(timerTask, 1000, Tick_Time);
        Log.d(TAG, "timer task inited ok");
    }

    /**
     * 向Can总线写数据
     */
    public void write(CanMsg msg) {
        if (enabled) {
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

            /**将报文存入waitAckMapList,只存PW报文*/
            if (msg.data[2] == CanParameter.PF_PW) {
                Integer msg_key = new Integer(getCanMsgKey(msg));
                waitAckMapList.put(msg_key, msg);
            }
        }
    }

    /**
     * @return
     */
    private int getCanMsgKey(CanMsg msg) {
        if (msg != null) {
            if (msg.data[2] == CanParameter.PF_PW) {
                int key = msg.data[0];
                key = (key << 8) | msg.data[3];
                return key;
            } else if (msg.data[2] == CanParameter.PF_PWA) {
                int key = msg.data[1];
                key = (key << 8) | msg.data[3];
                return key;
            }
        }
        return 0;
    }


    /**
     * 关闭can总线
     */
    public void close() {
        if (enabled) {
            try {
                inited_ok = false;
                output_stream.writeBytes("canconfig can0 stop\n");
                output_stream.flush();
                output_stream.writeBytes("exit\n");
                err_stream.close();
                input_stream.close();
                output_stream.close();
                can_process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置can线程的接收处理
     */
    public void setReceiveHandler(Handler handler) {
        rx_handler = handler;
    }

    /**
     * 设置can线程的缺省接收处理
     */
    public void setDefaultReceiveHandler() {
        rx_handler = rx_default_handler;
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
                ex.printStackTrace();
            }
        }

        public void run() {
            String line;
            try {
                while ((line = bReader.readLine()) != null) {
                    if (line.equals("read: Network is down")) {
                        inited_ok = false;
                    }
                    Log.d(TAG, "can error " + line);
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
    private short asc2Number(byte b) {
        return (short) (b >= '0' && b <= '9' ? b - 0x30
                : b <= 'F' && b >= 'A' ? b - 0x37 : b - 0x57);
    }


    /**
     * CAN是否连接判断
     */
    public static boolean isValid(int dest_id) {
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

    public static CanMsg createHeartBeatAckMsg(short dest_id) {
        CanMsg msg = new CanMsg();

        msg.id = CanParameter.Addr_Mask | dest_id;
        msg.data[0] = (short) (dest_id & 0xFF);
        msg.data[1] = (short) (CanParameter.Terminal_Addr & 0xFF);
        msg.data[2] = CanParameter.PF_HBA;
        msg.dlc = 3;

        return msg;
    }

    public static CanMsg createParamReadMsg(short dest_id, short Pn_Code) {
        CanMsg msg = new CanMsg();

        msg.id = CanParameter.Addr_Mask | dest_id;
        msg.data[0] = (short) (dest_id & 0xFF);
        msg.data[1] = (short) (CanParameter.Terminal_Addr & 0xFF);
        msg.data[2] = CanParameter.PF_PR;
        msg.data[3] = Pn_Code;
        msg.dlc = 4;

        return msg;
    }

    public static CanMsg createParamWriteMsg(short dest_id, short Pn_Code, int param) {
        CanMsg msg = new CanMsg();

        msg.id = CanParameter.Addr_Mask | dest_id;
        msg.data[0] = (short) (dest_id & 0xFF);
        msg.data[1] = (short) (CanParameter.Terminal_Addr & 0xFF);
        msg.data[2] = CanParameter.PF_PW;
        msg.data[3] = Pn_Code;
        msg.data[4] = (short) (param & 0xFF);
        msg.data[5] = (short) ((param >> 8) & 0xFF);
        msg.data[6] = (short) ((param >> 16) & 0xFF);
        msg.data[7] = (short) ((param >> 24) & 0xFF);
        msg.dlc = 8;

        return msg;
    }

    /**
     * @param can_msg
     * @return
     */
    public static int getParam(CanMsg can_msg) {
        if (can_msg != null) {
            if (can_msg.dlc == 8) {
                int byte0 = (can_msg.data[4] & 0xFF);
                int byte1 = (can_msg.data[5] & 0xFF) << 8;
                int byte2 = (can_msg.data[6] & 0xFF) << 16;
                int byte3 = (can_msg.data[7] & 0xFF) << 24;
                int param = byte0 + byte1 + byte2 + byte3;
                return param;
            } else {
                return -1;
            }
        } else {
            return -2;
        }
    }

    public static short getPnCode(CanMsg can_msg) {
        short pn_code = 0;
        if (can_msg != null) {
            switch (can_msg.data[2]) {
                case CanParameter.PF_HB:
                    pn_code = -1;
                    break;
                case CanParameter.PF_HBA:
                    pn_code = -1;
                    break;
                case CanParameter.PF_PR:
                    pn_code = can_msg.data[3];
                    break;
                case CanParameter.PF_PRA:
                    pn_code = can_msg.data[3];
                    break;
                case CanParameter.PF_PW:
                    pn_code = can_msg.data[3];
                    break;
                case CanParameter.PF_PWA:
                    pn_code = can_msg.data[3];
                    break;
                default:
                    pn_code = -1;
                    break;
            }

            return pn_code;
        } else {
            return -1;
        }
    }

    /**
     * @param net_msg
     * @return
     */
    public static CanMsg netMsg2CanMsg(byte[] net_msg) {
        if ((net_msg.length > 0) && (net_msg.length < 9)) {
            CanMsg msg = new CanMsg();

            msg.dlc = net_msg.length;
            msg.id = CanParameter.Addr_Mask | net_msg[0];

            for (int i = 0; i < net_msg.length; i++) {
                msg.data[i] = (short) (net_msg[i] & 0xFF);
            }
            return msg;
        } else {
            Log.d(TAG, "netMsg2CanMsg: DLC err" + net_msg.length);
            return null;
        }
    }
}
