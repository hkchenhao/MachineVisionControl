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
    QStringList buttominfo_fgx;
    for(QString& str : ButtonReflectTypeStrCnBuf) { buttominfo_fgx.append(str); }
    ui->box_fgx_0->addItems(buttominfo_fgx);
    ui->box_fgx_1->addItems(buttominfo_fgx);
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
            it = ButtonReflectTypeStrEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "reflectF"));
            if(it != ButtonReflectTypeStrEnMap.end())
            {
                ui->box_fgx_0->setCurrentIndex(it.value());
            }
            // 反面花色/主色/反光性信息
            jsonparentstr = QString("infoBack");
            if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
            {
                it = ButtonPatternEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "patternB"));
                if(it != ButtonPatternEnMap.end())
                {
                    ui->box_hs_1->setCurrentIndex(it.value());
                }
                it = ButtonColorEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "colorB"));
                if(it != ButtonColorEnMap.end())
                {
                    ui->box_zs_1->setCurrentIndex(it.value());
                }
                it = ButtonReflectTypeStrEnMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "reflectB"));
                if(it != ButtonReflectTypeStrEnMap.end())
                {
                    ui->box_fgx_1->setCurrentIndex(it.value());
                }
            }
            // 图片信息
            QString picpathstr = *p_buttonJsonPath;
            QString path(picpathstr.replace(p_buttonJsonPath->length()-3, 3, "jpg"));
            QImage buttonimage(path);
            QImage buttonimagescaled = buttonimage.scaled(ui->ButtonImage_zm->width(), ui->ButtonImage_zm->height());
            ui->ButtonImage_zm->setPixmap(QPixmap::fromImage(buttonimagescaled));
            ui->lable_pzwj->setText(buttonJsonName);
        }
        // 纽扣尺寸规格
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
        // 纽扣控制参数
        jsonparentstr = QString("systemParameter");
        if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
        {
            ui->lineEdit->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "lastTime")));
            ui->lineEdit_2->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "pressure")));
            ui->lineEdit_3->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "charge")));
            ui->lineEdit_4->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "transmit")));
            ui->lineEdit_7->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "turntable")));
            ui->lineEdit_5->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "light1")));
            ui->lineEdit_6->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "light2")));
            ui->lineEdit_8->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "light3")));
            ui->lineEdit_9->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "light4")));
            ui->lineEdit_11->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "light5")));
            ui->lineEdit_10->setText(QString::number(p_buttonJsonInfo->getInt(jsonparentstr + "." + "light6")));
        }
        // 检测项目配置
        jsonparentstr = QString("method");
        if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
        {
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethodCs1") == "0") ui->checkBox4_ak->setChecked(false);
            else ui->checkBox4_ak->setChecked(true);
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethodCs2") == "0") ui->checkBox4_cq->setChecked(false);
            else ui->checkBox4_cq->setChecked(true);
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethodCs3") == "0") ui->checkBox4_bjy->setChecked(false);
            else ui->checkBox4_bjy->setChecked(true);
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethodCs4") == "0") ui->checkBox4_sc->setChecked(false);
            else ui->checkBox4_sc->setChecked(true);
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethodCs5") == "0") ui->checkBox4_xc->setChecked(false);
            else ui->checkBox4_xc->setChecked(true);
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethodCs6") == "0") ui->checkBox4_lw->setChecked(false);
            else ui->checkBox4_lw->setChecked(true);
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethodCs7") == "0") ui->checkBox4_fhqx->setChecked(false);
            else ui->checkBox4_fhqx->setChecked(true);
        }
        // 检测方法配置
        jsonparentstr = QString("operator");
        if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
        {
            // 几何检测综合方法
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "geometryOperatorEnable") == "0")
            {
                ui->checkBox4_ccjc_2->setChecked(true);
                ui->checkBox4_ccjc_3->setChecked(false);
                it = GeometryMethodMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "geometryMethod"));
                if(it != GeometryMethodMap.end())
                    ui->box_cz_2->setCurrentIndex(it.value());
            }
            // 几何检测自定义方法
            else
            {
                ui->checkBox4_ccjc_2->setChecked(false);
                ui->checkBox4_ccjc_3->setChecked(true);
                it = GeometryOperator1Map.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "geometryOperator1"));
                if(it != GeometryOperator1Map.end())
                    ui->box_cz_3->setCurrentIndex(it.value());
                it = GeometryOperator2Map.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "geometryOperator2"));
                if(it != GeometryOperator2Map.end())
                    ui->box_cz_4->setCurrentIndex(it.value());
                it = GeometryOperator3Map.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "geometryOperator3"));
                if(it != GeometryOperator3Map.end())
                    ui->box_cz_5->setCurrentIndex(it.value());
            }
            // 表面检测综合方法
            if(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceOperatorEnable") == "0")
            {
                ui->checkBox4_ccjc_4->setChecked(true);
                ui->checkBox4_ccjc_5->setChecked(false);
                it = SurfaceMethodMap.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceMethod"));
                if(it != SurfaceMethodMap.end())
                    ui->box_cz_6->setCurrentIndex(it.value());
            }
            // 表面检测自定义方法
            else
            {
                ui->checkBox4_ccjc_4->setChecked(false);
                ui->checkBox4_ccjc_5->setChecked(true);
                it = SurfaceOperator1Map.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceOperator1"));
                if(it != SurfaceOperator1Map.end())
                    ui->box_cz_7->setCurrentIndex(it.value());
                it = SurfaceOperator2Map.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceOperator2"));
                if(it != SurfaceOperator1Map.end())
                    ui->box_cz_8->setCurrentIndex(it.value());
                it = SurfaceOperator3Map.find(p_buttonJsonInfo->getString(jsonparentstr + "." + "surfaceOperator3"));
                if(it != SurfaceOperator1Map.end())
                    ui->box_cz_9->setCurrentIndex(it.value());
            }
        }
        // 检测方法参数
        jsonparentstr = QString("algParameter");
        if(!p_buttonJsonInfo->getJsonObject(jsonparentstr).isEmpty())
        {
            ui->lineEdit_12->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter1")));
            ui->lineEdit_18->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter2")));
            ui->lineEdit_13->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter3")));
            ui->lineEdit_19->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter4")));
            ui->lineEdit_14->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter5")));
            ui->lineEdit_20->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter6")));
            ui->lineEdit_15->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter7")));
            ui->lineEdit_21->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "geometryMethodParameter8")));
            ui->lineEdit_28->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter1")));
            ui->lineEdit_29->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter2")));
            ui->lineEdit_31->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter3")));
            ui->lineEdit_30->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter4")));
            ui->lineEdit_33->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter5")));
            ui->lineEdit_32->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter6")));
            ui->lineEdit_35->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter7")));
            ui->lineEdit_34->setText(QString::number(p_buttonJsonInfo->getDouble(jsonparentstr + "." + "surfaceMethodParameter8")));
        }
    }
}

