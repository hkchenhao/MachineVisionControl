#include "MainForm.h"
#include "ui_MainForm.h"
#include "Data/DataPacket.h"
#include "Data/DataPacketEnum.h"
#include "Event/MainFormEvent.h"
#include "Event/NotificationCenter.h"
#include "Form/ButtonSelForm.h"
#include "Form/MachineLearnForm.h"
#include "Form/StatisticalForm.h"
#include "Mgr/FormFrame.h"
#include "Mgr/SystemFrame.h"
#include "Net/NetServer.h"
#include "Net/NetThread.h"
#include "Utils/LoadDialog.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/SystemUtils.h"

MainForm::MainForm(QWidget *parent) : QWidget(parent), ui(new Ui::MainWidget)
{
    ui->setupUi(this);
    // 初始化相机label控件指针数组
    buf_cameralinkstatus_[0] = ui->linkstatus1;
    buf_cameralinkstatus_[1] = ui->linkstatus2;
    buf_cameralinkstatus_[2] = ui->linkstatus3;
    for(qint32 i = 0; i < CAMERA_NUM; i++)
    {
        buf_cameralinkstatus_[i]->setText(QString("相机%1:").arg(i+1) + tr("<font color=red>%1</font>").arg("未连接"));
    }
    // 纽扣检测结果结构体初始化
    for(qint32 i = 0; i < CAMERA_NUM; i++)
    {
        buf_buttoncheckresult_[i].total_num = 0;
        buf_buttoncheckresult_[i].right_num = 0;
        buf_buttoncheckresult_[i].wrong_num = 0;
        buf_buttoncheckresult_[i].pass_rate = 0.00f;
        buf_buttoncheckresult_[i].fps_rate = 0.00f;
    }
    buf_buttoncheckresult_[0].plable_image_ = ui->image1;
    buf_buttoncheckresult_[0].plable_resulticon_ = ui->checkresult1;
//    buf_buttoncheckresult_[0].plable_totalnum = ui->TotalNumLabel1;
//    buf_buttoncheckresult_[0].plable_rightnum = ui->RightNumLabel1;
//    buf_buttoncheckresult_[0].plable_wrongnum = ui->WrongNumLabel1;
//    buf_buttoncheckresult_[0].plable_passrate = ui->PassRateLabel1;
//    buf_buttoncheckresult_[0].plable_fpsrate = ui->FpsRateLabel1;

    buf_buttoncheckresult_[1].plable_image_ = ui->image2;
    buf_buttoncheckresult_[1].plable_resulticon_ = ui->checkresult2;
//    buf_buttoncheckresult_[1].plable_totalnum = ui->TotalNumLabel2;
//    buf_buttoncheckresult_[1].plable_rightnum = ui->RightNumLabel2;
//    buf_buttoncheckresult_[1].plable_wrongnum = ui->WrongNumLabel2;
//    buf_buttoncheckresult_[1].plable_passrate = ui->PassRateLabel2;
//    buf_buttoncheckresult_[1].plable_fpsrate = ui->FpsRateLabel2;

    buf_buttoncheckresult_[2].plable_image_ = ui->image3;
    buf_buttoncheckresult_[2].plable_resulticon_ = ui->checkresult3;
//    buf_buttoncheckresult_[2].plable_totalnum = ui->TotalNumLabel3;
//    buf_buttoncheckresult_[2].plable_rightnum = ui->RightNumLabel3;
//    buf_buttoncheckresult_[2].plable_wrongnum = ui->WrongNumLabel3;
//    buf_buttoncheckresult_[2].plable_passrate = ui->PassRateLabel3;
//    buf_buttoncheckresult_[2].plable_fpsrate = ui->FpsRateLabel3;

    // 检测结果TableWidget控件初始化
    /*
    ui->ResultTableWidget->setFocusPolicy(Qt::NoFocus);                                     // 初始化时失去焦点
    ui->ResultTableWidget->setEditTriggers(QAbstractItemView::NoEditTriggers);              // 禁止编辑单元格 
    ui->ResultTableWidget->horizontalHeader()->setSectionResizeMode(QHeaderView::Stretch);  // 自适应宽度
    ui->ResultTableWidget->verticalHeader()->setSectionResizeMode(QHeaderView::Stretch);
    ui->ResultTableWidget->horizontalHeader()->setSectionsClickable(false);                 // 设置表头不可点击（默认点击后进行排序）
    ui->ResultTableWidget->verticalHeader()->setSectionsClickable(false);
    ui->ResultTableWidget->horizontalHeader()->setMinimumHeight(40);                        // 表头高度设置（水平-垂直）
    ui->ResultTableWidget->verticalHeader()->setMinimumWidth(65);
    // 设置表头字体
    ui->ResultTableWidget->horizontalHeader()->setStyleSheet("QHeaderView::section{font:16pt 'Yahei Mono'}");
    ui->ResultTableWidget->verticalHeader()->setStyleSheet("QHeaderView::section{font:16pt 'Yahei Mono'}");

    ui->ResultTableWidget->setItem(0, 0, new QTableWidgetItem(QString("888")));

    ui->ResultTableWidget->itemAt(1, 0)->setText(QString("589"));
    ui->ResultTableWidget->itemAt(1, 1)->setText(QString::number(20));
    ui->ResultTableWidget->itemAt(2, 2)->setText(QString::number(30));
    ui->ResultTableWidget->itemAt(1, 3)->setText(QString::number(0.569f, 'f', 2));

    ui->ResultTableWidget->show();*/
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
        // 处理相机连接状态
        case MainFormEvent::EventType_CarmeraLinkStatus:
        {
            if((customevent->cameraid_ >=0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                if(!customevent->linkstatus_)
                    buf_cameralinkstatus_[customevent->cameraid_]->setText(QString("相机%1:").arg(customevent->cameraid_+1) +\
                                                                           QObject::tr("<font color=red>%1</font>").arg("未连接"));
                else
                    buf_cameralinkstatus_[customevent->cameraid_]->setText(QString("相机%1:").arg(customevent->cameraid_+1) +\
                                                                           QObject::tr("<font color=green>%1</font>").arg("已连接"));
            }
            break;
        }
        case MainFormEvent::EventType_CarmeraCheckResult:
        {
            if((customevent->cameraid_ >=0 ) && (customevent->cameraid_ <= (CAMERA_NUM - 1)))
            {
                ButtonCheckResult* p_result = &buf_buttoncheckresult_[customevent->cameraid_];
                p_result->total_num++;
                // 显示检测图像
                //image.setColorTable(vcolorTable);	//设置颜色表
                uchar* p_image = (uchar*)(customevent->checkresult_packet_.data.data() + 12);
                QImage image(p_image, 128, 128, 128*3, QImage::Format_RGB888);
                QImage image_scaled = image.scaled(p_result->plable_image_->width(), p_result->plable_image_->height());
                p_result->plable_image_->setPixmap(QPixmap::fromImage(image_scaled));
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
                // 更新检测结果QTableWidget
                p_result->plable_totalnum->setText(QString("总数目:%1").arg(p_result->total_num));
                p_result->plable_rightnum->setText(QString("合格数目:") + tr("<font color=green>%1</font>").arg(p_result->right_num));
                p_result->plable_wrongnum->setText(QString("缺陷数目:") + tr("<font color=red>%1</font>").arg(p_result->wrong_num));
                p_result->plable_passrate->setText(QString("合格率:") + QString::number(p_result->pass_rate, 'f', 2) + "%");

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
void MainForm::on_FPSAdjustSlider_valueChanged(int value)
{
    ui->FPSLabel2->setText(QString("%1").arg(value));
    // 发送帧率调节的命令
    QJsonAnalysis json("{}", false);
    json.set("width", 128);
    json.set("height", 128);
    json.set("bpp", 24);
    json.set("format", 1);
    json.set("period", (qint32)(1000 / value));
    emit SignalNetSendPacket(MSG_NET_ALG_TEST_CONFIGURE, json.getJsonRawByte());
}

// [slot函数]程序关闭控件
void MainForm::on_pushButton_AppClose_clicked()
{
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

//void MainForm::on_pushButton_Learn_clicked()
//{

//}
