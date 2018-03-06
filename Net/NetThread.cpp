#include "NetThread.h"
#include "Data/DataFactory.h"
#include "Data/DataPacket.h"
#include "Event/MainFormEvent.h"
#include "Event/NotificationCenter.h"
#include "Form/MainForm.h"
#include "Mgr/FormFrame.h"
#include "Mgr/SystemFrame.h"
#include "Utils/QJsonAnalysis.h"

NetThread::NetThread(qintptr socketDescriptor, QObject* parent) : QObject(parent)
{
    // 私有变量初始
    temp = 0;
    netpacket_preread_length_ = 0;
    is_netpacket_done_ = true;
    net_thread_ = new QThread();
    // 父子关系变成this的子成员实现移动到新线程
    net_socket_ = new QTcpSocket(this);
    net_socket_->setSocketDescriptor(socketDescriptor);

    // 根据IP地址判断相机编号
    QString cilent_ipaddr = net_socket_->peerAddress().toString();
    if(cilent_ipaddr == QString(CAMERA1_IP))
    {
        camera_id_ = CameraIdentifier_Num1;
//        if(SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] != nullptr)
//        {
//            NetThread* p_oldthread = SystemFrame::GetInstance()->p_netthread_buf_[camera_id_];
//            p_oldthread->deleteLater();
//        }
        SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] = this;
        //SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] = SystemFrame::GetInstance()->p_netthread_;
        //SystemFrame::GetInstance()->p_netthread_ = nullptr;
    }
    else if(cilent_ipaddr == QString(CAMERA2_IP))
    {
        camera_id_ = CameraIdentifier_Num2;
        SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] = this;
        //SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] = SystemFrame::GetInstance()->p_netthread_;
        //SystemFrame::GetInstance()->p_netthread_ = nullptr;
    }
    else if(cilent_ipaddr == QString(CAMERA3_IP))
    {
        camera_id_ = CameraIdentifier_Num3;
        SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] = this;
//        SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] = SystemFrame::GetInstance()->p_netthread_;
//        SystemFrame::GetInstance()->p_netthread_ = nullptr;
    }
    else
    {
        // 未识别相机直接退出
        camera_id_ = CameraIdentifier_None;
        this->deleteLater();
//        delete SystemFrame::GetInstance()->p_netthread_;
//        SystemFrame::GetInstance()->p_netthread_ = nullptr;
        return;
    }

    // 信号与槽-UI-不同线程默认Qt::QueuedConnection连接
    QObject::connect(FormFrame::GetInstance()->p_mainform_, &MainForm::SignalNetSendPacket, this, &NetThread::SendNetPacket);
    QObject::connect(FormFrame::GetInstance()->p_mainform_, &MainForm::SignalNetSendJson, this, &NetThread::SendNetJsonPacket);

    QObject::connect(FormFrame::GetInstance()->p_mainform_, &MainForm::SignalNetClose, this, &NetThread::CloseNet);
    QObject::connect(FormFrame::GetInstance()->p_mainform_, &MainForm::SignalNetClose, this, [this](){ net_socket_->close(); });
    // 信号与槽-Thread-不同线程默认Qt::QueuedConnection连接
    QObject::connect(net_thread_, &QThread::started, this, &NetThread::StartNet);
    QObject::connect(net_thread_, &QThread::finished, this, &NetThread::CloseThread);
    // 信号与槽-TcpSocket-相同线程默认Qt::DirectConnection连接
    QObject::connect(net_socket_, &QTcpSocket::readyRead, this, &NetThread::GetNetPacket);
    QObject::connect(net_socket_, &QTcpSocket::disconnected, this, &NetThread::CloseNet);
    // 改变线程依附性
    this->moveToThread(net_thread_);
    // 开启线程
    net_thread_->start();
}

