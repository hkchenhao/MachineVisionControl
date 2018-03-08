#include "MachineLearnForm.h"
#include "ui_MachineLearnForm.h"
#include "Mgr/FormFrame.h"
#include "Utils/ButtonInfo.h"
#include "Utils/WidgetStyle.h"
#include "Utils/QJsonAnalysis.h"
#include "Utils/UserMsgBox.h"
#include "Utils/SystemConstants.h"

MachineLearnForm::MachineLearnForm(QWidget* parent, bool isedit, QString* pjsonpath, QJsonAnalysis* pbuttonjsoninfo, QString* jsonname) :
                                   QWidget(parent), ui(new Ui::MachineLearnWidget)
{
    ui->setupUi(this);
    ui->tabWidget->tabBar()->setStyle(new CustomTabStyle);
    isEdit_ = isedit;
    p_buttonJsonInfo = pbuttonjsoninfo;
    if(pjsonpath)
        p_buttonJsonPath = new QString(*pjsonpath);
    else
        p_buttonJsonPath = nullptr;
    if(jsonname)
        buttonJsonName = *jsonname;
    InitFormWidget();
}

MachineLearnForm::~MachineLearnForm()
{
    delete ui;
    if(p_buttonJsonPath != nullptr) {delete p_buttonJsonPath; p_buttonJsonPath = nullptr;}
}

