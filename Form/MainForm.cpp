#include "MainForm.h"
#include "ui_MainForm.h"
#include "Data/DataPacket.h"
#include "Data/DataPacketEnum.h"
#include "Event/MainFormEvent.h"
#include "Event/NotificationCenter.h"
#include "Form/ButtonSelForm.h"
#include "Form/MachineLearnForm.h"
#include "Form/StatisticalForm.h"
#include "Form/InfoCheckResForm.h"
#include "Form/InfoSizeForm.h"
#include "Form/SystemSetForm.h"
#include "Form/FileManagerForm.h"
#include "Form/HelpForm.h"
#include "Mgr/FormFrame.h"
#include "Mgr/SystemFrame.h"
#include "Net/NetServer.h"
#include "Net/NetThread.h"
#include "Net/CanBusMgr.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/SystemUtils.h"
#include "Utils/UserMsgBox.h"

#define TESTCOUNT 100
qint32 i = 0, j = 0, k = 0;
QTime net_timecal_[CAMERA_NUM];
qint32 fps_buf_[CAMERA_NUM] = {0};
double fpsave[CAMERA_NUM] = {0};

// 相机统计信息
extern ButtonStatisticalInfo CameraStatisticalInfo[CAMERA_NUM];
qint32 buf1[16] = {0};
qint32 buf2[16] = {0};

MainForm::MainForm(QWidget *parent) : QWidget(parent), ui(new Ui::MainWidget)
{
    ui->setupUi(this);
    p_buttonjson = nullptr;
    buttonname = "";
    p_infochenckresform = new InfoCheckResForm(this);
    p_infosizeform = new InfoSizeForm(this);
    p_infochenckresform->setVisible(false);
    p_infosizeform->setVisible(false);
    //p_infochenckresform->show();
    // 初始化CAN总线
//    CanBusMgr::GetInstance();
//    CanBusMgr::GetInstance()->SetCanID(561);
//    CanBusMgr::GetInstance()->SetMotorSpeed(600);
    // 初始化相机label控件指针数组
    buf_cameralinkstatus_[0] = ui->Label1_1;
    buf_cameralinkstatus_[1] = ui->Label1_2;
    buf_cameralinkstatus_[2] = ui->Label1_3;
    for(qint32 i = 0; i < CAMERA_NUM; i++)
        buf_cameralinkstatus_[i]->setText(QString("相机%1:").arg(i+1) + tr("<font color=red>%1</font>").arg("未连接"));
    // 纽扣检测结果结构体初始化
    for(qint32 i = 0; i < CAMERA_NUM; i++)
    {
        buf_buttoncheckresult_[i].total_num = 0;
        buf_buttoncheckresult_[i].right_num = 0;
        buf_buttoncheckresult_[i].wrong_num = 0;
        buf_buttoncheckresult_[i].pass_rate = 0.00f;
        buf_buttoncheckresult_[i].fps_rate = 0.00f;
    }
    buf_buttoncheckresult_[0].plable_image_ = ui->Label1_Image1;
    buf_buttoncheckresult_[0].plable_resulticon_ = ui->Label1_ResIcon1;
//    buf_buttoncheckresult_[0].plable_rightnum = ui->Label1_zps1;
//    buf_buttoncheckresult_[0].plable_wrongnum = ui->Label1_cps1;
//    buf_buttoncheckresult_[0].plable_passrate = ui->Label1_zpl1;

    buf_buttoncheckresult_[1].plable_image_ = ui->Label1_Image2;
    buf_buttoncheckresult_[1].plable_resulticon_ = ui->Label1_ResIcon2;
//    buf_buttoncheckresult_[1].plable_rightnum = ui->Label1_zps2;
//    buf_buttoncheckresult_[1].plable_wrongnum = ui->Label1_cps2;
//    buf_buttoncheckresult_[1].plable_passrate = ui->Label1_zpl2;

    buf_buttoncheckresult_[2].plable_image_ = ui->Label1_Image3;
    buf_buttoncheckresult_[2].plable_resulticon_ = ui->Label1_ResIcon3;
//    buf_buttoncheckresult_[2].plable_rightnum = ui->Label1_zps3;
//    buf_buttoncheckresult_[2].plable_wrongnum = ui->Label1_cps3;
//    buf_buttoncheckresult_[2].plable_passrate = ui->Label1_zpl3;

    // 启动TCP服务器监听
}