NetThread::~NetThread()
{
    // 释放p_NetConnectThread线程管理类资源
    if(net_thread_ != nullptr)
    {
        net_thread_->deleteLater();
        net_thread_ = nullptr;
    }
}

void NetThread::StartNet()
{
    // 向主界面发送相机连接成功事件
    qDebug() << "相机" << camera_id_ + 1 << "连接成功 IP地址为" << net_socket_->peerAddress().toString();
    MainFormEvent* mainformevent = new MainFormEvent(MainFormEvent::EventType_CarmeraLinkStatus, camera_id_);
    mainformevent->linkstatus_ = true;
    QCoreApplication::postEvent(FormFrame::GetInstance()->p_mainform_, mainformevent);

    // 让相机处于正常模式
//    QByteArray packet_data(1, 0x04);
//    DataFactory datafactory(MSG_NET_NORMAL, packet_data);
//    datafactory.SendDataPacketByNet(net_socket_);

//    QJsonAnalysis json("{}", false);
//    json.set("width", 128);
//    json.set("height", 128);
//    json.set("bpp", 24);
//    json.set("format", 1);
//    json.set("period", 20);
//    DataFactory datafactory1(MSG_NET_ALG_TEST_CONFIGURE, json.getJsonRawByte());
//    datafactory1.SendDataPacketByNet(net_socket_);
}

// 关闭线程与网络链接
void NetThread::CloseThread()
{
    qDebug() << "Thread End";
    if(SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] != nullptr)
    {
         SystemFrame::GetInstance()->p_netthread_buf_[camera_id_]->deleteLater();
         SystemFrame::GetInstance()->p_netthread_buf_[camera_id_] = nullptr;
    }
}

void NetThread::CloseNet()
{
    // 关闭TcpSocket连接（会触发QTcpSocket的disconnected信号）
    //net_socket_->close();
    // 退出线程（会触发QThread的finished信号，调用CloseThread）
    net_thread_->quit();
    net_thread_->wait();
}

// 通过网络发送数据包
void NetThread::SendNetPacket(DataPacketEnum datapacket_type, const QByteArray& databyte)
{
    DataFactory datafactory(datapacket_type, databyte);
    datafactory.SendDataPacketByNet(net_socket_);
}

void NetThread::SendNetJsonPacket(const QByteArray& databyte)
{
    DataFactory datafactory(MSG_NET_SEND_JSON, databyte);
    datafactory.SendDataPacketByNet(net_socket_);
    //net_socket_->write(databyte);
}

