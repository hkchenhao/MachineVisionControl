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
#include "Mgr/FormFrame.h"
#include "Mgr/SystemFrame.h"
#include "Net/NetServer.h"
#include "Net/NetThread.h"
#include "Utils/LoadDialog.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/SystemUtils.h"
#include "Utils/UserMsgBox.h"

MainForm::MainForm(QWidget *parent) : QWidget(parent), ui(new Ui::MainWidget)
{
    ui->setupUi(this);
    p_infochenckresform = new InfoCheckResForm(this);
    p_infochenckresform->setVisible(false);
    //p_infochenckresform->show();
    // 初始化相机label控件指针数组
//    buf_cameralinkstatus_[0] = ui->Label1_15;
//    buf_cameralinkstatus_[1] = ui->Label1_16;
//    buf_cameralinkstatus_[2] = ui->linkstatus3;
//    for(qint32 i = 0; i < CAMERA_NUM; i++)
//    {
//        //buf_cameralinkstatus_[i]->setText(QString("相机%1:").arg(i+1) + tr("<font color=red>%1</font>").arg("未连接"));
//    }
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
        // 相机连接状态
        case MainFormEvent::EventType_CarmeraLinkStatus:
        {
            if((customevent->cameraid_ >=0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                if(customevent->linkstatus_)
                    buf_cameralinkstatus_[customevent->cameraid_]->setText(QString("已连接"));
                else
                    buf_cameralinkstatus_[customevent->cameraid_]->setText(QString("未连接"));
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
                //image.setColorTable(vcolorTable);	//设置颜色表
                uchar* p_image = (uchar*)(customevent->checkresult_packet_.data.data() + 12);
                QImage image(p_image, 640, 480, 640*3, QImage::Format_RGB888);
                QImage image_scaled = image.scaled(p_result->plable_image_->width(), p_result->plable_image_->height());
                p_result->plable_image_->setPixmap(QPixmap::fromImage(image_scaled));
            }
            break;
        }
        // 检测结果
        case MainFormEvent::EventType_CarmeraCheckResult:
        {
            if((customevent->cameraid_ >=0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                ButtonCheckResult* p_result = &buf_buttoncheckresult_[customevent->cameraid_];
                p_result->total_num++;                
                // 显示检测结果
                qint32 check_result = customevent->checkresult_packet_.data.at(customevent->checkresult_packet_.data.size()-1);
                if(check_result == 0)
                {
                    // 显示图标
                    p_result->plable_resulticon_->setPixmap(QPixmap(":/icon/right_48px.png"));
                    // 计算合格率
                    p_result->right_num++;
                    p_result->pass_rate = (float)p_result->right_num / p_result->total_num * 100;
                }
                else if(check_result == 1)
                {
                    p_result->plable_resulticon_->setPixmap(QPixmap(":/icon/error_48px.png"));
                    p_result->wrong_num++;
                }
                else
                {
                    p_result->plable_resulticon_->setPixmap(QPixmap(":/icon/unknown_48px.png"));
                }
                // 更新每个相机检测结果
                p_result->plable_totalnum->setText(QString::number(p_result->total_num));
                p_result->plable_rightnum->setText(tr("<font color=green>%1</font>").arg(p_result->right_num));
                p_result->plable_wrongnum->setText(tr("<font color=red>%1</font>").arg(p_result->wrong_num));
                p_result->plable_passrate->setText(QString::number(p_result->pass_rate, 'f', 2) + "%");

            }
            // 更新总检测结果
            qint32 totalrightnum = 0, totalwrongnum = 0;
            for(qint32 i = 0; i < CAMERA_NUM; i++)
            {
                totalrightnum += buf_buttoncheckresult_[i].right_num;
                totalwrongnum += buf_buttoncheckresult_[i].wrong_num;
            }
            ui->Label2_zzps->setText(QString::number(totalrightnum));
            ui->Label2_zcps->setText(QString::number(totalwrongnum));
            //ui->Label2_zzpl->setText(QString::number(totalrightnum / (totalrightnum + totalwrongnum), 'f', 2) + "%");
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
    if(SystemFrame::GetInstance()->p_netserver_ == nullptr)
    {
        SystemFrame::GetInstance()->p_netserver_ = new NetServer();
        SystemFrame::GetInstance()->p_netserver_->StartNetServer();
    }
    ui->WorkStartButton->setEnabled(false);
    ui->WorkStartButton->setStyleSheet("border-style:none;padding:10px;border-radius:5px;color:#000000;background:#1ABC9C;");
    ui->WorkStopButton->setEnabled(true);
}
void MainForm::on_WorkStopButton_clicked()
{
    emit SignalDetectControl(false);
    //emit SignalNetClose();  // 发送网络关闭连接的信号
    ui->WorkStartButton->setEnabled(true);
    ui->WorkStartButton->setStyleSheet("");
    ui->WorkStopButton->setEnabled(false);
}
//void MainForm::on_FPSAdjustSlider_valueChanged(int value)
//{
//    ui->FPSLabel2->setText(QString("%1").arg(value));
//    // 发送帧率调节的命令
//    QJsonAnalysis json("{}", false);
//    json.set("width", 128);
//    json.set("height", 128);
//    json.set("bpp", 24);
//    json.set("format", 1);
//    json.set("period", (qint32)(1000 / value));
//    emit SignalNetSendPacket(MSG_NET_ALG_TEST_CONFIGURE, json.getJsonRawByte());
//}

// [slot函数]程序关闭控件
void MainForm::on_pushButton_AppClose_clicked()
{ 
    UserTextMsgBox* msg = new UserTextMsgBox("相机1收到纽扣配置信息文件", 100, this);
    msg->show();
    //qApp->quit();
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


void MainForm::on_Button1_slxx_clicked()
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