// [成员函数]初始化界面widget
void MachineLearnForm::InitFormWidget()
{
    // 加载各信息项配置
    QStringList buttominfo_cz;
    for(QString& str : ButtonMaterialStrCnBuf) { buttominfo_cz.append(str); }
    ui->box_cz_0->addItems(buttominfo_cz);
    ui->box_cz_1->addItems(buttominfo_cz);
    QStringList buttominfo_xz;
    for(QString& str : ButtonShapeStrCnBuf) { buttominfo_xz.append(str); }
    ui->box_xz_0->addItems(buttominfo_xz);
    ui->box_xz_1->addItems(buttominfo_xz);
    QStringList buttominfo_xks;
    for(QString& str : ButtonHoleNumStrCnBuf) { buttominfo_xks.append(str); }
    ui->box_xks_0->addItems(buttominfo_xks);
    ui->box_xks_1->addItems(buttominfo_xks);
    QStringList buttominfo_tmx;
    for(QString& str : ButtonLightStrCnBuf) { buttominfo_tmx.append(str); }
    ui->box_tmx_0->addItems(buttominfo_tmx);
    ui->box_tmx_1->addItems(buttominfo_tmx);
    QStringList buttominfo_hs;
    for(QString& str : ButtonPatternStrCnBuf) { buttominfo_hs.append(str); }
    ui->box_hs_0->addItems(buttominfo_hs);
    ui->box_hs_1->addItems(buttominfo_hs);
    QStringList buttominfo_zs;
    for(QString& str : ButtonColorStrCnBuf) { buttominfo_zs.append(str); }
    ui->box_zs_0->addItems(buttominfo_zs);
    ui->box_zs_1->addItems(buttominfo_zs);
    // 如果是纽扣编辑界面
    if(isEdit_ == true)
    {
        ui->pushButton_Save->setEnabled(false);
        QString jsonparentstr;
        QMap<QString, qint32>::iterator it;
        // 纽扣基本信息
        jsonparentstr = QString("infoFront");
        if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
        {
            ui->line_cc->setText(p_buttonJsonInfo->getString(jsonparentstr + "." + "sizeF"));
            it = ButtonMaterialEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "materialF"));
            if(it != ButtonMaterialEnMap.end())
            {
                ui->box_cz_0->setCurrentIndex(it.value());
                ui->box_cz_1->setCurrentIndex(it.value());
            }
            it = ButtonShapeEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "shapeF"));
            if(it != ButtonShapeEnMap.end())
            {
                ui->box_xz_0->setCurrentIndex(it.value());
                ui->box_xz_1->setCurrentIndex(it.value());
            }
            it = ButtonHoleNumEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "holeNumF"));
            if(it != ButtonHoleNumEnMap.end())
            {
                ui->box_xks_0->setCurrentIndex(it.value());
                ui->box_xks_1->setCurrentIndex(it.value());
            }
            it = ButtonLightEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "lightF"));
            if(it != ButtonLightEnMap.end())
            {
                ui->box_tmx_0->setCurrentIndex(it.value());
                ui->box_tmx_1->setCurrentIndex(it.value());
            }
            it = ButtonPatternEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "patternF"));
            if(it != ButtonPatternEnMap.end())
            {
                ui->box_hs_0->setCurrentIndex(it.value());
            }
            it = ButtonColorEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "colorF"));
            if(it != ButtonColorEnMap.end())
            {
                ui->box_zs_0->setCurrentIndex(it.value());
            }
            // 反面花色与主色信息
            jsonparentstr = QString("infoBack");
            if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
            {
                it = ButtonPatternEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "patternF"));
                if(it != ButtonPatternEnMap.end())
                {
                    ui->box_hs_1->setCurrentIndex(it.value());
                }
                it = ButtonColorEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "colorF"));
                if(it != ButtonColorEnMap.end())
                {
                    ui->box_zs_1->setCurrentIndex(it.value());
                }
            }
            // 图片信息
            QString path((*p_buttonJsonPath).replace(p_buttonJsonPath->length()-3, 3, "jpg"));
            QImage buttonimage(path);
            QImage buttonimagescaled = buttonimage.scaled(ui->ButtonImage_zm->width(), ui->ButtonImage_zm->height());
            ui->ButtonImage_zm->setPixmap(QPixmap::fromImage(buttonimagescaled));
            ui->lable_pzwj->setText(buttonJsonName);
        }
        // 纽扣尺寸信息
        jsonparentstr = QString("taskSize");
        if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
        {
            double buttonsize, buttonsizeup, buttonsizedown;
            buttonsize = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "outDia");
            buttonsizeup = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "outDiaDevUp");
            buttonsizedown = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "outDiaDevDown");
            ui->lineEdit2_wj->setText(QString("%1").arg(QString::number(buttonsize,'f',1)));
            ui->lineEdit2_wjpc_up->setText(QString("%1").arg(QString::number(buttonsizeup,'f',1)));
            ui->lineEdit2_wjpc_down->setText(QString("%1").arg(QString::number(buttonsizedown,'f',1)));
            buttonsize = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "holeDia");
            buttonsizeup = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "holeDiaDevUp");
            buttonsizedown = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "holeDiaDevDown");
            ui->lineEdit2_xkj->setText(QString("%1").arg(QString::number(buttonsize,'f',1)));
            ui->lineEdit2_xkjpc_up->setText(QString("%1").arg(QString::number(buttonsizeup,'f',1)));
            ui->lineEdit2_xkjpc_down->setText(QString("%1").arg(QString::number(buttonsizedown,'f',1)));
            buttonsize = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "holeDist");
            buttonsizeup = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "holeDistDevUp");
            buttonsizedown = p_buttonJsonInfo->getDouble(jsonparentstr + "." + "holeDistDevDown");
            ui->lineEdit2_xkjl->setText(QString("%1").arg(QString::number(buttonsize,'f',1)));
            ui->lineEdit2_xkjlpc_up->setText(QString("%1").arg(QString::number(buttonsizeup,'f',1)));
            ui->lineEdit2_xkjlpc_down->setText(QString("%1").arg(QString::number(buttonsizedown,'f',1)));
        }
    }
}

