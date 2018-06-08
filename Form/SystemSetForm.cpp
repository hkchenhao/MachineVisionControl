#include "SystemSetForm.h"
#include "ui_SystemSetForm.h"
#include "Mgr/FormFrame.h"
#include "Net/CanBusMgr.h"
#include "Utils/ButtonInfo.h"
#include "Utils/WidgetStyle.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/UserMsgBox.h"
#include "Utils/SystemConstants.h"

SystemSetForm::SystemSetForm(QWidget* parent) : QWidget(parent), ui(new Ui::SystemSetWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);
    QString path = QCoreApplication::applicationDirPath() + QString("/SystemConfig/");
    if(!path.isEmpty())
    {
        p_cameracalibrationJsonInfo = new QJsonAnalysis(path + "Camera1Config.ini", true);
        p_lightconfigJsonInfo = new QJsonAnalysis(path + "setting.ini", true);
        ui->lineEdit_58->setText(p_cameracalibrationJsonInfo->getString("calibration.real_dia"));
        ui->lineEdit_59->setText(p_cameracalibrationJsonInfo->getString("calibration.measured_dia"));
        ui->lineEdit_60->setText(p_cameracalibrationJsonInfo->getString("calibration.cali_factor"));

        ui->box_cz_13->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 1"));
        ui->box_cz_14->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 2"));
        ui->box_cz_15->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 3"));
        ui->box_cz_16->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 4"));
        ui->box_cz_17->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 5"));
        ui->box_cz_18->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 6"));
        ui->box_cz_19->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 7"));
        ui->box_cz_20->setCurrentIndex(p_lightconfigJsonInfo->getInt("light src drv setting.channel 8"));
    }
}

SystemSetForm::~SystemSetForm()
{
    delete ui;
    if(p_cameracalibrationJsonInfo) { delete p_cameracalibrationJsonInfo; p_cameracalibrationJsonInfo = nullptr;}
    if(p_lightconfigJsonInfo) { delete p_lightconfigJsonInfo; p_lightconfigJsonInfo = nullptr;}
}

// [slot函数]退出按键
void SystemSetForm::on_pushButton_Esc_clicked()
{
    FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_systemsetform_);
    delete FormFrame::GetInstance()->p_systemsetform_;
    FormFrame::GetInstance()->p_systemsetform_ = nullptr;
    FormFrame::GetInstance()->formstacked_id_.systemsetform_id = -1;
    FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
}

// [slot函数]控件槽函数-转盘电机控制
void SystemSetForm::on_pushButton_Revise_23_clicked()
{
    CanBusMgr::GetInstance()->SetMotorSpeed(ui->lineEdit_48->text().toInt());
    ui->lable_135->setText("启动");
}
void SystemSetForm::on_pushButton_Revise_24_clicked()
{
    CanBusMgr::GetInstance()->SetMotorSpeed(0);
    ui->lable_135->setText("停止");
}
void SystemSetForm::on_horizontalSlider_3_valueChanged(int value)
{
    ui->lineEdit_48->setText(QString("%1").arg(value));
}
void SystemSetForm::on_pushButton_Revise_25_clicked()
{
    qint32 num = ui->lineEdit_48->text().toInt();
    ui->lineEdit_48->setText(QString("%1").arg(num + 1));
    ui->horizontalSlider_3->setValue(num + 1);
}
void SystemSetForm::on_pushButton_Revise_26_clicked()
{
    qint32 num = ui->lineEdit_48->text().toInt();
    ui->lineEdit_48->setText(QString("%1").arg(num - 1));
    ui->horizontalSlider_3->setValue(num - 1);
}
// [slot函数]控件槽函数-输送电机控制
void SystemSetForm::on_pushButton_Revise_18_clicked()
{
    CanBusMgr::GetInstance()->SetConveyerBelt(true);
    ui->lable_133->setText("启动");
}
void SystemSetForm::on_pushButton_Revise_19_clicked()
{
    CanBusMgr::GetInstance()->SetConveyerBelt(false);
    ui->lable_133->setText("停止");
}

// [slot函数]控件槽函数-选项卡改变
void SystemSetForm::on_tabWidget_currentChanged(int index)
{
    switch(index)
    {
        case 4:
            ui->pushButton_Revise->setEnabled(false);
            ui->pushButton_Update->setEnabled(true);
            ui->pushButton_Aply->setEnabled(true);
            ui->pushButton_Save->setEnabled(true);
            break;
        default:
            break;
    }
}