MainForm::~MainForm()
{
    delete ui;
}

// 设置不同用户的访问权限
void MainForm::SetUserAccessPermission(qint32 user_id)
{
    switch (user_id)
    {
        case 0:
            ui->pushButton_Manage->setEnabled(false);
            ui->pushButton_Learn->setEnabled(false);
            ui->pushButton_Statistics->setEnabled(false);
            ui->pushButton_Settings->setEnabled(false);
            break;
        case 1:
            ui->pushButton_Learn->setEnabled(false);
            ui->pushButton_Settings->setEnabled(false);
            break;
        default:
            break;
    }
}

// 虚函数用于处理自定义事件
void MainForm::customEvent(QEvent* event)
{
    MainFormEvent* customevent = static_cast<MainFormEvent*>(event);
    switch (customevent->eventtype_)
    {
        // 纽扣json配置文件名称与图片
        case MainFormEvent::EventType_ButtonSelectedResult:
        {
            buttonname = customevent->button_id_;
            SystemUtils::SetNameForButtonConfigFile(buttonname);
            ui->label_ButtonId->setText(buttonname);
            QImage buttonimage(SystemUtils::GetPathForButtonConfigFile() + buttonname + "/" + buttonname + ".jpg");
            QImage buttonimagescaled = buttonimage.scaled(ui->label_ButtonImage->width(), ui->label_ButtonImage->height());
            ui->label_ButtonImage->setPixmap(QPixmap::fromImage(buttonimagescaled));
            // 解析json文件
            if(p_buttonjson != nullptr)
            {
                delete p_buttonjson;
                p_buttonjson = nullptr;
            }
            QString pathname(SystemUtils::GetPathForButtonConfigFile() + buttonname + "/" + buttonname + ".ini");
            p_buttonjson = new QJsonAnalysis(pathname, true);
            p_infosizeform->Show(p_buttonjson);
        }
        // 相机连接状态
        case MainFormEvent::EventType_CarmeraLinkStatus:
        {
            if((customevent->cameraid_ >=0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                if(customevent->linkstatus_)
                    buf_cameralinkstatus_[customevent->cameraid_]->setText(QString("相机%1:").arg(customevent->cameraid_+1) + tr("<font color=green>%1</font>").arg("已连接"));
                else
                    buf_cameralinkstatus_[customevent->cameraid_]->setText(QString("相机%1:").arg(customevent->cameraid_+1) + tr("<font color=red>%1</font>").arg("未连接"));
            }
            break;
        }
        // 纽扣图像
        case MainFormEvent::EventType_CarmeraButtonImage:
        {
            if((customevent->cameraid_ >=0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                ButtonCheckResult* p_result = &buf_buttoncheckresult_[customevent->cameraid_];
                //p_result->total_num++;
                // 显示检测图像
                uchar* p_image = (uchar*)(customevent->checkresult_packet_.data.data() + 8);
                QImage image(p_image, 128, 128, 128*3, QImage::Format_RGB888);
                QImage image_scaled = image.scaled(p_result->plable_image_->width(), p_result->plable_image_->height());
                p_result->plable_image_->setPixmap(QPixmap::fromImage(image_scaled));
            }
            break;
        }
        case MainFormEvent::EventType_CarmeraTest:
        {
            if((customevent->cameraid_ >=0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                ButtonCheckResult* p_result = &buf_buttoncheckresult_[customevent->cameraid_];
                //p_result->total_num++;
                uchar* p_image = (uchar*)(customevent->checkresult_packet_.data.data() + 12);
                QImage image(p_image, 128, 128, 128*3, QImage::Format_RGB888);
                QImage image_scaled = image.scaled(p_result->plable_image_->width(), p_result->plable_image_->height());
                p_result->plable_image_->setPixmap(QPixmap::fromImage(image_scaled));

                double fps = 0;
                int time = net_timecal_[customevent->cameraid_].msecsSinceStartOfDay();
                int time1 = time - fps_buf_[customevent->cameraid_];
                if(time1 != 0)
                    fps = 1000.0 / time1;
                qDebug() << "相机" << customevent->cameraid_ + 1 << "ms:" << time - fps_buf_[customevent->cameraid_];
                fps_buf_[customevent->cameraid_] = time;
                net_timecal_[customevent->cameraid_].restart();

                if(customevent->cameraid_ == 0)
                {
                    fpsave[0] += fps;
                    i++;
                    if(i == TESTCOUNT)
                    {
                        //ui->Label1_7->setText(QString("%1").arg(QString::number(fpsave[0] / (TESTCOUNT),'f',2)));
                        i = 0;
                        fpsave[0] = 0;
                    }
                }

                else if(customevent->cameraid_ == 1)
                {
                    fpsave[1] += fps;
                    j++;
                    if(j == TESTCOUNT)
                    {
                        //ui->Label1_8->setText(QString("%1").arg(QString::number(fpsave[1] / (TESTCOUNT + 1),'f',2)));
                        j = 0;
                        fpsave[1] = 0;
                    }
                }
                if(customevent->cameraid_ == 2)
                {
                    fpsave[2] += fps;
                    k++;
                    if(k == TESTCOUNT)
                    {
                        //ui->Label1_9->setText(QString("%1").arg(QString::number(fpsave[2] / (TESTCOUNT + 1),'f',2)));
                        k = 0;
                        fpsave[2] = 0;
                    }
                }
            }
            break;
        }
        // 检测结果
        case MainFormEvent::EventType_CarmeraCheckResult:
        {
            if((customevent->cameraid_ >= 0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                ButtonCheckResult* p_result = &buf_buttoncheckresult_[customevent->cameraid_];
                p_result->total_num++;                
                // 显示相机检测结果图片
                qint32 check_result = customevent->checkresult_packet_.data.at(0);
                if(check_result == 0)
                {
                    p_result->plable_resulticon_->setPixmap(QPixmap(":/icon/right_48px.png"));
                    p_result->right_num++;
                    //p_result->pass_rate = (float)p_result->right_num / p_result->total_num * 100;
                }
                else if(check_result == 1)
                {
                    p_result->plable_resulticon_->setPixmap(QPixmap(":/icon/error_48px.png"));
                    p_result->wrong_num++;
                }
                else
                    p_result->plable_resulticon_->setPixmap(QPixmap(":/icon/unknown_48px.png"));

                // 显示相机1统计信息
                char* ptr = customevent->checkresult_packet_.data.data() + 1;
                if(customevent->cameraid_ == 0)
                {
                    CameraStatisticalInfo[0].right_num = *((qint32*)(ptr + 1));
                    CameraStatisticalInfo[0].wrong_num = *((qint32*)(ptr + 6));
                    for(qint32 i = 0; i < 16; i++)
                    {
                        qint32 pos = *(ptr + 10 + i * 5) - 0x21;
                        CameraStatisticalInfo[0].wrongtypebuf[pos] = *((qint32*)(ptr + 11 + i * 5));
                    }
                    // 尺寸信息
                    if(p_infosizeform->isVisible())
                    {
                        QByteArray jsonbyte(ptr + 10 + 16 * 5);
                        QJsonAnalysis jsonres(jsonbyte);
                        buf2[0] = jsonres.getInt("button.outDia");
                        buf2[1] = jsonres.getInt("button.outDiaDev");
                        buf2[2] = jsonres.getInt("button.holeDia1");
                        buf2[3] = jsonres.getInt("button.holeDia2");
                        buf2[4] = jsonres.getInt("button.holeDia3");
                        buf2[5] = jsonres.getInt("button.holeDia4");
                        buf2[6] = jsonres.getInt("button.holeDist1");
                        buf2[7] = jsonres.getInt("button.holeDist2");
                        buf2[8] = jsonres.getInt("button.holeDist3");
                        buf2[9] = jsonres.getInt("button.holeDist4");
                        buf2[10] = jsonres.getInt("button.centerDrift");
                        buf2[11] = jsonres.getInt("button.judgeResult");
                        p_infosizeform->Update(buf2);
                    }
//                    CameraStatisticalInfo[0].jsoninfo = QJsonAnalysis(jsonbyte);
                }
                // 显示相机2/3统计信息
                else
                {
                    qint32 datalen = customevent->checkresult_packet_.length - 28 - 11;    // 缺陷类型长度
                    qint32 len = datalen / 5;
                    char* ptr = customevent->checkresult_packet_.data.data() + 1;
                    CameraStatisticalInfo[customevent->cameraid_].right_num = *((qint32*)(ptr + 1));
                    CameraStatisticalInfo[customevent->cameraid_].wrong_num = *((qint32*)(ptr + 6));
                    for(qint32 i = 0; i < len; i++)
                    {
                        qint32 pos = *(ptr + 10 + i * 5) - 0x21;
                        CameraStatisticalInfo[customevent->cameraid_].wrongtypebuf[pos] = *((qint32*)(ptr + 11 + i * 5));
                    }
                }

                // 更新每个相机检测结果
//                p_result->plable_totalnum->setText(QString::number(p_result->total_num));
//                p_result->plable_rightnum->setText(tr("<font color=green>%1</font>").arg(p_result->right_num));
//                p_result->plable_wrongnum->setText(tr("<font color=red>%1</font>").arg(p_result->wrong_num));
//                p_result->plable_passrate->setText(QString::number(p_result->pass_rate, 'f', 2) + "%");

            }

            // 更新总检测结果
            quint32 totalrightnum = CameraStatisticalInfo[0].right_num;
            quint32 totalwrongnum = CameraStatisticalInfo[0].wrong_num + CameraStatisticalInfo[1].wrong_num +\
                                    CameraStatisticalInfo[2].wrong_num;
            ui->Label2_zzps->setText(QString::number(totalrightnum));
            ui->Label2_zcps->setText(QString::number(totalwrongnum));
            ui->Label2_zs->setText(QString::number(totalrightnum + totalwrongnum));
            // 数量信息
            if(p_infochenckresform->isVisible())
            {
                buf1[0] = CameraStatisticalInfo[0].right_num;
                buf1[1] = CameraStatisticalInfo[0].wrong_num;
                buf1[2] = CameraStatisticalInfo[1].right_num;
                buf1[3] = CameraStatisticalInfo[1].wrong_num;
                buf1[4] = CameraStatisticalInfo[2].right_num;
                buf1[5] = CameraStatisticalInfo[2].wrong_num;
                p_infochenckresform->Update(buf1);
            }

            break;
        }
        default:
            break;
    }
}

// [slot函数]纽扣选择组控件
void MainForm::on_ButtonSelectButton_clicked()
{
    if(FormFrame::GetInstance()->formstacked_id_.buttonselform_id == -1)
    {
        FormFrame::GetInstance()->p_buttonselform_ = new ButtonSelForm;
        qint32 id = FormFrame::GetInstance()->p_formstacked_->addWidget(FormFrame::GetInstance()->p_buttonselform_);
        FormFrame::GetInstance()->formstacked_id_.buttonselform_id = id;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(id);
    }
}

// [slot函数]检测控制组控件
void MainForm::on_WorkStartButton_clicked()
{
    if(buttonname == "")
    {
        UserTextMsgBox* msg = new UserTextMsgBox("请先选择纽扣...", 768 / 2, this);
        msg->show();
        return;
    }
    ui->WorkStartButton->setEnabled(false);
    ui->WorkStartButton->setStyleSheet("border-style:none;padding:10px;border-radius:5px;color:#000000;background:#1ABC9C;");
    ui->WorkStopButton->setEnabled(true);
    // 发送纽扣配置json文件
//    QString pathname(SystemUtils::GetPathForButtonConfigFile() + buttonname + "/" + buttonname + ".ini");
//    p_buttonjson = new QJsonAnalysis(pathname, true);
    emit SignalNetSendJson(p_buttonjson->getJsonRawByte());
    // 禁止选择纽扣配置文件
    ui->ButtonSelectButton->setEnabled(false);
}
void MainForm::on_WorkStopButton_clicked()
{
    emit SignalDetectControl(false);
    //emit SignalNetClose();  // 发送网络关闭连接的信号
    ui->WorkStartButton->setEnabled(true);
    ui->WorkStartButton->setStyleSheet("");
    ui->WorkStopButton->setEnabled(false);
    ui->ButtonSelectButton->setEnabled(true);
}

// [slot函数]程序关闭控件
void MainForm::on_pushButton_AppClose_clicked()
{ 
//    UserTextMsgBox* msg = new UserTextMsgBox("相机1收到纽扣配置信息文件", 100, this);
//    msg->show();
//    UserLoadingMsgBox* msg = new UserLoadingMsgBox("纽扣搜索中，请稍后......", 100, this);
//    msg->show();
    qApp->quit();
    //QProcess::execute("sudo shutdown -h now");
}

// [slot函数]机器学习界面slot函数
void MainForm::on_pushButton_Learn_clicked()
{
    if(FormFrame::GetInstance()->formstacked_id_.machinelearnform_id == -1)
    {
        FormFrame::GetInstance()->p_machinelearnform_ = new MachineLearnForm;
        qint32 id = FormFrame::GetInstance()->p_formstacked_->addWidget(FormFrame::GetInstance()->p_machinelearnform_);
        FormFrame::GetInstance()->formstacked_id_.machinelearnform_id = id;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(id);
    }
}

// [slot函数]统计分析界面slot函数
void MainForm::on_pushButton_Statistics_clicked()
{
    if(FormFrame::GetInstance()->formstacked_id_.statisticalform_id == -1)
    {
        FormFrame::GetInstance()->p_statisticalform_ = new StatisticalForm;
        qint32 id = FormFrame::GetInstance()->p_formstacked_->addWidget(FormFrame::GetInstance()->p_statisticalform_);
        FormFrame::GetInstance()->formstacked_id_.statisticalform_id = id;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(id);
    }
}

// [slot函数]系统设置界面slot函数
void MainForm::on_pushButton_Settings_clicked()
{
    if(FormFrame::GetInstance()->formstacked_id_.systemsetform_id == -1)
    {
        FormFrame::GetInstance()->p_systemsetform_ = new SystemSetForm;
        qint32 id = FormFrame::GetInstance()->p_formstacked_->addWidget(FormFrame::GetInstance()->p_systemsetform_);
        FormFrame::GetInstance()->formstacked_id_.systemsetform_id = id;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(id);
    }
}

void MainForm::on_pushButton_Manage_clicked()
{
    if(FormFrame::GetInstance()->formstacked_id_.filemanagerform_id == -1)
    {
        FormFrame::GetInstance()->p_filemanagerform_ = new FileManagerForm;
        qint32 id = FormFrame::GetInstance()->p_formstacked_->addWidget(FormFrame::GetInstance()->p_filemanagerform_);
        FormFrame::GetInstance()->formstacked_id_.filemanagerform_id = id;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(id);
    }
}

void MainForm::on_pushButton_Help_clicked()
{
    if(FormFrame::GetInstance()->formstacked_id_.helpform_id == -1)
    {
        FormFrame::GetInstance()->p_helpform_ = new HelpForm;
        qint32 id = FormFrame::GetInstance()->p_formstacked_->addWidget(FormFrame::GetInstance()->p_helpform_);
        FormFrame::GetInstance()->formstacked_id_.helpform_id = id;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(id);
    }
}

void MainForm::on_Button_ccxx_clicked()
{
    if(!p_infosizeform->isVisible())
        p_infosizeform->setVisible(true);
}

void MainForm::on_Button_slxx_clicked()
{
//    LoadDialog* ld = new LoadDialog(this);
//    ld->show();
    if(p_infochenckresform->isVisible())
        qDebug() << "is visible\r\n";
    else
    {
        p_infochenckresform->setVisible(true);
        qDebug() << "ok\r\n";
    }
}

void MainForm::on_Button_clicked()
{
    if(SystemFrame::GetInstance()->p_netserver_ == nullptr)
    {
        SystemFrame::GetInstance()->p_netserver_ = new NetServer();
        SystemFrame::GetInstance()->p_netserver_->StartNetServer();
    }
}


void MainForm::on_Button_test_clicked()
{
    unsigned char databuf[8] = {0x01, 0x08, 0x25, 0xF8, 0x86, 0x74, 0x12, 0x0A};
    CanBusMgr::GetInstance()->SendCanData(0x0247, databuf, 8);

//    QByteArray packet_data(1, 0x04);
//    emit SignalNetSendPacket(MSG_NET_NORMAL, packet_data);
//    QJsonAnalysis json("{}", false);
//    json.set("width", 128);
//    json.set("height", 128);
//    json.set("bpp", 24);
//    json.set("format", 1);
//    json.set("period", 100);
//    emit SignalNetSendPacket(MSG_NET_ALG_TEST_CONFIGURE, json.getJsonRawByte());
}
