#include "MachineLearnForm.h"
#include "ui_MachineLearnForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/ButtonInfo.h"
#include "Utils/WidgetStyle.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/UserMsgBox.h"

MachineLearnForm::MachineLearnForm(QWidget* parent, bool isedit, QJsonAnalysis* pbuttoninfo) :
                                   QWidget(parent), ui(new Ui::MachineLearnWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);
    isEdit_ = isedit;
    p_buttonInfo = pbuttoninfo;
    if(isEdit_ == true)
        ui->pushButton_Revise->setEnabled(false);
}

MachineLearnForm::~MachineLearnForm()
{
    delete ui;
}


// [slot函数]保存按键
void MachineLearnForm::on_pushButton_Save_clicked()
{
    if(isEdit_ == false)
    {
        // 创建JSON文件
        QString jsonparentstr;
        QJsonAnalysis infojson("{}", false);
        // JSON时间信息
        infojson.set("time", QDate::currentDate().toString("yyyy-MM-dd"));
        // JSON纽扣正面基本信息
        jsonparentstr = "infoFront.";
        infojson.set(jsonparentstr+"materialF", ButtonMaterialStrEnBuf[ui->box_cz_0->currentIndex()]);
        infojson.set(jsonparentstr+"sizeF", ui->line_cc->text());
        infojson.set(jsonparentstr+"shapeF", ButtonShapeStrEnBuf[ui->box_xz_0->currentIndex()]);
        infojson.set(jsonparentstr+"holeNumF", ButtonHoleNumStrEnBuf[ui->box_xks_0->currentIndex()]);
        infojson.set(jsonparentstr+"lightF", ButtonLightStrEnBuf[ui->box_tmx_0->currentIndex()]);
        infojson.set(jsonparentstr+"patternF", ButtonPatternStrEnBuf[ui->box_hs_0->currentIndex()]);
        infojson.set(jsonparentstr+"colorF", ButtonColorStrEnBuf[ui->box_zs_0->currentIndex()]);
        // JSON纽扣反面基本信息
        jsonparentstr = "infoBack.";
        infojson.set(jsonparentstr+"materialB", ButtonMaterialStrEnBuf[ui->box_cz_1->currentIndex()]);
        infojson.set(jsonparentstr+"sizeB", ui->line_cc->text());
        infojson.set(jsonparentstr+"shapeB", ButtonShapeStrEnBuf[ui->box_xz_1->currentIndex()]);
        infojson.set(jsonparentstr+"holeNumB", ButtonHoleNumStrEnBuf[ui->box_xks_1->currentIndex()]);
        infojson.set(jsonparentstr+"lightB", ButtonLightStrEnBuf[ui->box_tmx_1->currentIndex()]);
        infojson.set(jsonparentstr+"patternB", ButtonPatternStrEnBuf[ui->box_hs_1->currentIndex()]);
        infojson.set(jsonparentstr+"colorB", ButtonColorStrEnBuf[ui->box_zs_1->currentIndex()]);

        infojson.save("info.json");
    }

}

// [slot函数]修改按键
void MachineLearnForm::on_pushButton_Revise_clicked()
{

}

// [slot函数]退出按键
void MachineLearnForm::on_pushButton_Esc_clicked()
{
    // 返回到主界面
    if(isEdit_ == false)
    {
        FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_machinelearnform_);
        delete FormFrame::GetInstance()->p_machinelearnform_;
        FormFrame::GetInstance()->p_machinelearnform_ = nullptr;
        FormFrame::GetInstance()->formstacked_id_.machinelearnform_id = -1;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.mainform_id);
    }
    // 返回到纽扣选择界面
    else
    {
        FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_machinelearnform_);
        delete FormFrame::GetInstance()->p_machinelearnform_;
        FormFrame::GetInstance()->p_machinelearnform_ = nullptr;
        FormFrame::GetInstance()->formstacked_id_.machinelearnform_id = -1;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.buttonselform_id);
    }
}