// 读取数据包
void NetThread::GetNetPacket()
{
    /* 变量定义 */
    qint32 needed_length = 0;                                  // 还需要读取的数据区长度
    qint32 head_pos = 0, data_pos = 0;                         // 帧头起始位置与数据起始位置
    qint32* packet_ptr = nullptr;                              // 指向数据区的指针
    qint32 socket_length = net_socket_->bytesAvailable();      // Socket缓冲区数据长度
    QByteArray socket_data = net_socket_->readAll();           // Socket缓冲区数据
    /* 从Socket缓冲区寻找有效数据包帧头 */
    if (is_netpacket_done_ == true)
    {
        for(qint32 i = 0; i < socket_length - 4; i++)
        {
            /* 找到有效数据包帧头 */
            if((socket_data.at(i) == 0x5A) && (socket_data.at(i+1) == 0x69) && \
               (socket_data.at(i+2) == 0x5A) && (socket_data.at(i+3) == 0x69))
            {
                /* 初始化相关变量 */
                //SystemFrame::GetInstance()->lock.lockForWrite();
                is_netpacket_done_ = false;
                head_pos = i;
                packet_ptr = (qint32*)(socket_data.data() + i );
                /* 填充帧头 */
                net_packet_.magic = packet_ptr[0];
                net_packet_.version = packet_ptr[1];
                net_packet_.type = packet_ptr[2];
                net_packet_.block = packet_ptr[3];
                net_packet_.length = packet_ptr[4];
                net_packet_.offset = packet_ptr[5];
                net_packet_.minid = packet_ptr[6];
                data_pos = head_pos + net_packet_.offset;
                /* 填充数据区 */
                net_packet_.data.clear();
                // 如果要读取的数据包data部分长度大于当前socket_data剩余字节就从下一次socket中读取
                if((net_packet_.length - net_packet_.offset) > (socket_length - data_pos))
                {
                    // 读取socket数据
                    net_packet_.data.append(socket_data.data() + data_pos, socket_length - data_pos);
                    // 已经读取到的数据长度
                    netpacket_preread_length_ = socket_length - data_pos;
                    // 可用数据读取完毕跳出
                    break;
                }
                else
                {
                    // 读取socket数据
                    net_packet_.data.append(socket_data.data() + data_pos, net_packet_.length - net_packet_.offset);
                    // for循环搜寻下一个数据包
                    i += net_packet_.length;
                    // 数据读取完毕并处理
                    is_netpacket_done_ = true;
                    netpacket_preread_length_ = 0;
                    HandleNetPacket();
                }
            }
        }
    }
    /* 从多个Socket缓冲区提取数据 */
    else
    {
        needed_length = net_packet_.length - net_packet_.offset - netpacket_preread_length_;
        // 如果还需要读取的数据包长度比当期Socket大则把该Socket完全读取
        if (needed_length >= socket_length)
        {
            net_packet_.data.append(socket_data.data(), socket_length);
            netpacket_preread_length_ += socket_length;
        }
        else
        {
            net_packet_.data.append(socket_data.data(), needed_length);
            netpacket_preread_length_ += needed_length;
        }
        // 判断数据是否完全读取完毕
        if (netpacket_preread_length_ == (net_packet_.length - net_packet_.offset))
        {
            //SystemFrame::GetInstance()->lock.unlock();
            is_netpacket_done_ = true;
            // 处理分发数据包
            //NOTIFY_CROSS_THREAD(1, net_packet_.GetDataPacketByteArray());
            HandleNetPacket();
        }
    }
}

void NetThread::HandleNetPacket()
{ 
    switch ((DataPacketEnum)net_packet_.minid)
    {
        case MSG_NET_SEND_JSON:
        {
            qDebug() << "相机" << camera_id_ + 1 << "json" << net_packet_.data;
        }
        case MSG_NET_ALG_IMAGE:
        {
//            qDebug() << net_timecal_.elapsed() << "ms";
//            net_timecal_.restart();
            qDebug() << "相机" << camera_id_ + 1 << "接收到图像数目:" << temp++;
            // 向UI线程投递检测结果事件
            MainFormEvent* mainformevent = new MainFormEvent(MainFormEvent::EventType_CarmeraButtonImage, camera_id_);
            mainformevent->checkresult_packet_ = net_packet_;
            QCoreApplication::postEvent(FormFrame::GetInstance()->p_mainform_, mainformevent);
            break;
        }
        // 1号相机检测结果
        case MSG_NET_ALG_RESULT:
        {
            // 向UI线程投递检测结果事件
            MainFormEvent* mainformevent = new MainFormEvent(MainFormEvent::EventType_CarmeraCheckResult, camera_id_);
            mainformevent->checkresult_packet_ = net_packet_;
            QCoreApplication::postEvent(FormFrame::GetInstance()->p_mainform_, mainformevent);
            break;
        }
        // 2 3号相机检测结果
        case MSG_NET_TOTAL_CNT:
        {
            // 向UI线程投递检测结果事件
            MainFormEvent* mainformevent = new MainFormEvent(MainFormEvent::EventType_CarmeraCheckResult, camera_id_);
            mainformevent->checkresult_packet_ = net_packet_;
            QCoreApplication::postEvent(FormFrame::GetInstance()->p_mainform_, mainformevent);
            break;
        }
        default:
            break;
    }
}