// [成员函数]根据界面widget保存json信息
void MachineLearnForm::SaveButtonJsonInfo(QJsonAnalysis* pjsoninfo)
{
    QString jsonparentstr;
    // 时间信息保存
    pjsoninfo->set("time", QDate::currentDate().toString("yyyy-MM-dd"));
    // 纽扣基本信息保存
    jsonparentstr = "infoFront.";
    pjsoninfo->set(jsonparentstr+"materialF", ButtonMaterialStrEnBuf[ui->box_cz_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"sizeF", ui->line_cc->text());
    pjsoninfo->set(jsonparentstr+"shapeF", ButtonShapeStrEnBuf[ui->box_xz_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"holeNumF", ButtonHoleNumStrEnBuf[ui->box_xks_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"lightF", ButtonLightStrEnBuf[ui->box_tmx_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"patternF", ButtonPatternStrEnBuf[ui->box_hs_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"colorF", ButtonColorStrEnBuf[ui->box_zs_0->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"reflectF", ButtonColorStrEnBuf[ui->box_fgx_0->currentIndex()]);
    jsonparentstr = "infoBack.";
    pjsoninfo->set(jsonparentstr+"materialB", ButtonMaterialStrEnBuf[ui->box_cz_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"sizeB", ui->line_cc->text());
    pjsoninfo->set(jsonparentstr+"shapeB", ButtonShapeStrEnBuf[ui->box_xz_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"holeNumB", ButtonHoleNumStrEnBuf[ui->box_xks_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"lightB", ButtonLightStrEnBuf[ui->box_tmx_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"patternB", ButtonPatternStrEnBuf[ui->box_hs_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"colorB", ButtonColorStrEnBuf[ui->box_zs_1->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"reflectB", ButtonColorStrEnBuf[ui->box_fgx_1->currentIndex()]);
    // 尺寸参数信息保存
    jsonparentstr = "taskSize.";
    pjsoninfo->set(jsonparentstr+"outDia", ui->lineEdit2_wj->text().toDouble());
    pjsoninfo->set(jsonparentstr+"outDiaDevUp", ui->lineEdit2_wjpc_up->text().toDouble());
    pjsoninfo->set(jsonparentstr+"outDiaDevDown", ui->lineEdit2_wjpc_down->text().toDouble());
    pjsoninfo->set(jsonparentstr+"holeDia", ui->lineEdit2_xkj->text().toDouble());
    pjsoninfo->set(jsonparentstr+"holeDiaDevUp", ui->lineEdit2_xkjpc_up->text().toDouble());
    pjsoninfo->set(jsonparentstr+"holeDiaDevDown", ui->lineEdit2_xkjpc_down->text().toDouble());
    pjsoninfo->set(jsonparentstr+"holeDist", ui->lineEdit2_xkjl->text().toDouble());
    pjsoninfo->set(jsonparentstr+"holeDistDevUp", ui->lineEdit2_xkjlpc_up->text().toDouble());
    pjsoninfo->set(jsonparentstr+"holeDistDevDown", ui->lineEdit2_xkjlpc_down->text().toDouble());
    // 纽扣控制参数保存
    jsonparentstr = "systemParameter.";
    pjsoninfo->set(jsonparentstr+"lastTime", ui->lineEdit->text().toInt());
    pjsoninfo->set(jsonparentstr+"pressure", ui->lineEdit_2->text().toInt());
    pjsoninfo->set(jsonparentstr+"charge", ui->lineEdit_3->text().toInt());
    pjsoninfo->set(jsonparentstr+"transmit", ui->lineEdit_4->text().toInt());
    pjsoninfo->set(jsonparentstr+"turntable", ui->lineEdit_7->text().toInt());
    pjsoninfo->set(jsonparentstr+"light1", ui->lineEdit_5->text().toInt());
    pjsoninfo->set(jsonparentstr+"light2", ui->lineEdit_6->text().toInt());
    pjsoninfo->set(jsonparentstr+"light3", ui->lineEdit_8->text().toInt());
    pjsoninfo->set(jsonparentstr+"light4", ui->lineEdit_9->text().toInt());
    pjsoninfo->set(jsonparentstr+"light5", ui->lineEdit_11->text().toInt());
    pjsoninfo->set(jsonparentstr+"light6", ui->lineEdit_10->text().toInt());
    // 检测项目配置保存
    jsonparentstr ="method.";
    if(ui->checkBox4_ak->isChecked()) pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "1");
    else pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "0");
    if(ui->checkBox4_cq->isChecked()) pjsoninfo->set(jsonparentstr+"surfaceMethodCs2", "1");
    else pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "0");
    if(ui->checkBox4_bjy->isChecked()) pjsoninfo->set(jsonparentstr+"surfaceMethodCs3", "1");
    else pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "0");
    if(ui->checkBox4_sc->isChecked()) pjsoninfo->set(jsonparentstr+"surfaceMethodCs4", "1");
    else pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "0");
    if(ui->checkBox4_xc->isChecked()) pjsoninfo->set(jsonparentstr+"surfaceMethodCs5", "1");
    else pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "0");
    if(ui->checkBox4_lw->isChecked()) pjsoninfo->set(jsonparentstr+"surfaceMethodCs6", "1");
    else pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "0");
    if(ui->checkBox4_fhqx->isChecked()) pjsoninfo->set(jsonparentstr+"surfaceMethodCs7", "1");
    else pjsoninfo->set(jsonparentstr+"surfaceMethodCs1", "0");
    // 检测方法配置保存
    jsonparentstr = "operator.";
    pjsoninfo->set(jsonparentstr+"geometryMethod", GeometryMethodBuf[ui->box_cz_2->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"geometryOperator1", GeometryOperator1Buf[ui->box_cz_3->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"geometryOperator2", GeometryOperator2Buf[ui->box_cz_4->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"geometryOperator3", GeometryOperator3Buf[ui->box_cz_5->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"surfaceMethod", SurfaceMethodBuf[ui->box_cz_6->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"surfaceOperator1", SurfaceOperator1Buf[ui->box_cz_7->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"surfaceOperator2", SurfaceOperator2Buf[ui->box_cz_8->currentIndex()]);
    pjsoninfo->set(jsonparentstr+"surfaceOperator3", SurfaceOperator3Buf[ui->box_cz_9->currentIndex()]);
    if((ui->checkBox4_ccjc_2->isChecked() == false) && (ui->checkBox4_ccjc_3->isChecked() == true))
        pjsoninfo->set(jsonparentstr+"geometryOperatorEnable", "1");
    else if((ui->checkBox4_ccjc_2->isChecked() == true) && (ui->checkBox4_ccjc_3->isChecked() == false))
        pjsoninfo->set(jsonparentstr+"geometryOperatorEnable", "0");
    else
        pjsoninfo->set(jsonparentstr+"geometryOperatorEnable", "-1");
    if((ui->checkBox4_ccjc_4->isChecked() == false) && (ui->checkBox4_ccjc_5->isChecked() == true))
        pjsoninfo->set(jsonparentstr+"surfaceOperatorEnable", "1");
    else if((ui->checkBox4_ccjc_4->isChecked() == true) && (ui->checkBox4_ccjc_5->isChecked() == false))
        pjsoninfo->set(jsonparentstr+"surfaceOperatorEnable", "0");
    else
        pjsoninfo->set(jsonparentstr+"surfaceOperatorEnable", "-1");
    // 检测方法参数保存
    jsonparentstr = "algParameter.";
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter1", ui->lineEdit_12->text().toDouble());
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter2", ui->lineEdit_18->text().toDouble());
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter3", ui->lineEdit_13->text().toDouble());
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter4", ui->lineEdit_19->text().toDouble());
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter5", ui->lineEdit_14->text().toDouble());
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter6", ui->lineEdit_20->text().toDouble());
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter7", ui->lineEdit_15->text().toDouble());
    pjsoninfo->set(jsonparentstr+"geometryMethodParameter8", ui->lineEdit_21->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter1", ui->lineEdit_28->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter2", ui->lineEdit_29->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter3", ui->lineEdit_31->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter4", ui->lineEdit_30->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter5", ui->lineEdit_33->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter6", ui->lineEdit_32->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter7", ui->lineEdit_35->text().toDouble());
    pjsoninfo->set(jsonparentstr+"surfaceMethodParameter8", ui->lineEdit_34->text().toDouble());
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
        //p_buttonJsonInfo->save(*p_buttonJsonPath);
        UserTextMsgBox* msgbox = new UserTextMsgBox("纽扣信息修改成功！", 500, 110, this);
        msgbox->show();
    }
}

// [slot函数]修改按键
void MachineLearnForm::on_pushButton_Revise_clicked()
{
    QMessageBox* pmsgbox = new QMessageBox(QMessageBox::Question, "", "是否确定修改纽扣配置信息？",
                                           QMessageBox::Yes | QMessageBox::No, this);
    if(pmsgbox->exec() == QMessageBox::No)  { return; }
    // 修改json文件配置信息
    SaveButtonJsonInfo(p_buttonJsonInfo);
    p_buttonJsonInfo->save(*p_buttonJsonPath);
    UserTextMsgBox* msgbox = new UserTextMsgBox("纽扣信息修改成功！", 500, 110, this);
    msgbox->show();
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
void MachineLearnForm::on_checkBox4_ccjc_2_clicked(bool checked)
{
    if(checked)
    {
        ui->checkBox4_ccjc_3->setChecked(false);
        ui->checkBox4_ccjc_3->setEnabled(true);
        ui->checkBox4_ccjc_2->setEnabled(false);
        ui->box_cz_3->setEnabled(false);
        ui->box_cz_4->setEnabled(false);
        ui->box_cz_5->setEnabled(false);
        ui->box_cz_2->setEnabled(true);
    }
}
void MachineLearnForm::on_checkBox4_ccjc_3_clicked(bool checked)
{
    if(checked)
    {
        ui->checkBox4_ccjc_2->setChecked(false);
        ui->checkBox4_ccjc_2->setEnabled(true);
        ui->checkBox4_ccjc_3->setEnabled(false);
        ui->box_cz_3->setEnabled(true);
        ui->box_cz_4->setEnabled(true);
        ui->box_cz_5->setEnabled(true);
        ui->box_cz_2->setEnabled(false);
    }
}
void MachineLearnForm::on_checkBox4_ccjc_4_clicked(bool checked)
{
    if(checked)
    {
        ui->checkBox4_ccjc_5->setChecked(false);
        ui->checkBox4_ccjc_5->setEnabled(true);
        ui->checkBox4_ccjc_4->setEnabled(false);
        ui->box_cz_7->setEnabled(false);
        ui->box_cz_8->setEnabled(false);
        ui->box_cz_9->setEnabled(false);
        ui->box_cz_6->setEnabled(true);
    }
}
void MachineLearnForm::on_checkBox4_ccjc_5_clicked(bool checked)
{
    if(checked)
    {
        ui->checkBox4_ccjc_4->setChecked(false);
        ui->checkBox4_ccjc_4->setEnabled(true);
        ui->checkBox4_ccjc_5->setEnabled(false);
        ui->box_cz_7->setEnabled(true);
        ui->box_cz_8->setEnabled(true);
        ui->box_cz_9->setEnabled(true);
        ui->box_cz_6->setEnabled(false);
    }
}