// [成员函数]根据界面widget保存json信息
void MachineLearnForm::SaveButtonJsonInfo(QJsonAnalysis* pjsoninfo)
{
    QString jsonparentstr;
    // JSON时间信息
    pjsoninfo->set("time", QDate::currentDate().toString("yyyy-MM-dd"));
    // JSON纽扣正面基本信息
    jsonparentstr = "infoFront.";
    pjsoninfo->set(jsonparentstr+"materialF", ButtonMaterialStrEnBuf[ui->box_cz_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"sizeF", ui->line_cc->text());
    pjsoninfo->set(jsonparentstr+"shapeF", ButtonShapeStrEnBuf[ui->box_xz_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"holeNumF", ButtonHoleNumStrEnBuf[ui->box_xks_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"lightF", ButtonLightStrEnBuf[ui->box_tmx_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"patternF", ButtonPatternStrEnBuf[ui->box_hs_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"colorF", ButtonColorStrEnBuf[ui->box_zs_0->currentIndex()]);
    // JSON纽扣反面基本信息
    jsonparentstr = "infoBack.";
    pjsoninfo->set(jsonparentstr+"materialB", ButtonMaterialStrEnBuf[ui->box_cz_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"sizeB", ui->line_cc->text());
    pjsoninfo->set(jsonparentstr+"shapeB", ButtonShapeStrEnBuf[ui->box_xz_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"holeNumB", ButtonHoleNumStrEnBuf[ui->box_xks_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"lightB", ButtonLightStrEnBuf[ui->box_tmx_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"patternB", ButtonPatternStrEnBuf[ui->box_hs_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"colorB", ButtonColorStrEnBuf[ui->box_zs_1->currentIndex()]);

    if(isEdit_ == false)
    {

    }
    else
    {

        p_buttonJsonInfo->save(*p_buttonJsonPath);
    }
}

// [slot函数]保存按键
void MachineLearnForm::on_pushButton_Save_clicked()
{
    if(isEdit_ == false)
    {
        QMessageBox* pmsgbox = new QMessageBox(QMessageBox::Question, "", "是否确定保存纽扣配置信息？",
                                               QMessageBox::Yes | QMessageBox::No, this);
        if(pmsgbox->exec() == QMessageBox::No)  { return; }
        // 创建JSON文件
        QJsonAnalysis infojson("{}", false);
        SaveButtonJsonInfo(&infojson);
        infojson.save("info.json");
        UserTextMsgBox* msgbox = new UserTextMsgBox("纽扣信息保存成功！", 500, 110, this);
        msgbox->show();
    }
    else
    {
        QMessageBox* pmsgbox = new QMessageBox(QMessageBox::Question, "", "是否确定修改纽扣配置信息？",
                                               QMessageBox::Yes | QMessageBox::No, this);
        if(pmsgbox->exec() == QMessageBox::No)  { return; }
        // 修改json文件配置信息
        SaveButtonJsonInfo(p_buttonJsonInfo);
        UserTextMsgBox* msgbox = new UserTextMsgBox("纽扣信息修改成功！", 500, 110, this);
        msgbox->show();
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
        emit SignalUpdateButtonSelectForm();
        FormFrame::GetInstance()->p_formstacked_->removeWidget(FormFrame::GetInstance()->p_machinelearnform_);
        delete FormFrame::GetInstance()->p_machinelearnform_;
        FormFrame::GetInstance()->p_machinelearnform_ = nullptr;
        FormFrame::GetInstance()->formstacked_id_.machinelearnform_id = -1;
        FormFrame::GetInstance()->p_formstacked_->setCurrentIndex(FormFrame::GetInstance()->formstacked_id_.buttonselform_id);
    }
}

// [slot函数]控件槽函数
void MachineLearnForm::on_box_cz_0_currentIndexChanged(int index) {ui->box_cz_1->setCurrentIndex(index);}
void MachineLearnForm::on_box_xz_0_currentIndexChanged(int index) {ui->box_xz_1->setCurrentIndex(index);}
void MachineLearnForm::on_box_xks_0_currentIndexChanged(int index) {ui->box_xks_1->setCurrentIndex(index);}
void MachineLearnForm::on_box_tmx_0_currentIndexChanged(int index) {ui->box_tmx_1->setCurrentIndex(index);}

